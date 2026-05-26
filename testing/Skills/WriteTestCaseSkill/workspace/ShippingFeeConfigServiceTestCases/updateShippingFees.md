---
name: unit-test-updateShippingFees-condition-coverage
description: Unit tests with 100% condition coverage for ShippingFeeConfigService.updateShippingFees method.
---

## Infomation
Service Name: ShippingFeeConfigService
Method Name: updateShippingFees(List<ReqUpdateShippingFeeDTO> request)
Mock class: 
   1. ShippingFeeConfigRepository shippingFeeConfigRepository
   2. AddressDataService addressDataService
   3. SecurityUtil (Static mock)
   4. UserRepository userRepository

Mock Data:
   - provinceCode: "79"
   - provinceName: "TP. Hồ Chí Minh"
   - currentConfigs: Map of existing configurations.

## Testcase
### Testcase 1
Short Description: Test updateShippingFees fails when request is null or empty.
Input: request = null or request = [].
Expected Output: BusinessException (400, "Province and shipping fee are required")
Actual Output: BusinessException (400, "Province and shipping fee are required")

### Testcase 2
Short Description: Test updateShippingFees handles new province configuration.
Input: Province key not in currentConfigs.
Expected Output: Repository save called with a newly built ShippingFeeConfig.
Actual Output: Repository save called with a newly built ShippingFeeConfig.

### Testcase 3
Short Description: Test updateShippingFees updates existing province configuration.
Input: Province key already in currentConfigs.
Expected Output: Repository save called with the existing config object updated with new fee.
Actual Output: Repository save called with the existing config object updated with new fee.

## Code of Test Case
```java
@Test
void updateShippingFees_EmptyRequest_ThrowsBadRequest() {
    mockAdminAccess();
    assertThrows(BusinessException.class, () -> 
        shippingFeeConfigService.updateShippingFees(List.of()));
}

@Test
void updateShippingFees_NewProvince_SavesNewConfig() {
    // Arrange
    mockAdminAccess();
    ReqUpdateShippingFeeDTO item = new ReqUpdateShippingFeeDTO();
    item.setProvinceCode("79");
    item.setProvince("TP. Hồ Chí Minh");
    item.setShippingFee("25000");

    ResProvinceDTO pDTO = new ResProvinceDTO(); pDTO.setCode("79"); pDTO.setName("TP. Hồ Chí Minh");
    when(addressDataService.requireProvince(any(), any())).thenReturn(pDTO);
    when(shippingFeeConfigRepository.findAll()).thenReturn(List.of()); // No configs yet

    // Act
    shippingFeeConfigService.updateShippingFees(List.of(item));

    // Assert
    verify(shippingFeeConfigRepository).save(argThat(cfg -> 
        cfg.getProvinceCode().equals("79") && 
        cfg.getProvince().equals("TP. Hồ Chí Minh") &&
        cfg.getShippingFee().doubleValue() == 25000.0));
}

@Test
void updateShippingFees_ExistingProvince_UpdatesConfig() {
    // Arrange
    mockAdminAccess();
    ReqUpdateShippingFeeDTO item = new ReqUpdateShippingFeeDTO();
    item.setProvince("TP. Hồ Chí Minh");
    item.setShippingFee("30000");

    ResProvinceDTO pDTO = new ResProvinceDTO(); pDTO.setCode("79"); pDTO.setName("TP. Hồ Chí Minh");
    when(addressDataService.requireProvince(any(), any())).thenReturn(pDTO);
    
    ShippingFeeConfig existing = new ShippingFeeConfig();
    existing.setProvinceKey("tp ho chi minh");
    existing.setShippingFee(new BigDecimal("20000"));
    when(shippingFeeConfigRepository.findAll()).thenReturn(List.of(existing));

    // Act
    shippingFeeConfigService.updateShippingFees(List.of(item));

    // Assert
    assertEquals(new BigDecimal("30000.00"), existing.getShippingFee());
    verify(shippingFeeConfigRepository).save(existing);
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
