package org.example.model;

import lombok.Data;

@Data
public class ProductMessage {

    /**
     * mq id
     */
    private long messageId;

    /**
     * out trade number
     */
    private String outTradeNo;

    /**
     * stock lock task id
     */
    private long taskId;
}
