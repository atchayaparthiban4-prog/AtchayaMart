package com.atchayamart.controller;

import com.atchayamart.config.AuthInterceptor;
import com.atchayamart.exception.AuthException;
import com.atchayamart.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public String handleAuthException(AuthException ex, HttpSession session, Model model) {
        User user = (User) session.getAttribute(AuthInterceptor.SESSION_USER);
        String message = ex.getMessage();
        if ("Product not found".equals(message)) {
            return "redirect:/products?error=notfound";
        }
        if ("Order not found".equals(message)) {
            return "redirect:/orders?error=notfound";
        }
        if ("Admin access required".equals(message) || "Seller access required".equals(message)) {
            if (user == null) {
                return "redirect:/login";
            }
            return "redirect:/dashboard?error=forbidden";
        }
        if ("Your cart is empty".equals(message)) {
            return "redirect:/cart";
        }
        if (user == null) {
            return "redirect:/login";
        }
        return "redirect:/dashboard?error=forbidden";
    }
}