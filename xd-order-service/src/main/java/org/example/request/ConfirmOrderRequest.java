package org.example.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ConfirmOrderRequest {

    /**
     * cart use coupon
     *
     * notice: if null or < 0 means don't use coupon
     */
    @JsonProperty("coupon_record_id")
    private Long couponRecordId;

    /**
     * final purchase product id list
     *
     * id, and read count from cart
     */
    @JsonProperty("product_id_list")
    private List<Long> productIdList;

    /**
     * payment type
     */
    @JsonProperty("pay_type")
    private String payType;

    /**
     * client type
     */
    @JsonProperty("client_type")
    private String clientType;

    /**
     * receive address id
     */
    @JsonProperty("address_id")
    private long addressId;

    /**
     * total price from frontend, backend confirm
     */
    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    /**
     * real pay price after coupon, if not use will same as total price
     */
    @JsonProperty("real_pay_amount")
    private BigDecimal realPayAmount;

    /**
     * token prevent order redundant
     */
    private String token;
}
