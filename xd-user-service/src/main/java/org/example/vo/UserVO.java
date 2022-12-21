package org.example.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserVO {

    /**
     * user id
     */
    private Long id;

    /**
     * name
     */
    private String name;

    /**
     * avatar
     */
    @JsonProperty("head_img")
    private String headImg;

    /**
     * sign
     */
    private String slogan;

    /**
     * 0 is female，1 is male
     */
    private Integer sex;

    /**
     * point
     */
    private Integer points;

    /**
     * email
     */
    private String mail;

}
