package org.example.enums;

import lombok.Data;
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
    ACCOUNT_PWD_ERROR(250003,"name or password error"),
    ACCOUNT_NOT_LOGIN(250004,"account not login"),

    /**
     * coupon
     */
    COUPON_NOT_EXIST(260001, "coupon not exist"),
    COUPON_NO_STOCK(260002, "coupon no stock"),
    COUPON_OUT_OF_TIME(260003, "coupon out of time"),
    COUPON_OUT_OF_LIMIT(260004, "coupon out of limit"),
    COUPON_GET_FAIL(260004, "coupon out of limit"),

    /**
     * cart
     */
    CART_FAIL(270001, "add to cart fail"),

    /**
     * address
     */
    ADDRESS_NOT_EXIST(290001, "address not exist"),
    ADDRESS_DEL_FAIL(290001, "address delete fail"),
    /**
     * file related
     */
    FILE_UPLOAD_USER_IMG_FAIL(600101,"user avatar upload fail");

    @Getter
    private String msg;

    @Getter
    private int code;

    private BizCodeEnum(int code, String msg){
        this.code = code;
        this.msg = msg;
    }


}
