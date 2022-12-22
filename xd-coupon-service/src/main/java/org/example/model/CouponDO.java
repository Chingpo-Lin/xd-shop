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
@TableName("coupon")
public class CouponDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * coupon type [NEW_USER，TASK，PROMOTION]
     */
    private String category;

    /**
     * publish status, PUBLISH，DRAFT，OFFLINE
     */
    private String publish;

    /**
     * coupon image
     */
    private String couponImg;

    /**
     * coupon title
     */
    private String couponTitle;

    /**
     * price deduct
     */
    private BigDecimal price;

    /**
     * limit per person
     */
    private Integer userLimit;

    /**
     * coupon start time
     */
    private Date startTime;

    /**
     * expired time
     */
    private Date endTime;

    /**
     * total count
     */
    private Integer publishCount;

    /**
     * stock
     */
    private Integer stock;

    private Date createTime;

    /**
     * use minimum require
     */
    private BigDecimal conditionPrice;


}
