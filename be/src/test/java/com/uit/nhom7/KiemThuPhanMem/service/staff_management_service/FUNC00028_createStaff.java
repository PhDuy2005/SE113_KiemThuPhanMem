package com.uit.nhom7.KiemThuPhanMem.service.staff_management_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqCreateStaffDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResUserDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00028_createStaff extends StaffManagementServiceTestBase {

    private MockedStatic<SecurityUtil> mockedSecurityUtil;
    private Fixture fixture;

    private final UUID adminId = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private final Long roleId = 2L;
    private final UUID staffId = UUID.fromString("55555555-5555-5555-5555-555555555555");

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
        mockedSecurityUtil = mockStatic(SecurityUtil.class);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtil.close();
    }

    private ReqCreateStaffDTO createValidRequest() {
        ReqCreateStaffDTO req = new ReqCreateStaffDTO();
        req.setEmail("valid@uit.edu.vn");
        req.setFullName("Valid");
        req.setRoleId(roleId);
        return req;
    }

    private User getAdmin(String accountStatus, String roleName) {
        Role role = roleName == null && !("ACTIVE".equals(accountStatus) && roleName == null) ? null : Role.builder().name(roleName).build();
        // The TC specifies different null levels
        if (roleName == null && "ACTIVE".equals(accountStatus)) {
            // Need to distinguish between role is null vs role.name is null
            // We'll handle this in the TCs directly if needed
        }
        return User.builder()
                .id(adminId)
                .accountStatus(accountStatus)
                .role(role)
                .build();
    }

    @Test
    @DisplayName("FUNC00028_UTCID01 - Fails because user is not authenticated")
    void TC1_NoLogin() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.staffManagementService.createStaff(createValidRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00028_UTCID02 - Fails because email is not in DB")
    void TC2_NoSession() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.staffManagementService.createStaff(createValidRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00028_UTCID03 - Fails because admin accountStatus is null")
    void TC3_AdminStatusNull() {
        User admin = User.builder().id(adminId).accountStatus(null).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> fixture.staffManagementService.createStaff(createValidRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00028_UTCID04 - Fails because admin is not ACTIVE")
    void TC4_AdminInactive() {
        User admin = User.builder().id(adminId).accountStatus("INACTIVE").build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> fixture.staffManagementService.createStaff(createValidRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00028_UTCID05 - Fails because admin role is null")
    void TC5_AdminRoleNull() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(null).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> fixture.staffManagementService.createStaff(createValidRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00028_UTCID06 - Fails because admin role name is null")
    void TC6_AdminRoleNameNull() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(Role.builder().name(null).build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> fixture.staffManagementService.createStaff(createValidRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00028_UTCID07 - Fails because user is not BUSINESS_ADMIN (is STAFF)")
    void TC7_AdminIsStaff() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(Role.builder().name("STAFF").build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> fixture.staffManagementService.createStaff(createValidRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00028_UTCID08 - Fails because request object is null")
    void TC8_RequestNull() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(Role.builder().name("BUSINESS_ADMIN").build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> fixture.staffManagementService.createStaff(null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Required field is missing")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00028_UTCID09 - Fails because request.email is null")
    void TC9_EmailNull() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(Role.builder().name("BUSINESS_ADMIN").build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        ReqCreateStaffDTO req = createValidRequest();
        req.setEmail(null);

        assertThatThrownBy(() -> fixture.staffManagementService.createStaff(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Required field is missing")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00028_UTCID10 - Fails because request.email is blank")
    void TC10_EmailBlank() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(Role.builder().name("BUSINESS_ADMIN").build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        ReqCreateStaffDTO req = createValidRequest();
        req.setEmail("   ");

        assertThatThrownBy(() -> fixture.staffManagementService.createStaff(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Required field is missing")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00028_UTCID11 - Fails because request.fullName is null")
    void TC11_FullNameNull() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(Role.builder().name("BUSINESS_ADMIN").build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        ReqCreateStaffDTO req = createValidRequest();
        req.setFullName(null);

        assertThatThrownBy(() -> fixture.staffManagementService.createStaff(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Required field is missing")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00028_UTCID12 - Fails because request.fullName is blank")
    void TC12_FullNameBlank() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(Role.builder().name("BUSINESS_ADMIN").build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        ReqCreateStaffDTO req = createValidRequest();
        req.setFullName("   ");

        assertThatThrownBy(() -> fixture.staffManagementService.createStaff(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Required field is missing")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00028_UTCID13 - Fails because request.roleId is null")
    void TC13_RoleIdNull() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(Role.builder().name("BUSINESS_ADMIN").build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        ReqCreateStaffDTO req = createValidRequest();
        req.setRoleId(null);

        assertThatThrownBy(() -> fixture.staffManagementService.createStaff(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Required field is missing")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00028_UTCID14 - Fails because email format is invalid")
    void TC14_InvalidEmail() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(Role.builder().name("BUSINESS_ADMIN").build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        ReqCreateStaffDTO req = createValidRequest();
        req.setEmail("invalid-email");

        assertThatThrownBy(() -> fixture.staffManagementService.createStaff(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email format is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00028_UTCID15 - Fails because email already exists")
    void TC15_EmailExists() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(Role.builder().name("BUSINESS_ADMIN").build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        ReqCreateStaffDTO req = createValidRequest();
        when(fixture.userRepository.existsByEmail("valid@uit.edu.vn")).thenReturn(true);

        assertThatThrownBy(() -> fixture.staffManagementService.createStaff(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Account already exists")
                .extracting("status")
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("FUNC00028_UTCID16 - Fails because assigned role is not found")
    void TC16_RoleNotFound() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(Role.builder().name("BUSINESS_ADMIN").build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        ReqCreateStaffDTO req = createValidRequest();
        when(fixture.userRepository.existsByEmail("valid@uit.edu.vn")).thenReturn(false);
        when(fixture.roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.staffManagementService.createStaff(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Role not found")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00028_UTCID17 - Fails because assigned role name is null")
    void TC17_TargetRoleNameNull() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(Role.builder().name("BUSINESS_ADMIN").build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        ReqCreateStaffDTO req = createValidRequest();
        when(fixture.userRepository.existsByEmail("valid@uit.edu.vn")).thenReturn(false);
        when(fixture.roleRepository.findById(roleId)).thenReturn(Optional.of(Role.builder().id(roleId).name(null).build()));

        assertThatThrownBy(() -> fixture.staffManagementService.createStaff(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only STAFF role can be assigned to staff accounts")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00028_UTCID18 - Fails because assigned role is not STAFF")
    void TC18_TargetRoleCustomer() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(Role.builder().name("BUSINESS_ADMIN").build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        ReqCreateStaffDTO req = createValidRequest();
        when(fixture.userRepository.existsByEmail("valid@uit.edu.vn")).thenReturn(false);
        when(fixture.roleRepository.findById(roleId)).thenReturn(Optional.of(Role.builder().id(roleId).name("CUSTOMER").build()));

        assertThatThrownBy(() -> fixture.staffManagementService.createStaff(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only STAFF role can be assigned to staff accounts")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00028_UTCID19 - Success (creates staff, saves to DB, sends email)")
    void TC19_Success() {
        User admin = User.builder().id(adminId).accountStatus("ACTIVE").role(Role.builder().name("BUSINESS_ADMIN").build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));

        ReqCreateStaffDTO req = createValidRequest();
        when(fixture.userRepository.existsByEmail("valid@uit.edu.vn")).thenReturn(false);
        
        Role targetRole = Role.builder().id(roleId).name("STAFF").description("Staff Role").active(true).build();
        when(fixture.roleRepository.findById(roleId)).thenReturn(Optional.of(targetRole));
        when(fixture.passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        
        User savedUser = User.builder()
                .id(staffId)
                .email("valid@uit.edu.vn")
                .userFullName("Valid")
                .accountStatus("ACTIVE")
                .role(targetRole)
                .build();
        when(fixture.userRepository.save(any(User.class))).thenReturn(savedUser);

        ResUserDTO result = fixture.staffManagementService.createStaff(req);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(staffId);
        assertThat(result.getEmail()).isEqualTo("valid@uit.edu.vn");
        assertThat(result.getName()).isEqualTo("Valid");
        assertThat(result.getAccountStatus()).isEqualTo("ACTIVE");
        assertThat(result.getRole()).isNotNull();
        assertThat(result.getRole().getName()).isEqualTo("STAFF");
        assertThat(result.getMessage()).isEqualTo("Staff account created successfully");

        verify(fixture.emailService).sendStaffLoginDetails(anyString(), anyString(), anyString());
    }
}
