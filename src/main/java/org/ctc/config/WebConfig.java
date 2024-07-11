package org.ctc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 對所有路徑進行CORS設置
                .allowedOrigins("*") // 允許所有來源
                .allowedMethods("*") // 允許所有HTTP方法
                .allowedHeaders("*"); // 允許所有標頭
    }
}

