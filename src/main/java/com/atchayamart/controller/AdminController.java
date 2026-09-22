package com.atchayamart.controller;

import com.atchayamart.config.AuthInterceptor;
import com.atchayamart.dto.ProductRequest;
import com.atchayamart.exception.AuthException;
import com.atchayamart.model.Category;
import com.atchayamart.model.Order;
import com.atchayamart.model.OrderStatus;
import com.atchayamart.model.Product;
import com.atchayamart.model.User;
import com.atchayamart.service.CartService;
import com.atchayamart.service.OrderService;
import com.atchayamart.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final OrderService orderService;
    private final CartService cartService;

    public AdminController(ProductService productService, OrderService orderService, CartService cartService) {
        this.productService = productService;
        this.orderService = orderService;
        this.cartService = cartService;
    }

    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        User user = requireAdmin(session, model);
        model.addAttribute("cartCount", cartService.size(session));
        model.addAttribute("productCount", productService.count());
        model.addAttribute("lowStockCount", productService.lowStock().size());
        model.addAttribute("outOfStockCount", productService.outOfStock().size());
        List<Order> orders = orderService.getAll();
        model.addAttribute("orderCount", orders.size());
        model.addAttribute("pendingCount",
                orders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING
                        || o.getStatus() == OrderStatus.CONFIRMED).count());
        BigDecimal revenue = orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .map(Order::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("revenue", revenue);
        return "admin";
    }

    @GetMapping("/stock")
    public String stock(@RequestParam(required = false) String q,
                        @RequestParam(required = false) String category,
                        @RequestParam(required = false) String sort,
                        HttpSession session, Model model) {
        User user = requireAdmin(session, model);
        model.addAttribute("cartCount", cartService.size(session));
        model.addAttribute("categories", Category.values());
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("sort", sort);
        model.addAttribute("products", productService.adminList(q, category, sort));
        return "admin-stock";
    }

    @GetMapping("/products/new")
    public String newProduct(HttpSession session, Model model) {
        requireAdmin(session, model);
        model.addAttribute("user", session.getAttribute(AuthInterceptor.SESSION_USER));
        model.addAttribute("cartCount", cartService.size(session));
        model.addAttribute("productRequest", new ProductRequest());
        model.addAttribute("editing", false);
        return "admin-product-form";
    }

    @PostMapping("/products")
    public String create(@Valid @ModelAttribute("productRequest") ProductRequest request,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model) {
        User user = requireAdmin(session, model);
        if (bindingResult.hasErrors()) {
            model.addAttribute("editing", false);
            return "admin-product-form";
        }
        productService.create(request, user);
        return "redirect:/admin/stock?created";
    }

    @GetMapping("/products/{id}/edit")
    public String editPage(@PathVariable Long id, HttpSession session, Model model) {
        User user = requireAdmin(session, model);
        model.addAttribute("cartCount", cartService.size(session));
        model.addAttribute("product", productService.getById(id));
        model.addAttribute("editing", true);
        model.addAttribute("productRequest", toRequest(productService.getById(id)));
        return "admin-product-form";
    }

    @PostMapping("/products/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("productRequest") ProductRequest request,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model) {
        User user = requireAdmin(session, model);
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("cartCount", cartService.size(session));
            model.addAttribute("product", productService.getById(id));
            model.addAttribute("editing", true);
            return "admin-product-form";
        }
        productService.update(id, request, user);
        return "redirect:/admin/stock?updated";
    }

    @PostMapping("/products/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute(AuthInterceptor.SESSION_USER);
        try {
            productService.delete(id, user);
            return "redirect:/admin/stock?deleted";
        } catch (AuthException ex) {
            return "redirect:/admin/stock?error";
        }
    }

    @PostMapping("/stock/{id}/adjust")
    public String adjust(@PathVariable Long id,
                         @RequestParam(defaultValue = "add") String operation,
                         @RequestParam(defaultValue = "0") int amount,
                         HttpSession session) {
        User user = (User) session.getAttribute(AuthInterceptor.SESSION_USER);
        try {
            int delta = "reduce".equals(operation) ? -1 * Math.abs(amount) : Math.abs(amount);
            productService.adjustStock(id, delta, user);
            return "redirect:/admin/stock?stockupdated";
        } catch (AuthException ex) {
            return "redirect:/admin/stock?error";
        }
    }

    private User requireAdmin(HttpSession session, Model model) {
        User user = (User) session.getAttribute(AuthInterceptor.SESSION_USER);
        model.addAttribute("user", user);
        if (user == null || !user.isAdmin()) {
            throw new AuthException("Admin access required");
        }
        return user;
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