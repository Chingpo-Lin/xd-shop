package org.example.model;

import lombok.Data;

@Data
public class OrderMessage {

    /**
     * message id
     */
    private Long messageId;

    /**
     * order number
     */
    private String outTradeNo;

}
