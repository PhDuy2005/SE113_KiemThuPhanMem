## Information
Service Name: OrderService
Method Name: updateShippingStatus(UUID orderId, ReqUpdateShippingStatusDTO request)
Mock class: 
   1. UserRepository
   2. OrderRepository
   3. NotificationRepository
   4. OrderItemRepository
   5. PaymentRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Staff/Admin) | TC 2 (Empty Tracking) | TC 3 (Order Not Found) | TC 4 (Invalid Status) | TC 5 (Success from APPROVED) | TC 6 (Success from SHIPPING) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Unauthorized attempt by customer | Missing or blank tracking number | Order ID does not exist | Order is not APPROVED or SHIPPING | Successfully move APPROVED order to SHIPPING | Successfully update tracking number of SHIPPING order |
| **Inputs** | | | | | | |
| `orderId` | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" | "99999999-9999-9999-9999-999999999999" | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" |
| `request.trackingNumber` | "SPX123456" | "   " | "SPX123456" | "SPX123456" | "SPX123456" | "SPX987654" |
| **Mock / Context Setup** | | | | | | |
| `SecurityContext` | logged in as "customer@example.com" | logged in as "staff@example.com" | logged in as "staff@example.com" | logged in as "staff@example.com" | logged in as "staff@example.com" | logged in as "staff@example.com" |
| `userRepository.findByEmail` | returns User(role=Role(name="CUSTOMER")) | returns User(role=Role(name="STAFF")) | returns User(role=Role(name="STAFF")) | returns User(role=Role(name="STAFF")) | returns User(role=Role(name="STAFF")) | returns User(role=Role(name="STAFF")) |
| `OrderRepository.findById` | null | null | returns empty | returns Order(status="PENDING") | returns Order(status="APPROVED") | returns Order(status="SHIPPING", trackingNumber="OLD123") |
| **Expected Output** | BusinessException(403, "Only staff or business admin can perform this action") | BusinessException(400, "Tracking number is required") | BusinessException(404, "Order not found") | BusinessException(400, "Only approved orders can be moved to shipping") | resOrderDetailDTO.status = "SHIPPING", trackingNumber = "SPX123456", message = "Shipping status updated successfully" | resOrderDetailDTO.status = "SHIPPING", trackingNumber = "SPX987654", message = "Shipping status updated successfully" |
