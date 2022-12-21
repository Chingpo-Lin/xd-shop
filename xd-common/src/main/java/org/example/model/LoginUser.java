package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginUser {

    /**
     * key
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
     * mail
     */
    private String mail;
}
