---
name: unit-test-getShippingFeeForProvince-condition-coverage
description: Unit tests with 100% condition coverage for ShippingFeeConfigService.getShippingFeeForProvince methods.
---

## Infomation
Service Name: ShippingFeeConfigService
Method Name: getShippingFeeForProvince(String province) and getShippingFeeForProvince(String provinceCode, String province)
Mock class: 
   1. ShippingFeeConfigRepository shippingFeeConfigRepository

Mock Data:
   - provinceCode: "79"
   - provinceName: "TP. Hồ Chí Minh"
   - provinceKey: "tp ho chi minh"

## Testcase
### Testcase 1
Short Description: Test getShippingFeeForProvince (Single arg) fails if not configured.
Input: provinceKey = "unknown", repository returns empty.
Expected Output: BusinessException (400, "Shipping fee is not configured for province: unknown")
Actual Output: BusinessException (400, "Shipping fee is not configured for province: unknown")

### Testcase 2
Short Description: Test getShippingFeeForProvince (Two args) by Code succeeds.
Input: provinceCode = "79", province = "TP. HCM".
Expected Output: BigDecimal value from config.
Actual Output: BigDecimal value from config.

### Testcase 3
Short Description: Test getShippingFeeForProvince (Two args) by Code fails if not in DB.
Input: provinceCode = "99", repository returns empty.
Expected Output: BusinessException (400, "Shipping fee is not configured for province: TP. HCM")
Actual Output: BusinessException (400, "Shipping fee is not configured for province: TP. HCM")

### Testcase 4
Short Description: Test getShippingFeeForProvince (Two args) falls back to Name when Code is null or blank.
Input: provinceCode = null, province = "Đồng Nai".
Expected Output: Calls repository.findByProvinceKey("dong nai").
Actual Output: Calls repository.findByProvinceKey("dong nai").

## Code of Test Case
```java
@Test
void getShippingFeeForProvince_ByName_NotFound_ThrowsBadRequest() {
    when(shippingFeeConfigRepository.findByProvinceKey("unknown")).thenReturn(Optional.empty());
    
    assertThrows(BusinessException.class, () -> 
        shippingFeeConfigService.getShippingFeeForProvince("unknown"));
}

@Test
void getShippingFeeForProvince_ByCode_Succeeds() {
    ShippingFeeConfig cfg = new ShippingFeeConfig(); cfg.setShippingFee(new BigDecimal("20000"));
    when(shippingFeeConfigRepository.findByProvinceCode("79")).thenReturn(Optional.of(cfg));
    
    BigDecimal fee = shippingFeeConfigService.getShippingFeeForProvince("79", "TP. HCM");
    assertEquals(new BigDecimal("20000"), fee);
}

@Test
void getShippingFeeForProvince_ByCode_NotFound_ThrowsBadRequest() {
    when(shippingFeeConfigRepository.findByProvinceCode("99")).thenReturn(Optional.empty());
    
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        shippingFeeConfigService.getShippingFeeForProvince("99", "Random Province"));
    assertTrue(exception.getMessage().contains("Shipping fee is not configured"));
}

@Test
void getShippingFeeForProvince_NullCode_FallsBackToName() {
    ShippingFeeConfig cfg = new ShippingFeeConfig(); cfg.setShippingFee(new BigDecimal("30000"));
    when(shippingFeeConfigRepository.findByProvinceKey("dong nai")).thenReturn(Optional.of(cfg));
    
    BigDecimal fee = shippingFeeConfigService.getShippingFeeForProvince(null, "Đồng Nai");
    assertEquals(new BigDecimal("30000"), fee);
    verify(shippingFeeConfigRepository).findByProvinceKey("dong nai");
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
