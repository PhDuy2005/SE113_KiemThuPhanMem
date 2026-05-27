## Information
Service Name: OrderService
Method Name: approveOrder(UUID orderId)
Mock class: 
   1. UserRepository
   2. OrderRepository
   3. NotificationRepository
   4. PaymentRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Logged In) | TC 2 (Not Staff/Admin) | TC 3 (Order Not Found) | TC 4 (Order Not Pending) | TC 5 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Approve order when not logged in | Approve order using CUSTOMER role (not authorized) | Approve non-existent order | Approve order that is already approved or shipped | Approve a pending order successfully |
| **Inputs** | | | | | |
| `orderId` | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" | "99999999-9999-9999-9999-999999999999" | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" |
| **Mock / Context Setup** | | | | | |
| `SecurityContext` | empty (not logged in) | logged in as "customer@example.com" | logged in as "staff@example.com" | logged in as "staff@example.com" | logged in as "staff@example.com" |
| `userRepository.findByEmail` | null | returns User(role=Role(name="CUSTOMER")) | returns User(role=Role(name="STAFF")) | returns User(role=Role(name="STAFF")) | returns User(role=Role(name="STAFF")) |
| `OrderRepository.findById` | null | null | returns empty | returns Order(status="APPROVED") | returns Order(status="PENDING") |
| **Expected Output** | BusinessException(401, "You must login first") | BusinessException(403, "Only staff or business admin can perform this action") | BusinessException(404, "Order not found") | BusinessException(400, "Only pending orders can be approved") | resOrderDTO.status = "APPROVED", message = "Order approved successfully" |
