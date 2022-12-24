package org.example.controller;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.example.service.CouponRecordService;
import org.example.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}

