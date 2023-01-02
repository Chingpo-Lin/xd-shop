package org.example.service;

import org.example.model.OrderMessage;
import org.example.model.ProductOrderDO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.request.ConfirmOrderRequest;
import org.example.utils.JsonData;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Bob
 * @since 2022-12-25
 */
public interface ProductOrderService {

    /**
     * create order
     * @param confirmOrderRequest
     * @return
     */
    JsonData confirmOrder(ConfirmOrderRequest confirmOrderRequest);

    /**
     * query order state
     * @param outTradeNo
     * @return
     */
    String queryProductOrderState(String outTradeNo);

    /**
     * listen close order
     * @param orderMessage
     * @return
     */
    boolean closeProductOrder(OrderMessage orderMessage);
}
