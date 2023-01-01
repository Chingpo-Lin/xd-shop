package org.example.service;

import org.example.request.CartItemRequest;
import org.example.vo.CartItemVO;
import org.example.vo.CartVO;

import java.util.List;

public interface CartService {

    /**
     * add items to cart
     * @param cartItemRequest
     */
    void addToCart(CartItemRequest cartItemRequest);

    /**
     * clear cart
     */
    void clear();

    /**
     * check my cart
     * @return
     */
    CartVO getMyCart();

    /**
     * delete cart item
     * @param productId
     */
    void deleteItem(long productId);

    /**
     * change cart item count
     * @param cartItemRequest
     */
    void changeItemNum(CartItemRequest cartItemRequest);

    /**
     * confirm cart items info
     * @param productIdList
     * @return
     */
    List<CartItemVO> confirmOrderCartItems(List<Long> productIdList);
}
