package com.uit.nhom7.KiemThuPhanMem.service.user_service;

import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.uit.nhom7.KiemThuPhanMem.repository.RoleRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.service.EmailService;
import com.uit.nhom7.KiemThuPhanMem.service.UserService;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;

public abstract class UserServiceTestBase {
    protected static class Fixture {
        public final UserRepository userRepository = Mockito.mock(UserRepository.class);
        public final RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
        public final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        public final EmailService emailService = Mockito.mock(EmailService.class);
        public final SecurityUtil securityUtil = Mockito.mock(SecurityUtil.class);

        public final UserService userService = new UserService(
                userRepository,
                roleRepository,
                passwordEncoder,
                emailService,
                securityUtil
        );
    }
}
