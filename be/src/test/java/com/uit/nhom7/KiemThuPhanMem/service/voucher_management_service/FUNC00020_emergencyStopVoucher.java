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

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResVoucherDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Voucher;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00020_emergencyStopVoucher extends VoucherManagementServiceTestBase {

    private MockedStatic<SecurityUtil> mockedSecurityUtil;
    private Fixture fixture;

    private final UUID userId = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private final UUID voucherId = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private Instant today;
    private Instant nextMonth;

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
        mockedSecurityUtil = mockStatic(SecurityUtil.class);

        today = Instant.now();
        nextMonth = today.plus(30, ChronoUnit.DAYS);
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
    @DisplayName("FUNC00020_UTCID01 - Fails because user is not authenticated")
    void TC1_NoLogin() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.voucherManagementService.emergencyStopVoucher(voucherId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00020_UTCID02 - Fails because email is not in DB")
    void TC2_NoSession() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.voucherManagementService.emergencyStopVoucher(voucherId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00020_UTCID03 - Fails because user is not ACTIVE")
    void TC3_NotActive() {
        User user = User.builder().id(userId).accountStatus("PENDING").build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.voucherManagementService.emergencyStopVoucher(voucherId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00020_UTCID04 - Fails because user is not BUSINESS_ADMIN")
    void TC4_NotAdmin() {
        Role role = new Role();
        role.setName("CUSTOMER");
        User user = User.builder().id(userId).accountStatus("ACTIVE").role(role).build();
        
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.voucherManagementService.emergencyStopVoucher(voucherId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00020_UTCID05 - Fails because voucherId does not exist")
    void TC5_VoucherNotFound() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));
        
        when(fixture.voucherRepository.findById(voucherId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.voucherManagementService.emergencyStopVoucher(voucherId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Voucher not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FUNC00020_UTCID06 - Stops voucher successfully (active=false, status=STOPPED)")
    void TC6_Success() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));
        
        Voucher existingVoucher = Voucher.builder()
                .id(voucherId)
                .code("SUMMER")
                .type("PERCENT")
                .value(BigDecimal.valueOf(10))
                .maxUsage(100)
                .usedCount(5)
                .minOrderAmount(BigDecimal.ZERO)
                .startDate(today)
                .endDate(nextMonth)
                .active(true)
                .status("ACTIVE")
                .build();
        when(fixture.voucherRepository.findById(voucherId)).thenReturn(Optional.of(existingVoucher));
        
        Voucher stoppedVoucher = Voucher.builder()
                .id(voucherId)
                .code("SUMMER")
                .type("PERCENT")
                .value(BigDecimal.valueOf(10))
                .maxUsage(100)
                .usedCount(5)
                .minOrderAmount(BigDecimal.ZERO)
                .startDate(today)
                .endDate(nextMonth)
                .active(false)
                .status("STOPPED")
                .build();
        when(fixture.voucherRepository.save(any(Voucher.class))).thenReturn(stoppedVoucher);

        ResVoucherDTO result = fixture.voucherManagementService.emergencyStopVoucher(voucherId);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Voucher stopped successfully");
        assertThat(result.getId()).isEqualTo(voucherId);
        assertThat(result.getVoucherCode()).isEqualTo("SUMMER");
        assertThat(result.getDiscountType()).isEqualTo("PERCENT");
        assertThat(result.getDiscountValue().doubleValue()).isEqualTo(10.0);
        assertThat(result.getQuantity()).isEqualTo(100);
        assertThat(result.getUsedCount()).isEqualTo(5);
        assertThat(result.getMinOrderAmount().doubleValue()).isEqualTo(0.0);
        assertThat(result.isActive()).isFalse();
        assertThat(result.getStatus()).isEqualTo("STOPPED");

        ArgumentCaptor<Voucher> captor = ArgumentCaptor.forClass(Voucher.class);
        verify(fixture.voucherRepository).save(captor.capture());
        Voucher saved = captor.getValue();
        assertThat(saved.isActive()).isFalse();
        assertThat(saved.getStatus()).isEqualTo("STOPPED");
    }
}
