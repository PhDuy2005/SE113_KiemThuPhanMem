package com.uit.nhom7.KiemThuPhanMem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.PaymentMethod;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.domain.table.ShippingFeeConfig;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResProvinceDTO;
import com.uit.nhom7.KiemThuPhanMem.repository.PaymentMethodRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.RoleRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ShippingFeeConfigRepository;
import com.uit.nhom7.KiemThuPhanMem.service.AddressDataService;
import com.uit.nhom7.KiemThuPhanMem.util.UuidV7Generator;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final String CUSTOMER_ROLE = "CUSTOMER";
    private static final String STAFF_ROLE = "STAFF";
    private static final String BUSINESS_ADMIN_ROLE = "BUSINESS_ADMIN";
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ShippingFeeConfigRepository shippingFeeConfigRepository;
    private final AddressDataService addressDataService;
    private final PasswordEncoder passwordEncoder;

    @Value("${techsales.admin.email:business.admin@techsales.com}")
    private String adminEmail;

    @Value("${techsales.admin.password:Admin@123456}")
    private String adminPassword;

    public DataInitializer(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PaymentMethodRepository paymentMethodRepository,
            ShippingFeeConfigRepository shippingFeeConfigRepository,
            AddressDataService addressDataService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.shippingFeeConfigRepository = shippingFeeConfigRepository;
        this.addressDataService = addressDataService;
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

        if (shippingFeeConfigRepository.count() == 0) {
            for (ResProvinceDTO province : addressDataService.getProvinces()) {
                int codeNum = 0;
                try {
                    codeNum = Integer.parseInt(province.getCode());
                } catch (Exception e) {
                    codeNum = Math.abs(province.getName().hashCode());
                }
                
                BigDecimal fee;
                if (codeNum % 4 == 0) {
                    fee = BigDecimal.valueOf(140.00);
                } else if (codeNum % 3 == 0) {
                    fee = BigDecimal.valueOf(130.00);
                } else if (codeNum % 2 == 0) {
                    fee = BigDecimal.valueOf(120.00);
                } else if (codeNum % 1 == 0) {
                    fee = BigDecimal.valueOf(110.00);
                } else {
                    fee = BigDecimal.valueOf(100.00);
                }

                shippingFeeConfigRepository.save(ShippingFeeConfig.builder()
                        .id(UuidV7Generator.generate())
                        .provinceCode(province.getCode())
                        .province(province.getName())
                        .provinceKey(addressDataService.normalizeKey(province.getName()))
                        .shippingFee(fee)
                        .build());
            }
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
