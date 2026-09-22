package com.atchayamart.controller;

import com.atchayamart.config.AuthInterceptor;
import com.atchayamart.exception.AuthException;
import com.atchayamart.model.Order;
import com.atchayamart.model.OrderStatus;
import com.atchayamart.model.User;
import com.atchayamart.service.CartService;
import com.atchayamart.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    public OrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }

    @GetMapping("/orders")
    public String orders(HttpSession session, Model model) {
        User user = (User) session.getAttribute(AuthInterceptor.SESSION_USER);
        model.addAttribute("user", user);
        model.addAttribute("cartCount", cartService.size(session));
        model.addAttribute("orders", orderService.getForUser(user));
        return "orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute(AuthInterceptor.SESSION_USER);
        Order order = orderService.getById(id);
        if (!orderService.canUserView(order, user)) {
            throw new AuthException("Order not found");
        }
        model.addAttribute("user", user);
        model.addAttribute("cartCount", cartService.size(session));
        model.addAttribute("order", order);
        return "order-detail";
    }

    @GetMapping("/admin/orders")
    public String adminOrders(HttpSession session, Model model) {
        User user = requireAdmin(session, model);
        model.addAttribute("cartCount", cartService.size(session));
        model.addAttribute("orders", orderService.getAll());
        model.addAttribute("statuses", OrderStatus.values());
        return "admin-orders";
    }

    @PostMapping("/admin/orders/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam OrderStatus status,
                               HttpSession session) {
        User user = (User) session.getAttribute(AuthInterceptor.SESSION_USER);
        try {
            orderService.updateStatus(id, status, user);
            return "redirect:/admin/orders?updated";
        } catch (AuthException ex) {
            return "redirect:/admin/orders?error";
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
}