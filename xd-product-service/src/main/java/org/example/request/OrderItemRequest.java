package org.example.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "product item")
@Data
public class OrderItemRequest {

    @ApiModelProperty(value = "product id", example = "1")
    @JsonProperty("product_id")
    private long productId;

    @ApiModelProperty(value = "buy number", example = "2")
    @JsonProperty("buy_num")
    private int buyNum;

}
