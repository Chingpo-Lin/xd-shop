package org.example.component;

import org.example.enums.ProductOrderPayTypeEnum;
import org.example.vo.PayInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PayFactory {

    @Autowired
    private AlipayPayStrategy alipayPayStrategy;

    /**
     * create payment, simple factory mode
     * @param payInfoVO
     * @return
     */
    public String pay(PayInfoVO payInfoVO) {
        String payType = payInfoVO.getPayType();
        if (ProductOrderPayTypeEnum.ALIPAY.name().equalsIgnoreCase(payType)) {
            PayStategyContext payStategyContext = new PayStategyContext(alipayPayStrategy);
            return payStategyContext.executeUnifiedOrder(payInfoVO);
        } else if (ProductOrderPayTypeEnum.DEBIT.name().equalsIgnoreCase(payType)) {
            // add corresponding strategy
        }
        return "";
    }

    /**
     * query order pay state
     * return null if pay fail
     * @param payInfoVO
     * @return
     */
    public String queryPaySuccess(PayInfoVO payInfoVO) {
        String payType = payInfoVO.getPayType();
        if (ProductOrderPayTypeEnum.ALIPAY.name().equalsIgnoreCase(payType)) {
            PayStategyContext payStategyContext = new PayStategyContext(alipayPayStrategy);
            return payStategyContext.executeQueryPaySuccess(payInfoVO);
        } else if (ProductOrderPayTypeEnum.DEBIT.name().equalsIgnoreCase(payType)) {
            // add corresponding strategy
        }
        return "";
    }


}
