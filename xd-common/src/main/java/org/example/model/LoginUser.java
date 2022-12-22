package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
