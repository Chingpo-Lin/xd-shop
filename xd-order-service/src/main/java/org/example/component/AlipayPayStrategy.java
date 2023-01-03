package org.example.component;

import lombok.extern.slf4j.Slf4j;
import org.example.vo.PayInfoVO;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * example for implement pay strategy
 *
 */
@Slf4j
@Service
public class AlipayPayStrategy implements PayStrategy {

    @Override
    public String unifiedOrder(PayInfoVO payInfoVO) {
        return null;
    }

    @Override
    public String refund(PayInfoVO payInfoVO) {
        return null;
    }

    @Override
    public String queryPaySuccess(PayInfoVO payInfoVO) {
        return null;
    }
}
