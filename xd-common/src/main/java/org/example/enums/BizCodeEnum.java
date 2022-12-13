package org.example.enums;

import lombok.Getter;

/**
 * enum class for status and err msg
 *
 * first 3 number means service, last 3 number means interface
 */
public enum BizCodeEnum {
    /**
     * general operation code
     * */
    OPS_REPEAT(110001,"repete operation"),
    /**
     * verify code
     * */
    CODE_TO_ERROR(240001,"receive number does not match rules"),
    CODE_LIMITED(240002,"code send too quick"),
    CODE_ERROR(240003,"code error"),
    CODE_CAPTCHA_ERROR(240101,"picture code error"),
    /**
     * account
     * */
    ACCOUNT_REPEAT(250001,"account already exist"),
    ACCOUNT_UNREGISTER(250002,"account not exist"),
    ACCOUNT_PWD_ERROR(250003,"name or password error");

    @Getter
    private String msg;

    @Getter
    private int code;

    private BizCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
