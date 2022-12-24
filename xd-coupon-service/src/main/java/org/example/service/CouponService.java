package org.example.service;

import org.example.enums.CouponCategoryEnum;
import org.example.model.CouponDO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.request.NewUserCouponRequest;
import org.example.utils.JsonData;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Bob
 * @since 2022-12-21
 */
public interface CouponService {

    /**
     * pagination for coupon
     * @param page
     * @param size
     * @return
     */
    Map<String, Object> pageCouponActivity(int page, int size);

    /**
     * add coupon interface
     * @param couponId
     * @param category
     * @return
     */
    JsonData addCoupon(long couponId, CouponCategoryEnum category);

    /**
     * new user coupon
     * @param newUserCouponRequest
     * @return
     */
    JsonData initNewUserCoupon(NewUserCouponRequest newUserCouponRequest);
}
