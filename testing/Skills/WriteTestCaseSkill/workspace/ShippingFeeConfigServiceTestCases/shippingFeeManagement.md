---
name: unit-test-shipping-fee-management-condition-coverage
description: Unit tests with 100% condition coverage for ShippingFeeConfigService update and validation logic.
---

## Infomation
Service Name: ShippingFeeConfigService
Method Name: updateShippingFees(List<ReqUpdateShippingFeeDTO>) and validations
Mock class: 
   1. ShippingFeeConfigRepository shippingFeeConfigRepository
   2. AddressDataService addressDataService
   3. UserRepository userRepository
   4. SecurityUtil (Static mock)

Mock Data:
   - provinceCode: "79"
   - provinceName: "TP. Hồ Chí Minh"

## Testcase
### Testcase 1
Short Description: Test updateShippingFees fails for empty request.
Input: request = empty list.
Expected Output: BusinessException (400, "Province and shipping fee are required")
Actual Output: BusinessException (400, "Province and shipping fee are required")

### Testcase 2
Short Description: Test requireProvince fails for missing province info.
Input: item.provinceCode = null, item.province = "".
Expected Output: BusinessException (400, "Province and shipping fee are required")
Actual Output: BusinessException (400, "Province and shipping fee are required")

### Testcase 3
Short Description: Test parseShippingFee fails for negative amount.
Input: value = "-15000".
Expected Output: BusinessException (400, "Shipping fee must not be negative")
Actual Output: BusinessException (400, "Shipping fee must not be negative")

### Testcase 4
Short Description: Test parseShippingFee fails for invalid decimal format.
Input: value = "15000.123" (3 decimals).
Expected Output: BusinessException (400, "Shipping fee must be a valid amount with at most 2 decimal places")
Actual Output: BusinessException (400, "Shipping fee must be a valid amount with at most 2 decimal places")

### Testcase 5
Short Description: Test normalizeProvinceKey handles accents and spaces correctly.
Input: province = "  Hồ   Chí   Minh  ".
Expected Output: "ho chi minh"
Actual Output: "ho chi minh"

### Testcase 6
Short Description: Test updateShippingFees creates new config for unknown province.
Input: Valid request for new province.
Expected Output: Repository save called with new ShippingFeeConfig object.
Actual Output: Repository save called with new ShippingFeeConfig object.

## Code of Test Case
```java
@Test
void requireProvince_MissingFields_ThrowsBadRequest() {
    ReqUpdateShippingFeeDTO item = new ReqUpdateShippingFeeDTO();
    item.setProvinceCode("");
    item.setProvince(null);

    assertThrows(BusinessException.class, () -> 
        ReflectionTestUtils.invokeMethod(shippingFeeConfigService, "requireProvince", item));
}

@Test
void parseShippingFee_Negative_ThrowsBadRequest() {
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        ReflectionTestUtils.invokeMethod(shippingFeeConfigService, "parseShippingFee", "-1000"));
    assertEquals("Shipping fee must not be negative", exception.getMessage());
}

@Test
void parseShippingFee_InvalidDecimals_ThrowsBadRequest() {
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        ReflectionTestUtils.invokeMethod(shippingFeeConfigService, "parseShippingFee", "10.123"));
    assertEquals("Shipping fee must be a valid amount with at most 2 decimal places", exception.getMessage());
}

@Test
void normalizeProvinceKey_AccentsAndSpaces_Succeeds() {
    String result = ReflectionTestUtils.invokeMethod(shippingFeeConfigService, "normalizeProvinceKey", "  Hồ  Chí  Minh  ");
    assertEquals("ho chi minh", result);
    
    String result2 = ReflectionTestUtils.invokeMethod(shippingFeeConfigService, "normalizeProvinceKey", "Đồng Nai");
    assertEquals("dong nai", result2);
}

@Test
void updateShippingFees_NewConfig_Saves() {
    mockAdminAccess();
    ReqUpdateShippingFeeDTO item = new ReqUpdateShippingFeeDTO();
    item.setProvinceCode("79");
    item.setProvince("TP. Hồ Chí Minh");
    item.setShippingFee("25000");
    
    ResProvinceDTO pDTO = new ResProvinceDTO(); pDTO.setCode("79"); pDTO.setName("TP. Hồ Chí Minh");
    when(addressDataService.requireProvince(any(), any())).thenReturn(pDTO);
    when(shippingFeeConfigRepository.findAll()).thenReturn(List.of());
    
    shippingFeeConfigRepository.save(any());
    
    shippingFeeConfigService.updateShippingFees(List.of(item));
    
    verify(shippingFeeConfigRepository).save(argThat(cfg -> cfg.getProvinceCode().equals("79") && cfg.getShippingFee().doubleValue() == 25000.0));
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
