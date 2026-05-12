package com.uit.nhom7.KiemThuPhanMem.config;

import java.nio.file.Path;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfiguration implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String storageLocation = Path.of("storage").toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/storage/**")
                .addResourceLocations(storageLocation);
    }
}
