package com.atchayamart.config;

import com.atchayamart.model.Category;
import com.atchayamart.model.Product;
import com.atchayamart.model.Role;
import com.atchayamart.model.User;
import com.atchayamart.repository.ProductRepository;
import com.atchayamart.repository.UserRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(UserRepository userRepository, ProductRepository productRepository) {
        return args -> {
            User admin = userRepository.findByEmail("admin@atchayamart.com").orElse(null);
            if (admin == null) {
                admin = new User(
                        "admin@atchayamart.com",
                        "Administrator",
                        new BCryptPasswordEncoder().encode("admin123"),
                        Role.ADMIN);
                admin.setMobile("9876543210");
                admin.setAddress("AtchayaMart Head Office, Chennai");
                userRepository.save(admin);
                System.out.println("[AtchayaMart] Admin created: admin@atchayamart.com / admin123");
            }

            User seller = userRepository.findByEmail("seller@atchayamart.com").orElse(null);
            if (seller == null) {
                seller = new User(
                        "seller@atchayamart.com",
                        "Vendor Ramesh",
                        new BCryptPasswordEncoder().encode("seller123"),
                        Role.SELLER);
                seller.setMobile("9876543201");
                seller.setAddress("Wholesale Market, Chennai");
                userRepository.save(seller);
                System.out.println("[AtchayaMart] Seller created: seller@atchayamart.com / seller123");
            }

            if (!productRepository.existsByName("Tomato")) {
                if (productRepository.count() > 0) {
                    productRepository.deleteAll();
                    System.out.println("[AtchayaMart] Removed old catalog, reseeding grocery products.");
                }
                List<Product> catalog = catalog(admin);
                productRepository.saveAll(catalog);
                System.out.println("[AtchayaMart] Seeded " + catalog.size() + " grocery products across "
                        + Category.values().length + " categories.");
            }

            if (!productRepository.existsByName("Tender Coconut")) {
                List<Product> sellerCatalog = new ArrayList<>();
                add(sellerCatalog, seller, "Tender Coconut", "Cooling tender coconuts, perfect for a summer drink.", 40, Category.FRUITS, "piece", 25, 6, 0);
                add(sellerCatalog, seller, "Farm Eggs", "Free-range country eggs from our farm.", 75, Category.DAIRY_EGGS, "dozen", 30, 8, 5);
                productRepository.saveAll(sellerCatalog);
                System.out.println("[AtchayaMart] Seeded " + sellerCatalog.size() + " products for seller account.");
            }
        };
    }

    private List<Product> catalog(User seller) {
        List<Product> products = new ArrayList<>();
        add(products, seller, "Apple", "Crisp, juicy apples - perfect for snacks and salads.", 120, Category.FRUITS, "kg", 40, 10, 0);
        add(products, seller, "Banana", "Fresh ripe bananas, rich in potassium.", 40, Category.FRUITS, "dozen", 60, 12, 0);
        add(products, seller, "Orange", "Sweet and tangy oranges from trusted farms.", 60, Category.FRUITS, "kg", 45, 10, 0);
        add(products, seller, "Mango", "Juicy alphonso and kesar mangoes in season.", 90, Category.FRUITS, "kg", 30, 8, 0);
        add(products, seller, "Grapes", "Seedless green grapes, freshly plucked.", 80, Category.FRUITS, "kg", 35, 8, 0);
        add(products, seller, "Watermelon", "Hydrating, whole watermelon - perfect for summer.", 30, Category.FRUITS, "kg", 25, 6, 0);
        add(products, seller, "Pomegranate", "Juicy red pomegranates, rich in antioxidants.", 160, Category.FRUITS, "kg", 20, 5, 0);
        add(products, seller, "Papaya", "Naturally ripened papaya, rich in fibre.", 40, Category.FRUITS, "kg", 30, 8, 0);
        add(products, seller, "Guava", "Fresh guavas with a sweet aroma.", 50, Category.FRUITS, "kg", 40, 10, 0);
        add(products, seller, "Pineapple", "Whole ripe pineapple, juicy and sweet.", 45, Category.FRUITS, "piece", 25, 6, 0);

        add(products, seller, "Tomato", "Farm fresh tomatoes, ideal for curries and salads.", 40, Category.VEGETABLES, "kg", 25, 8, 0);
        add(products, seller, "Potato", "Good-quality potatoes, all-purpose grade.", 30, Category.VEGETABLES, "kg", 80, 12, 0);
        add(products, seller, "Onion", "Fresh onions with a strong flavour.", 35, Category.VEGETABLES, "kg", 90, 12, 0);
        add(products, seller, "Carrot", "Crunchy orange carrots, rich in vitamin A.", 50, Category.VEGETABLES, "kg", 40, 8, 0);
        add(products, seller, "Beans", "Fresh green beans, tender and crisp.", 60, Category.VEGETABLES, "kg", 30, 8, 0);
        add(products, seller, "Cabbage", "Firm fresh cabbage heads.", 25, Category.VEGETABLES, "kg", 35, 8, 0);
        add(products, seller, "Cauliflower", "White, firm cauliflower florets.", 40, Category.VEGETABLES, "kg", 28, 8, 0);
        add(products, seller, "Brinjal", "Fresh purple brinjals for bhaingan dishes.", 30, Category.VEGETABLES, "kg", 32, 8, 0);
        add(products, seller, "Beetroot", "Deep red beetroots, earthy and sweet.", 40, Category.VEGETABLES, "kg", 30, 6, 0);
        add(products, seller, "Spinach", "Fresh leafy spinach, washed and trimmed.", 20, Category.VEGETABLES, "bunch", 30, 6, 0);
        add(products, seller, "Green Peas", "Tender green peas for pulao and curries.", 80, Category.VEGETABLES, "kg", 22, 6, 0);
        add(products, seller, "Lady's Finger", "Fresh okra, no cuts, snap-worthy.", 35, Category.VEGETABLES, "kg", 30, 6, 0);

        add(products, seller, "Basmati Rice", "Aged basmati rice - long grain, aromatic.", 120, Category.RICE_GRAINS, "kg", 35, 8, 10);
        add(products, seller, "Ponni Rice", "Soft-cooking ponni rice, everyday staple.", 65, Category.RICE_GRAINS, "kg", 50, 10, 10);
        add(products, seller, "Sona Masuri Rice", "Fluffy Sona Masuri for daily meals.", 68, Category.RICE_GRAINS, "kg", 55, 10, 10);
        add(products, seller, "Idli Rice", "Parboiled idli rice - soft idlis every time.", 55, Category.RICE_GRAINS, "kg", 45, 10, 8);
        add(products, seller, "Brown Rice", "Healthy unpolished brown rice.", 90, Category.RICE_GRAINS, "kg", 30, 8, 10);
        add(products, seller, "Wheat", "Whole wheat grain, cleaned and graded.", 45, Category.RICE_GRAINS, "kg", 40, 8, 8);
        add(products, seller, "Rava", "Coarse semolina for upma and sweets.", 40, Category.RICE_GRAINS, "kg", 38, 8, 0);
        add(products, seller, "Maida", "Refined wheat flour for bakery needs.", 35, Category.RICE_GRAINS, "kg", 40, 8, 0);
        add(products, seller, "Atta", "Chakki fresh whole wheat atta.", 45, Category.RICE_GRAINS, "kg", 55, 10, 10);

        add(products, seller, "Toor Dal", "Premium yellow pigeon-pea dal.", 130, Category.DALS_PULSES, "kg", 30, 6, 0);
        add(products, seller, "Moong Dal", "Skinned green gram dal, easy to cook.", 120, Category.DALS_PULSES, "kg", 32, 6, 5);
        add(products, seller, "Urad Dal", "Whole black gram with skin.", 140, Category.DALS_PULSES, "kg", 28, 6, 0);
        add(products, seller, "Chana Dal", "Bengal gram dal for sambar and sweets.", 90, Category.DALS_PULSES, "kg", 40, 8, 5);
        add(products, seller, "Green Gram", "Whole moong beans, sprout-friendly.", 110, Category.DALS_PULSES, "kg", 30, 6, 0);
        add(products, seller, "Black Gram", "Clean whole black gram for vada and papad.", 150, Category.DALS_PULSES, "kg", 25, 6, 0);
        add(products, seller, "Chickpeas", "Kabuli chana, plump and uniform.", 95, Category.DALS_PULSES, "kg", 40, 8, 0);
        add(products, seller, "Rajma", "Red kidney beans for rajma curry.", 130, Category.DALS_PULSES, "kg", 28, 6, 0);

        add(products, seller, "Sunflower Oil", "Pure refined sunflower cooking oil.", 140, Category.COOKING_ESSENTIALS, "L", 25, 6, 15);
        add(products, seller, "Groundnut Oil", "Traditional cold-pressed groundnut oil.", 160, Category.COOKING_ESSENTIALS, "L", 22, 6, 12);
        add(products, seller, "Coconut Oil", "Pure coconut cooking oil.", 180, Category.COOKING_ESSENTIALS, "L", 20, 6, 10);
        add(products, seller, "Mustard Oil", "Kachi ghani mustard oil.", 110, Category.COOKING_ESSENTIALS, "L", 24, 6, 10);
        add(products, seller, "Salt", "Iodised table salt, free flowing.", 20, Category.COOKING_ESSENTIALS, "kg", 80, 12, 0);
        add(products, seller, "Sugar", "Fine crystalline white sugar.", 45, Category.COOKING_ESSENTIALS, "kg", 70, 12, 0);
        add(products, seller, "Jaggery", "Natural unrefined jaggery blocks.", 70, Category.COOKING_ESSENTIALS, "kg", 35, 8, 0);
        add(products, seller, "Turmeric Powder", "Pure turmeric powder, vivid colour.", 60, Category.COOKING_ESSENTIALS, "100g", 40, 8, 0);
        add(products, seller, "Chilli Powder", "Spicy, bright red chilli powder.", 55, Category.COOKING_ESSENTIALS, "100g", 45, 8, 0);
        add(products, seller, "Coriander Powder", "Freshly ground coriander powder.", 50, Category.COOKING_ESSENTIALS, "100g", 42, 8, 0);
        add(products, seller, "Pepper", "Whole black pepper, bold and pungent.", 250, Category.COOKING_ESSENTIALS, "100g", 25, 5, 0);
        add(products, seller, "Cumin", "Whole cumin seeds for tempering.", 120, Category.COOKING_ESSENTIALS, "100g", 38, 8, 0);

        add(products, seller, "Garam Masala", "Blended aromatic garam masala powder.", 90, Category.SPICES_MASALA, "100g", 30, 6, 0);
        add(products, seller, "Sambar Powder", "Authentic standard sambar masala.", 70, Category.SPICES_MASALA, "100g", 40, 8, 0);
        add(products, seller, "Rasam Powder", "Aromatic rasam spice blend.", 65, Category.SPICES_MASALA, "100g", 38, 8, 0);
        add(products, seller, "Chicken Masala", "Rich chicken curry masala blend.", 80, Category.SPICES_MASALA, "100g", 35, 6, 0);
        add(products, seller, "Curry Powder", "Versatile everyday curry powder.", 60, Category.SPICES_MASALA, "100g", 42, 8, 0);
        add(products, seller, "Biriyani Masala", "Restaurant-style biriyani masala.", 75, Category.SPICES_MASALA, "100g", 32, 6, 0);
        add(products, seller, "Pepper Powder", "Fine ground black pepper powder.", 260, Category.SPICES_MASALA, "100g", 25, 5, 0);
        add(products, seller, "Ginger Garlic Paste", "Fresh ginger garlic paste, ready to cook.", 60, Category.SPICES_MASALA, "250g", 30, 6, 0);

        add(products, seller, "Milk", "Farm-fresh toned milk.", 28, Category.DAIRY_EGGS, "L", 40, 8, 0);
        add(products, seller, "Curd", "Thick, creamy set curd.", 35, Category.DAIRY_EGGS, "500g", 35, 8, 0);
        add(products, seller, "Butter", "Salted butter for cooking and spreading.", 55, Category.DAIRY_EGGS, "100g", 30, 6, 0);
        add(products, seller, "Cheese", "Creamy processed cheese slices.", 120, Category.DAIRY_EGGS, "200g", 25, 5, 10);
        add(products, seller, "Paneer", "Soft, fresh paneer block.", 90, Category.DAIRY_EGGS, "200g", 28, 5, 10);
        add(products, seller, "Eggs", "Farm-fresh white eggs.", 60, Category.DAIRY_EGGS, "dozen", 45, 10, 0);

        add(products, seller, "Tea Powder", "Premium CTC tea powder, strong brew.", 120, Category.BEVERAGES, "250g", 40, 8, 10);
        add(products, seller, "Coffee", "Fresh roasted filter coffee powder.", 150, Category.BEVERAGES, "100g", 35, 6, 10);
        add(products, seller, "Health Drink", "Malt-based health drink with vitamins.", 420, Category.BEVERAGES, "500g", 20, 5, 8);
        add(products, seller, "Fruit Juice", "100% natural mixed fruit juice.", 60, Category.BEVERAGES, "L", 45, 8, 0);
        add(products, seller, "Soft Drinks", "Chilled carbonated soft drink.", 40, Category.BEVERAGES, "750ml", 60, 10, 0);
        add(products, seller, "Mineral Water", "Purified drinking mineral water.", 20, Category.BEVERAGES, "L", 80, 12, 0);

        add(products, seller, "Biscuits", "Crunchy tea-time biscuits.", 30, Category.SNACKS, "pack", 70, 10, 20);
        add(products, seller, "Chips", "Salted potato chips packet.", 20, Category.SNACKS, "pack", 80, 10, 15);
        add(products, seller, "Cookies", "Chocolate chipped cookies.", 45, Category.SNACKS, "pack", 50, 8, 10);
        add(products, seller, "Mixture", "Spicy south-Indian snack mixture.", 60, Category.SNACKS, "pack", 35, 6, 0);
        add(products, seller, "Namkeen", "Assorted savoury namkeen.", 70, Category.SNACKS, "pack", 40, 6, 20);
        add(products, seller, "Chocolates", "Milk chocolate bar.", 50, Category.SNACKS, "pack", 55, 8, 0);
        add(products, seller, "Cakes", "Bakery-fresh cream cake box.", 120, Category.SNACKS, "pack", 20, 5, 0);

        add(products, seller, "Soap", "Skin-soft bathing soap.", 35, Category.PERSONAL_CARE, "piece", 90, 12, 0);
        add(products, seller, "Shampoo", "Nourishing hair shampoo.", 150, Category.PERSONAL_CARE, "200ml", 45, 8, 0);
        add(products, seller, "Toothpaste", "Cavity-protection toothpaste.", 80, Category.PERSONAL_CARE, "100g", 55, 10, 0);
        add(products, seller, "Toothbrush", "Soft-bristle medium toothbrush.", 40, Category.PERSONAL_CARE, "piece", 60, 10, 0);
        add(products, seller, "Face Wash", "Gentle daily face wash.", 120, Category.PERSONAL_CARE, "100g", 40, 8, 0);
        add(products, seller, "Hand Wash", "Anti-bacterial liquid hand wash.", 90, Category.PERSONAL_CARE, "200ml", 50, 8, 0);

        add(products, seller, "Detergent", "Everyday laundry detergent powder.", 90, Category.HOUSEHOLD, "kg", 40, 8, 0);
        add(products, seller, "Dishwash", "Grease-cutting dishwash liquid.", 45, Category.HOUSEHOLD, "500ml", 35, 6, 0);
        add(products, seller, "Floor Cleaner", "Fragrant floor cleaning liquid.", 60, Category.HOUSEHOLD, "L", 30, 6, 0);
        add(products, seller, "Toilet Cleaner", "Disinfectant toilet cleaner.", 70, Category.HOUSEHOLD, "500ml", 28, 6, 0);
        add(products, seller, "Washing Powder", "Front-load washing powder.", 120, Category.HOUSEHOLD, "kg", 38, 8, 0);
        add(products, seller, "Garbage Bags", "Strong 24-inch garbage bag pack.", 50, Category.HOUSEHOLD, "pack", 45, 8, 0);
        add(products, seller, "Tissue Paper", "Soft 2-ply facial tissue box.", 35, Category.HOUSEHOLD, "pack", 50, 8, 0);

        return products;
    }

    private void add(List<Product> products, User seller, String name, String description,
                     double price, Category category, String unit, int stock, int minStock, int discount) {
        products.add(new Product(name, description, BigDecimal.valueOf(price), category,
                unit, stock, minStock, discount, null, seller));
    }
}