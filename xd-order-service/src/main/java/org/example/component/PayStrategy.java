package org.example.component;

import org.example.vo.PayInfoVO;

public interface PayStrategy {

    /**
     * order
     * @return
     */
    String unifiedOrder(PayInfoVO payInfoVO);

    /**
     * refund
     * @param payInfoVO
     * @return
     */
    default String refund(PayInfoVO payInfoVO) {return "";}

    /**
     * query if pay success
     * @param payInfoVO
     * @return
     */
    default String queryPaySuccess(PayInfoVO payInfoVO) {return "";}


}
