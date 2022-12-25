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
import org.example.vo.CartVO;
import org.example.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Override
    public void clear() {
        String cartKey = getCartKey();
        redisTemplate.delete(cartKey);
    }

    @Override
    public CartVO getMyCart() {

        // get all cart item
        List<CartItemVO> cartItemVOList = buildCartItem(true);

        // encapsulate into cartVO and return
        CartVO cartVO = new CartVO();
        cartVO.setCartItems(cartItemVOList);

        return cartVO;
    }

    @Override
    public void deleteItem(long productId) {
        BoundHashOperations<String, Object, Object> mycart = getMyCartOps();
        mycart.delete(productId);
    }

    @Override
    public void changeItemNum(CartItemRequest cartItemRequest) {
        BoundHashOperations<String, Object, Object> mycart = getMyCartOps();

        Object cacheObj = mycart.get(cartItemRequest.getProductId());
        if (cacheObj == null) {
            throw new BizException(BizCodeEnum.CART_FAIL);
        }

        String obj = (String)cacheObj;
        CartItemVO cartItemVO = JSON.parseObject(obj, CartItemVO.class);
        cartItemVO.setCount(cartItemRequest.getBuyNum());
        mycart.put(cartItemRequest.getProductId(), JSON.toJSONString(cartItemVO));
    }

    /**
     * get latest item if need latest price
     * @param latestPrice
     * @return
     */
    private List<CartItemVO> buildCartItem(boolean latestPrice) {

        BoundHashOperations<String, Object, Object> myCart = getMyCartOps();
        List<Object> itemList = myCart.values();
        List<CartItemVO> cartItemVOList = new ArrayList<>();

        // get ids need to check latest price
        List<Long> productIdList = new ArrayList<>();

        if (itemList.size() == 0) {
            return null;
        }

        for (Object item: itemList) {
            CartItemVO cartItemVO = JSON.parseObject((String)item, CartItemVO.class);
            cartItemVOList.add(cartItemVO);

            productIdList.add(cartItemVO.getProductId());
        }

        // check latest product price
        if (latestPrice) {
            setProductLatestPrice(cartItemVOList, productIdList);
        }
        return cartItemVOList;
    }

    /**
     * set product latest price
     * @param cartItemVOList
     * @param productIdList
     */
    private void setProductLatestPrice(List<CartItemVO> cartItemVOList, List<Long> productIdList) {

        // find batch
        List<ProductVO> productVOList = productService.findProductByIdBatch(productIdList);

        // classify
        Map<Long, ProductVO> maps = productVOList.stream()
                .collect(Collectors.toMap(ProductVO::getId, Function.identity()));

        cartItemVOList.stream().forEach(item -> {
            ProductVO productVO = maps.get(item.getProductId());
            item.setProductTitle(productVO.getTitle());
            item.setProductImg(productVO.getCoverImg());
            item.setPrice(productVO.getPrice());
        });
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
