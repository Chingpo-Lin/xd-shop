package org.example.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(value = "lock coupon request")
@Data
public class LockCouponRecordRequest {

    /**
     * coupon note id list
     */
    @ApiModelProperty(value = "lock coupon record ids", example = "[1,2,3]")
    @JsonProperty("lock_coupon_record_ids")
    private List<Long> lockCouponRecordIds;

    /**
     * order out trade number
     */
    @ApiModelProperty(value = "order out trade no", example = "123456")
    private String orderOutTradeNo;
}
