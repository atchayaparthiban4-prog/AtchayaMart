package com.atchayamart.controller;

import com.atchayamart.config.AuthInterceptor;
import com.atchayamart.dto.ProfileRequest;
import com.atchayamart.model.User;
import com.atchayamart.service.AuthService;
import com.atchayamart.service.CartService;
import com.atchayamart.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileController {

    private final AuthService authService;
    private final OrderService orderService;
    private final CartService cartService;

    public ProfileController(AuthService authService, OrderService orderService, CartService cartService) {
        this.authService = authService;
        this.orderService = orderService;
        this.cartService = cartService;
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute(AuthInterceptor.SESSION_USER);
        model.addAttribute("user", user);
        model.addAttribute("cartCount", cartService.size(session));
        model.addAttribute("orders", orderService.getForUser(user));
        model.addAttribute("profileRequest", toRequest(user));
        return "profile";
    }

    @PostMapping("/profile")
    public String update(@Valid @ModelAttribute("profileRequest") ProfileRequest request,
                         BindingResult bindingResult,
                         HttpSession session,
                         Model model) {
        User user = (User) session.getAttribute(AuthInterceptor.SESSION_USER);
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("cartCount", cartService.size(session));
            model.addAttribute("orders", orderService.getForUser(user));
            return "profile";
        }
        User updated = authService.updateProfile(user, request);
        session.setAttribute(AuthInterceptor.SESSION_USER, updated);
        return "redirect:/profile?updated";
    }

    private ProfileRequest toRequest(User user) {
        ProfileRequest request = new ProfileRequest();
        request.setName(user.getName());
        request.setMobile(user.getMobile());
        request.setAddress(user.getAddress());
        return request;
    }
}