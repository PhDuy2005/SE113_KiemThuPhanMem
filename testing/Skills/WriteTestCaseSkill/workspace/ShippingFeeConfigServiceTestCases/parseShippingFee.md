---
name: unit-test-parseShippingFee-condition-coverage
description: Unit tests with 100% condition coverage for ShippingFeeConfigService.parseShippingFee method.
---

## Infomation
Service Name: ShippingFeeConfigService
Method Name: parseShippingFee(String value)
Mock class: None (Helper method)

Mock Data:
   - validValue: "25000"
   - validDecimal: "25000.5"
   - invalidValue: "abc"
   - negativeValue: "-1000"

## Testcase
### Testcase 1
Short Description: Test parseShippingFee fails when value is null or blank.
Input: value = null, or value = "  ".
Expected Output: BusinessException (400, "Province and shipping fee are required")
Actual Output: BusinessException (400, "Province and shipping fee are required")

### Testcase 2
Short Description: Test parseShippingFee fails for negative amount.
Input: value = "-5000".
Expected Output: BusinessException (400, "Shipping fee must not be negative")
Actual Output: BusinessException (400, "Shipping fee must not be negative")

### Testcase 3
Short Description: Test parseShippingFee fails for invalid decimal format (too many decimals).
Input: value = "10.123".
Expected Output: BusinessException (400, "Shipping fee must be a valid amount with at most 2 decimal places")
Actual Output: BusinessException (400, "Shipping fee must be a valid amount with at most 2 decimal places")

### Testcase 4
Short Description: Test parseShippingFee fails for non-numeric input.
Input: value = "twenty thousand".
Expected Output: BusinessException (400, "Shipping fee must be a valid amount with at most 2 decimal places")
Actual Output: BusinessException (400, "Shipping fee must be a valid amount with at most 2 decimal places")

### Testcase 5
Short Description: Test parseShippingFee succeeds with valid numeric value.
Input: value = "15000.5".
Expected Output: BigDecimal("15000.50").
Actual Output: BigDecimal("15000.50").

## Code of Test Case
```java
@Test
void parseShippingFee_NullOrBlank_ThrowsBadRequest() {
    assertThrows(BusinessException.class, () -> 
        ReflectionTestUtils.invokeMethod(shippingFeeConfigService, "parseShippingFee", (Object) null));
    assertThrows(BusinessException.class, () -> 
        ReflectionTestUtils.invokeMethod(shippingFeeConfigService, "parseShippingFee", "   "));
}

@Test
void parseShippingFee_Negative_ThrowsBadRequest() {
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        ReflectionTestUtils.invokeMethod(shippingFeeConfigService, "parseShippingFee", "-100"));
    assertEquals("Shipping fee must not be negative", exception.getMessage());
}

@Test
void parseShippingFee_InvalidFormat_ThrowsBadRequest() {
    assertThrows(BusinessException.class, () -> 
        ReflectionTestUtils.invokeMethod(shippingFeeConfigService, "parseShippingFee", "10.123"));
    assertThrows(BusinessException.class, () -> 
        ReflectionTestUtils.invokeMethod(shippingFeeConfigService, "parseShippingFee", "abc"));
}

@Test
void parseShippingFee_Valid_ReturnsBigDecimal() {
    BigDecimal result = ReflectionTestUtils.invokeMethod(shippingFeeConfigService, "parseShippingFee", "15000.5");
    assertEquals(new BigDecimal("15000.50"), result);
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
