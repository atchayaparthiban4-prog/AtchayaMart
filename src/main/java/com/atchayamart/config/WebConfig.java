package com.atchayamart.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(
                        "/dashboard", "/dashboard/**",
                        "/seller", "/seller/**",
                        "/products", "/products/**",
                        "/cart", "/cart/**",
                        "/checkout", "/checkout/**",
                        "/orders", "/orders/**",
                        "/profile", "/profile/**",
                        "/admin", "/admin/**")
                .excludePathPatterns("/css/**", "/js/**", "/images/**");
    }
}