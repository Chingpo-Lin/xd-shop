package org.example.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class CartItemRequest {

    @ApiModelProperty(value = "product id", example = "11")
    @JsonProperty("product_id")
    private long productId;

    @ApiModelProperty(value = "buy number", example = "1")
    @JsonProperty("buy_num")
    private int buyNum;
}
