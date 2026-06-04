package com.uit.nhom7.KiemThuPhanMem.service.user_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqForgotPasswordDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResAuthActionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00004_forgotPassword extends UserServiceTestBase {

    @Test
    @DisplayName("FUNC00004_UTCID01 - Fails because email is null (testing normalizeEmail condition)")
    void TC1_NullEmail() {
        Fixture fixture = new Fixture();
        ReqForgotPasswordDTO request = ReqForgotPasswordDTO.builder()
                .email(null)
                .build();

        when(fixture.userRepository.findByEmail(null)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.userService.forgotPassword(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email does not exist")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FUNC00004_UTCID02 - Fails because email does not exist in the database")
    void TC2_EmailNotFound() {
        Fixture fixture = new Fixture();
        ReqForgotPasswordDTO request = ReqForgotPasswordDTO.builder()
                .email("notfound@example.com")
                .build();

        when(fixture.userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.userService.forgotPassword(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email does not exist")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FUNC00004_UTCID03 - Generates reset token and sends email successfully")
    void TC3_Success() {
        Fixture fixture = new Fixture();
        ReqForgotPasswordDTO request = ReqForgotPasswordDTO.builder()
                .email("user@example.com")
                .build();

        User mockUser = User.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .email("user@example.com")
                .build();

        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));

        ResAuthActionDTO result = fixture.userService.forgotPassword(request);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Password reset email sent");
        assertThat(result.getToken()).isNotNull();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(fixture.userRepository).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getResetPasswordToken()).isEqualTo(result.getToken());
        assertThat(savedUser.getResetPasswordTokenExpiresAt()).isNotNull();

        verify(fixture.emailService).sendPasswordReset("user@example.com", result.getToken());
    }
}
