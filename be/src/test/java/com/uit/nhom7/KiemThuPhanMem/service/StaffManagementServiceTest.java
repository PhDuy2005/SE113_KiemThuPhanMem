package com.uit.nhom7.KiemThuPhanMem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqCreateStaffDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResUserDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.RoleRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

class StaffManagementServiceTest {
    @Test
    void createStaffShouldRequireLogin() {
        Fixture fixture = new Fixture();
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> fixture.service.createStaff(validCreateStaffRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void createStaffShouldRejectWhenSessionIsInvalid() {
        Fixture fixture = new Fixture();
        authenticate("missing@example.com");
        when(fixture.userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.service.createStaff(validCreateStaffRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
        fixture.clearAuth();
    }

    @Test
    void createStaffShouldRejectWhenAdminAccountStatusIsNull() {
        Fixture fixture = new Fixture();
        User admin = userWithRole("admin@example.com", null, "BUSINESS_ADMIN");

        authenticate(admin.getEmail());
        when(fixture.userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> fixture.service.createStaff(validCreateStaffRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
        fixture.clearAuth();
    }

    @Test
    void createStaffShouldRejectWhenUserIsNotBusinessAdmin() {
        Fixture fixture = new Fixture();
        User staffUser = userWithRole("staff@example.com", "ACTIVE", "STAFF");

        authenticate(staffUser.getEmail());
        when(fixture.userRepository.findByEmail(staffUser.getEmail())).thenReturn(Optional.of(staffUser));

        assertThatThrownBy(() -> fixture.service.createStaff(validCreateStaffRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
        fixture.clearAuth();
    }

    @Test
    void createStaffShouldRejectWhenRequestIsNull() {
        Fixture fixture = new Fixture();
        stubBusinessAdmin(fixture);

        assertThatThrownBy(() -> fixture.service.createStaff(null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Required field is missing")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        fixture.clearAuth();
    }

    @Test
    void createStaffShouldRejectWhenEmailIsBlank() {
        Fixture fixture = new Fixture();
        stubBusinessAdmin(fixture);
        ReqCreateStaffDTO request = validCreateStaffRequest();
        request.setEmail("   ");

        assertThatThrownBy(() -> fixture.service.createStaff(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Required field is missing")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        fixture.clearAuth();
    }

    @Test
    void createStaffShouldRejectWhenFullNameIsBlank() {
        Fixture fixture = new Fixture();
        stubBusinessAdmin(fixture);
        ReqCreateStaffDTO request = validCreateStaffRequest();
        request.setFullName("   ");

        assertThatThrownBy(() -> fixture.service.createStaff(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Required field is missing")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        fixture.clearAuth();
    }

    @Test
    void createStaffShouldRejectWhenRoleIdIsNull() {
        Fixture fixture = new Fixture();
        stubBusinessAdmin(fixture);
        ReqCreateStaffDTO request = validCreateStaffRequest();
        request.setRoleId(null);

        assertThatThrownBy(() -> fixture.service.createStaff(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Required field is missing")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        fixture.clearAuth();
    }

    @Test
    void createStaffShouldRejectWhenEmailFormatIsInvalid() {
        Fixture fixture = new Fixture();
        stubBusinessAdmin(fixture);
        ReqCreateStaffDTO request = validCreateStaffRequest();
        request.setEmail("invalid-email");

        assertThatThrownBy(() -> fixture.service.createStaff(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email format is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        fixture.clearAuth();
    }

    @Test
    void createStaffShouldRejectWhenAccountAlreadyExists() {
        Fixture fixture = new Fixture();
        stubBusinessAdmin(fixture);
        when(fixture.userRepository.existsByEmail("valid@uit.edu.vn")).thenReturn(true);

        assertThatThrownBy(() -> fixture.service.createStaff(validCreateStaffRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Account already exists")
                .extracting("status")
                .isEqualTo(HttpStatus.CONFLICT);
        fixture.clearAuth();
    }

    @Test
    void createStaffShouldRejectWhenRoleIsNotFound() {
        Fixture fixture = new Fixture();
        stubBusinessAdmin(fixture);
        when(fixture.userRepository.existsByEmail("valid@uit.edu.vn")).thenReturn(false);
        when(fixture.roleRepository.findById(fixture.staffRoleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.service.createStaff(validCreateStaffRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Role not found")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        fixture.clearAuth();
    }

    @Test
    void createStaffShouldRejectWhenAssignedRoleIsNotStaff() {
        Fixture fixture = new Fixture();
        stubBusinessAdmin(fixture);
        when(fixture.userRepository.existsByEmail("valid@uit.edu.vn")).thenReturn(false);
        when(fixture.roleRepository.findById(fixture.staffRoleId))
                .thenReturn(Optional.of(Role.builder().id(fixture.staffRoleId).name("CUSTOMER").build()));

        assertThatThrownBy(() -> fixture.service.createStaff(validCreateStaffRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only STAFF role can be assigned to staff accounts")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        fixture.clearAuth();
    }

    @Test
    void createStaffShouldCreateStaffSuccessfully() {
        Fixture fixture = new Fixture();
        stubBusinessAdmin(fixture);
        when(fixture.userRepository.existsByEmail("valid@uit.edu.vn")).thenReturn(false);
        when(fixture.roleRepository.findById(fixture.staffRoleId)).thenReturn(Optional.of(fixture.staffRole));
        when(fixture.passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(fixture.userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(fixture.staffId);
            return user;
        });

        ResUserDTO result = fixture.service.createStaff(validCreateStaffRequest());

        assertThat(result.getId()).isEqualTo(fixture.staffId);
        assertThat(result.getEmail()).isEqualTo("valid@uit.edu.vn");
        assertThat(result.getName()).isEqualTo("Valid");
        assertThat(result.getAccountStatus()).isEqualTo("ACTIVE");
        assertThat(result.getRole().getId()).isEqualTo(fixture.staffRoleId);
        assertThat(result.getRole().getName()).isEqualTo("STAFF");
        assertThat(result.getMessage()).isEqualTo("Staff account created successfully");
        verify(fixture.emailService).sendStaffLoginDetails(anyString(), anyString(), anyString());
        fixture.clearAuth();
    }

    @Test
    void lockStaffShouldRequireLogin() {
        Fixture fixture = new Fixture();
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> fixture.service.lockStaff(fixture.staffId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void lockStaffShouldRejectWhenSessionIsInvalid() {
        Fixture fixture = new Fixture();
        authenticate("missing@example.com");
        when(fixture.userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.service.lockStaff(fixture.staffId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
        fixture.clearAuth();
    }

    @Test
    void lockStaffShouldRejectWhenAdminIsNotBusinessAdmin() {
        Fixture fixture = new Fixture();
        User admin = userWithRole("staff@example.com", "ACTIVE", "STAFF");

        authenticate(admin.getEmail());
        when(fixture.userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> fixture.service.lockStaff(fixture.staffId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
        fixture.clearAuth();
    }

    @Test
    void lockStaffShouldRejectWhenStaffAccountIsNotFound() {
        Fixture fixture = new Fixture();
        stubBusinessAdmin(fixture);
        when(fixture.userRepository.findById(fixture.staffId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.service.lockStaff(fixture.staffId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Staff account not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
        fixture.clearAuth();
    }

    @Test
    void lockStaffShouldRejectWhenTargetAccountIsNotStaff() {
        Fixture fixture = new Fixture();
        stubBusinessAdmin(fixture);
        User customer = User.builder()
                .id(fixture.staffId)
                .email("staff@example.com")
                .userFullName("Nguyen Van Staff")
                .accountStatus("ACTIVE")
                .role(Role.builder().id(fixture.staffRoleId).name("CUSTOMER").build())
                .build();
        when(fixture.userRepository.findById(fixture.staffId)).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> fixture.service.lockStaff(fixture.staffId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only staff accounts can be locked by this action")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        fixture.clearAuth();
    }

    @Test
    void lockStaffShouldLockStaffSuccessfully() {
        Fixture fixture = new Fixture();
        stubBusinessAdmin(fixture);
        User staff = User.builder()
                .id(fixture.staffId)
                .email("staff@example.com")
                .userFullName("Nguyen Van Staff")
                .accountStatus("ACTIVE")
                .refreshToken("refresh-token")
                .lockedUntil(java.time.Instant.now())
                .role(fixture.staffRole)
                .build();
        when(fixture.userRepository.findById(fixture.staffId)).thenReturn(Optional.of(staff));
        when(fixture.userRepository.save(staff)).thenAnswer(invocation -> invocation.getArgument(0));

        ResUserDTO result = fixture.service.lockStaff(fixture.staffId);

        assertThat(staff.getAccountStatus()).isEqualTo("LOCKED");
        assertThat(staff.getRefreshToken()).isNull();
        assertThat(staff.getLockedUntil()).isNull();
        assertThat(result.getId()).isEqualTo(fixture.staffId);
        assertThat(result.getAccountStatus()).isEqualTo("LOCKED");
        assertThat(result.getRole().getName()).isEqualTo("STAFF");
        assertThat(result.getMessage()).isEqualTo("Staff account locked successfully");
        fixture.clearAuth();
    }

    private static void stubBusinessAdmin(Fixture fixture) {
        authenticate(fixture.admin.getEmail());
        when(fixture.userRepository.findByEmail(fixture.admin.getEmail())).thenReturn(Optional.of(fixture.admin));
    }

    private static ReqCreateStaffDTO validCreateStaffRequest() {
        ReqCreateStaffDTO request = new ReqCreateStaffDTO();
        request.setEmail("valid@uit.edu.vn");
        request.setFullName("Valid");
        request.setRoleId(Fixture.STAFF_ROLE_ID);
        return request;
    }

    private static void authenticate(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null));
    }

    private static User userWithRole(String email, String accountStatus, String roleName) {
        return User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .userFullName("Admin User")
                .password("encoded")
                .accountStatus(accountStatus)
                .role(roleName == null ? null : Role.builder().name(roleName).build())
                .build();
    }

    private static class Fixture {
        private static final Long STAFF_ROLE_ID = 222L;

        private final EmailService emailService = Mockito.mock(EmailService.class);
        private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        private final RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
        private final UserRepository userRepository = Mockito.mock(UserRepository.class);
        private final StaffManagementService service = new StaffManagementService(
                emailService,
                passwordEncoder,
                roleRepository,
                userRepository);

        private final UUID staffId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        private final Long staffRoleId = STAFF_ROLE_ID;
        private final Role staffRole = Role.builder()
                .id(staffRoleId)
                .name("STAFF")
                .description("Staff Role")
                .active(true)
                .build();
        private final User admin = userWithRole("admin@example.com", "ACTIVE", "BUSINESS_ADMIN");

        private void clearAuth() {
            SecurityContextHolder.clearContext();
        }
    }
}
