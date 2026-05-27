## Information
Service Name: ShippingAddressService
Method Name: createAddress(ReqShippingAddressDTO request)
Mock class: 
   1. UserRepository
   2. ShippingAddressRepository
   3. AddressDataService

Mock Data:
- currentUser: User(id="550e8400-e29b-41d4-a716-446655440000", email="customer@example.com", accountStatus="ACTIVE")

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Logged In) | TC 2 (Blocked User) | TC 3 (First Address Success) | TC 4 (Subsequent Address Success) | TC 5 (Validation Failed) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Create address when user not logged in | Create address when user blocked | Create first address (should set as default) | Create subsequent address (should NOT set as default) | Validation fails in AddressDataService |
| **Inputs** | | | | | |
| `request.provinceCode`| "79" | "79" | "79" | "79" | "79" |
| `request.wardCode` | "27382" | "27382" | "27382" | "27382" | "27382" |
| `request.detail` | "123 Lý Tự Trọng" | "123 Lý Tự Trọng" | "123 Lý Tự Trọng" | "123 Lý Tự Trọng" | "   " |
| **Mock / Context Setup** | | | | | |
| `SecurityContext` | empty (not logged in) | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" |
| `currentUser.accountStatus`| null | "BLOCKED" | "ACTIVE" | "ACTIVE" | "ACTIVE" |
| `ShippingAddressRepository.existsByUserIdAndDeletedAtIsNull` | null | null | returns false | returns true | null |
| `AddressDataService.resolveAddress` | null | null | returns ResolvedAddress(provinceCode="79", province="TP. Hồ Chí Minh", wardCode="27382", ward="Phường Bến Nghé", detail="123 Lý Tự Trọng") | returns ResolvedAddress(provinceCode="79", province="TP. Hồ Chí Minh", wardCode="27382", ward="Phường Bến Nghé", detail="123 Lý Tự Trọng") | throws BusinessException(400, "Detail must not be empty") |
| **Expected Output** | BusinessException(401, "You must login first") | BusinessException(403, "User account is not active") | resShippingAddressDTO.provinceCode = "79", resShippingAddressDTO.wardCode = "27382", defaultAddress = true | resShippingAddressDTO.provinceCode = "79", resShippingAddressDTO.wardCode = "27382", defaultAddress = false | BusinessException(400, "Detail must not be empty") |
