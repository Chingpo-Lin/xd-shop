package org.example.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.example.enums.CouponCategoryEnum;
import org.example.service.CouponService;
import org.example.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Bob
 * @since 2022-12-21
 */
@Api("coupon module")
@RestController
@RequestMapping("/api/coupon/v1")
public class CouponController {

    @Autowired
    private CouponService couponService;
    
    @ApiOperation("pagination of coupon")
    @GetMapping("page_coupon")
    public JsonData pageCouponList(
            @ApiParam(value = "current page") @RequestParam(value = "page", defaultValue = "1") int page,
            @ApiParam(value = "count in each page") @RequestParam(value = "size", defaultValue = "10") int size) {

        Map<String, Object> pageMap = couponService.pageCouponActivity(page, size);
        return JsonData.buildSuccess(pageMap);
    }

    @ApiOperation("get coupon")
    @GetMapping("add/promotion/{coupon_id}")
    public JsonData addPromotionCoupon(
            @ApiParam(value = "coupon id", required = true) @PathVariable("coupon_id") long couponId) {

        JsonData jsonData = couponService.addCoupon(couponId, CouponCategoryEnum.PROMOTION);
        return jsonData;
    }

}

