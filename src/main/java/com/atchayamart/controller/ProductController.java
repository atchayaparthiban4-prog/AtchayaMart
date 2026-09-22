package com.atchayamart.controller;

import com.atchayamart.config.AuthInterceptor;
import com.atchayamart.dto.ProductRequest;
import com.atchayamart.exception.AuthException;
import com.atchayamart.model.Product;
import com.atchayamart.model.User;
import com.atchayamart.service.CartService;
import com.atchayamart.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CartService cartService;

    public ProductController(ProductService productService, CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }

    @GetMapping("/products/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        addUserToModel(session, model);
        model.addAttribute("cartCount", cartService.size(session));
        model.addAttribute("product", productService.getById(id));
        model.addAttribute("returnTo", "/products/" + id);
        return "product-detail";
    }

    @GetMapping("/seller/products")
    public String myProducts(HttpSession session, Model model) {
        User user = requireSeller(session, model);
        model.addAttribute("cartCount", cartService.size(session));
        model.addAttribute("products", productService.getBySeller(user));
        return "seller-products";
    }

    @GetMapping("/seller/products/new")
    public String newProduct(HttpSession session, Model model) {
        requireSeller(session, model);
        model.addAttribute("cartCount", cartService.size(session));
        model.addAttribute("categories", com.atchayamart.model.Category.values());
        model.addAttribute("productRequest", new ProductRequest());
        model.addAttribute("editing", false);
        return "seller-product-form";
    }

    @PostMapping("/seller/products")
    public String create(@Valid @ModelAttribute("productRequest") ProductRequest request,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model) {
        User user = requireSeller(session, model);
        if (bindingResult.hasErrors()) {
            model.addAttribute("cartCount", cartService.size(session));
            model.addAttribute("categories", com.atchayamart.model.Category.values());
            model.addAttribute("editing", false);
            return "seller-product-form";
        }
        productService.create(request, user);
        return "redirect:/seller/products?created";
    }

    @GetMapping("/seller/products/{id}/edit")
    public String editPage(@PathVariable Long id, HttpSession session, Model model) {
        User user = requireSeller(session, model);
        Product product = productService.getById(id);
        if (!productService.canManage(product, user)) {
            return "redirect:/seller/products?forbidden";
        }
        model.addAttribute("cartCount", cartService.size(session));
        model.addAttribute("categories", com.atchayamart.model.Category.values());
        model.addAttribute("product", product);
        model.addAttribute("editing", true);
        model.addAttribute("productRequest", toRequest(product));
        return "seller-product-form";
    }

    @PostMapping("/seller/products/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("productRequest") ProductRequest request,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model) {
        User user = requireSeller(session, model);
        if (bindingResult.hasErrors()) {
            Product product = productService.getById(id);
            model.addAttribute("cartCount", cartService.size(session));
            model.addAttribute("categories", com.atchayamart.model.Category.values());
            model.addAttribute("product", product);
            model.addAttribute("editing", true);
            return "seller-product-form";
        }
        try {
            productService.update(id, request, user);
            return "redirect:/seller/products?updated";
        } catch (AuthException ex) {
            return "redirect:/seller/products?forbidden";
        }
    }

    @PostMapping("/seller/products/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute(AuthInterceptor.SESSION_USER);
        try {
            productService.delete(id, user);
            return "redirect:/seller/products?deleted";
        } catch (AuthException ex) {
            return "redirect:/seller/products?forbidden";
        }
    }

    private User requireSeller(HttpSession session, Model model) {
        User user = (User) session.getAttribute(AuthInterceptor.SESSION_USER);
        model.addAttribute("user", user);
        if (user == null || !user.isSellerOrAdmin()) {
            throw new AuthException("Seller access required");
        }
        return user;
    }

    private void addUserToModel(HttpSession session, Model model) {
        model.addAttribute("user", session.getAttribute(AuthInterceptor.SESSION_USER));
    }

    private ProductRequest toRequest(Product product) {
        ProductRequest request = new ProductRequest();
        request.setName(product.getName());
        request.setDescription(product.getDescription());
        request.setPrice(product.getPrice());
        request.setCategory(product.getCategory());
        request.setUnit(product.getUnit());
        request.setStock(product.getStock());
        request.setMinStock(product.getMinStock());
        request.setDiscountPercent(product.getDiscountPercent());
        request.setImageUrl(product.getImageUrl());
        return request;
    }
}