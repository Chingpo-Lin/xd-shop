package org.example.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(value = "product lock object", description = "product lock object protocol")
@Data
public class LockProductRequest {

    @ApiModelProperty(value = "order id", example = "123123123123")
    @JsonProperty("order_out_trade_no")
    private String orderOutTradeNo;

    @ApiModelProperty(value = "order item")
    @JsonProperty("order_item_list")
    private List<OrderItemRequest> orderItemList;

}
