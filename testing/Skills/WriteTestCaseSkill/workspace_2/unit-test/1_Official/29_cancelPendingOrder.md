## Information
Service Name: OrderService
Method Name: cancelPendingOrder(UUID orderId)
Mock class: 
   1. UserRepository
   2. OrderRepository
   3. OrderItemRepository
   4. InventoryRepository
   5. PaymentRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Logged In) | TC 2 (Not Found / Not Owned) | TC 3 (Order Not Found by Staff) | TC 4 (Order Not Pending) | TC 5 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Cancel pending order when not logged in | Customer attempts to cancel an order they do not own (or doesn't exist) | Staff attempts to cancel an order that does not exist | Order is already APPROVED or SHIPPING | Successfully cancel a PENDING order |
| **Inputs** | | | | | |
| `orderId` | "88888888-8888-8888-8888-888888888888" | "99999999-9999-9999-9999-999999999999" | "99999999-9999-9999-9999-999999999999" | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" |
| **Mock / Context Setup** | | | | | |
| `SecurityContext` | empty (not logged in) | logged in as "customer@example.com" | logged in as "staff@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" |
| `userRepository.findByEmail` | null | returns User(id="550e...", role=Role(name="CUSTOMER")) | returns User(id="1111...", role=Role(name="STAFF")) | returns User(id="550e...", role=Role(name="CUSTOMER")) | returns User(id="550e...", role=Role(name="CUSTOMER")) |
| `OrderRepository.findByIdAndUserId`| null | returns empty | null | returns Order(status="APPROVED", user=User(id="550e...")) | returns Order(status="PENDING", user=User(id="550e...")) |
| `OrderRepository.findById` | null | null | returns empty | null | null |
| `OrderItemRepository.findByOrderIdWithProduct`| null | null | null | null | returns [OrderItem(quantity=2, product=Product(id="1111..."))] |
| `InventoryRepository.findById`| null | null | null | null | returns Inventory(quantity=10) |
| `PaymentRepository.findByOrderId`| null | null | null | null | returns Payment(status="PENDING") |
| **Expected Output** | BusinessException(401, "You must login first") | BusinessException(404, "Order not found") | BusinessException(404, "Order not found") | BusinessException(400, "Order cannot be cancelled because it has been processed") | resOrderDTO.status = "CANCELLED", message = "Order cancelled successfully" |
