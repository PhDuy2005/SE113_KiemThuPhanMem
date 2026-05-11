package com.uit.nhom7.KiemThuPhanMem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.RoleRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final String BUSINESS_ADMIN_ROLE = "BUSINESS_ADMIN";
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${techsales.admin.email:business.admin@techsales.com}")
    private String adminEmail;

    @Value("${techsales.admin.password:Admin@123456}")
    private String adminPassword;

    public DataInitializer(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Role businessAdminRole = roleRepository.findByName(BUSINESS_ADMIN_ROLE);
        if (businessAdminRole == null) {
            businessAdminRole = roleRepository.save(Role.builder()
                    .name(BUSINESS_ADMIN_ROLE)
                    .description("Business admin - quan tri toan bo nghiep vu he thong")
                    .active(true)
                    .build());
        }

        String normalizedEmail = adminEmail.trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            return;
        }

        User admin = User.builder()
                .email(normalizedEmail)
                .password(passwordEncoder.encode(adminPassword))
                .userFullName("Business Admin")
                .accountStatus(ACTIVE_ACCOUNT_STATUS)
                .failedLoginAttempts(0)
                .role(businessAdminRole)
                .build();

        userRepository.save(admin);
    }
}
