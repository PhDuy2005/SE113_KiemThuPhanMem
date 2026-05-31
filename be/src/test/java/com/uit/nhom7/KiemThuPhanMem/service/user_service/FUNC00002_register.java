package com.uit.nhom7.KiemThuPhanMem.service.user_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqRegisterDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResAuthActionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00002_register extends UserServiceTestBase {

    @Test
    @DisplayName("FUNC00002_UTCID01 - Fails because password and confirmPassword do not match")
    void TC1_PasswordMismatch() {
        Fixture fixture = new Fixture();
        ReqRegisterDTO request = ReqRegisterDTO.builder()
                .email("new@example.com")
                .password("password123")
                .confirmPassword("wrong_password")
                .build();

        assertThatThrownBy(() -> fixture.userService.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Password confirmation does not match")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00002_UTCID02 - Continues with null email to test normalizeEmail condition")
    void TC2_NullEmail() {
        Fixture fixture = new Fixture();
        ReqRegisterDTO request = ReqRegisterDTO.builder()
                .email(null)
                .password("password123")
                .confirmPassword("password123")
                .build();

        Role role = new Role();
        role.setId(1L);
        role.setName("CUSTOMER");

        when(fixture.userRepository.existsByEmail(null)).thenReturn(false);
        when(fixture.roleRepository.findByName("CUSTOMER")).thenReturn(role);
        when(fixture.passwordEncoder.encode("password123")).thenReturn("encoded_password");

        ResAuthActionDTO result = fixture.userService.register(request);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Registration created. Please verify your email.");
        assertThat(result.getToken()).isNotNull();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(fixture.userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isNull();
        assertThat(savedUser.getPassword()).isEqualTo("encoded_password");
        assertThat(savedUser.getAccountStatus()).isEqualTo("PENDING");
        assertThat(savedUser.getRole()).isEqualTo(role);
        assertThat(savedUser.getVerificationToken()).isEqualTo(result.getToken());

        verify(fixture.emailService).sendRegistrationVerification(null, result.getToken());
    }

    @Test
    @DisplayName("FUNC00002_UTCID03 - Fails because email already exists in the system")
    void TC3_EmailExists() {
        Fixture fixture = new Fixture();
        ReqRegisterDTO request = ReqRegisterDTO.builder()
                .email("exist@example.com")
                .password("password123")
                .confirmPassword("password123")
                .build();

        when(fixture.userRepository.existsByEmail("exist@example.com")).thenReturn(true);

        assertThatThrownBy(() -> fixture.userService.register(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Account already exists")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00002_UTCID04 - Registers new user successfully")
    void TC4_Success() {
        Fixture fixture = new Fixture();
        ReqRegisterDTO request = ReqRegisterDTO.builder()
                .email("new@example.com")
                .password("password123")
                .confirmPassword("password123")
                .build();

        Role role = new Role();
        role.setId(1L);
        role.setName("CUSTOMER");

        when(fixture.userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(fixture.roleRepository.findByName("CUSTOMER")).thenReturn(role);
        when(fixture.passwordEncoder.encode("password123")).thenReturn("encoded_password");

        ResAuthActionDTO result = fixture.userService.register(request);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Registration created. Please verify your email.");
        assertThat(result.getToken()).isNotNull();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(fixture.userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("new@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("encoded_password");
        assertThat(savedUser.getAccountStatus()).isEqualTo("PENDING");
        assertThat(savedUser.getRole()).isEqualTo(role);
        assertThat(savedUser.getVerificationToken()).isEqualTo(result.getToken());

        verify(fixture.emailService).sendRegistrationVerification("new@example.com", result.getToken());
    }
}
