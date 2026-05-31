package com.uit.nhom7.KiemThuPhanMem.service.staff_management_service;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.uit.nhom7.KiemThuPhanMem.repository.RoleRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.service.EmailService;
import com.uit.nhom7.KiemThuPhanMem.service.StaffManagementService;

public class StaffManagementServiceTestBase {

    protected static class Fixture {
        public EmailService emailService;
        public PasswordEncoder passwordEncoder;
        public RoleRepository roleRepository;
        public UserRepository userRepository;
        
        public StaffManagementService staffManagementService;

        public Fixture() {
            emailService = mock(EmailService.class);
            passwordEncoder = mock(PasswordEncoder.class);
            roleRepository = mock(RoleRepository.class);
            userRepository = mock(UserRepository.class);
            
            staffManagementService = new StaffManagementService(
                emailService,
                passwordEncoder,
                roleRepository,
                userRepository
            );
        }
    }
}
