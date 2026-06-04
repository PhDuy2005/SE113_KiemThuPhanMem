package com.uit.nhom7.KiemThuPhanMem.service.user_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqLoginDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.service.UserService.AuthResult;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00001_handleLogin extends UserServiceTestBase {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("FUNC00001_UTCID01 - Login fails because email does not exist")
    void TC1_LoginFailsBecauseEmailDoesNotExist() {
        Fixture fixture = new Fixture();
        ReqLoginDTO loginDTO = ReqLoginDTO.builder()
                .email("user@example.com")
                .password("wrong_password")
                .build();

        when(fixture.userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.userService.handleLogin(loginDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email or password incorrect")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00001_UTCID02 - Login fails because account is temporarily locked")
    void TC2_LoginFailsBecauseAccountIsTemporarilyLocked() {
        Fixture fixture = new Fixture();
        ReqLoginDTO loginDTO = ReqLoginDTO.builder()
                .email("user@example.com")
                .password("correct_password")
                .build();

        User mockUser = User.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .email("user@example.com")
                .password("encoded_password")
                .accountStatus("ACTIVE")
                .failedLoginAttempts(5)
                .lockedUntil(Instant.parse("2099-12-31T23:59:59Z"))
                .build();

        when(fixture.userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(mockUser));

        assertThatThrownBy(() -> fixture.userService.handleLogin(loginDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Account is temporarily locked")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00001_UTCID03 - Login fails because of incorrect password (failed attempts < 5)")
    void TC3_LoginFailsBecauseOfIncorrectPassword() {
        Fixture fixture = new Fixture();
        ReqLoginDTO loginDTO = ReqLoginDTO.builder()
                .email("user@example.com")
                .password("wrong_password")
                .build();

        Role role = new Role();
        role.setId(1L);
        role.setName("CUSTOMER");

        User mockUser = User.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .email("user@example.com")
                .password("encoded_password")
                .accountStatus("ACTIVE")
                .failedLoginAttempts(0)
                .lockedUntil(null)
                .role(role)
                .build();

        when(fixture.userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(mockUser));
        when(fixture.passwordEncoder.matches("wrong_password", "encoded_password")).thenReturn(false);

        assertThatThrownBy(() -> fixture.userService.handleLogin(loginDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email or password incorrect")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00001_UTCID04 - Login fails because incorrect password reaches 5th attempt")
    void TC4_LoginFailsBecauseIncorrectPasswordReaches5thAttempt() {
        Fixture fixture = new Fixture();
        ReqLoginDTO loginDTO = ReqLoginDTO.builder()
                .email("user@example.com")
                .password("wrong_password")
                .build();

        User mockUser = User.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .email("user@example.com")
                .password("encoded_password")
                .accountStatus("ACTIVE")
                .failedLoginAttempts(4)
                .lockedUntil(null)
                .build();

        when(fixture.userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(mockUser));
        when(fixture.passwordEncoder.matches("wrong_password", "encoded_password")).thenReturn(false);

        assertThatThrownBy(() -> fixture.userService.handleLogin(loginDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Account locked due to too many failed attempts")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00001_UTCID05 - Login fails because account status is not ACTIVE")
    void TC5_LoginFailsBecauseAccountStatusIsNotActive() {
        Fixture fixture = new Fixture();
        ReqLoginDTO loginDTO = ReqLoginDTO.builder()
                .email("user@example.com")
                .password("correct_password")
                .build();

        User mockUser = User.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .email("user@example.com")
                .password("encoded_password")
                .accountStatus("PENDING")
                .failedLoginAttempts(0)
                .lockedUntil(null)
                .build();

        when(fixture.userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(mockUser));
        when(fixture.passwordEncoder.matches("correct_password", "encoded_password")).thenReturn(true);

        assertThatThrownBy(() -> fixture.userService.handleLogin(loginDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00001_UTCID06 - Login successfully, user has role")
    void TC6_LoginSuccessfullyUserHasRole() {
        Fixture fixture = new Fixture();
        ReqLoginDTO loginDTO = ReqLoginDTO.builder()
                .email("user@example.com")
                .password("correct_password")
                .build();

        Role role = new Role();
        role.setId(1L);
        role.setName("CUSTOMER");

        User mockUser = User.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .email("user@example.com")
                .password("encoded_password")
                .accountStatus("ACTIVE")
                .failedLoginAttempts(0)
                .lockedUntil(null)
                .role(role)
                .build();

        when(fixture.userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(mockUser));
        when(fixture.passwordEncoder.matches("correct_password", "encoded_password")).thenReturn(true);
        when(fixture.securityUtil.createAccessToken(eq(loginDTO.getEmail()), any())).thenReturn("mock_access_token");
        when(fixture.securityUtil.createRefreshToken(eq(loginDTO.getEmail()), any())).thenReturn("mock_refresh_token");

        AuthResult result = fixture.userService.handleLogin(loginDTO);

        assertThat(result).isNotNull();
        assertThat(result.getRefreshToken()).isEqualTo("mock_refresh_token");
        
        assertThat(result.getResLoginDTO()).isNotNull();
        assertThat(result.getResLoginDTO().getAccessToken()).isEqualTo("mock_access_token");
        
        assertThat(result.getResLoginDTO().getUser()).isNotNull();
        assertThat(result.getResLoginDTO().getUser().getId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        assertThat(result.getResLoginDTO().getUser().getEmail()).isEqualTo("user@example.com");
        assertThat(result.getResLoginDTO().getUser().getName()).isNull();
        assertThat(result.getResLoginDTO().getUser().getPhoneNumber()).isNull();
        assertThat(result.getResLoginDTO().getUser().getAvatarUrl()).isNull();
        assertThat(result.getResLoginDTO().getUser().getDateOfBirth()).isNull();

        assertThat(result.getResLoginDTO().getRole()).isNotNull();
        assertThat(result.getResLoginDTO().getRole().getRoleId()).isEqualTo(1L);
        assertThat(result.getResLoginDTO().getRole().getRoleName()).isEqualTo("CUSTOMER");
    }

    @Test
    @DisplayName("FUNC00001_UTCID07 - Login successfully, user has no role")
    void TC7_LoginSuccessfullyUserHasNoRole() {
        Fixture fixture = new Fixture();
        ReqLoginDTO loginDTO = ReqLoginDTO.builder()
                .email("user@example.com")
                .password("correct_password")
                .build();

        User mockUser = User.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .email("user@example.com")
                .password("encoded_password")
                .accountStatus("ACTIVE")
                .failedLoginAttempts(0)
                .lockedUntil(null)
                .role(null)
                .build();

        when(fixture.userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(mockUser));
        when(fixture.passwordEncoder.matches("correct_password", "encoded_password")).thenReturn(true);
        when(fixture.securityUtil.createAccessToken(eq(loginDTO.getEmail()), any())).thenReturn("mock_access_token");
        when(fixture.securityUtil.createRefreshToken(eq(loginDTO.getEmail()), any())).thenReturn("mock_refresh_token");

        AuthResult result = fixture.userService.handleLogin(loginDTO);

        assertThat(result).isNotNull();
        assertThat(result.getRefreshToken()).isEqualTo("mock_refresh_token");
        
        assertThat(result.getResLoginDTO()).isNotNull();
        assertThat(result.getResLoginDTO().getAccessToken()).isEqualTo("mock_access_token");
        
        assertThat(result.getResLoginDTO().getUser()).isNotNull();
        assertThat(result.getResLoginDTO().getUser().getId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        assertThat(result.getResLoginDTO().getUser().getEmail()).isEqualTo("user@example.com");
        
        assertThat(result.getResLoginDTO().getRole()).isNull();
    }
}
