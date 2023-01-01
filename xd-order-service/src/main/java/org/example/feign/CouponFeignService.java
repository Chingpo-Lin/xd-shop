package org.example.feign;

import org.example.request.LockCouponRecordRequest;
import org.example.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "xd-coupon-service")
public interface CouponFeignService {

    /**
     * check user coupon if usable, prevent horizontal attack
     * @param recordId
     * @return
     */
    @GetMapping("/api/coupon_record/v1/detail/{record_id}")
    JsonData findUserCouponRecordById(@PathVariable("record_id") long recordId);

    /**
     * lock coupon
     * @param lockCouponRecordRequest
     * @return
     */
    @PostMapping("/api/coupon_record/v1/lock_records")
    JsonData lockCouponRecords(@RequestBody LockCouponRecordRequest lockCouponRecordRequest);
}
