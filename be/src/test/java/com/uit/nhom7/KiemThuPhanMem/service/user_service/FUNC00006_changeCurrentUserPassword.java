package com.uit.nhom7.KiemThuPhanMem.service.user_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqChangePasswordDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResAuthActionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00006_changeCurrentUserPassword extends UserServiceTestBase {

    private MockedStatic<SecurityUtil> mockedSecurityUtil;
    private Fixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
        mockedSecurityUtil = mockStatic(SecurityUtil.class);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtil.close();
    }

    @Test
    @DisplayName("FUNC00006_UTCID01 - Fails because new password and confirm password do not match")
    void TC1_PasswordMismatch() {
        ReqChangePasswordDTO request = ReqChangePasswordDTO.builder()
                .currentPassword("any_pass")
                .newPassword("new_pass")
                .confirmPassword("wrong_pass")
                .build();

        assertThatThrownBy(() -> fixture.userService.changeCurrentUserPassword(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Password confirmation does not match")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00006_UTCID02 - Fails because user is not authenticated")
    void TC2_NotLoggedIn() {
        ReqChangePasswordDTO request = ReqChangePasswordDTO.builder()
                .currentPassword("old_pass")
                .newPassword("new_pass")
                .confirmPassword("new_pass")
                .build();

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.userService.changeCurrentUserPassword(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00006_UTCID03 - Fails because authenticated email is not in DB")
    void TC3_UserNotFound() {
        ReqChangePasswordDTO request = ReqChangePasswordDTO.builder()
                .currentPassword("old_pass")
                .newPassword("new_pass")
                .confirmPassword("new_pass")
                .build();

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.userService.changeCurrentUserPassword(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00006_UTCID04 - Fails because user account status is not ACTIVE")
    void TC4_UserNotActive() {
        ReqChangePasswordDTO request = ReqChangePasswordDTO.builder()
                .currentPassword("old_pass")
                .newPassword("new_pass")
                .confirmPassword("new_pass")
                .build();

        User mockUser = User.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .accountStatus("PENDING")
                .build();

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));

        assertThatThrownBy(() -> fixture.userService.changeCurrentUserPassword(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00006_UTCID05 - Fails because current password does not match")
    void TC5_WrongCurrentPassword() {
        ReqChangePasswordDTO request = ReqChangePasswordDTO.builder()
                .currentPassword("wrong_old_pass")
                .newPassword("new_pass")
                .confirmPassword("new_pass")
                .build();

        User mockUser = User.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .accountStatus("ACTIVE")
                .password("encoded_old_password")
                .build();

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));
        when(fixture.passwordEncoder.matches("wrong_old_pass", "encoded_old_password")).thenReturn(false);

        assertThatThrownBy(() -> fixture.userService.changeCurrentUserPassword(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Current password is incorrect")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00006_UTCID06 - Changes password successfully")
    void TC6_Success() {
        ReqChangePasswordDTO request = ReqChangePasswordDTO.builder()
                .currentPassword("old_pass")
                .newPassword("new_pass")
                .confirmPassword("new_pass")
                .build();

        User mockUser = User.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .accountStatus("ACTIVE")
                .password("encoded_old_password")
                .build();

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));
        when(fixture.passwordEncoder.matches("old_pass", "encoded_old_password")).thenReturn(true);
        when(fixture.passwordEncoder.encode("new_pass")).thenReturn("encoded_new_password");

        ResAuthActionDTO result = fixture.userService.changeCurrentUserPassword(request);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Password changed successfully");
        assertThat(result.getToken()).isNull();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(fixture.userRepository).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getPassword()).isEqualTo("encoded_new_password");
    }
}
