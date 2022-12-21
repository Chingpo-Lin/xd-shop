package org.example.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "address object", description = "add address object")
public class AddressAddRequest {

    /**
     * if default address:0->no;1->yes
     */
    @ApiModelProperty(value = "if default address, 0->no;1->yes", example = "0")
    @JsonProperty("default_status")
    private Integer defaultStatus;

    /**
     * receive name
     */
    @ApiModelProperty(value = "receive name", example = "Bob")
    @JsonProperty("receive_name")
    private String receiveName;

    /**
     * receive phone
     */
    @ApiModelProperty(value = "receive phone", example = "2065913460")
    @JsonProperty("phone")
    private String phone;

    /**
     * province
     */
    @ApiModelProperty(value = "state", example = "California")
    @JsonProperty("province")
    private String province;

    /**
     * city
     */
    @ApiModelProperty(value = "city", example = "Santa Clara")
    @JsonProperty("city")
    private String city;

    /**
     * region
     */
    @ApiModelProperty(value = "county", example = "Santa Clara")
    @JsonProperty("region")
    private String region;

    /**
     * detail address
     */
    @ApiModelProperty(value = "detail address", example = "1050 Benton Street")
    @JsonProperty("detail_address")
    private String detailAddress;
}
