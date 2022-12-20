package org.example.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "login object", description = "login request object")
public class UserLoginRequest {

    @ApiModelProperty(value = "mail", example = "ljb199992@gmail.com")
    private String mail;

    @ApiModelProperty(value = "password", example = "123456")
    private String pwd;

}
