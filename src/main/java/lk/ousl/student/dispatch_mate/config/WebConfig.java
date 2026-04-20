package lk.ousl.student.dispatch_mate.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration tells Spring to treat this class as a configuration source.
// WebMvcConfigurer lets us plug our interceptor into the MVC pipeline.

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final SessionInterceptor sessionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
                // Apply the interceptor to ALL paths …
                .addPathPatterns("/**")
                // … EXCEPT login, logout, and static files (CSS/JS/images)
                .excludePathPatterns("/", "/login", "/logout", "/css/**", "/js/**", "/error");
    }
}
