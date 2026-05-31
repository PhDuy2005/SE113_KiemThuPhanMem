package com.uit.nhom7.KiemThuPhanMem.service.staff_management_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResUserDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00029_lockStaff extends StaffManagementServiceTestBase {

    private MockedStatic<SecurityUtil> mockedSecurityUtil;
    private Fixture fixture;

    private final UUID adminId = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private final UUID staffId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final Long roleId = 2L;

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
        mockedSecurityUtil = mockStatic(SecurityUtil.class);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtil.close();
    }

    private User getAdmin(String accountStatus, String roleName) {
        Role role = roleName == null && !("ACTIVE".equals(accountStatus) && roleName == null) ? null : Role.builder().name(roleName).build();
        return User.builder()
                .id(adminId)
                .accountStatus(accountStatus)
                .role(role)
                .build();
    }

    private User getStaff(String roleName) {
        Role role = roleName == null ? null : Role.builder().id(roleId).name(roleName).description("Staff Role").active(true).build();
        return User.builder()
                .id(staffId)
                .email("staff@example.com")
                .userFullName("Nguyen Van Staff")
                .accountStatus("ACTIVE")
                .role(role)
                .build();
    }

    @Test
    @DisplayName("FUNC00029_UTCID01 - Fails because user is not authenticated")
    void TC1_NoLogin() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.staffManagementService.lockStaff(staffId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00029_UTCID02 - Fails because email is not in DB")
    void TC2_NoSession() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.staffManagementService.lockStaff(staffId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00029_UTCID03 - Fails because admin accountStatus is null")
    void TC3_AdminStatusNull() {
        User admin = User.builder().id(adminId).accountStatus(null).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> fixture.staffManagementService.lockStaff(staffId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00029_UTCID04 - Fails because admin is not ACTIVE")
    void TC4_AdminInactive() {
        User admin = User.builder().id(adminId).accountStatus("INACTIVE").build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> fixture.staffManagementService.lockStaff(staffId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00029_UTCID05 - Fails because admin role is null")
    void TC5_AdminRoleNull() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(null).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> fixture.staffManagementService.lockStaff(staffId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00029_UTCID06 - Fails because admin role name is null")
    void TC6_AdminRoleNameNull() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(Role.builder().name(null).build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> fixture.staffManagementService.lockStaff(staffId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00029_UTCID07 - Fails because admin is not BUSINESS_ADMIN (is STAFF)")
    void TC7_AdminIsStaff() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(Role.builder().name("STAFF").build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> fixture.staffManagementService.lockStaff(staffId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00029_UTCID08 - Fails because target staff not found")
    void TC8_StaffNotFound() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(Role.builder().name("BUSINESS_ADMIN").build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        when(fixture.userRepository.findById(staffId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.staffManagementService.lockStaff(staffId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Staff account not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FUNC00029_UTCID09 - Fails because target staff role is null")
    void TC9_TargetRoleNull() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(Role.builder().name("BUSINESS_ADMIN").build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        User staff = getStaff(null); // Role is null
        when(fixture.userRepository.findById(staffId)).thenReturn(Optional.of(staff));

        assertThatThrownBy(() -> fixture.staffManagementService.lockStaff(staffId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only staff accounts can be locked by this action")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00029_UTCID10 - Fails because target staff role name is null")
    void TC10_TargetRoleNameNull() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(Role.builder().name("BUSINESS_ADMIN").build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        User staff = User.builder()
                .id(staffId)
                .email("staff@example.com")
                .userFullName("Nguyen Van Staff")
                .accountStatus("ACTIVE")
                .role(Role.builder().id(roleId).name(null).build())
                .build();
        when(fixture.userRepository.findById(staffId)).thenReturn(Optional.of(staff));

        assertThatThrownBy(() -> fixture.staffManagementService.lockStaff(staffId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only staff accounts can be locked by this action")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00029_UTCID11 - Fails because target staff is CUSTOMER")
    void TC11_TargetRoleCustomer() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(Role.builder().name("BUSINESS_ADMIN").build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        User staff = getStaff("CUSTOMER");
        when(fixture.userRepository.findById(staffId)).thenReturn(Optional.of(staff));

        assertThatThrownBy(() -> fixture.staffManagementService.lockStaff(staffId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only staff accounts can be locked by this action")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00029_UTCID12 - Success (target is STAFF, locked successfully)")
    void TC12_Success() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(Role.builder().name("BUSINESS_ADMIN").build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        User staff = getStaff("STAFF");
        when(fixture.userRepository.findById(staffId)).thenReturn(Optional.of(staff));

        User savedStaff = getStaff("STAFF");
        savedStaff.setAccountStatus("LOCKED");
        savedStaff.setRefreshToken(null);
        savedStaff.setLockedUntil(null);
        
        when(fixture.userRepository.save(any(User.class))).thenReturn(savedStaff);

        ResUserDTO result = fixture.staffManagementService.lockStaff(staffId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(staffId);
        assertThat(result.getEmail()).isEqualTo("staff@example.com");
        assertThat(result.getName()).isEqualTo("Nguyen Van Staff");
        assertThat(result.getAccountStatus()).isEqualTo("LOCKED");
        assertThat(result.getRole()).isNotNull();
        assertThat(result.getRole().getId()).isEqualTo(roleId);
        assertThat(result.getRole().getName()).isEqualTo("STAFF");
        assertThat(result.getMessage()).isEqualTo("Staff account locked successfully");
    }
}
