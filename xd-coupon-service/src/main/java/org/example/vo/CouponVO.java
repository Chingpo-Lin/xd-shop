package org.example.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CouponVO {


    /**
     * id
     */
    private Long id;

    /**
     * coupon type [NEW_USER，TASK，PROMOTION]
     */
    private String category;

    /**
     * coupon image
     */
    @JsonProperty("coupon_img")
    private String couponImg;

    /**
     * coupon title
     */
    @JsonProperty("coupon_title")
    private String couponTitle;

    /**
     * price deduct
     */
    private BigDecimal price;

    /**
     * limit per person
     */
    @JsonProperty("user_limit")
    private Integer userLimit;

    /**
     * coupon start time
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "US/Pacific")
    @JsonProperty("start_time")
    private Date startTime;

    /**
     * expired time
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "US/Pacific")
    @JsonProperty("end_time")
    private Date endTime;

    /**
     * total count
     */
    @JsonProperty("publish_count")
    private Integer publishCount;

    /**
     * stock
     */
    private Integer stock;

    /**
     * use minimum require
     */
    @JsonProperty("condition_price")
    private BigDecimal conditionPrice;
}
