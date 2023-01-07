package org.example.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class ProductOrderVO {

    private Long id;

    /**
     * product order id
     */
    @JsonProperty("out_trade_no")
    private String outTradeNo;

    /**
     * NEW,PAY,CANCEL
     */
    private String state;

    /**
     * order create time
     */
    @JsonProperty("create_time")
    private Date createTime;

    /**
     * order total amount
     */
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    /**
     * order pay amount
     */
    @JsonProperty("pay_amount")
    private BigDecimal payAmount;

    /**
     * paytype，debit/credit
     */
    @JsonProperty("pay_type")
    private String payType;

    /**
     * name
     */
    @JsonProperty("nick_name")
    private String nickname;

    /**
     * head image
     */
    @JsonProperty("head_img")
    private String headImg;

    /**
     * user id
     */
    @JsonProperty("user_id")
    private Long userId;

    /**
     * 0 not delete，
     1 mean delete
     */
    private Integer del;

    /**
     * update time
     */
    @JsonProperty("update_time")
    private Date updateTime;

    /**
     * order type DAILY，PROMOTION
     */
    @JsonProperty("order_type")
    private String orderType;

    /**
     * receiver address json
     */
    @JsonProperty("receiver_address")
    private String receiverAddress;

    @JsonProperty("order_item_list")
    private List<OrderItemVO> orderItemList;

}
