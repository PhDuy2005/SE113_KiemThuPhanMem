---
name: unit-test-convertToDTO-condition-coverage
description: Unit tests with 100% condition coverage for ShippingAddressService.convertToDTO method.
---

## Infomation
Service Name: ShippingAddressService
Method Name: convertToDTO(ShippingAddress address)
Mock class: None

Mock Data:
   - user: User {id: "11111111-1111-1111-1111-111111111111"}
   - address: ShippingAddress {
       id: "22222222-2222-2222-2222-222222222222",
       user: user,
       province: "Ho Chi Minh",
       ward: "Ward 1",
       detail: "123 Street",
       defaultAddress: true
     }

## Testcase
### Testcase 1
Short Description: Test convertToDTO successfully maps all fields from ShippingAddress entity to ResShippingAddressDTO.
Input: address object with valid fields.
Expected Output: ResShippingAddressDTO {id: "22222222-2222-2222-2222-222222222222", userId: "11111111-1111-1111-1111-111111111111", province: "Ho Chi Minh", ward: "Ward 1", detail: "123 Street", defaultAddress: true}
Actual Output: ResShippingAddressDTO {id: "22222222-2222-2222-2222-222222222222", userId: "11111111-1111-1111-1111-111111111111", province: "Ho Chi Minh", ward: "Ward 1", detail: "123 Street", defaultAddress: true}

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
