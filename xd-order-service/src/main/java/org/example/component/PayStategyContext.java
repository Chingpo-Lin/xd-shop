package org.example.component;

import org.example.vo.PayInfoVO;

public class PayStategyContext {

    private PayStrategy payStrategy;

    public PayStategyContext(PayStrategy payStrategy) {
        this.payStrategy = payStrategy;
    }

    /**
     * pay based on different pay strategy
     * @param payInfoVO
     * @return
     */
    public String executeUnifiedOrder(PayInfoVO payInfoVO) {
        return this.payStrategy.unifiedOrder(payInfoVO);
    }

    /**
     * query payment status based on different
     * @param payInfoVO
     * @return
     */
    public String executeQueryPaySuccess(PayInfoVO payInfoVO) {
        return this.payStrategy.queryPaySuccess(payInfoVO);
    }
}
