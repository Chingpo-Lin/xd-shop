package org.example.service;

import org.example.request.CartItemRequest;

public interface CartService {

    /**
     * add items to cart
     * @param cartItemRequest
     */
    void addToCart(CartItemRequest cartItemRequest);
}
