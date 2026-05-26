---
name: unit-test-createVoucher-condition-coverage
description: Unit tests with 100% condition coverage for VoucherManagementService.createVoucher method.
---

## Infomation
Service Name: VoucherManagementService
Method Name: createVoucher(ReqCreateVoucherDTO request)
Mock class: 
   1. VoucherRepository voucherRepository
   2. UserRepository userRepository
   3. SecurityUtil (Static mock)

Mock Data:
   - voucherCode: "SUMMER2024"
   - startDate: Today
   - endDate: Tomorrow

## Testcase
### Testcase 1
Short Description: Test validateCreateVoucherRequest fails for missing fields.
Input: request.voucherCode = null.
Expected Output: BusinessException (400, "Required field is missing")
Actual Output: BusinessException (400, "Required field is missing")

### Testcase 2
Short Description: Test validateCreateVoucherRequest fails when endDate is not after startDate.
Input: startDate = Today, endDate = Today.
Expected Output: BusinessException (400, "End date must be after start date")
Actual Output: BusinessException (400, "End date must be after start date")

### Testcase 3
Short Description: Test validateCreateVoucherRequest fails for non-positive values.
Input: discountValue = 0, or quantity = -1.
Expected Output: BusinessException (400, "Discount value and quantity must be greater than 0")
Actual Output: BusinessException (400, "Discount value and quantity must be greater than 0")

### Testcase 4
Short Description: Test normalizeDiscountType fails for invalid type.
Input: discountType = "UNKNOWN".
Expected Output: BusinessException (400, "discountType must be FIXED or PERCENT")
Actual Output: BusinessException (400, "discountType must be FIXED or PERCENT")

### Testcase 5
Short Description: Test createVoucher fails if code already exists.
Input: voucherRepository.existsByCodeIgnoreCase("SUMMER2024") returns true.
Expected Output: BusinessException (409, "Voucher code already exists")
Actual Output: BusinessException (409, "Voucher code already exists")

### Testcase 6
Short Description: Test createVoucher sets status to ACTIVE when it starts today.
Input: startDate = Today.
Expected Output: Voucher object with active = true, status = "ACTIVE".
Actual Output: Voucher object with active = true, status = "ACTIVE".

### Testcase 7
Short Description: Test createVoucher sets status to SCHEDULED when it starts in future.
Input: startDate = Tomorrow.
Expected Output: Voucher object with active = false, status = "SCHEDULED".
Actual Output: Voucher object with active = false, status = "SCHEDULED".

## Code of Test Case
```java
@Test
void validateCreateVoucherRequest_DatesInvalid_ThrowsBadRequest() {
    ReqCreateVoucherDTO req = new ReqCreateVoucherDTO();
    Instant now = Instant.now();
    req.setVoucherCode("CODE"); req.setDiscountValue(BigDecimal.TEN); req.setDiscountType("FIXED");
    req.setQuantity(10); req.setStartDate(now); req.setEndDate(now); // Same date

    BusinessException exception = assertThrows(BusinessException.class, () -> 
        ReflectionTestUtils.invokeMethod(voucherManagementService, "validateCreateVoucherRequest", req));
    assertEquals("End date must be after start date", exception.getMessage());
}

@Test
void normalizeDiscountType_Invalid_ThrowsBadRequest() {
    assertThrows(BusinessException.class, () -> 
        ReflectionTestUtils.invokeMethod(voucherManagementService, "normalizeDiscountType", "INVALID"));
}

@Test
void createVoucher_DuplicateCode_ThrowsConflict() {
    mockAdminAccess();
    ReqCreateVoucherDTO req = new ReqCreateVoucherDTO();
    req.setVoucherCode("SUMMER2024");
    req.setDiscountValue(BigDecimal.TEN); req.setDiscountType("FIXED");
    req.setQuantity(10); req.setStartDate(Instant.now()); req.setEndDate(Instant.now().plusSeconds(86400));

    when(voucherRepository.existsByCodeIgnoreCase("SUMMER2024")).thenReturn(true);

    assertThrows(BusinessException.class, () -> voucherManagementService.createVoucher(req));
}

@Test
void createVoucher_StartsToday_StatusActive() {
    // Arrange
    mockAdminAccess();
    Instant now = Instant.now();
    ReqCreateVoucherDTO req = new ReqCreateVoucherDTO();
    req.setVoucherCode("NEW2024");
    req.setDiscountValue(BigDecimal.TEN); req.setDiscountType("FIXED");
    req.setQuantity(100); req.setStartDate(now); req.setEndDate(now.plusSeconds(86400));

    when(voucherRepository.existsByCodeIgnoreCase(anyString())).thenReturn(false);
    when(voucherRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    // Act
    ResVoucherDTO result = voucherManagementService.createVoucher(req);

    // Assert
    assertEquals("ACTIVE", result.getStatus());
    assertTrue(result.isActive());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
