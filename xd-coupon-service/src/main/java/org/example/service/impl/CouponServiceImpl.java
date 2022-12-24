package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.BizCodeEnum;
import org.example.enums.CouponCategoryEnum;
import org.example.enums.CouponPublishEnum;
import org.example.enums.CouponStateEnum;
import org.example.exception.BizException;
import org.example.interceptor.LoginInterceptor;
import org.example.mapper.CouponRecordMapper;
import org.example.model.CouponDO;
import org.example.mapper.CouponMapper;
import org.example.model.CouponRecordDO;
import org.example.model.LoginUser;
import org.example.service.CouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.utils.CommonUtil;
import org.example.utils.JsonData;
import org.example.vo.CouponVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Bob
 * @since 2022-12-21
 */

@Service
@Slf4j
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private CouponRecordMapper couponRecordMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public Map<String, Object> pageCouponActivity(int page, int size) {

        Page<CouponDO> pageInfo = new Page<>(page, size);

        IPage<CouponDO> couponDOIPage = couponMapper.selectPage(pageInfo, new QueryWrapper<CouponDO>()
                .eq("publish", CouponPublishEnum.PUBLISH)
                .eq("category", CouponCategoryEnum.PROMOTION)
                .orderByDesc("create_time"));

        Map<String, Object> pageMap = new HashMap<>(3);
        pageMap.put("total_record", couponDOIPage.getTotal());
        pageMap.put("total_page", couponDOIPage.getPages());
        pageMap.put("current_data", couponDOIPage.getRecords().stream().map(obj -> {
            return beanProcess(obj);
        }).collect(Collectors.toList()));

        return pageMap;
    }

    /**
     * add coupon
     * 1. check if coupon exist
     * 2. check if coupon can be added (time, stock, limit)
     * 3. stock - 1
     * 4. save record
     * @param couponId
     * @param category
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public JsonData addCoupon(long couponId, CouponCategoryEnum category) {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        String lockKey = "lock:coupon:" + couponId;
        RLock rLock = redissonClient.getLock(lockKey);
        // multiple thread enter will stop and release lock
        rLock.lock();
        // lock with 10s expire time, but give up default watchdog function
//        rLock.lock(10, TimeUnit.SECONDS);
//        // comment part is use lua + redis, but we use redisson + redis to improve
//        String uuid = CommonUtil.generateUUID();
//        String lockKey = "lock:coupon:" + couponId;
//        Boolean lockFlag = redisTemplate.opsForValue().setIfAbsent(lockKey, uuid, Duration.ofMinutes(10));

//        if (lockFlag) {
        // lock success
        log.info("add coupon lock success:{}", Thread.currentThread().getId());

//        // test watchdog of redisson auto extend ttl of redis key
//        try {
//            TimeUnit.SECONDS.sleep(90);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        try {
            // logic
            CouponDO couponDO = couponMapper.selectOne(new QueryWrapper<CouponDO>()
                    .eq("id", couponId)
                    .eq("category", category.name())
                    .eq("publish", CouponPublishEnum.PUBLISH));

            if (couponDO == null) {
                throw new BizException(BizCodeEnum.COUPON_NOT_EXIST);
            }

            // check if coupon can be added
            checkCoupon(couponDO, loginUser.getId());

            // create coupon record
            CouponRecordDO couponRecordDO = new CouponRecordDO();
            BeanUtils.copyProperties(couponDO, couponRecordDO);
            couponRecordDO.setCreateTime(new Date());
            couponRecordDO.setUseState(CouponStateEnum.NEW.name());
            couponRecordDO.setUserId(loginUser.getId());
            couponRecordDO.setUserName(loginUser.getName());
            couponRecordDO.setCouponId(couponId);
            couponRecordDO.setId(null); // beanutils will copy coupon id to here which is wrong

            // minus stock
            int rows = couponMapper.reduceStock(couponId);

            if (rows == 1) {
                // save when success deduct stock
                couponRecordMapper.insert(couponRecordDO);
            } else {
                log.warn("fail to get coupon:{}, user:{}", couponId, loginUser);
                throw new BizException(BizCodeEnum.COUPON_NO_STOCK);
            }
        } finally {
            rLock.unlock();
                // script can ensure atomic since get and del success together
//                String script = "if redis.call('get',KEYS[1]) == ARGV[1]" +
//                        " then return redis.call('del',KEYS[1])" +
//                        " else return 0 end";
//
//                Integer result = redisTemplate.execute(new DefaultRedisScript<>(script, Integer.class),
//                        Arrays.asList(lockKey), uuid);
        }

//        } else {
            // lock fail
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                log.error("fail to recursion");
//            }
//            addCoupon(couponId, category);
//        }
        return JsonData.buildSuccess();
    }

    /**
     * check if coupon can be added
     * @param couponDO
     * @param userId
     */
    private void checkCoupon(CouponDO couponDO, Long userId) {

        // if published
        if (!couponDO.getPublish().equals(CouponPublishEnum.PUBLISH.name())) {
            throw new BizException(BizCodeEnum.COUPON_GET_FAIL);
        }

        // stock is great than 0
        if (couponDO.getStock() <= 0) {
            throw new BizException(BizCodeEnum.COUPON_NO_STOCK);
        }

        // if still working
        long time = CommonUtil.getCurrentTimestamp();
        long start = couponDO.getStartTime().getTime();
        long end = couponDO.getEndTime().getTime();

        if (time < start || time > end) {
            throw new BizException(BizCodeEnum.COUPON_OUT_OF_TIME);
        }

        // if user exceed add limit
        int recordNum = couponRecordMapper.selectCount(new QueryWrapper<CouponRecordDO>()
                .eq("coupon_id", couponDO.getId())
                .eq("user_id", userId));

        if (recordNum >= couponDO.getUserLimit()) {
            throw new BizException(BizCodeEnum.COUPON_OUT_OF_LIMIT);
        }
    }

    private CouponVO beanProcess(CouponDO couponDO) {
        CouponVO couponVO = new CouponVO();
        BeanUtils.copyProperties(couponDO, couponVO);
        return couponVO;
    }
}
