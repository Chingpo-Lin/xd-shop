package org.example.constants;

public class CacheKey {

    /**
     * register code, first is type, send is receiver number
     */
    public static final String CHECK_CODE_KEY = "code:%s:%s";

    /**
     * cart hash, key is unique for user
     */
    public static final String CART_KEY = "cart:%s";
}
