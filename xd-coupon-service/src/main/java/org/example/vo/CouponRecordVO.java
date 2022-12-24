package org.example.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author Bob
 * @since 2022-12-21
 */

@Data
public class CouponRecordVO{

    private Long id;

    /**
     * coupon id
     */
    @JsonProperty("coupon_id")
    private Long couponId;

    /**
     * use status  NEW,USED,EXPIRED;
     */
    @JsonProperty("use_state")
    private String useState;

    /**
     * user id
     */
    @JsonProperty("user_id")
    private Long userId;

    /**
     * user name
     */
    @JsonProperty("user_name")
    private String userName;

    /**
     * coupon title
     */
    @JsonProperty("coupon_title")
    private String couponTitle;

    /**
     * start time
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "US/Pacific")
    @JsonProperty("start_time")
    private Date startTime;

    /**
     * end time
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "US/Pacific")
    @JsonProperty("end_time")
    private Date endTime;

    /**
     * order id
     */
    @JsonProperty("order_id")
    private Long orderId;

    /**
     * discount price
     */
    private BigDecimal price;

    /**
     * use limitation
     */
    @JsonProperty("condition_price")
    private BigDecimal conditionPrice;
}
