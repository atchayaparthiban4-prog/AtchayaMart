package com.atchayamart.service;

import com.atchayamart.model.CartEntry;
import com.atchayamart.model.Product;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    public static final String SESSION_CART = "cart";

    public Map<Long, Integer> getCart(HttpSession session) {
        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute(SESSION_CART);
        if (cart == null) {
            cart = new LinkedHashMap<>();
            session.setAttribute(SESSION_CART, cart);
        }
        return cart;
    }

    public int size(HttpSession session) {
        return getCart(session).values().stream().mapToInt(Integer::intValue).sum();
    }

    public void add(HttpSession session, Product product, int qty) {
        Map<Long, Integer> cart = getCart(session);
        int quantity = Math.min(Math.max(1, qty), product.getStock());
        cart.merge(product.getId(), quantity, Integer::sum);
        capAtStock(cart, product);
    }

    public void setQuantity(HttpSession session, Product product, int qty) {
        Map<Long, Integer> cart = getCart(session);
        if (qty <= 0) {
            cart.remove(product.getId());
            return;
        }
        int max = Math.max(product.getStock(), 0);
        if (max <= 0) {
            cart.remove(product.getId());
            return;
        }
        cart.put(product.getId(), Math.min(qty, max));
    }

    public void remove(HttpSession session, Long productId) {
        getCart(session).remove(productId);
    }

    public void clear(HttpSession session) {
        getCart(session).clear();
    }

    public List<CartEntry> getEntries(HttpSession session) {
        Map<Long, Integer> cart = getCart(session);
        List<CartEntry> entries = new ArrayList<>();
        cart.forEach((productId, qty) -> {
            Product product = productService.getByIdOrNull(productId);
            if (product != null) {
                entries.add(new CartEntry(product, Math.min(qty, product.getStock())));
            }
        });
        return entries;
    }

    public BigDecimal subtotal(List<CartEntry> entries) {
        return entries.stream().map(CartEntry::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal originalSubtotal(List<CartEntry> entries) {
        return entries.stream().map(CartEntry::getOriginalTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalDiscount(List<CartEntry> entries) {
        return entries.stream().map(CartEntry::getLineDiscount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isEmpty(HttpSession session) {
        return getCart(session).isEmpty();
    }

    private void capAtStock(Map<Long, Integer> cart, Product product) {
        cart.computeIfPresent(product.getId(), (id, qty) -> Math.min(qty, product.getStock()));
    }

    private final ProductService productService;

    public CartService(ProductService productService) {
        this.productService = productService;
    }
}