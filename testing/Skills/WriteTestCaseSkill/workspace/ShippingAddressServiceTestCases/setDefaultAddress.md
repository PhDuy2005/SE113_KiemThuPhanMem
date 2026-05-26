---
name: unit-test-setDefaultAddress-condition-coverage
description: Unit tests with 100% condition coverage for ShippingAddressService.setDefaultAddress method.
---

## Infomation
Service Name: ShippingAddressService
Method Name: setDefaultAddress(UUID addressId)
Mock class: 
   1. ShippingAddressRepository shippingAddressRepository
   2. UserRepository userRepository
   3. SecurityUtil (Security context mock)

Mock Data:
   - user: User {id: "11111111-1111-1111-1111-111111111111", email: "user@example.com", accountStatus: "ACTIVE"}
   - addressNotDefault: ShippingAddress {id: "22222222-2222-2222-2222-222222222222", user: user, defaultAddress: false}
   - addressAlreadyDefault: ShippingAddress {id: "33333333-3333-3333-3333-333333333333", user: user, defaultAddress: true}

## Testcase
### Testcase 1
Short Description: Test setDefaultAddress fails when the shipping address is not found for the current user.
Input: addressId = "22222222-2222-2222-2222-222222222222", shippingAddressRepository.findByIdAndUserIdAndDeletedAtIsNull returns Optional.empty().
Expected Output: BusinessException (404, "Shipping address not found")
Actual Output: BusinessException (404, "Shipping address not found")

### Testcase 2
Short Description: Test setDefaultAddress succeeds when the address is not currently the default (clears other defaults).
Input: addressId = "22222222-2222-2222-2222-222222222222", shippingAddressRepository.findByIdAndUserIdAndDeletedAtIsNull returns Optional.of(addressNotDefault).
Expected Output: ResShippingAddressDTO {id: "22222222-2222-2222-2222-222222222222", defaultAddress: true}, clearDefaultByUserId called.
Actual Output: ResShippingAddressDTO {id: "22222222-2222-2222-2222-222222222222", defaultAddress: true}, clearDefaultByUserId called.

### Testcase 3
Short Description: Test setDefaultAddress succeeds when the address is already the default (no need to clear others).
Input: addressId = "33333333-3333-3333-3333-333333333333", shippingAddressRepository.findByIdAndUserIdAndDeletedAtIsNull returns Optional.of(addressAlreadyDefault).
Expected Output: ResShippingAddressDTO {id: "33333333-3333-3333-3333-333333333333", defaultAddress: true}, clearDefaultByUserId NOT called.
Actual Output: ResShippingAddressDTO {id: "33333333-3333-3333-3333-333333333333", defaultAddress: true}, clearDefaultByUserId NOT called.

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
