package com.atchayamart.controller;

import com.atchayamart.config.AuthInterceptor;
import com.atchayamart.dto.CheckoutRequest;
import com.atchayamart.exception.AuthException;
import com.atchayamart.model.CartEntry;
import com.atchayamart.model.Order;
import com.atchayamart.model.User;
import com.atchayamart.service.CartService;
import com.atchayamart.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;

    public CheckoutController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping
    public String checkout(HttpSession session, Model model) {
        if (cartService.isEmpty(session)) {
            return "redirect:/cart";
        }
        User user = (User) session.getAttribute(AuthInterceptor.SESSION_USER);
        List<CartEntry> entries = cartService.getEntries(session);
        CheckoutRequest request = new CheckoutRequest();
        request.setName(user.getName());
        request.setMobile(user.getMobile());
        request.setAddress(user.getAddress());
        model.addAttribute("checkoutRequest", request);
        model.addAttribute("user", user);
        model.addAttribute("cartCount", cartService.size(session));
        model.addAttribute("entries", entries);
        model.addAttribute("subtotal", cartService.originalSubtotal(entries));
        model.addAttribute("discount", cartService.totalDiscount(entries));
        model.addAttribute("total", cartService.subtotal(entries));
        return "checkout";
    }

    @PostMapping
    public String placeOrder(@Valid @ModelAttribute("checkoutRequest") CheckoutRequest request,
                             BindingResult bindingResult,
                             HttpSession session,
                             Model model) {
        User user = (User) session.getAttribute(AuthInterceptor.SESSION_USER);
        List<CartEntry> entries = cartService.getEntries(session);
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("cartCount", cartService.size(session));
            model.addAttribute("entries", entries);
            model.addAttribute("subtotal", cartService.originalSubtotal(entries));
            model.addAttribute("discount", cartService.totalDiscount(entries));
            model.addAttribute("total", cartService.subtotal(entries));
            return "checkout";
        }
        try {
            Order order = orderService.placeOrder(user, request, entries);
            cartService.clear(session);
            return "redirect:/orders/" + order.getId() + "?placed";
        } catch (AuthException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("user", user);
            model.addAttribute("cartCount", cartService.size(session));
            model.addAttribute("entries", entries);
            model.addAttribute("subtotal", cartService.originalSubtotal(entries));
            model.addAttribute("discount", cartService.totalDiscount(entries));
            model.addAttribute("total", cartService.subtotal(entries));
            return "checkout";
        }
    }
}