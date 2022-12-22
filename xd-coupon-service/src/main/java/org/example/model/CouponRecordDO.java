package org.example.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author Bob
 * @since 2022-12-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("coupon_record")
public class CouponRecordDO implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * coupon id
     */
    private Long couponId;

    /**
     * get time
     */
    private Date createTime;

    /**
     * use status  NEW,USED,EXPIRED;
     */
    private String useState;

    /**
     * user id
     */
    private Long userId;

    /**
     * user name
     */
    private String userName;

    /**
     * coupon title
     */
    private String couponTitle;

    /**
     * start time
     */
    private Date startTime;

    /**
     * end time
     */
    private Date endTime;

    /**
     * order id
     */
    private Long orderId;

    /**
     * discount price
     */
    private BigDecimal price;

    /**
     * use limitation
     */
    private BigDecimal conditionPrice;


}
