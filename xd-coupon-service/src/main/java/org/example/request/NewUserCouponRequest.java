package org.example.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class NewUserCouponRequest {

    @ApiModelProperty(value = "user id", example = "2")
    @JsonProperty("user_id")
    private long userId;

    @ApiModelProperty(value = "user name", example = "Bob")
    private String name;


}
