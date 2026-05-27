## Information
Service Name: ShippingFeeConfigService
Method Name: updateShippingFees(List<ReqUpdateShippingFeeDTO> request)
Mock class: 
   1. UserRepository
   2. ShippingFeeConfigRepository
   3. AddressDataService

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Admin) | TC 2 (Empty Request) | TC 3 (Negative Fee) | TC 4 (Invalid Fee Format) | TC 5 (Invalid Province) | TC 6 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Try to update shipping fees with CUSTOMER role | Send an empty list of shipping fee configurations | Send a negative shipping fee value | Send a shipping fee with 3 decimal places | Send a province code that does not exist | Successfully update shipping fees for multiple provinces |
| **Inputs** | | | | | | |
| `request` | `[{province="Ho Chi Minh", shippingFee="30000"}]` | `[]` | `[{province="Ho Chi Minh", shippingFee="-50000"}]` | `[{province="Ho Chi Minh", shippingFee="30000.123"}]` | `[{province="Invalid", shippingFee="30000"}]` | `[{province="Ho Chi Minh", shippingFee="30000"}]` |
| **Mock / Context Setup** | | | | | | |
| `SecurityContext` | logged in as "cust@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" |
| `userRepository.findByEmail`| returns User(role=Role(name="CUSTOMER")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) |
| `addressDataService.requireProvince` | not called | not called | not called | not called | throws BusinessException(404, "Province not found") | returns ResProvinceDTO(code="79", name="Ho Chi Minh") |
| `shippingFeeConfigRepository.findAll`| not called | not called | not called | not called | not called | returns `[]` |
| `shippingFeeConfigRepository.save` | not called | not called | not called | not called | not called | executes successfully |
| `shippingFeeConfigRepository.findAllByOrderByProvinceAsc` | not called | not called | not called | not called | not called | returns `[ShippingFeeConfig(province="Ho Chi Minh", shippingFee=30000)]` |
| **Expected Output** | BusinessException(403, "Only business admin can perform this action") | BusinessException(400, "Province and shipping fee are required") | BusinessException(400, "Shipping fee must not be negative") | BusinessException(400, "Shipping fee must be a valid amount with at most 2 decimal places") | BusinessException(404, "Province not found") | Returns `[ResShippingFeeDTO(message="Shipping fees updated successfully", province="Ho Chi Minh", shippingFee=30000)]` |
