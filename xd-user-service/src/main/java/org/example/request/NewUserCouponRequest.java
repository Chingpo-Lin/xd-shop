package org.example.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NewUserCouponRequest {

    @JsonProperty("user_id")
    private long userId;

    private String name;


}
