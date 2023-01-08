package org.example.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.api.domain.ItemVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.example.component.PayFactory;
import org.example.config.RabbitMQConfig;
import org.example.constants.CacheKey;
import org.example.enums.*;
import org.example.exception.BizException;
import org.example.feign.CouponFeignService;
import org.example.feign.ProductFeignService;
import org.example.feign.UserFeignService;
import org.example.interceptor.LoginInterceptor;
import org.example.mapper.ProductOrderItemMapper;
import org.example.model.LoginUser;
import org.example.model.OrderMessage;
import org.example.model.ProductOrderDO;
import org.example.mapper.ProductOrderMapper;
import org.example.model.ProductOrderItemDO;
import org.example.request.ConfirmOrderRequest;
import org.example.request.LockCouponRecordRequest;
import org.example.request.LockProductRequest;
import org.example.request.OrderItemRequest;
import org.example.service.ProductOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.utils.CommonUtil;
import org.example.utils.JsonData;
import org.example.vo.CouponRecordVO;
import org.example.vo.OrderItemVO;
import org.example.vo.ProductOrderAddressVO;
import org.example.vo.ProductOrderVO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private ProductOrderItemMapper orderItemMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Autowired
    private PayFactory payFactory;

    @Autowired
    private StringRedisTemplate redisTemplate;

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
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public JsonData confirmOrder(ConfirmOrderRequest confirmOrderRequest) {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        String orderToken = confirmOrderRequest.getToken();
        if (StringUtils.isEmpty(orderToken)) {
            throw new BizException(BizCodeEnum.ORDER_NOT_EXIST);
        }

        // atomic check token, delete token
        String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";

        Long result = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
                Arrays.asList(String.format(CacheKey.SUBMIT_ORDER_TOKEN_KEY, loginUser.getId())), orderToken);

        if (result == 0L) {
            throw new BizException(BizCodeEnum.ORDER_NOT_EXIST);
        }

        String orderOutTradeNo = CommonUtil.getStringNumRandom(32);

        ProductOrderAddressVO addressVO = this.getUserAddress(confirmOrderRequest.getAddressId());

        log.info("receive address info:{}", addressVO);

        List<Long> productIdList = confirmOrderRequest.getProductIdList();

        JsonData cartItemData = productFeignService.confirmOrderCartItem(productIdList);
        log.info("get data:{}", cartItemData);
        List<OrderItemVO> orderItemVOList = cartItemData.getData(new TypeReference<>(){});
        log.info("get order:{}", orderItemVOList);

        if (orderItemVOList == null) {
            // cart item product not exist
            throw new BizException(BizCodeEnum.ORDER_NOT_EXIST);
        }
        log.info("order item VO list:{}", orderItemVOList);
        // check price, minus coupon
        this.checkPrice(orderItemVOList, confirmOrderRequest);

        // lock coupon
        this.lockCouponRecords(confirmOrderRequest, orderOutTradeNo);

        // lock stock
        this.lockProductStocks(orderItemVOList, orderOutTradeNo);

        // create order
        ProductOrderDO productOrderDO = this.saveProductOrder(confirmOrderRequest,
                loginUser, orderOutTradeNo, addressVO);

        // create order items
        this.saveProductOrderItems(orderOutTradeNo, productOrderDO.getId(), orderItemVOList);

        // send delay msg. used for auto close order
        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setOutTradeNo(orderOutTradeNo);

        rabbitTemplate.convertAndSend(rabbitMQConfig.getEventExchange(), rabbitMQConfig.getOrderCloseDelayRoutingKey(), orderMessage);

        // create payment
        // payFactory.pay(null);
        // because I don't implement official payment service.
        // I just change payment to PAY status
//        try {
//            TimeUnit.SECONDS.sleep(10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        productOrderMapper.updateOrderPayState(orderOutTradeNo,
                ProductOrderStateEnum.PAY.name(), ProductOrderStateEnum.NEW.name());

        return JsonData.buildSuccess("Congradulation! you have complete the order");
    }

    /**
     * save order item
     * @param orderOutTradeNo
     * @param orderId
     * @param orderItemVOList
     */
    private void saveProductOrderItems(String orderOutTradeNo, Long orderId, List<OrderItemVO> orderItemVOList) {
        List<ProductOrderItemDO> list = orderItemVOList.stream().map(obj -> {
            ProductOrderItemDO itemDO = new ProductOrderItemDO();
            itemDO.setBuyNum(obj.getCount());
            itemDO.setProductId(obj.getProductId());
            itemDO.setProductImg(obj.getProductImg());
            itemDO.setProductName(obj.getProductTitle());

            itemDO.setOutTradeNo(orderOutTradeNo);
            itemDO.setCreateTime(new Date());

            itemDO.setAmount(obj.getPrice());
            itemDO.setTotalAmount(obj.getTotalPrice());
            itemDO.setProductOrderId(orderId);
            return itemDO;
        }).collect(Collectors.toList());

        orderItemMapper.insertBatch(list);

    }

    /**
     * create order
     * @param confirmOrderRequest
     * @param loginUser
     * @param orderOutTradeNo
     * @param addressVO
     */
    private ProductOrderDO saveProductOrder(ConfirmOrderRequest confirmOrderRequest, LoginUser loginUser, String orderOutTradeNo, ProductOrderAddressVO addressVO) {
        ProductOrderDO productOrderDO = new ProductOrderDO();
        productOrderDO.setUserId(loginUser.getId());
        productOrderDO.setHeadImg(loginUser.getHeadImg());
        productOrderDO.setNickname(loginUser.getName());
        productOrderDO.setOutTradeNo(orderOutTradeNo);
        productOrderDO.setCreateTime(new Date());
        productOrderDO.setDel(0);
        productOrderDO.setOrderType(ProductOrderTypeEnum.DAILY.name());

        // real pay amount
        productOrderDO.setPayAmount(confirmOrderRequest.getRealPayAmount());

        // total price without using coupon
        productOrderDO.setTotalAmount(confirmOrderRequest.getTotalPrice());
        productOrderDO.setState(ProductOrderStateEnum.NEW.name());
        productOrderDO.setPayType(ProductOrderPayTypeEnum.valueOf(confirmOrderRequest.getPayType()).name());

        productOrderDO.setReceiverAddress(JSON.toJSONString(addressVO));

        productOrderMapper.insert(productOrderDO);
        return productOrderDO;
    }

    /**
     * lock product stock
     * @param orderItemVOList
     * @param orderOutTradeNo
     */
    private void lockProductStocks(List<OrderItemVO> orderItemVOList, String orderOutTradeNo) {
        List<OrderItemRequest> itemRequestList = orderItemVOList.stream().map(obj -> {

            OrderItemRequest request = new OrderItemRequest();
            request.setBuyNum(obj.getCount());
            request.setProductId(obj.getProductId());
            return request;

        }).collect(Collectors.toList());

        LockProductRequest lockProductRequest = new LockProductRequest();
        lockProductRequest.setOrderOutTradeNo(orderOutTradeNo);
        lockProductRequest.setOrderItemList(itemRequestList);

        JsonData jsonData = productFeignService.lockProductStock(lockProductRequest);
        if (jsonData.getCode() != 0) {
            log.error("lock stock fail:{}", lockProductRequest);
            throw new BizException(BizCodeEnum.ORDER_NOT_EXIST);
        }
    }

    /**
     * lock coupon
     * @param confirmOrderRequest
     * @param orderOutTradeNo
     */
    private void lockCouponRecords(ConfirmOrderRequest confirmOrderRequest, String orderOutTradeNo) {
        List<Long> lockCouponRecordIds = new ArrayList<>();
        if (confirmOrderRequest.getCouponRecordId() > 0) {
            lockCouponRecordIds.add(confirmOrderRequest.getCouponRecordId());

            LockCouponRecordRequest lockCouponRecordRequest = new LockCouponRecordRequest();
            lockCouponRecordRequest.setOrderOutTradeNo(orderOutTradeNo);
            lockCouponRecordRequest.setLockCouponRecordIds(lockCouponRecordIds);

            // send lock request
            JsonData jsonData = couponFeignService.lockCouponRecords(lockCouponRecordRequest);
            if (jsonData.getCode() != 0) {
                throw new BizException(BizCodeEnum.COUPON_GET_FAIL);
            }
        }
    }

    /**
     * check price
     * 1. calculate latest price
     * 2. get coupon (if can use), total - coupon = final price
     * @param orderItemVOList
     * @param confirmOrderRequest
     */
    private void checkPrice(List<OrderItemVO> orderItemVOList, ConfirmOrderRequest confirmOrderRequest) {
        // product total price
        BigDecimal realPayAmount = new BigDecimal("0");
        if (orderItemVOList != null) {
            log.info("order item list:{}", orderItemVOList);
            for (OrderItemVO orderItemVO : orderItemVOList) {
                BigDecimal itemRealPayAmount = orderItemVO.getTotalPrice();
                realPayAmount = realPayAmount.add(itemRealPayAmount);
            }
        }

        // get coupon, check if can use
        CouponRecordVO couponRecordVO = getCartCouponRecord(confirmOrderRequest.getCouponRecordId());
        // calculate price of cart if satisfy coupon use condition
        if (couponRecordVO != null) {
            log.info("coupon record info:{}", couponRecordVO);
            // calculate if satisfy condition
            if (realPayAmount.compareTo(couponRecordVO.getConditionPrice()) < 0) {
                throw new BizException(BizCodeEnum.COUPON_GET_FAIL);
            }

            if (couponRecordVO.getPrice().compareTo(realPayAmount) > 0) {
                realPayAmount = BigDecimal.ZERO;
            } else {
                realPayAmount = realPayAmount.subtract(couponRecordVO.getPrice());
            }
        }

        if (realPayAmount.compareTo(confirmOrderRequest.getRealPayAmount()) != 0) {
            log.error("price not consistent:{}, real price:{}", confirmOrderRequest, realPayAmount);
            throw new BizException(BizCodeEnum.ORDER_PRICE_FAIL);
        }
    }

    /**
     * get cart coupon record
     * @param couponRecordId
     * @return
     */
    private CouponRecordVO getCartCouponRecord(Long couponRecordId) {
        if (couponRecordId == null || couponRecordId < 0) {
            return null;
        }
        JsonData couponData = couponFeignService.findUserCouponRecordById(couponRecordId);
        
        if (couponData.getCode() != 0) {
            throw new BizException(BizCodeEnum.COUPON_GET_FAIL);
        }
        
        CouponRecordVO couponRecordVO = couponData.getData(new TypeReference<>(){});
        if (!couponAvailable(couponRecordVO)) {
            log.error("coupon use fail");
            throw new BizException(BizCodeEnum.COUPON_GET_FAIL);
        }
        
        return couponRecordVO;
    }

    /**
     * check if coupon available
     * @param couponRecordVO
     * @return
     */
    private boolean couponAvailable(CouponRecordVO couponRecordVO) {
        if (couponRecordVO.getUseState().equalsIgnoreCase(CouponStateEnum.NEW.name())) {
            long currentTimestamp = CommonUtil.getCurrentTimestamp();
            long end = couponRecordVO.getEndTime().getTime();
            long start = couponRecordVO.getStartTime().getTime();
            if (currentTimestamp >= start && currentTimestamp <= end) {
                return true;
            }
        }
        return false;
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

    /**
     * close order
     * @param orderMessage
     * @return
     */
    @Override
    public boolean closeProductOrder(OrderMessage orderMessage) {

        ProductOrderDO productOrderDO = productOrderMapper.selectOne(new QueryWrapper<ProductOrderDO>()
                .eq("out_trade_no", orderMessage.getOutTradeNo()));

        if (productOrderDO == null) {
            // order not exist
            log.warn("confirm since order not exist:{}", orderMessage);
            return true;
        }

        if (productOrderDO.getState().equalsIgnoreCase(ProductOrderStateEnum.PAY.name())) {
            log.info("confirm since order is paied", orderMessage);
            return true;
        }

        // check payment status from payment platform (if fail to notify us)
        String payResult = "";

        // result is null means pay fail, cancel order
        if (StringUtils.isEmpty(payResult)) {
            productOrderMapper.updateOrderPayState(productOrderDO.getOutTradeNo(),
                    ProductOrderStateEnum.CANCEL.name(), ProductOrderStateEnum.NEW.name());
            log.info("result is empty, pay fail, cancel order", orderMessage);
            return true;
        } else {
            // pay success, change state to pay
            log.warn("pay success, but payment flatform redirect may fail");
            productOrderMapper.updateOrderPayState(productOrderDO.getOutTradeNo(),
                    ProductOrderStateEnum.PAY.name(), ProductOrderStateEnum.NEW.name());
            return true;
        }
    }

    /**
     * paging order
     * @param page
     * @param size
     * @param state
     * @return
     */
    @Override
    @Cacheable(value = {"order"}, key = "#root.methodName + #page + '_' + #size + '_' + #state")
    public Map<String, Object> page(int page, int size, String state) {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        Page<ProductOrderDO> pageInfo = new Page<>(page, size);

        IPage<ProductOrderDO> orderDOPage = null;

        if (StringUtils.isEmpty(state)) {
            orderDOPage = productOrderMapper.selectPage(pageInfo, new QueryWrapper<ProductOrderDO>()
                    .eq("user_id", loginUser.getId()));
        } else {
            orderDOPage = productOrderMapper.selectPage(pageInfo, new QueryWrapper<ProductOrderDO>()
                    .eq("user_id", loginUser.getId())
                    .eq("state", state));
        }

        // get order list
        List<ProductOrderDO> productOrderDOList = orderDOPage.getRecords();

        List<ProductOrderVO> productOrderVOList = productOrderDOList.stream().map(orderDO -> {
            List<ProductOrderItemDO> itemDOList = orderItemMapper.selectList(new QueryWrapper<ProductOrderItemDO>()
                    .eq("product_order_id", orderDO.getId()));

            List<OrderItemVO> itemVOList = itemDOList.stream().map(item -> {
                OrderItemVO itemVO = new OrderItemVO();
                BeanUtils.copyProperties(item, itemVO);
                itemVO.setCount(item.getBuyNum());
                itemVO.setPrice(item.getAmount());
                itemVO.setProductTitle(item.getProductName());
                log.info("item vo list:{}, item:{}", itemVO, item);
                return itemVO;
            }).collect(Collectors.toList());

            ProductOrderVO productOrderVO = new ProductOrderVO();
            BeanUtils.copyProperties(orderDO, productOrderVO);
            productOrderVO.setOrderItemList(itemVOList);
            return productOrderVO;
        }).collect(Collectors.toList());

        Map<String, Object> pageMap = new HashMap<>(3);
        pageMap.put("total_record", orderDOPage.getTotal());
        pageMap.put("total_page", orderDOPage.getPages());
        pageMap.put("current_data",productOrderVOList);

        return pageMap;
    }
}
