## Information
Service Name: ShippingAddressService
Method Name: setDefaultAddress(UUID addressId)
Mock class: 
   1. UserRepository
   2. ShippingAddressRepository

Mock Data:
- currentUser: User(id="550e8400-e29b-41d4-a716-446655440000", email="customer@example.com", accountStatus="ACTIVE")

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Logged In) | TC 2 (Address Not Found) | TC 3 (Already Default) | TC 4 (Set Default Success) |
| :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Set default address when not logged in | Set default address when address not found or not owned | Set default when already default | Set default successfully |
| **Inputs** | | | | |
| `addressId` | "33333333-3333-3333-3333-333333333333" | "22222222-2222-2222-2222-222222222222" | "33333333-3333-3333-3333-333333333333" | "44444444-4444-4444-4444-444444444444" |
| **Mock / Context Setup** | | | | |
| `SecurityContext` | empty (not logged in) | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" |
| `currentUser.accountStatus`| null | "ACTIVE" | "ACTIVE" | "ACTIVE" |
| `ShippingAddressRepository.findByIdAndUserIdAndDeletedAtIsNull` | null | returns empty | returns ShippingAddress(id="33333333-3333-3333-3333-333333333333", user=currentUser, defaultAddress=true) | returns ShippingAddress(id="44444444-4444-4444-4444-444444444444", user=currentUser, defaultAddress=false) |
| **Expected Output** | BusinessException(401, "You must login first") | BusinessException(404, "Shipping address not found") | resShippingAddressDTO.defaultAddress = true | resShippingAddressDTO.defaultAddress = true |
