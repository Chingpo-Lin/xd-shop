package org.example.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class AddressVO {

    private Long id;

    /**
     * user id
     */
    @JsonProperty("user_id")
    private Long userId;

    /**
     * if default address:0->no;1->yes
     */
    @JsonProperty("default_status")
    private Integer defaultStatus;

    /**
     * receive name
     */
    @JsonProperty("receive_name")
    private String receiveName;

    /**
     * receive phone
     */
    private String phone;

    /**
     * province
     */
    private String province;

    /**
     * city
     */
    private String city;

    /**
     * region
     */
    private String region;

    /**
     * detail address
     */
    @JsonProperty("detail_address")
    private String detailAddress;
}
