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
 * @since 2022-12-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("product_order_item")
public class ProductOrderItemDO implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * order no
     */
    private Long productOrderId;

    private String outTradeNo;

    /**
     * product id
     */
    private Long productId;

    /**
     * product name
     */
    private String productName;

    /**
     * product image
     */
    private String productImg;

    /**
     * buy number
     */
    private Integer buyNum;

    private Date createTime;

    /**
     * total price
     */
    private BigDecimal totalAmount;

    /**
     * single price
     */
    private BigDecimal amount;


}
