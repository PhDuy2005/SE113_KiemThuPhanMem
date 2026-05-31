package com.uit.nhom7.KiemThuPhanMem.service.checkout_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqApplyVoucherDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResVoucherApplicationDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Voucher;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00022_applyVoucher extends CheckoutServiceTestBase {

    private MockedStatic<SecurityUtil> mockedSecurityUtil;
    private Fixture fixture;

    private final UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID voucherId = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
        mockedSecurityUtil = mockStatic(SecurityUtil.class);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtil.close();
    }

    private User getActiveUser() {
        return User.builder()
                .id(userId)
                .email("user@example.com")
                .accountStatus("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("FUNC00022_UTCID01 - Fails because user is not authenticated")
    void TC1_NoLogin() {
        ReqApplyVoucherDTO request = new ReqApplyVoucherDTO();
        request.setVoucherCode("VOUCHER10");
        request.setTotalOrderAmount(BigDecimal.valueOf(100000));

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.checkoutService.applyVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00022_UTCID02 - Fails because email is not in DB")
    void TC2_NoSession() {
        ReqApplyVoucherDTO request = new ReqApplyVoucherDTO();
        request.setVoucherCode("VOUCHER10");
        request.setTotalOrderAmount(BigDecimal.valueOf(100000));

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.checkoutService.applyVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00022_UTCID03 - Fails because user accountStatus is null")
    void TC3_StatusNull() {
        ReqApplyVoucherDTO request = new ReqApplyVoucherDTO();
        request.setVoucherCode("VOUCHER10");
        request.setTotalOrderAmount(BigDecimal.valueOf(100000));

        User user = User.builder().id(userId).accountStatus(null).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.checkoutService.applyVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00022_UTCID04 - Fails because user is not ACTIVE")
    void TC4_NotActive() {
        ReqApplyVoucherDTO request = new ReqApplyVoucherDTO();
        request.setVoucherCode("VOUCHER10");
        request.setTotalOrderAmount(BigDecimal.valueOf(100000));

        User user = User.builder().id(userId).accountStatus("INACTIVE").build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.checkoutService.applyVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00022_UTCID05 - Fails because voucher is not found")
    void TC5_VoucherNotFound() {
        ReqApplyVoucherDTO request = new ReqApplyVoucherDTO();
        request.setVoucherCode("VOUCHER10");
        request.setTotalOrderAmount(BigDecimal.valueOf(100000));

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.checkoutService.applyVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired voucher")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00022_UTCID06 - Fails because voucher is not active")
    void TC6_NotActiveVoucher() {
        ReqApplyVoucherDTO request = new ReqApplyVoucherDTO();
        request.setVoucherCode("VOUCHER10");
        request.setTotalOrderAmount(BigDecimal.valueOf(100000));

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Voucher voucher = Voucher.builder().id(voucherId).code("VOUCHER10").active(false).build();
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));

        assertThatThrownBy(() -> fixture.checkoutService.applyVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired voucher")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00022_UTCID07 - Fails because start date is in the future")
    void TC7_StartInFuture() {
        ReqApplyVoucherDTO request = new ReqApplyVoucherDTO();
        request.setVoucherCode("VOUCHER10");
        request.setTotalOrderAmount(BigDecimal.valueOf(100000));

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Voucher voucher = Voucher.builder().id(voucherId).code("VOUCHER10").active(true)
                .startDate(Instant.parse("3000-01-01T00:00:00Z")).build();
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));

        assertThatThrownBy(() -> fixture.checkoutService.applyVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired voucher")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00022_UTCID08 - Fails because end date is in the past")
    void TC8_EndInPast() {
        ReqApplyVoucherDTO request = new ReqApplyVoucherDTO();
        request.setVoucherCode("VOUCHER10");
        request.setTotalOrderAmount(BigDecimal.valueOf(100000));

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Voucher voucher = Voucher.builder().id(voucherId).code("VOUCHER10").active(true)
                .endDate(Instant.parse("2000-01-01T00:00:00Z")).build();
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));

        assertThatThrownBy(() -> fixture.checkoutService.applyVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired voucher")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00022_UTCID09 - Fails because max usage is reached")
    void TC9_UsageReached() {
        ReqApplyVoucherDTO request = new ReqApplyVoucherDTO();
        request.setVoucherCode("VOUCHER10");
        request.setTotalOrderAmount(BigDecimal.valueOf(100000));

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Voucher voucher = Voucher.builder().id(voucherId).code("VOUCHER10").active(true)
                .maxUsage(100).usedCount(100).build();
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));

        assertThatThrownBy(() -> fixture.checkoutService.applyVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired voucher")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00022_UTCID10 - Fails because total < minOrderAmount")
    void TC10_TotalLessMin() {
        ReqApplyVoucherDTO request = new ReqApplyVoucherDTO();
        request.setVoucherCode("VOUCHER10");
        request.setTotalOrderAmount(BigDecimal.valueOf(100000));

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Voucher voucher = Voucher.builder().id(voucherId).code("VOUCHER10").active(true)
                .minOrderAmount(BigDecimal.valueOf(200000)).build();
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));

        assertThatThrownBy(() -> fixture.checkoutService.applyVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired voucher")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00022_UTCID11 - Success (0% discount due to null value)")
    void TC11_PercentNull() {
        ReqApplyVoucherDTO request = new ReqApplyVoucherDTO();
        request.setVoucherCode("VOUCHER10");
        request.setTotalOrderAmount(BigDecimal.valueOf(100000));

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Voucher voucher = Voucher.builder().id(voucherId).code("VOUCHER10").active(true)
                .type(Voucher.PERCENT_TYPE).value(null).build();
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));

        ResVoucherApplicationDTO result = fixture.checkoutService.applyVoucher(request);

        assertThat(result).isNotNull();
        assertThat(result.getVoucherCode()).isEqualTo("VOUCHER10");
        assertThat(result.getDiscountAmount().doubleValue()).isEqualTo(0.0);
        assertThat(result.getFinalTotal().doubleValue()).isEqualTo(100000.0);
        assertThat(result.getMessage()).isEqualTo("Voucher applied successfully");
    }

    @Test
    @DisplayName("FUNC00022_UTCID12 - Success (10% discount)")
    void TC12_PercentNormal() {
        ReqApplyVoucherDTO request = new ReqApplyVoucherDTO();
        request.setVoucherCode("VOUCHER10");
        request.setTotalOrderAmount(BigDecimal.valueOf(100000));

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Voucher voucher = Voucher.builder().id(voucherId).code("VOUCHER10").active(true)
                .startDate(Instant.parse("2000-01-01T00:00:00Z"))
                .endDate(Instant.parse("3000-01-01T00:00:00Z"))
                .maxUsage(100)
                .usedCount(null)
                .minOrderAmount(BigDecimal.valueOf(50000))
                .type(Voucher.PERCENT_TYPE)
                .value(BigDecimal.valueOf(10))
                .build();
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));

        ResVoucherApplicationDTO result = fixture.checkoutService.applyVoucher(request);

        assertThat(result).isNotNull();
        assertThat(result.getVoucherCode()).isEqualTo("VOUCHER10");
        assertThat(result.getDiscountAmount().doubleValue()).isEqualTo(10000.0);
        assertThat(result.getFinalTotal().doubleValue()).isEqualTo(90000.0);
        assertThat(result.getMessage()).isEqualTo("Voucher applied successfully");
    }

    @Test
    @DisplayName("FUNC00022_UTCID13 - Success (0 fixed discount due to null)")
    void TC13_FixedNull() {
        ReqApplyVoucherDTO request = new ReqApplyVoucherDTO();
        request.setVoucherCode("VOUCHER10");
        request.setTotalOrderAmount(BigDecimal.valueOf(100000));

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Voucher voucher = Voucher.builder().id(voucherId).code("VOUCHER10").active(true)
                .startDate(Instant.parse("2000-01-01T00:00:00Z"))
                .endDate(Instant.parse("3000-01-01T00:00:00Z"))
                .maxUsage(100)
                .usedCount(null)
                .minOrderAmount(BigDecimal.valueOf(50000))
                .type(Voucher.FIXED_TYPE)
                .value(null)
                .build();
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));

        ResVoucherApplicationDTO result = fixture.checkoutService.applyVoucher(request);

        assertThat(result).isNotNull();
        assertThat(result.getVoucherCode()).isEqualTo("VOUCHER10");
        assertThat(result.getDiscountAmount().doubleValue()).isEqualTo(0.0);
        assertThat(result.getFinalTotal().doubleValue()).isEqualTo(100000.0);
        assertThat(result.getMessage()).isEqualTo("Voucher applied successfully");
    }

    @Test
    @DisplayName("FUNC00022_UTCID14 - Success (20000 fixed discount)")
    void TC14_FixedNormal() {
        ReqApplyVoucherDTO request = new ReqApplyVoucherDTO();
        request.setVoucherCode("VOUCHER10");
        request.setTotalOrderAmount(BigDecimal.valueOf(100000));

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Voucher voucher = Voucher.builder().id(voucherId).code("VOUCHER10").active(true)
                .startDate(Instant.parse("2000-01-01T00:00:00Z"))
                .endDate(Instant.parse("3000-01-01T00:00:00Z"))
                .maxUsage(100)
                .usedCount(50)
                .minOrderAmount(BigDecimal.valueOf(50000))
                .type(Voucher.FIXED_TYPE)
                .value(BigDecimal.valueOf(20000))
                .build();
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));

        ResVoucherApplicationDTO result = fixture.checkoutService.applyVoucher(request);

        assertThat(result).isNotNull();
        assertThat(result.getVoucherCode()).isEqualTo("VOUCHER10");
        assertThat(result.getDiscountAmount().doubleValue()).isEqualTo(20000.0);
        assertThat(result.getFinalTotal().doubleValue()).isEqualTo(80000.0);
        assertThat(result.getMessage()).isEqualTo("Voucher applied successfully");
    }

    @Test
    @DisplayName("FUNC00022_UTCID15 - Fails because voucher type is invalid")
    void TC15_InvalidType() {
        ReqApplyVoucherDTO request = new ReqApplyVoucherDTO();
        request.setVoucherCode("VOUCHER10");
        request.setTotalOrderAmount(BigDecimal.valueOf(100000));

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Voucher voucher = Voucher.builder().id(voucherId).code("VOUCHER10").active(true)
                .startDate(Instant.parse("2000-01-01T00:00:00Z"))
                .endDate(Instant.parse("3000-01-01T00:00:00Z"))
                .maxUsage(100)
                .usedCount(50)
                .minOrderAmount(BigDecimal.valueOf(50000))
                .type("UNKNOWN")
                .value(BigDecimal.valueOf(20000))
                .build();
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));

        assertThatThrownBy(() -> fixture.checkoutService.applyVoucher(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired voucher")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
