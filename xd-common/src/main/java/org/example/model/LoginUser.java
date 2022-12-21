package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
