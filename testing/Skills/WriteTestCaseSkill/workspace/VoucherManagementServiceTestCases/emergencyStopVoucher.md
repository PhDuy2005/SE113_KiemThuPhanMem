---
name: unit-test-emergencyStopVoucher-condition-coverage
description: Unit tests with 100% condition coverage for VoucherManagementService.emergencyStopVoucher method.
---

## Infomation
Service Name: VoucherManagementService
Method Name: emergencyStopVoucher(UUID voucherId)
Mock class: 
   1. VoucherRepository voucherRepository
   2. SecurityUtil (Static mock)
   3. UserRepository userRepository

Mock Data:
   - voucherId: "550e8400-e29b-41d4-a716-446655440001"
   - existingVoucher: Voucher { id: voucherId, code: "SUMMER", active: true, status: "ACTIVE" }

## Testcase
### Testcase 1
Short Description: Test emergencyStopVoucher fails when voucher is not found.
Input: voucherRepository.findById returns empty.
Expected Output: BusinessException (404, "Voucher not found")
Actual Output: BusinessException (404, "Voucher not found")

### Testcase 2
Short Description: Test emergencyStopVoucher succeeds and updates status.
Input: Valid voucherId for an active voucher.
Expected Output: active = false, status = "STOPPED", success message returned.
Actual Output: active = false, status = "STOPPED", success message returned.

## Code of Test Case
```java
@Test
void emergencyStopVoucher_NotFound_ThrowsNotFound() {
    mockAdminAccess();
    UUID id = UUID.randomUUID();
    when(voucherRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> voucherManagementService.emergencyStopVoucher(id));
}

@Test
void emergencyStopVoucher_Valid_Succeeds() {
    // Arrange
    mockAdminAccess();
    UUID id = UUID.randomUUID();
    Voucher voucher = new Voucher();
    voucher.setId(id);
    voucher.setActive(true);
    voucher.setStatus("ACTIVE");
    
    when(voucherRepository.findById(id)).thenReturn(Optional.of(voucher));
    when(voucherRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    // Act
    ResVoucherDTO result = voucherManagementService.emergencyStopVoucher(id);

    // Assert
    assertFalse(result.isActive());
    assertEquals("STOPPED", result.getStatus());
    assertEquals("Voucher stopped successfully", result.getMessage());
    verify(voucherRepository).save(voucher);
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
