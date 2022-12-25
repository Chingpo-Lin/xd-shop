package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.example.request.CartItemRequest;
import org.example.service.CartService;
import org.example.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("shopping cart Module")
@RestController
@RequestMapping("/api/cart/v1")
public class CartController {

    @Autowired
    private CartService cartService;

    @ApiOperation("add to cart")
    @PostMapping("add")
    public JsonData addToCart(
            @ApiParam("shopping item")
            @RequestBody CartItemRequest cartItemRequest) {

        cartService.addToCart(cartItemRequest);
        return JsonData.buildSuccess();
    }
}
