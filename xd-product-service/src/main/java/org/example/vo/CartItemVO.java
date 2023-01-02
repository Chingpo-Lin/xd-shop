package org.example.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * single item for cart
 */
public class CartItemVO {

    /**
     * product id
     */
    @JsonProperty("product_id")
    private Long productId;

    /**
     * product count
     */
    private Integer count;

    /**
     * product title
     */
    @JsonProperty("product_title")
    private String productTitle;

    /**
     * product image
     */
    @JsonProperty("product_img")
    private String productImg;

    /**
     * single price for product
     */
    private BigDecimal price;

    /**
     * total price for product
     */
    @JsonProperty("total_price")
    private BigDecimal totalPrice;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    @Override
    public String toString() {
        return "CartItemVO{" +
                "productId=" + productId +
                ", count=" + count +
                ", productTitle='" + productTitle + '\'' +
                ", productImg='" + productImg + '\'' +
                ", price=" + price +
                ", totalPrice=" + totalPrice +
                '}';
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(this.count));
    }
}
