package com.uit.nhom7.KiemThuPhanMem.service.user_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateProfileDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResUserDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00007_updateCurrentUserProfile extends UserServiceTestBase {

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
    @DisplayName("FUNC00007_UTCID01 - Fails because user is not authenticated")
    void TC1_NotLoggedIn() {
        ReqUpdateProfileDTO request = ReqUpdateProfileDTO.builder()
                .fullName("New Name")
                .phoneNumber("0123456789")
                .build();

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.userService.updateCurrentUserProfile(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00007_UTCID02 - Fails because authenticated email is not in DB")
    void TC2_UserNotFound() {
        ReqUpdateProfileDTO request = ReqUpdateProfileDTO.builder()
                .fullName("New Name")
                .phoneNumber("0123456789")
                .build();

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.userService.updateCurrentUserProfile(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00007_UTCID03 - Fails because user account status is not ACTIVE")
    void TC3_UserNotActive() {
        ReqUpdateProfileDTO request = ReqUpdateProfileDTO.builder()
                .fullName("New Name")
                .phoneNumber("0123456789")
                .build();

        User mockUser = User.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .accountStatus("PENDING")
                .build();

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));

        assertThatThrownBy(() -> fixture.userService.updateCurrentUserProfile(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00007_UTCID04 - Updates profile successfully for user with a role")
    void TC4_SuccessWithRole() {
        ReqUpdateProfileDTO request = ReqUpdateProfileDTO.builder()
                .fullName("New Name")
                .phoneNumber("0123456789")
                .build();

        Role role = new Role();
        role.setId(1L);
        role.setName("CUSTOMER");
        role.setDescription("Customer role");
        role.setActive(true);

        User mockUser = User.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .email("user@example.com")
                .accountStatus("ACTIVE")
                .role(role)
                .build();

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));
        when(fixture.userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        ResUserDTO result = fixture.userService.updateCurrentUserProfile(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        assertThat(result.getEmail()).isEqualTo("user@example.com");
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getPhoneNumber()).isEqualTo("0123456789");
        assertThat(result.getAccountStatus()).isEqualTo("ACTIVE");
        assertThat(result.getRole()).isNotNull();
        assertThat(result.getRole().getId()).isEqualTo(1L);
        assertThat(result.getRole().getName()).isEqualTo("CUSTOMER");
        assertThat(result.getRole().getDescription()).isEqualTo("Customer role");
        assertThat(result.getRole().isActive()).isTrue();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(fixture.userRepository).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUserFullName()).isEqualTo("New Name");
        assertThat(savedUser.getPhoneNumber()).isEqualTo("0123456789");
    }

    @Test
    @DisplayName("FUNC00007_UTCID05 - Updates profile successfully for user without a role")
    void TC5_SuccessWithoutRole() {
        ReqUpdateProfileDTO request = ReqUpdateProfileDTO.builder()
                .fullName("New Name")
                .phoneNumber("0123456789")
                .build();

        User mockUser = User.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .email("user@example.com")
                .accountStatus("ACTIVE")
                .role(null)
                .build();

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));
        when(fixture.userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        ResUserDTO result = fixture.userService.updateCurrentUserProfile(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        assertThat(result.getEmail()).isEqualTo("user@example.com");
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getPhoneNumber()).isEqualTo("0123456789");
        assertThat(result.getAccountStatus()).isEqualTo("ACTIVE");
        assertThat(result.getRole()).isNull();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(fixture.userRepository).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUserFullName()).isEqualTo("New Name");
        assertThat(savedUser.getPhoneNumber()).isEqualTo("0123456789");
    }
}
