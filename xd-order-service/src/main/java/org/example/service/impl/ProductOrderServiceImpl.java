package org.example.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.BizCodeEnum;
import org.example.exception.BizException;
import org.example.feign.UserFeignService;
import org.example.interceptor.LoginInterceptor;
import org.example.model.LoginUser;
import org.example.model.ProductOrderDO;
import org.example.mapper.ProductOrderMapper;
import org.example.request.ConfirmOrderRequest;
import org.example.service.ProductOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.utils.CommonUtil;
import org.example.utils.JsonData;
import org.example.vo.ProductOrderAddressVO;
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
@Slf4j
public class ProductOrderServiceImpl implements ProductOrderService {

    @Autowired
    private ProductOrderMapper productOrderMapper;

    @Autowired
    private UserFeignService userFeignService;

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

        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        String orderOutTradeNo = CommonUtil.getStringNumRandom(32);

        ProductOrderAddressVO addressVO = this.getUserAddress(confirmOrderRequest.getAddressId());

        log.info("receive address info:{}", addressVO);

        return null;
    }

    /**
     * get receive address detail
     * @param addressId
     * @return
     */
    private ProductOrderAddressVO getUserAddress(long addressId) {

        JsonData addressData = userFeignService.detail(addressId);

        if (addressData.getCode() != 0) {
            log.error("get address fail");
            throw new BizException(BizCodeEnum.ADDRESS_NOT_EXIST);
        }

        ProductOrderAddressVO addressVO = addressData.getData(new TypeReference<>(){});

        return addressVO;
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
