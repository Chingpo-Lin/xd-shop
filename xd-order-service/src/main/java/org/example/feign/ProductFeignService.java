package org.example.feign;

import org.example.request.LockProductRequest;
import org.example.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "xd-product-service")
public interface ProductFeignService {

    /**
     * get latest order price and clear cart
     * @param productIdList
     * @return
     */
    @PostMapping("/api/cart/v1/confirm_order_cart_items")
    JsonData confirmOrderCartItem(@RequestBody List<Long> productIdList);

    /**
     * lock product stock
     * @param lockProductRequest
     * @return
     */
    @PostMapping("/api/product/v1/lock_product")
    JsonData lockProductStock(@RequestBody LockProductRequest lockProductRequest);
}
