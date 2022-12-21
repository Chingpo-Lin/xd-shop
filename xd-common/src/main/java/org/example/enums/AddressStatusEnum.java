package org.example.enums;

public enum AddressStatusEnum {

    /**
     * default address
     */
    DEFAULT_STATUS(1),

    /**
     * not default address
     */
    COMMON_STATUS(0);

    private int status;

    private AddressStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
