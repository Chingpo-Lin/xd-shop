package org.example.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.BizCodeEnum;
import org.example.enums.CouponCategoryEnum;
import org.example.interceptor.LoginInterceptor;
import org.example.model.LoginUser;
import org.example.request.NewUserCouponRequest;
import org.example.service.CouponService;
import org.example.utils.JsonData;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

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
@Slf4j
@RequestMapping("/api/coupon/v1")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @Autowired
    private RedissonClient redissonClient;
    
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

        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        String lockKey = "lock:coupon:" + couponId + ":" + loginUser.getId();
        RLock rLock = redissonClient.getLock(lockKey);
        // multiple thread enter will stop and release lock
        rLock.lock();

        try {
            JsonData jsonData = couponService.addCoupon(couponId, CouponCategoryEnum.PROMOTION);
            return jsonData;
        } catch (Exception e) {
            log.error("add error:{}", e);
            return JsonData.buildResult(BizCodeEnum.COUPON_GET_FAIL);
        } finally {
            rLock.unlock();
        }
    }

    @GetMapping("lock")
    public JsonData testLock() {
        RLock lock = redissonClient.getLock("lock:coupon:1");
        // stop wait
//        lock.lock(10, TimeUnit.SECONDS);
        lock.lock();

        try {
            log.info("add lock success, deal with logic ..." + Thread.currentThread().getId());
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            log.info("unlock success..." + Thread.currentThread().getId());
            lock.unlock();
        }

        return JsonData.buildSuccess();
    }

    @ApiOperation("RPC-new user register")
    @PostMapping("new_user_coupon")
    public JsonData addNewUserCoupon(
            @ApiParam("new user coupon object")
            @RequestBody NewUserCouponRequest newUserCouponRequest) {

        JsonData jsonData = couponService.initNewUserCoupon(newUserCouponRequest);
        return jsonData;
    }
}

