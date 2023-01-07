package org.example.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.enums.ClientType;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayInfoVO {

    /**
     * out trade number
     */
    private String outTradeNo;

    /**
     * total fee
     */
    private BigDecimal payFee;

    /**
     * pay type
     */
    private String payType;

    /**
     * client type
     */
    private String ClientType;

    /**
     * title
     */
    private String title;

    /**
     * description
     */
    private String description;

    /**
     * pay time limit
     */
    private long orderPayTimeoutMills;
}
