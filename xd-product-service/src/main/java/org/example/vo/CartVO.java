package org.example.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class CartVO {

    /**
     * cart items
     */
    @JsonProperty("cart_items")
    private List<CartItemVO> cartItems;

    /**
     * total count of items
     */
    @JsonProperty("total_count")
    private Integer totalCount;

    /**
     * total price of items
     */
    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    /**
     * real pay amount after discount
     */
    @JsonProperty("pay_price")
    private BigDecimal payPrice;

    public List<CartItemVO> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemVO> cartItems) {
        this.cartItems = cartItems;
    }

    /**
     * get total count
     * @return
     */
    public Integer getTotalCount() {
        if (this.cartItems != null) {
            return cartItems.stream().mapToInt(CartItemVO::getCount).sum();
        }
        return 0;
    }

    /**
     * get total price
     * @return
     */
    public BigDecimal getTotalPrice() {
        BigDecimal total = new BigDecimal("0");
        if (this.cartItems != null) {
            for (CartItemVO cartItemVO: cartItems) {
                BigDecimal itemTotalPrice = cartItemVO.getTotalPrice();
                total = total.add(itemTotalPrice);
            }
        }
        return total;
    }

    /**
     * get price after discount
     * @return
     */
    public BigDecimal getPayPrice() {
        BigDecimal total = new BigDecimal("0");
        if (this.cartItems != null) {
            for (CartItemVO cartItemVO: cartItems) {
                BigDecimal itemTotalPrice = cartItemVO.getTotalPrice();
                total = total.add(itemTotalPrice);
            }
        }
        return total;
    }
}
