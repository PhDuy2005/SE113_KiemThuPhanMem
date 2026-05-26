---
name: unit-test-applyVoucher-condition-coverage
description: Unit tests with 100% condition coverage for CheckoutService.applyVoucher method.
---

## Infomation
Service Name: CheckoutService
Method Name: applyVoucher(ReqApplyVoucherDTO request)
Mock class: 
   1. VoucherRepository voucherRepository
   2. UserRepository userRepository
   3. SecurityUtil (Static mock)

Mock Data:
   - voucherCode: "SAVE10"
   - percentVoucher: Voucher { code: "SAVE10", type: "PERCENT", value: 10.0, active: true, minOrderAmount: 100.0 }
   - fixedVoucher: Voucher { code: "FIXED20", type: "FIXED", value: 20.0, active: true }

## Testcase
### Testcase 1
Short Description: Test applyVoucher fails when voucher code not found.
Input: voucherCode = "INVALID", totalOrderAmount = 200.0, voucherRepository returns empty.
Expected Output: BusinessException (400, "Invalid or expired voucher")
Actual Output: BusinessException (400, "Invalid or expired voucher")

### Testcase 2
Short Description: Test applyVoucher fails when voucher is inactive.
Input: voucherCode = "INACTIVE", active = false.
Expected Output: BusinessException (400, "Invalid or expired voucher")
Actual Output: BusinessException (400, "Invalid or expired voucher")

### Testcase 3
Short Description: Test applyVoucher fails when now is before startDate.
Input: voucherCode = "FUTURE", startDate = tomorrow.
Expected Output: BusinessException (400, "Invalid or expired voucher")
Actual Output: BusinessException (400, "Invalid or expired voucher")

### Testcase 4
Short Description: Test applyVoucher fails when now is after endDate.
Input: voucherCode = "EXPIRED", endDate = yesterday.
Expected Output: BusinessException (400, "Invalid or expired voucher")
Actual Output: BusinessException (400, "Invalid or expired voucher")

### Testcase 5
Short Description: Test applyVoucher fails when maxUsage is reached.
Input: voucherCode = "FULL", maxUsage = 10, usedCount = 10.
Expected Output: BusinessException (400, "Invalid or expired voucher")
Actual Output: BusinessException (400, "Invalid or expired voucher")

### Testcase 6
Short Description: Test applyVoucher fails when totalOrderAmount < minOrderAmount.
Input: voucherCode = "MIN500", minOrderAmount = 500.0, totalOrderAmount = 200.0.
Expected Output: BusinessException (400, "Invalid or expired voucher")
Actual Output: BusinessException (400, "Invalid or expired voucher")

### Testcase 7
Short Description: Test applyVoucher succeeds with PERCENT type.
Input: voucherCode = "SAVE10", type = "PERCENT", value = 10, totalOrderAmount = 200.0.
Expected Output: ResVoucherApplicationDTO with discountAmount = 20.0, finalTotal = 180.0.
Actual Output: ResVoucherApplicationDTO with discountAmount = 20.0, finalTotal = 180.0.

### Testcase 8
Short Description: Test applyVoucher succeeds with FIXED type.
Input: voucherCode = "FIXED20", type = "FIXED", value = 20, totalOrderAmount = 200.0.
Expected Output: ResVoucherApplicationDTO with discountAmount = 20.0, finalTotal = 180.0.
Actual Output: ResVoucherApplicationDTO with discountAmount = 20.0, finalTotal = 180.0.

### Testcase 9
Short Description: Test applyVoucher fails with unknown type.
Input: voucherCode = "MAGIC", type = "UNKNOWN".
Expected Output: BusinessException (400, "Invalid or expired voucher")
Actual Output: BusinessException (400, "Invalid or expired voucher")

## Code of Test Case
```java
@Test
void applyVoucher_VoucherNotFound_ThrowsBadRequest() {
    // Arrange
    mockActiveUser();
    ReqApplyVoucherDTO req = new ReqApplyVoucherDTO();
    req.setVoucherCode("INVALID");
    req.setTotalOrderAmount(new BigDecimal("200.00"));
    when(voucherRepository.findByCodeIgnoreCase("INVALID")).thenReturn(Optional.empty());

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        checkoutService.applyVoucher(req));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
}

@Test
void applyVoucher_InactiveVoucher_ThrowsBadRequest() {
    // Arrange
    mockActiveUser();
    Voucher voucher = new Voucher();
    voucher.setActive(false);
    when(voucherRepository.findByCodeIgnoreCase("INACTIVE")).thenReturn(Optional.of(voucher));

    ReqApplyVoucherDTO req = new ReqApplyVoucherDTO();
    req.setVoucherCode("INACTIVE");
    req.setTotalOrderAmount(new BigDecimal("200.00"));

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        checkoutService.applyVoucher(req));
    assertEquals("Invalid or expired voucher", exception.getMessage());
}

@Test
void applyVoucher_PercentType_CalculatesCorrectly() {
    // Arrange
    mockActiveUser();
    Voucher voucher = new Voucher();
    voucher.setCode("SAVE10");
    voucher.setType("PERCENT");
    voucher.setValue(new BigDecimal("10"));
    voucher.setActive(true);
    when(voucherRepository.findByCodeIgnoreCase("SAVE10")).thenReturn(Optional.of(voucher));

    ReqApplyVoucherDTO req = new ReqApplyVoucherDTO();
    req.setVoucherCode("SAVE10");
    req.setTotalOrderAmount(new BigDecimal("200.00"));

    // Act
    ResVoucherApplicationDTO result = checkoutService.applyVoucher(req);

    // Assert
    assertEquals(new BigDecimal("20.00"), result.getDiscountAmount());
    assertEquals(new BigDecimal("180.00"), result.getFinalTotal());
}

@Test
void applyVoucher_FixedType_CalculatesCorrectly() {
    // Arrange
    mockActiveUser();
    Voucher voucher = new Voucher();
    voucher.setCode("FIXED50");
    voucher.setType("FIXED");
    voucher.setValue(new BigDecimal("50"));
    voucher.setActive(true);
    when(voucherRepository.findByCodeIgnoreCase("FIXED50")).thenReturn(Optional.of(voucher));

    ReqApplyVoucherDTO req = new ReqApplyVoucherDTO();
    req.setVoucherCode("FIXED50");
    req.setTotalOrderAmount(new BigDecimal("200.00"));

    // Act
    ResVoucherApplicationDTO result = checkoutService.applyVoucher(req);

    // Assert
    assertEquals(new BigDecimal("50.00"), result.getDiscountAmount());
    assertEquals(new BigDecimal("150.00"), result.getFinalTotal());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
