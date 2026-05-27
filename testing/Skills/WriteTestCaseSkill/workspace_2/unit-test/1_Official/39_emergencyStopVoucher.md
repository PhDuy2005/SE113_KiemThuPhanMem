## Information
Service Name: VoucherManagementService
Method Name: emergencyStopVoucher(UUID voucherId)
Mock class: 
   1. UserRepository
   2. VoucherRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Admin) | TC 2 (Voucher Not Found) | TC 3 (Success) |
| :--- | :--- | :--- | :--- |
| **Short Description** | Try to stop voucher with CUSTOMER role | Try to stop a voucher that does not exist | Successfully stop an active voucher |
| **Inputs** | | | |
| `voucherId` | "11111111-1111-1111-1111-111111111111" | "99999999-9999-9999-9999-999999999999" | "11111111-1111-1111-1111-111111111111" |
| **Mock / Context Setup** | | | |
| `SecurityContext` | logged in as "cust@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" |
| `userRepository.findByEmail` | returns User(role=Role(name="CUSTOMER")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) |
| `voucherRepository.findById`| not called | returns empty | returns Voucher(id="1111...", status="ACTIVE") |
| `voucherRepository.save` | not called | not called | returns Voucher(id="1111...", status="STOPPED", active=false) |
| **Expected Output** | BusinessException(403, "Only business admin can perform this action") | BusinessException(404, "Voucher not found") | resVoucherDTO.message = "Voucher stopped successfully", status becomes "STOPPED" |
