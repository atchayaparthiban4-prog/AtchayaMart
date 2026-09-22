package com.atchayamart.controller;

import com.atchayamart.config.AuthInterceptor;
import com.atchayamart.model.Category;
import com.atchayamart.service.CartService;
import com.atchayamart.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StoreController {

    private final ProductService productService;
    private final CartService cartService;

    public StoreController(ProductService productService, CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String q,
                            @RequestParam(required = false) String category,
                            HttpSession session, Model model) {
        buildStorefront(q, category, session, model);
        return "dashboard";
    }

    @GetMapping("/products")
    public String products(@RequestParam(required = false) String q,
                           @RequestParam(required = false) String category,
                           HttpSession session, Model model) {
        buildStorefront(q, category, session, model);
        return "products";
    }

    private void buildStorefront(String q, String category, HttpSession session, Model model) {
        model.addAttribute("user", session.getAttribute(AuthInterceptor.SESSION_USER));
        model.addAttribute("cartCount", cartService.size(session));
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("categories", Category.values());
        model.addAttribute("products", productService.search(q, category));
        model.addAttribute("offers", productService.offers());
        model.addAttribute("lowStockProducts", productService.lowStock());
        model.addAttribute("outOfStockProducts", productService.outOfStock());
    }
}