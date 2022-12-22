package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.CouponCategoryEnum;
import org.example.enums.CouponPublishEnum;
import org.example.model.CouponDO;
import org.example.mapper.CouponMapper;
import org.example.service.CouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.utils.JsonData;
import org.example.vo.CouponVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
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
     * @param couponId
     * @param category
     * @return
     */
    @Override
    public JsonData addCoupon(long couponId, CouponCategoryEnum category) {
        return JsonData.buildSuccess();
    }

    private CouponVO beanProcess(CouponDO couponDO) {
        CouponVO couponVO = new CouponVO();
        BeanUtils.copyProperties(couponDO, couponVO);
        return couponVO;
    }
}
