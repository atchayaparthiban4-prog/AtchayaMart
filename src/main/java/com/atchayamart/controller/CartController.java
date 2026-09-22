package com.atchayamart.controller;

import com.atchayamart.config.AuthInterceptor;
import com.atchayamart.model.CartEntry;
import com.atchayamart.model.Product;
import com.atchayamart.service.CartService;
import com.atchayamart.service.ProductService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final ProductService productService;
    private final CartService cartService;

    public CartController(ProductService productService, CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }

    @GetMapping
    public String cart(HttpSession session, Model model) {
        List<CartEntry> entries = cartService.getEntries(session);
        model.addAttribute("user", session.getAttribute(AuthInterceptor.SESSION_USER));
        model.addAttribute("cartCount", cartService.size(session));
        model.addAttribute("entries", entries);
        model.addAttribute("subtotal", cartService.originalSubtotal(entries));
        model.addAttribute("discount", cartService.totalDiscount(entries));
        model.addAttribute("total", cartService.subtotal(entries));
        return "cart";
    }

    @PostMapping("/add")
    public String add(@RequestParam Long productId,
                      @RequestParam(defaultValue = "1") int qty,
                      @RequestParam(required = false) String returnTo,
                      HttpSession session) {
        Product product = productService.getById(productId);
        cartService.add(session, product, qty);
        return "redirect:" + safeReturnTo(returnTo);
    }

    @PostMapping("/update")
    public String update(@RequestParam Long productId,
                         @RequestParam int qty,
                         HttpSession session) {
        Product product = productService.getById(productId);
        cartService.setQuantity(session, product, qty);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String remove(@RequestParam Long productId, HttpSession session) {
        cartService.remove(session, productId);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clear(HttpSession session) {
        cartService.clear(session);
        return "redirect:/cart";
    }

    private String safeReturnTo(String returnTo) {
        if (returnTo != null && returnTo.startsWith("/") && !returnTo.startsWith("//")) {
            return returnTo;
        }
        return "/dashboard";
    }
}