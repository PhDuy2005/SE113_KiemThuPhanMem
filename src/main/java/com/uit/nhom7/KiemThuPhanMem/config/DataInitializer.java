package com.uit.nhom7.KiemThuPhanMem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.PaymentMethod;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.PaymentMethodRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.RoleRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.UuidV7Generator;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final String CUSTOMER_ROLE = "CUSTOMER";
    private static final String STAFF_ROLE = "STAFF";
    private static final String BUSINESS_ADMIN_ROLE = "BUSINESS_ADMIN";
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${techsales.admin.email:business.admin@techsales.com}")
    private String adminEmail;

    @Value("${techsales.admin.password:Admin@123456}")
    private String adminPassword;

    public DataInitializer(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PaymentMethodRepository paymentMethodRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        ensureRole(CUSTOMER_ROLE, "Customer - khach hang mua san pham");
        ensureRole(STAFF_ROLE, "Staff - nhan vien xu ly don hang va danh gia");
        Role businessAdminRole = roleRepository.findByName(BUSINESS_ADMIN_ROLE);
        if (businessAdminRole == null) {
            businessAdminRole = roleRepository.save(Role.builder()
                    .name(BUSINESS_ADMIN_ROLE)
                    .description("Business admin - quan tri toan bo nghiep vu he thong")
                    .active(true)
                    .build());
        }

        paymentMethodRepository.findByNameIgnoreCase("Cash")
                .orElseGet(() -> paymentMethodRepository.save(PaymentMethod.builder()
                        .id(UuidV7Generator.generate())
                        .name("Cash")
                        .type(PaymentMethod.CASH_TYPE)
                        .build()));

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

    private Role ensureRole(String name, String description) {
        Role role = roleRepository.findByName(name);
        if (role != null) {
            return role;
        }
        return roleRepository.save(Role.builder()
                .name(name)
                .description(description)
                .active(true)
                .build());
    }
}
