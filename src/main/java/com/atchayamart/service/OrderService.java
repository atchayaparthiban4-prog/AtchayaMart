package com.atchayamart.service;

import com.atchayamart.dto.CheckoutRequest;
import com.atchayamart.exception.AuthException;
import com.atchayamart.model.CartEntry;
import com.atchayamart.model.Order;
import com.atchayamart.model.OrderItem;
import com.atchayamart.model.OrderStatus;
import com.atchayamart.model.Product;
import com.atchayamart.model.User;
import com.atchayamart.repository.OrderRepository;
import com.atchayamart.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order placeOrder(User user, CheckoutRequest request, List<CartEntry> entries) {
        if (entries.isEmpty()) {
            throw new AuthException("Your cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setCustomerName(request.getName().trim());
        order.setMobile(request.getMobile().trim());
        order.setAddress(request.getAddress().trim());

        BigDecimal total = BigDecimal.ZERO;
        for (CartEntry entry : entries) {
            Product product = entry.getProduct();
            int qty = entry.getQuantity();
            if (qty <= 0 || product.getStock() < qty) {
                throw new AuthException("Insufficient stock for " + product.getName());
            }
            total = total.add(entry.getLineTotal());

            OrderItem item = new OrderItem(product.getName(), product.getUnit(),
                    product.getDiscountedPrice(), qty);
            item.setTotalPrice(entry.getLineTotal());
            order.addItem(item);

            product.setStock(product.getStock() - qty);
            productRepository.save(product);
        }
        order.setTotal(total);
        return orderRepository.save(order);
    }

    public List<Order> getForUser(User user) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(user.getId());
    }

    public List<Order> getAll() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    public Order getById(Long id) {
        return orderRepository.findWithItemsById(id)
                .orElseThrow(() -> new AuthException("Order not found"));
    }

    public boolean canUserView(Order order, User user) {
        return user.isAdmin() || order.getUser().getId().equals(user.getId());
    }

    @Transactional
    public Order updateStatus(Long id, OrderStatus status, User admin) {
        if (!admin.isAdmin()) {
            throw new AuthException("Admin access required");
        }
        Order order = getById(id);
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new AuthException("Completed orders cannot change status");
        }
        order.setStatus(status);
        return orderRepository.save(order);
    }
}