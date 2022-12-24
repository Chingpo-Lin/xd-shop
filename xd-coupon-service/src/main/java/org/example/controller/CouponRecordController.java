package org.example.controller;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.BizCodeEnum;
import org.example.service.CouponRecordService;
import org.example.utils.JsonData;
import org.example.vo.CouponRecordVO;
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
@RestController
@Slf4j
@RequestMapping("/api/coupon_record/v1")
public class CouponRecordController {

    @Autowired
    private CouponRecordService couponRecordService;

    @GetMapping("page")
    @ApiOperation("paging of coupon record")
    public JsonData page(@ApiParam("current page") @RequestParam(value = "page", defaultValue = "1") int page,
                         @ApiParam("count in each page") @RequestParam(value = "size", defaultValue = "10") int size) {

        Map<String, Object> pageResult = couponRecordService.page(page, size);
        return JsonData.buildSuccess(pageResult);
    }

    @ApiOperation("find coupon detail")
    @GetMapping("detail/{record_id}")
    public JsonData getCouponRecordDetail(
            @ApiParam(value = "record id")
            @PathVariable("record_id") long recordId) {

        CouponRecordVO couponRecordVO = couponRecordService.findById(recordId);

        return couponRecordVO == null ? JsonData.buildResult(BizCodeEnum.COUPON_NOT_EXIST) : JsonData.buildSuccess(couponRecordVO);

    }
}

