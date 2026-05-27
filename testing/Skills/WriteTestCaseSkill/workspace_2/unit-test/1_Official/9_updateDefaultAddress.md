## Information
Service Name: ShippingAddressService
Method Name: updateDefaultAddress(ReqShippingAddressDTO request)
Mock class: 
   1. UserRepository
   2. ShippingAddressRepository
   3. AddressDataService

Mock Data:
- currentUser: User(id="550e8400-e29b-41d4-a716-446655440000", email="customer@example.com", accountStatus="ACTIVE")

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Logged In) | TC 2 (Blocked User) | TC 3 (Default Not Found) | TC 4 (Validation Failed) | TC 5 (Update Success) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Update default address when not logged in | Update default address when user blocked | Update default address when no default address exists | Validation fails in AddressDataService | Update default address successfully with new valid values |
| **Inputs** | | | | | |
| `request.provinceCode`| "01" | "01" | "01" | "01" | "01" |
| `request.wardCode` | "00001" | "00001" | "00001" | "00001" | "00001" |
| `request.detail` | "456 Phúc Xá" | "456 Phúc Xá" | "456 Phúc Xá" | "   " | "456 Phúc Xá" |
| **Mock / Context Setup** | | | | | |
| `SecurityContext` | empty (not logged in) | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" |
| `currentUser.accountStatus`| null | "BLOCKED" | "ACTIVE" | "ACTIVE" | "ACTIVE" |
| `ShippingAddressRepository.findByUserIdAndDefaultAddressTrueAndDeletedAtIsNull` | null | null | returns empty | returns ShippingAddress(id="33333333-3333-3333-3333-333333333333", user=currentUser, defaultAddress=true) | returns ShippingAddress(id="33333333-3333-3333-3333-333333333333", user=currentUser, defaultAddress=true) |
| `AddressDataService.resolveAddress` | null | null | null | throws BusinessException(400, "Detail must not be empty") | returns ResolvedAddress(provinceCode="01", province="Thành phố Hà Nội", wardCode="00001", ward="Phường Phúc Xá", detail="456 Phúc Xá") |
| **Expected Output** | BusinessException(401, "You must login first") | BusinessException(403, "User account is not active") | BusinessException(404, "Default shipping address not found") | BusinessException(400, "Detail must not be empty") | resShippingAddressDTO.provinceCode = "01", resShippingAddressDTO.wardCode = "00001", resShippingAddressDTO.detail = "456 Phúc Xá", defaultAddress = true |
