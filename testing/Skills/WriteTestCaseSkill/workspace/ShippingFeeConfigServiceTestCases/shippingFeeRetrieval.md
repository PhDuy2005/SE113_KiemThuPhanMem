---
name: unit-test-shipping-fee-retrieval-condition-coverage
description: Unit tests with 100% condition coverage for ShippingFeeConfigService retrieval logic.
---

## Infomation
Service Name: ShippingFeeConfigService
Method Name: getShippingFees() and getShippingFeeForProvince(...)
Mock class: 
   1. ShippingFeeConfigRepository shippingFeeConfigRepository
   2. SecurityUtil (Static mock)

Mock Data:
   - provinceCode: "79"
   - provinceKey: "tp ho chi minh"
   - shippingFee: 20000.00

## Testcase
### Testcase 1
Short Description: Test getShippingFeeForProvince by Code fails if not configured.
Input: provinceCode = "79", repository returns empty.
Expected Output: BusinessException (400, "Shipping fee is not configured for province: TP. Hồ Chí Minh")
Actual Output: BusinessException (400, "Shipping fee is not configured for province: TP. Hồ Chí Minh")

### Testcase 2
Short Description: Test getShippingFeeForProvince by Code succeeds.
Input: provinceCode = "79".
Expected Output: BigDecimal(20000.00).
Actual Output: BigDecimal(20000.00).

### Testcase 3
Short Description: Test getShippingFeeForProvince by Name (fallback) when code is blank.
Input: provinceCode = "", province = "Đồng Nai".
Expected Output: Calls repository.findByProvinceKey("dong nai").
Actual Output: Calls repository.findByProvinceKey("dong nai").

### Testcase 4
Short Description: Test getShippingFees listing order.
Input: Repository returns list.
Expected Output: Calls findAllByOrderByProvinceAsc.
Actual Output: Calls findAllByOrderByProvinceAsc.

## Code of Test Case
```java
@Test
void getShippingFeeForProvince_ByCode_NotFound_ThrowsBadRequest() {
    when(shippingFeeConfigRepository.findByProvinceCode("79")).thenReturn(Optional.empty());
    
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        shippingFeeConfigService.getShippingFeeForProvince("79", "TP. HCM"));
    assertTrue(exception.getMessage().contains("Shipping fee is not configured"));
}

@Test
void getShippingFeeForProvince_ByCode_Succeeds() {
    ShippingFeeConfig config = new ShippingFeeConfig();
    config.setShippingFee(new BigDecimal("20000.00"));
    when(shippingFeeConfigRepository.findByProvinceCode("79")).thenReturn(Optional.of(config));
    
    BigDecimal fee = shippingFeeConfigService.getShippingFeeForProvince("79", "TP. HCM");
    assertEquals(0, fee.compareTo(new BigDecimal("20000.00")));
}

@Test
void getShippingFeeForProvince_FallbackToName_Succeeds() {
    ShippingFeeConfig config = new ShippingFeeConfig();
    config.setShippingFee(new BigDecimal("30000.00"));
    when(shippingFeeConfigRepository.findByProvinceKey("dong nai")).thenReturn(Optional.of(config));
    
    BigDecimal fee = shippingFeeConfigService.getShippingFeeForProvince("", "Đồng Nai");
    assertEquals(0, fee.compareTo(new BigDecimal("30000.00")));
}

@Test
void getShippingFees_Listing_Succeeds() {
    mockAdminAccess();
    when(shippingFeeConfigRepository.findAllByOrderByProvinceAsc()).thenReturn(List.of(new ShippingFeeConfig()));
    
    List<ResShippingFeeDTO> result = shippingFeeConfigService.getShippingFees();
    assertFalse(result.isEmpty());
    verify(shippingFeeConfigRepository).findAllByOrderByProvinceAsc();
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
