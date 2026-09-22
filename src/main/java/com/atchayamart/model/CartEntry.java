package com.atchayamart.model;

import java.math.BigDecimal;

public class CartEntry {

    private final Product product;
    private final int quantity;

    public CartEntry(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getLineTotal() {
        return product.getDiscountedPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getOriginalTotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getLineDiscount() {
        return product.getDiscountAmount().multiply(BigDecimal.valueOf(quantity));
    }
}