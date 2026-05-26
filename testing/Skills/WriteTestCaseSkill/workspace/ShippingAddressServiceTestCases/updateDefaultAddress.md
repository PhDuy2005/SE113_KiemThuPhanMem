---
name: unit-test-updateDefaultAddress-condition-coverage
description: Unit tests with 100% condition coverage for ShippingAddressService.updateDefaultAddress method.
---

## Infomation
Service Name: ShippingAddressService
Method Name: updateDefaultAddress(ReqShippingAddressDTO request)
Mock class: 
   1. ShippingAddressRepository shippingAddressRepository
   2. UserRepository userRepository
   3. SecurityUtil (Security context mock)

Mock Data:
   - user: User {id: "11111111-1111-1111-1111-111111111111", email: "user@example.com", accountStatus: "ACTIVE"}
   - reqUpdate: {province: "Hanoi", ward: "Hoan Kiem", detail: "456 Avenue"}
   - defaultAddress: ShippingAddress {id: "22222222-2222-2222-2222-222222222222", user: user, province: "HCM", ward: "District 1", detail: "123 St", defaultAddress: true}

## Testcase
### Testcase 1
Short Description: Test updateDefaultAddress fails when the user has no default shipping address.
Input: request = reqUpdate, shippingAddressRepository.findByUserIdAndDefaultAddressTrueAndDeletedAtIsNull returns Optional.empty().
Expected Output: BusinessException (404, "Default shipping address not found")
Actual Output: BusinessException (404, "Default shipping address not found")

### Testcase 2
Short Description: Test updateDefaultAddress succeeds when default address exists.
Input: request = reqUpdate, shippingAddressRepository.findByUserIdAndDefaultAddressTrueAndDeletedAtIsNull returns Optional.of(defaultAddress).
Expected Output: ResShippingAddressDTO {id: "22222222-2222-2222-2222-222222222222", province: "Hanoi", ward: "Hoan Kiem", detail: "456 Avenue", defaultAddress: true}
Actual Output: ResShippingAddressDTO {id: "22222222-2222-2222-2222-222222222222", province: "Hanoi", ward: "Hoan Kiem", detail: "456 Avenue", defaultAddress: true}

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
