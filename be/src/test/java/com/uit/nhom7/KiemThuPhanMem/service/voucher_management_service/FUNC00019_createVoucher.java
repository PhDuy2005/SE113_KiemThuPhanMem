package com.uit.nhom7.KiemThuPhanMem.service.voucher_management_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqCreateVoucherDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResVoucherDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Voucher;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00019_createVoucher extends VoucherManagementServiceTestBase {

    private MockedStatic<SecurityUtil> mockedSecurityUtil;
    private Fixture fixture;

    private final UUID userId = UUID.fromString("44444444-4444-4444-4444-444444444444");

    private Instant today;
    private Instant tomorrow;
    private Instant nextWeek;

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
        mockedSecurityUtil = mockStatic(SecurityUtil.class);

        today = Instant.now();
        tomorrow = today.plus(1, ChronoUnit.DAYS);
        nextWeek = today.plus(7, ChronoUnit.DAYS);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtil.close();
    }

    private User getAdminUser() {
        Role role = new Role();
        role.setName("BUSINESS_ADMIN");
        return User.builder()
                .id(userId)
                .email("admin@example.com")
                .accountStatus("ACTIVE")
                .role(role)
                .build();
    }

    @Test
    @DisplayName("FUNC00019_UTCID01 - Fails because user is not authenticated")
    void TC1_NoLogin() {
        ReqCreateVoucherDTO request = new ReqCreateVoucherDTO();
        request.setVoucherCode("SUMMER");

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.voucherManagementService.createVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00019_UTCID02 - Fails because email is not in DB")
    void TC2_NoSession() {
        ReqCreateVoucherDTO request = new ReqCreateVoucherDTO();
        request.setVoucherCode("SUMMER");

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.voucherManagementService.createVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00019_UTCID03 - Fails because user is not ACTIVE")
    void TC3_NotActive() {
        ReqCreateVoucherDTO request = new ReqCreateVoucherDTO();
        request.setVoucherCode("SUMMER");

        User user = User.builder().id(userId).accountStatus("PENDING").build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.voucherManagementService.createVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00019_UTCID04 - Fails because user is not BUSINESS_ADMIN")
    void TC4_NotAdmin() {
        ReqCreateVoucherDTO request = new ReqCreateVoucherDTO();
        request.setVoucherCode("SUMMER");

        Role role = new Role();
        role.setName("CUSTOMER");
        User user = User.builder().id(userId).accountStatus("ACTIVE").role(role).build();
        
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.voucherManagementService.createVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00019_UTCID05 - Fails because request is null")
    void TC5_NullReq() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));

        assertThatThrownBy(() -> fixture.voucherManagementService.createVoucher(null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Required field is missing")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00019_UTCID06 - Fails because endDate <= startDate")
    void TC6_InvalidDate() {
        ReqCreateVoucherDTO request = new ReqCreateVoucherDTO();
        request.setVoucherCode("SUMMER");
        request.setDiscountType("PERCENT");
        request.setDiscountValue(BigDecimal.valueOf(10));
        request.setQuantity(100);
        request.setStartDate(tomorrow);
        request.setEndDate(today); // End date before start date

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));

        assertThatThrownBy(() -> fixture.voucherManagementService.createVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("End date must be after start date")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00019_UTCID07 - Fails because discountValue is 0")
    void TC7_InvalidVal() {
        ReqCreateVoucherDTO request = new ReqCreateVoucherDTO();
        request.setVoucherCode("SUMMER");
        request.setDiscountType("PERCENT");
        request.setDiscountValue(BigDecimal.valueOf(0)); // Invalid value
        request.setQuantity(100);
        request.setStartDate(today);
        request.setEndDate(tomorrow);

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));

        assertThatThrownBy(() -> fixture.voucherManagementService.createVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Discount value and quantity must be greater than 0")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00019_UTCID08 - Fails because discountType is invalid")
    void TC8_InvalidType() {
        ReqCreateVoucherDTO request = new ReqCreateVoucherDTO();
        request.setVoucherCode("SUMMER");
        request.setDiscountType("OTHER"); // Invalid type
        request.setDiscountValue(BigDecimal.valueOf(10));
        request.setQuantity(100);
        request.setStartDate(today);
        request.setEndDate(tomorrow);

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));

        assertThatThrownBy(() -> fixture.voucherManagementService.createVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("discountType must be FIXED or PERCENT")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00019_UTCID09 - Fails because voucher code already exists")
    void TC9_CodeExists() {
        ReqCreateVoucherDTO request = new ReqCreateVoucherDTO();
        request.setVoucherCode("SUMMER");
        request.setDiscountType("PERCENT");
        request.setDiscountValue(BigDecimal.valueOf(10));
        request.setQuantity(100);
        request.setStartDate(today);
        request.setEndDate(tomorrow);

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));
        
        when(fixture.voucherRepository.existsByCodeIgnoreCase("SUMMER")).thenReturn(true);

        assertThatThrownBy(() -> fixture.voucherManagementService.createVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Voucher code already exists")
                .extracting("status")
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("FUNC00019_UTCID10 - Creates voucher successfully with ACTIVE status (starts today)")
    void TC10_SuccessActive() {
        ReqCreateVoucherDTO request = new ReqCreateVoucherDTO();
        request.setVoucherCode("SUMMER");
        request.setDiscountType("PERCENT");
        request.setDiscountValue(BigDecimal.valueOf(10));
        request.setQuantity(100);
        request.setStartDate(today);
        request.setEndDate(tomorrow);

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));
        
        when(fixture.voucherRepository.existsByCodeIgnoreCase("SUMMER")).thenReturn(false);
        
        UUID voucherId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Voucher savedVoucher = Voucher.builder()
                .id(voucherId)
                .code("SUMMER")
                .type("PERCENT")
                .value(BigDecimal.valueOf(10))
                .maxUsage(100)
                .usedCount(0)
                .startDate(today)
                .endDate(tomorrow)
                .active(true)
                .status("ACTIVE")
                .build();
        when(fixture.voucherRepository.save(any(Voucher.class))).thenReturn(savedVoucher);

        ResVoucherDTO result = fixture.voucherManagementService.createVoucher(request);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Voucher created successfully");
        assertThat(result.getId()).isEqualTo(voucherId);
        assertThat(result.getVoucherCode()).isEqualTo("SUMMER");
        assertThat(result.getDiscountType()).isEqualTo("PERCENT");
        assertThat(result.getDiscountValue().doubleValue()).isEqualTo(10.0);
        assertThat(result.getQuantity()).isEqualTo(100);
        assertThat(result.getUsedCount()).isEqualTo(0);
        assertThat(result.isActive()).isTrue();
        assertThat(result.getStatus()).isEqualTo("ACTIVE");

        ArgumentCaptor<Voucher> captor = ArgumentCaptor.forClass(Voucher.class);
        verify(fixture.voucherRepository).save(captor.capture());
        Voucher saved = captor.getValue();
        assertThat(saved.getCode()).isEqualTo("SUMMER");
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("FUNC00019_UTCID11 - Creates voucher successfully with SCHEDULED status (starts tomorrow)")
    void TC11_SuccessSched() {
        ReqCreateVoucherDTO request = new ReqCreateVoucherDTO();
        request.setVoucherCode("SUMMER");
        request.setDiscountType("PERCENT");
        request.setDiscountValue(BigDecimal.valueOf(10));
        request.setQuantity(100);
        request.setStartDate(tomorrow);
        request.setEndDate(nextWeek);

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));
        
        when(fixture.voucherRepository.existsByCodeIgnoreCase("SUMMER")).thenReturn(false);
        
        UUID voucherId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        Voucher savedVoucher = Voucher.builder()
                .id(voucherId)
                .code("SUMMER")
                .type("PERCENT")
                .value(BigDecimal.valueOf(10))
                .maxUsage(100)
                .usedCount(0)
                .startDate(tomorrow)
                .endDate(nextWeek)
                .active(false)
                .status("SCHEDULED")
                .build();
        when(fixture.voucherRepository.save(any(Voucher.class))).thenReturn(savedVoucher);

        ResVoucherDTO result = fixture.voucherManagementService.createVoucher(request);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Voucher created successfully");
        assertThat(result.getId()).isEqualTo(voucherId);
        assertThat(result.getVoucherCode()).isEqualTo("SUMMER");
        assertThat(result.getDiscountType()).isEqualTo("PERCENT");
        assertThat(result.getDiscountValue().doubleValue()).isEqualTo(10.0);
        assertThat(result.getQuantity()).isEqualTo(100);
        assertThat(result.getUsedCount()).isEqualTo(0);
        assertThat(result.isActive()).isFalse();
        assertThat(result.getStatus()).isEqualTo("SCHEDULED");

        ArgumentCaptor<Voucher> captor = ArgumentCaptor.forClass(Voucher.class);
        verify(fixture.voucherRepository).save(captor.capture());
        Voucher saved = captor.getValue();
        assertThat(saved.getCode()).isEqualTo("SUMMER");
        assertThat(saved.isActive()).isFalse();
        assertThat(saved.getStatus()).isEqualTo("SCHEDULED");
    }
}
