package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.model.ProductOrderDO;
import org.example.mapper.ProductOrderMapper;
import org.example.request.ConfirmOrderRequest;
import org.example.service.ProductOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Bob
 * @since 2022-12-25
 */
@Service
public class ProductOrderServiceImpl implements ProductOrderService {

    @Autowired
    private ProductOrderMapper productOrderMapper;

    /**
     * 1. check if submit order redundant
     * 2. check address belongs to current user
     * 3. get latest latest cart item and price
     * 4. order check (if price same as latest)
     *      - get coupon
     *      - check price
     * 5. lock coupon
     * 6. lock stock
     * 7. create order object
     * 8. create child order object
     * 9. send delay msg for auto close order
     * 10 create payment info (use payment api)
     * @param confirmOrderRequest
     * @return
     */
    @Override
    public JsonData confirmOrder(ConfirmOrderRequest confirmOrderRequest) {
        return null;
    }

    /**
     * query order state
     * @param outTradeNo
     * @return
     */
    @Override
    public String queryProductOrderState(String outTradeNo) {

        ProductOrderDO productOrderDO = productOrderMapper.selectOne(new QueryWrapper<ProductOrderDO>().eq("out_trade_no", outTradeNo));
        if (productOrderDO == null) {
            return "";
        } else {
            return productOrderDO.getState();
        }
    }
}
