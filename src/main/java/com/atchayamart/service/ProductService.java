package com.atchayamart.service;

import com.atchayamart.dto.ProductRequest;
import com.atchayamart.exception.AuthException;
import com.atchayamart.model.Category;
import com.atchayamart.model.Product;
import com.atchayamart.model.User;
import com.atchayamart.repository.ProductRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> search(String query, String categoryName) {
        Category category = null;
        if (categoryName != null && !categoryName.isBlank()) {
            category = Category.valueOf(categoryName);
        }
        return productRepository.search(category, query == null ? "" : query.trim());
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new AuthException("Product not found"));
    }

    public Product getByIdOrNull(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> getBySeller(User seller) {
        return productRepository.findBySellerIdOrderByCreatedAtDesc(seller.getId());
    }

    @Transactional
    public Product create(ProductRequest request, User seller) {
        Product product = new Product(
                request.getName().trim(),
                request.getDescription().trim(),
                request.getPrice(),
                request.getCategory(),
                request.getUnit() == null ? "1" : request.getUnit().trim(),
                request.getStock(),
                request.getMinStock() == null ? 0 : request.getMinStock(),
                request.getDiscountPercent() == null ? 0 : request.getDiscountPercent(),
                normalizeImage(request.getImageUrl()),
                seller);
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, ProductRequest request, User editor) {
        Product product = getById(id);
        if (!canManage(product, editor)) {
            throw new AuthException("You can only edit your own products");
        }
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription().trim());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setStock(request.getStock());
        product.setUnit(request.getUnit() == null ? "1" : request.getUnit().trim());
        product.setMinStock(request.getMinStock() == null ? 0 : request.getMinStock());
        product.setDiscountPercent(request.getDiscountPercent() == null ? 0 : request.getDiscountPercent());
        product.setImageUrl(normalizeImage(request.getImageUrl()));
        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id, User editor) {
        Product product = getById(id);
        if (!canManage(product, editor)) {
            throw new AuthException("You can only delete your own products");
        }
        productRepository.delete(product);
    }

    public long count() {
        return productRepository.count();
    }

    public List<Product> lowStock() {
        return productRepository.findLowStock();
    }

    public List<Product> outOfStock() {
        return productRepository.findOutOfStock();
    }

    public List<Product> offers() {
        return productRepository.findByDiscountPercentGreaterThan(0);
    }

    public List<Product> adminList(String query, String categoryName, String sort) {
        List<Product> products = search(query, categoryName);
        if (sort == null) {
            return products;
        }
        Comparator<Product> comparator = switch (sort) {
            case "nameAsc" -> Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
            case "nameDesc" -> Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER).reversed();
            case "priceAsc" -> Comparator.comparing(Product::getPrice);
            case "priceDesc" -> Comparator.comparing(Product::getPrice).reversed();
            case "stockAsc" -> Comparator.comparingInt(Product::getStock);
            case "stockDesc" -> Comparator.comparingInt(Product::getStock).reversed();
            default -> Comparator.comparing(Product::getId).reversed();
        };
        products.sort(comparator);
        return products;
    }

    @Transactional
    public Product adjustStock(Long id, int delta, User editor) {
        Product product = getById(id);
        if (!editor.isAdmin()) {
            throw new AuthException("Admin access required");
        }
        product.setStock(Math.max(0, product.getStock() + delta));
        return productRepository.save(product);
    }

    public boolean canManage(Product product, User user) {
        return user.isAdmin() || product.getSeller().getId().equals(user.getId());
    }

    private String normalizeImage(String imageUrl) {
        return imageUrl == null || imageUrl.isBlank() ? null : imageUrl.trim();
    }
}