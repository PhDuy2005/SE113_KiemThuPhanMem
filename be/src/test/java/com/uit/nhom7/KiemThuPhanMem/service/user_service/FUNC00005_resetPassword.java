package com.uit.nhom7.KiemThuPhanMem.service.user_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqResetPasswordDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResAuthActionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00005_resetPassword extends UserServiceTestBase {

    @Test
    @DisplayName("FUNC00005_UTCID01 - Fails because newPassword and confirmPassword do not match")
    void TC1_PasswordMismatch() {
        Fixture fixture = new Fixture();
        ReqResetPasswordDTO request = ReqResetPasswordDTO.builder()
                .token("any_token")
                .newPassword("new_password")
                .confirmPassword("wrong_password")
                .build();

        assertThatThrownBy(() -> fixture.userService.resetPassword(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Password confirmation does not match")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00005_UTCID02 - Fails because token is not found in database")
    void TC2_TokenNotFound() {
        Fixture fixture = new Fixture();
        ReqResetPasswordDTO request = ReqResetPasswordDTO.builder()
                .token("invalid_token")
                .newPassword("new_password")
                .confirmPassword("new_password")
                .build();

        when(fixture.userRepository.findByResetPasswordToken("invalid_token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.userService.resetPassword(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired reset link")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00005_UTCID03 - Fails because token expiration date is null")
    void TC3_TokenNullExpiry() {
        Fixture fixture = new Fixture();
        ReqResetPasswordDTO request = ReqResetPasswordDTO.builder()
                .token("valid_token")
                .newPassword("new_password")
                .confirmPassword("new_password")
                .build();

        User mockUser = User.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .resetPasswordTokenExpiresAt(null)
                .build();

        when(fixture.userRepository.findByResetPasswordToken("valid_token")).thenReturn(Optional.of(mockUser));

        assertThatThrownBy(() -> fixture.userService.resetPassword(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired reset link")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00005_UTCID04 - Fails because token expiration date is in the past")
    void TC4_TokenExpired() {
        Fixture fixture = new Fixture();
        ReqResetPasswordDTO request = ReqResetPasswordDTO.builder()
                .token("valid_token")
                .newPassword("new_password")
                .confirmPassword("new_password")
                .build();

        User mockUser = User.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .resetPasswordTokenExpiresAt(Instant.parse("2020-01-01T00:00:00Z"))
                .build();

        when(fixture.userRepository.findByResetPasswordToken("valid_token")).thenReturn(Optional.of(mockUser));

        assertThatThrownBy(() -> fixture.userService.resetPassword(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired reset link")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00005_UTCID05 - Resets password successfully")
    void TC5_Success() {
        Fixture fixture = new Fixture();
        ReqResetPasswordDTO request = ReqResetPasswordDTO.builder()
                .token("valid_token")
                .newPassword("new_password")
                .confirmPassword("new_password")
                .build();

        User mockUser = User.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .resetPasswordTokenExpiresAt(Instant.parse("2099-12-31T23:59:59Z"))
                .resetPasswordToken("valid_token")
                .failedLoginAttempts(5)
                .lockedUntil(Instant.parse("2099-12-31T23:59:59Z"))
                .build();

        when(fixture.userRepository.findByResetPasswordToken("valid_token")).thenReturn(Optional.of(mockUser));
        when(fixture.passwordEncoder.encode("new_password")).thenReturn("encoded_new_password");

        ResAuthActionDTO result = fixture.userService.resetPassword(request);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Password reset successfully");
        assertThat(result.getToken()).isNull();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(fixture.userRepository).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getPassword()).isEqualTo("encoded_new_password");
        assertThat(savedUser.getResetPasswordToken()).isNull();
        assertThat(savedUser.getResetPasswordTokenExpiresAt()).isNull();
        assertThat(savedUser.getFailedLoginAttempts()).isEqualTo(0);
        assertThat(savedUser.getLockedUntil()).isNull();
    }
}
