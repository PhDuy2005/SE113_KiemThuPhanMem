## Information
Service Name: CheckoutService
Method Name: selectPaymentMethod(ReqSelectPaymentMethodDTO request)
Mock class: 
   1. UserRepository
   2. PaymentMethodRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Logged In) | TC 2 (Blocked User) | TC 3 (Method Not Found) | TC 4 (Non-Cash Method) | TC 5 (Success Cash) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Select payment method when not logged in | Select when user account is BLOCKED | Select payment method ID that does not exist | Select a method that is not cash (e.g. VNPAY) | Successfully select cash payment method |
| **Inputs** | | | | | |
| `request.paymentMethodId` | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "99999999-9999-9999-9999-999999999999" | "22222222-2222-2222-2222-222222222222" | "11111111-1111-1111-1111-111111111111" |
| **Mock / Context Setup** | | | | | |
| `SecurityContext` | empty (not logged in) | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" |
| `currentUser.accountStatus`| null | "BLOCKED" | "ACTIVE" | "ACTIVE" | "ACTIVE" |
| `PaymentMethodRepository.findById` | null | null | returns empty | returns PaymentMethod(id="2222...", type="VNPAY") | returns PaymentMethod(id="1111...", name="Thanh toán khi nhận hàng", type="CASH") |
| **Expected Output** | BusinessException(401, "You must login first") | BusinessException(403, "User account is not active") | BusinessException(400, "Invalid payment method") | BusinessException(400, "Only cash payment is supported at the moment") | resPaymentMethodSelectionDTO.paymentMethodId = "11111111-1111-1111-1111-111111111111", name = "Thanh toán khi nhận hàng", type = "CASH" |
