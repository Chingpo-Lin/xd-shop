package org.example.model;

import lombok.Data;

@Data
public class CouponRecordMessage {

    /**
     * msg id
     */
    private long messageId;

    /**
     * out trade no
     */
    private String outTradeNo;

    /**
     * lock stock task id
     */
    private Long taskId;
}
