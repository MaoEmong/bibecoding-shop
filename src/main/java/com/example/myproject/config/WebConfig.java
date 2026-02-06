package com.example.myproject.config;

import com.example.myproject.common.AuthInterceptor;
import com.example.myproject.common.RoleInterceptor;
import com.example.myproject.user.UserRepository;
import com.example.myproject.user.UserRole;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final UserRepository userRepository;

    public WebConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
            .addPathPatterns("/api/**")
            .excludePathPatterns(
                "/api/auth/**",
                "/api/products",
                "/api/products/*",
                "/h2-console/**"
            );

        registry.addInterceptor(new RoleInterceptor(userRepository, UserRole.BUYER, true))
            .addPathPatterns("/api/cart/**", "/api/orders");
        registry.addInterceptor(new RoleInterceptor(userRepository, UserRole.SELLER, true))
            .addPathPatterns("/api/seller/**");

        registry.addInterceptor(new RoleInterceptor(userRepository, UserRole.BUYER, false))
            .addPathPatterns("/cart", "/order", "/order/complete", "/orders");
        registry.addInterceptor(new RoleInterceptor(userRepository, UserRole.SELLER, false))
            .addPathPatterns("/seller/**");
    }
}
