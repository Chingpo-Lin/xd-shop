package org.example.feign;

import io.swagger.annotations.ApiParam;
import org.example.request.NewUserCouponRequest;
import org.example.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "xd-coupon-service")
public interface CouponFeignService {

    /**
     * new user register coupon distribute
     * @param newUserCouponRequest
     * @return
     */
    @PostMapping("/api/coupon/v1/new_user_coupon")
    JsonData addNewUserCoupon(@RequestBody NewUserCouponRequest newUserCouponRequest);
}
