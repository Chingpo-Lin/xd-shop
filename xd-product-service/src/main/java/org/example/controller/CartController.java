package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.example.request.CartItemRequest;
import org.example.service.CartService;
import org.example.utils.JsonData;
import org.example.vo.CartItemVO;
import org.example.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("shopping cart Module")
@RestController
@Slf4j
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

    /**
     * confirm order latest price
     *
     * will also clear cart
     * @param productIdList
     * @return
     */
    @ApiOperation("get corresponding order items")
    @PostMapping("confirm_order_cart_items")
    public JsonData confirmOrderCartItems(
            @ApiParam("product id list")
            @RequestBody List<Long> productIdList) {

        List<CartItemVO> cartItemVOList = cartService.confirmOrderCartItems(productIdList);
        log.info("get return list:{}", cartItemVOList);
        return JsonData.buildSuccess(cartItemVOList);

    }
}
