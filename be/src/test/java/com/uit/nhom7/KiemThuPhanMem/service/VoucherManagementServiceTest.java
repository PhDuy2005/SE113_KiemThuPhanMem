package com.uit.nhom7.KiemThuPhanMem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqCreateVoucherDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResVoucherDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Voucher;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.VoucherRepository;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

class VoucherManagementServiceTest {
    @Test
    void createVoucherShouldRequireLogin() {
        Fixture fixture = new Fixture();
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> fixture.service.createVoucher(validRequestStartingToday()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void createVoucherShouldRejectWhenSessionIsInvalid() {
        Fixture fixture = new Fixture();
        authenticate("missing@example.com");
        when(fixture.userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.service.createVoucher(validRequestStartingToday()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
        SecurityContextHolder.clearContext();
    }

    @Test
    void createVoucherShouldRejectWhenAccountIsInactive() {
        Fixture fixture = new Fixture();
        User pendingUser = userWithRole("admin@example.com", "PENDING", "BUSINESS_ADMIN");

        authenticate(pendingUser.getEmail());
        when(fixture.userRepository.findByEmail(pendingUser.getEmail())).thenReturn(Optional.of(pendingUser));

        assertThatThrownBy(() -> fixture.service.createVoucher(validRequestStartingToday()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
        SecurityContextHolder.clearContext();
    }

    @Test
    void createVoucherShouldRejectWhenUserIsNotBusinessAdmin() {
        Fixture fixture = new Fixture();
        User customer = userWithRole("customer@example.com", "ACTIVE", "CUSTOMER");

        authenticate(customer.getEmail());
        when(fixture.userRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> fixture.service.createVoucher(validRequestStartingToday()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
        SecurityContextHolder.clearContext();
    }

    @Test
    void createVoucherShouldRejectWhenRequestIsNull() {
        Fixture fixture = new Fixture();
        User admin = activeBusinessAdmin();

        authenticate(admin.getEmail());
        when(fixture.userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> fixture.service.createVoucher(null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Required field is missing")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        SecurityContextHolder.clearContext();
    }

    @Test
    void createVoucherShouldRejectWhenEndDateIsNotAfterStartDate() {
        Fixture fixture = new Fixture();
        User admin = activeBusinessAdmin();
        ReqCreateVoucherDTO request = validRequestStartingTomorrow();
        request.setEndDate(request.getStartDate());

        authenticate(admin.getEmail());
        when(fixture.userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> fixture.service.createVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("End date must be after start date")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        SecurityContextHolder.clearContext();
    }

    @Test
    void createVoucherShouldRejectWhenDiscountValueIsNotPositive() {
        Fixture fixture = new Fixture();
        User admin = activeBusinessAdmin();
        ReqCreateVoucherDTO request = validRequestStartingToday();
        request.setDiscountValue(BigDecimal.ZERO);

        authenticate(admin.getEmail());
        when(fixture.userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> fixture.service.createVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Discount value and quantity must be greater than 0")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        SecurityContextHolder.clearContext();
    }

    @Test
    void createVoucherShouldRejectWhenDiscountTypeIsInvalid() {
        Fixture fixture = new Fixture();
        User admin = activeBusinessAdmin();
        ReqCreateVoucherDTO request = validRequestStartingToday();
        request.setDiscountType("OTHER");

        authenticate(admin.getEmail());
        when(fixture.userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> fixture.service.createVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("discountType must be FIXED or PERCENT")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        SecurityContextHolder.clearContext();
    }

    @Test
    void createVoucherShouldRejectWhenCodeAlreadyExists() {
        Fixture fixture = new Fixture();
        User admin = activeBusinessAdmin();
        ReqCreateVoucherDTO request = validRequestStartingToday();

        authenticate(admin.getEmail());
        when(fixture.userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(fixture.voucherRepository.existsByCodeIgnoreCase("SUMMER")).thenReturn(true);

        assertThatThrownBy(() -> fixture.service.createVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Voucher code already exists")
                .extracting("status")
                .isEqualTo(HttpStatus.CONFLICT);
        SecurityContextHolder.clearContext();
    }

    @Test
    void createVoucherShouldCreateActiveVoucherWhenStartDateIsToday() {
        Fixture fixture = new Fixture();
        User admin = activeBusinessAdmin();
        ReqCreateVoucherDTO request = validRequestStartingToday();

        authenticate(admin.getEmail());
        when(fixture.userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(fixture.voucherRepository.existsByCodeIgnoreCase("SUMMER")).thenReturn(false);
        when(fixture.voucherRepository.save(any(Voucher.class))).thenAnswer(invocation -> persistVoucher(invocation.getArgument(0)));

        ResVoucherDTO result = fixture.service.createVoucher(request);

        assertThat(result.getVoucherCode()).isEqualTo("SUMMER");
        assertThat(result.getDiscountType()).isEqualTo(Voucher.PERCENT_TYPE);
        assertThat(result.getDiscountValue()).isEqualByComparingTo("10");
        assertThat(result.getQuantity()).isEqualTo(100);
        assertThat(result.getUsedCount()).isZero();
        assertThat(result.isActive()).isTrue();
        assertThat(result.getStatus()).isEqualTo(Voucher.ACTIVE_STATUS);
        assertThat(result.getMessage()).isEqualTo("Voucher created successfully");
        SecurityContextHolder.clearContext();
    }

    @Test
    void createVoucherShouldCreateScheduledVoucherWhenStartDateIsInFuture() {
        Fixture fixture = new Fixture();
        User admin = activeBusinessAdmin();
        ReqCreateVoucherDTO request = validRequestStartingTomorrow();

        authenticate(admin.getEmail());
        when(fixture.userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(fixture.voucherRepository.existsByCodeIgnoreCase("SUMMER")).thenReturn(false);
        when(fixture.voucherRepository.save(any(Voucher.class))).thenAnswer(invocation -> persistVoucher(invocation.getArgument(0)));

        ResVoucherDTO result = fixture.service.createVoucher(request);

        assertThat(result.getVoucherCode()).isEqualTo("SUMMER");
        assertThat(result.isActive()).isFalse();
        assertThat(result.getStatus()).isEqualTo("SCHEDULED");
        assertThat(result.getMessage()).isEqualTo("Voucher created successfully");
        SecurityContextHolder.clearContext();
    }

    @Test
    void emergencyStopVoucherShouldRequireLogin() {
        Fixture fixture = new Fixture();
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> fixture.service.emergencyStopVoucher(UUID.randomUUID()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void emergencyStopVoucherShouldRejectWhenSessionIsInvalid() {
        Fixture fixture = new Fixture();
        authenticate("missing@example.com");
        when(fixture.userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.service.emergencyStopVoucher(UUID.randomUUID()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
        SecurityContextHolder.clearContext();
    }

    @Test
    void emergencyStopVoucherShouldRejectWhenAccountIsInactive() {
        Fixture fixture = new Fixture();
        User pendingUser = userWithRole("admin@example.com", "PENDING", "BUSINESS_ADMIN");

        authenticate(pendingUser.getEmail());
        when(fixture.userRepository.findByEmail(pendingUser.getEmail())).thenReturn(Optional.of(pendingUser));

        assertThatThrownBy(() -> fixture.service.emergencyStopVoucher(UUID.randomUUID()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
        SecurityContextHolder.clearContext();
    }

    @Test
    void emergencyStopVoucherShouldRejectWhenUserIsNotBusinessAdmin() {
        Fixture fixture = new Fixture();
        User customer = userWithRole("customer@example.com", "ACTIVE", "CUSTOMER");

        authenticate(customer.getEmail());
        when(fixture.userRepository.findByEmail(customer.getEmail())).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> fixture.service.emergencyStopVoucher(UUID.randomUUID()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
        SecurityContextHolder.clearContext();
    }

    @Test
    void emergencyStopVoucherShouldRejectWhenVoucherIsNotFound() {
        Fixture fixture = new Fixture();
        User admin = activeBusinessAdmin();
        UUID voucherId = UUID.randomUUID();

        authenticate(admin.getEmail());
        when(fixture.userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(fixture.voucherRepository.findById(voucherId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.service.emergencyStopVoucher(voucherId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Voucher not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
        SecurityContextHolder.clearContext();
    }

    @Test
    void emergencyStopVoucherShouldStopVoucherSuccessfully() {
        Fixture fixture = new Fixture();
        User admin = activeBusinessAdmin();
        UUID voucherId = UUID.randomUUID();
        Voucher voucher = Voucher.builder()
                .id(voucherId)
                .code("SUMMER")
                .type(Voucher.PERCENT_TYPE)
                .value(BigDecimal.TEN)
                .maxUsage(100)
                .usedCount(5)
                .minOrderAmount(BigDecimal.ZERO)
                .startDate(today())
                .endDate(today().plus(30, ChronoUnit.DAYS))
                .active(true)
                .status(Voucher.ACTIVE_STATUS)
                .build();

        authenticate(admin.getEmail());
        when(fixture.userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(fixture.voucherRepository.findById(voucherId)).thenReturn(Optional.of(voucher));
        when(fixture.voucherRepository.save(any(Voucher.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResVoucherDTO result = fixture.service.emergencyStopVoucher(voucherId);

        assertThat(result.getVoucherCode()).isEqualTo("SUMMER");
        assertThat(result.isActive()).isFalse();
        assertThat(result.getStatus()).isEqualTo(Voucher.STOPPED_STATUS);
        assertThat(result.getMessage()).isEqualTo("Voucher stopped successfully");
        verify(fixture.voucherRepository).save(voucher);
        SecurityContextHolder.clearContext();
    }

    private void authenticate(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null));
    }

    private static ReqCreateVoucherDTO validRequestStartingToday() {
        ReqCreateVoucherDTO request = new ReqCreateVoucherDTO();
        request.setVoucherCode(" summer ");
        request.setDiscountType(" percent ");
        request.setDiscountValue(BigDecimal.TEN);
        request.setQuantity(100);
        request.setMinOrderAmount(BigDecimal.ZERO);
        request.setStartDate(today());
        request.setEndDate(today().plus(1, ChronoUnit.DAYS));
        return request;
    }

    private static ReqCreateVoucherDTO validRequestStartingTomorrow() {
        ReqCreateVoucherDTO request = validRequestStartingToday();
        request.setStartDate(today().plus(1, ChronoUnit.DAYS));
        request.setEndDate(today().plus(7, ChronoUnit.DAYS));
        return request;
    }

    private static Instant today() {
        return LocalDate.now(ZoneId.systemDefault())
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();
    }

    private static Voucher persistVoucher(Voucher voucher) {
        voucher.setId(UUID.randomUUID());
        voucher.setCreatedAt(Instant.now());
        return voucher;
    }

    private static User activeBusinessAdmin() {
        return userWithRole("admin@example.com", "ACTIVE", "BUSINESS_ADMIN");
    }

    private static User userWithRole(String email, String accountStatus, String roleName) {
        return User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password("encoded")
                .accountStatus(accountStatus)
                .role(Role.builder().name(roleName).build())
                .build();
    }

    private static class Fixture {
        private final UserRepository userRepository = Mockito.mock(UserRepository.class);
        private final VoucherRepository voucherRepository = Mockito.mock(VoucherRepository.class);
        private final VoucherManagementService service = new VoucherManagementService(userRepository, voucherRepository);
    }
}
