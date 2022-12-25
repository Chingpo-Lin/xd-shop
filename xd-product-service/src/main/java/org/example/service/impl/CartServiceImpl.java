package org.example.service.impl;

import com.alibaba.fastjson.JSON;
import jodd.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.example.constants.CacheKey;
import org.example.enums.BizCodeEnum;
import org.example.exception.BizException;
import org.example.interceptor.LoginInterceptor;
import org.example.model.LoginUser;
import org.example.request.CartItemRequest;
import org.example.service.CartService;
import org.example.service.ProductService;
import org.example.vo.CartItemVO;
import org.example.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void addToCart(CartItemRequest cartItemRequest) {

        long productId = cartItemRequest.getProductId();
        int buyNum = cartItemRequest.getBuyNum();

        // get cart from redis
        BoundHashOperations<String, Object, Object> myCart = getMyCartOps();
        
        Object cacheObj = myCart.get(productId);
        String result = "";
        
        if (cacheObj != null) {
            result = (String) cacheObj;
        }
        
        if (StringUtils.isEmpty(result)) {
            // not exist, add product
            CartItemVO cartItemVO = new CartItemVO();

            ProductVO productVO = productService.findProductById(productId);
            if (productVO == null) {
                throw new BizException(BizCodeEnum.CART_FAIL);
            }

            cartItemVO.setPrice(productVO.getPrice());
            cartItemVO.setCount(buyNum);
            cartItemVO.setProductId(productId);
            cartItemVO.setProductImg(productVO.getCoverImg());
            cartItemVO.setProductTitle(productVO.getTitle());

            myCart.put(productId, JSON.toJSONString(cartItemVO));

        } else {
            // exist, add count to product
            CartItemVO cartItem = JSON.parseObject(result, CartItemVO.class);
            cartItem.setCount(cartItem.getCount() + buyNum);
            myCart.put(productId, JSON.toJSONString(cartItem));
        }
    }

    /**
     * get my cart general method
     * @return
     */
    private BoundHashOperations<String, Object, Object> getMyCartOps() {
        String cartKey = getCartKey();
        return redisTemplate.boundHashOps(cartKey);
    }

    /**
     * cart key
     * @return
     */
    private String getCartKey() {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();

        String cartKey = String.format(CacheKey.CART_KEY, loginUser.getId());
        return cartKey;
    }
}
