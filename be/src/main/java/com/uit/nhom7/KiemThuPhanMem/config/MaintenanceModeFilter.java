package com.uit.nhom7.KiemThuPhanMem.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.uit.nhom7.KiemThuPhanMem.repository.SystemConfigRepository;
import com.uit.nhom7.KiemThuPhanMem.service.MaintenanceService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class MaintenanceModeFilter extends OncePerRequestFilter {
    private final SystemConfigRepository systemConfigRepository;
    private final Set<String> adminWhitelistedIps;

    public MaintenanceModeFilter(
            SystemConfigRepository systemConfigRepository,
            @Value("${techsales.admin.whitelisted-ips:127.0.0.1,0:0:0:0:0:0:0:1,::1}") String whitelistedIps) {
        this.systemConfigRepository = systemConfigRepository;
        this.adminWhitelistedIps = Arrays.stream(whitelistedIps.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toSet());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        if (!isMaintenanceMode() || isAdminWhitelistedIp(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("""
                {"statusCode":503,"error":"Service Unavailable","message":"System is under maintenance"}
                """);
    }

    private boolean isMaintenanceMode() {
        return systemConfigRepository.findById(MaintenanceService.SYSTEM_STATUS_KEY)
                .map(config -> MaintenanceService.MAINTENANCE_STATUS.equalsIgnoreCase(config.getValue()))
                .orElse(false);
    }

    private boolean isAdminWhitelistedIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        String clientIp = forwardedFor == null || forwardedFor.isBlank()
                ? request.getRemoteAddr()
                : forwardedFor.split(",")[0].trim();
        return adminWhitelistedIps.contains(clientIp);
    }
}
