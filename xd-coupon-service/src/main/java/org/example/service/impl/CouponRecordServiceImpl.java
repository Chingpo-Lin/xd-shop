package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rabbitmq.client.AMQP;
import lombok.extern.slf4j.Slf4j;
import org.example.config.RabbitMQConfig;
import org.example.enums.BizCodeEnum;
import org.example.enums.CouponStateEnum;
import org.example.enums.ProductOrderStateEnum;
import org.example.enums.StockTaskStateEnum;
import org.example.exception.BizException;
import org.example.feign.ProductOrderFeignService;
import org.example.interceptor.LoginInterceptor;
import org.example.mapper.CouponTaskMapper;
import org.example.model.CouponRecordDO;
import org.example.mapper.CouponRecordMapper;
import org.example.model.CouponRecordMessage;
import org.example.model.CouponTaskDO;
import org.example.model.LoginUser;
import org.example.request.LockCouponRecordRequest;
import org.example.service.CouponRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.utils.JsonData;
import org.example.vo.CouponRecordVO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
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
public class CouponRecordServiceImpl implements CouponRecordService {

    @Autowired
    private CouponRecordMapper couponRecordMapper;

    @Autowired
    private CouponTaskMapper couponTaskMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Autowired
    private ProductOrderFeignService productOrderFeignService;

    /**
     * paging
     * @param page
     * @param size
     * @return
     */
    @Override
    public Map<String, Object> page(int page, int size) {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        // encapsulate page info
        Page<CouponRecordDO> pageInfo = new Page<CouponRecordDO>(page, size);

        IPage<CouponRecordDO> recordDOIPage = couponRecordMapper.selectPage(pageInfo,
                new QueryWrapper<CouponRecordDO>().eq("user_id", loginUser.getId())
                        .orderByDesc("create_time"));

        Map<String, Object> pageMap = new HashMap<>(3);

        pageMap.put("total_record", recordDOIPage.getTotal());
        pageMap.put("total_page", recordDOIPage.getPages());
        pageMap.put("current_data", recordDOIPage.getRecords().stream()
                .map(obj -> beanProcess(obj)).collect(Collectors.toList()));

        return pageMap;
    }

    @Override
    public CouponRecordVO findById(long recordId) {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        CouponRecordDO couponRecordDO = couponRecordMapper.selectOne(new QueryWrapper<CouponRecordDO>()
                .eq("id", recordId).eq("user_id", loginUser.getId()));

        if (couponRecordDO == null) {
            return null;
        }
        return beanProcess(couponRecordDO);
    }

    /**
     * lock coupon
     *
     * 1. lock coupon record
     * 2. insert record in task
     * 3. send delayed msg
     * @param recordRequest
     * @return
     */
    @Override
    public JsonData lockCouponRecords(LockCouponRecordRequest recordRequest) {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        String orderOutTradeNo = recordRequest.getOrderOutTradeNo();
        List<Long> lockCouponRecordIds = recordRequest.getLockCouponRecordIds();

        int updateRows = couponRecordMapper.lockUseStateBatch(loginUser.getId(), CouponStateEnum.USED.name(), lockCouponRecordIds);

        List<CouponTaskDO> couponTaskDOList = lockCouponRecordIds.stream().map(obj -> {
            CouponTaskDO couponTaskDO = new CouponTaskDO();
            couponTaskDO.setCreateTime(new Date());
            couponTaskDO.setOutTradeNo(orderOutTradeNo);
            couponTaskDO.setCouponRecordId(obj);
            couponTaskDO.setLockState(StockTaskStateEnum.LOCK.name());
            return couponTaskDO;
        }).collect(Collectors.toList());

        int insertRows = couponTaskMapper.insertBatch(couponTaskDOList);

        log.info("coupon record lock updateRows={}", updateRows);
        log.info("coupon record lock insertRows={}", insertRows);

        if (lockCouponRecordIds.size() == insertRows && insertRows == updateRows) {
            for (CouponTaskDO couponTaskDO: couponTaskDOList) {
                CouponRecordMessage couponRecordMessage = new CouponRecordMessage();
                couponRecordMessage.setOutTradeNo(couponTaskDO.getOutTradeNo());
                couponRecordMessage.setTaskId(couponTaskDO.getId());

                rabbitTemplate.convertAndSend(
                        rabbitMQConfig.getEventExchange(),
                        rabbitMQConfig.getCouponReleaseDelayRoutingKey(),
                        couponRecordMessage);
                log.info("coupon lock msg send success:{}", couponRecordMessage);
            }

            return JsonData.buildSuccess();
        } else {
            throw new BizException(BizCodeEnum.COUPON_RECORD_LOCK_FAIL);
        }
    }

    /**
     * unlock coupon record
     * 1. check task order if exist
     * 2. check order status
     * @param recordMessage
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public boolean releaseCouponRecord(CouponRecordMessage recordMessage) {

        // find if task exist
        CouponTaskDO couponTaskDO = couponTaskMapper.selectOne(new QueryWrapper<CouponTaskDO>()
                .eq("id", recordMessage.getTaskId()));

        if (couponTaskDO == null) {
            log.warn("order did not exist, msg:{}", recordMessage);
        }

        // only handle when lock status
        if (couponTaskDO.getLockState().equalsIgnoreCase(StockTaskStateEnum.LOCK.name())) {
            // find order status
           JsonData jsonData = productOrderFeignService.queryProductOrderState(recordMessage.getOutTradeNo());
           if (jsonData.getCode() == 0) {
               // normal respond, check status
               String state = jsonData.getData().toString();
               if (state.equalsIgnoreCase(ProductOrderStateEnum.NEW.name())) {
                   // NEW state, return to mq, send again
                   log.warn("state is new, return to mq, send again:{}", recordMessage);
                   return false;
               }

               if (state.equalsIgnoreCase(ProductOrderStateEnum.PAY.name())) {
                   // pay state, update task status to finish
                   couponTaskDO.setLockState(StockTaskStateEnum.FINISH.name());
                   couponTaskMapper.update(couponTaskDO, new QueryWrapper<CouponTaskDO>().eq("id", recordMessage.getTaskId()));
                   log.info("order is paid, revise stock lock task to finish:{}", recordMessage);
                   return true;
               }
           }
            // order not exist or cancel, confirm msg,
            // change task status to cancel, recover coupon use record to new
            log.warn("order not exist or cancel, confirm msg, change task status to cancel, " +
                    "recover coupon use record to new:{}", recordMessage);
            couponTaskDO.setLockState(StockTaskStateEnum.CANCEL.name());
            couponTaskMapper.update(couponTaskDO, new QueryWrapper<CouponTaskDO>().eq("id", recordMessage.getTaskId()));

            // recover coupon record to new
            couponRecordMapper.updateState(couponTaskDO.getCouponRecordId(), CouponStateEnum.NEW.name());
            return true;
        } else {
            log.warn("order status is not log, state={}, msg={}", couponTaskDO.getLockState(), recordMessage);
            return true;
        }
    }

    private CouponRecordVO beanProcess(CouponRecordDO couponRecordDO) {
        CouponRecordVO couponRecordVO = new CouponRecordVO();
        BeanUtils.copyProperties(couponRecordDO, couponRecordVO);
        return couponRecordVO;
    }
}
