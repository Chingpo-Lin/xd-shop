package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.example.request.CartItemRequest;
import org.example.service.CartService;
import org.example.utils.JsonData;
import org.example.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation("clear cart")
    @DeleteMapping("clear")
    public JsonData clearMyCart() {
        cartService.clear();
        return JsonData.buildSuccess();
    }

    @ApiOperation("check my cart")
    @GetMapping("mycart")
    public JsonData findMyCart() {
        CartVO cartVO = cartService.getMyCart();
        return JsonData.buildSuccess(cartVO);
    }

    @ApiOperation("delete cart item")
    @DeleteMapping("delete/{product_id}")
    public JsonData deleteItem(
            @ApiParam(value = "product id", required = true)
            @PathVariable("product_id") long productId) {

        cartService.deleteItem(productId);
        return JsonData.buildSuccess();
    }

    @ApiOperation("change cart item count")
    @PostMapping("change")
    public JsonData changeItem(@ApiParam("cart item") @RequestBody CartItemRequest cartItemRequest) {
        cartService.changeItemNum(cartItemRequest);
        return JsonData.buildSuccess();
    }
}
