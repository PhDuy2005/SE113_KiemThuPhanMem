## Information
Service Name: OrderService
Method Name: markOrderDelivered(UUID orderId)
Mock class: 
   1. UserRepository
   2. OrderRepository
   3. NotificationRepository
   4. PaymentRepository
   5. OrderItemRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Staff/Admin) | TC 2 (Order Not Found) | TC 3 (Invalid Status) | TC 4 (Success with Payment) | TC 5 (Success without Payment) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Unauthorized attempt by customer | Order ID does not exist | Order is not in SHIPPING status | Mark order as DELIVERED and update payment status | Mark order as DELIVERED when payment is missing |
| **Inputs** | | | | | |
| `orderId` | "88888888-8888-8888-8888-888888888888" | "99999999-9999-9999-9999-999999999999" | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" |
| **Mock / Context Setup** | | | | | |
| `SecurityContext` | logged in as "customer@example.com" | logged in as "staff@example.com" | logged in as "staff@example.com" | logged in as "staff@example.com" | logged in as "staff@example.com" |
| `userRepository.findByEmail` | returns User(role=Role(name="CUSTOMER")) | returns User(role=Role(name="STAFF")) | returns User(role=Role(name="STAFF")) | returns User(role=Role(name="STAFF")) | returns User(role=Role(name="STAFF")) |
| `OrderRepository.findById` | null | returns empty | returns Order(status="APPROVED") | returns Order(status="SHIPPING") | returns Order(status="SHIPPING") |
| `PaymentRepository.findByOrderId` | null | null | null | returns Payment(status="PENDING") | returns empty |
| **Expected Output** | BusinessException(403, "Only staff or business admin can perform this action") | BusinessException(404, "Order not found") | BusinessException(400, "Only shipping orders can be marked as delivered") | resOrderDetailDTO.status = "DELIVERED", completedAt is not null, payment status updated to SUCCESS, message = "Order delivered successfully" | resOrderDetailDTO.status = "DELIVERED", completedAt is not null, message = "Order delivered successfully" |
