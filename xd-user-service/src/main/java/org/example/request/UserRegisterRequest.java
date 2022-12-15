package org.example.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "user register object", description = "user register request object")
@Data
public class UserRegisterRequest {

    @ApiModelProperty(value = "name", example = "Bob")
    private String name;

    @ApiModelProperty(value = "password", example = "123456")
    private String pwd;

    @ApiModelProperty(value = "avatar", example = "https://bob-xdshop-img.oss-us-west-1.aliyuncs.com/user/2022/12/14/8514ecbbdee7428caf86a63c9e4421b6.jpeg")
    @JsonProperty("head_img")
    private String headImg;

    @ApiModelProperty(value = "user slogan", example = "Hello, I am Bob")
    private String slogan;

    @ApiModelProperty(value = "0 is female, 1 is male", example = "1")
    private Integer sex;

    @ApiModelProperty(value = "email", example = "m13823601165@163.com")
    private String mail;

    @ApiModelProperty(value = "confirmation code", example = "123456")
    private String code;
}
