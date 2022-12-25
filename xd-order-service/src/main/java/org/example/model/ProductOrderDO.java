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
@TableName("product_order")
public class ProductOrderDO implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * product order id
     */
    private String outTradeNo;

    /**
     * NEW,PAY,CANCEL
     */
    private String state;

    /**
     * order create time
     */
    private Date createTime;

    /**
     * order total amount
     */
    private BigDecimal totalAmount;

    /**
     * order pay amount
     */
    private BigDecimal payAmount;

    /**
     * paytype，debit/credit
     */
    private String payType;

    /**
     * name
     */
    private String nickname;

    /**
     * head image
     */
    private String headImg;

    /**
     * user id
     */
    private Integer userId;

    /**
     * 0 not delete，
1 mean delete
     */
    private Integer del;

    /**
     * update time
     */
    private Date updateTime;

    /**
     * order type DAILY，PROMOTION
     */
    private String orderType;

    /**
     * receiver address json
     */
    private String receiverAddress;


}
