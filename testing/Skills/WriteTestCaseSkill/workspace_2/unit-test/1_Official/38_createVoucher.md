## Information
Service Name: VoucherManagementService
Method Name: createVoucher(ReqCreateVoucherDTO request)
Mock class: 
   1. UserRepository
   2. VoucherRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Admin) | TC 2 (Missing Fields) | TC 3 (Invalid Dates) | TC 4 (Duplicate Code) | TC 5 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Try to create voucher with CUSTOMER role | Try to create voucher without specifying discount value or quantity | Try to set endDate before or equal to startDate | Try to create voucher with a code that already exists | Successfully create a new voucher |
| **Inputs** | | | | | |
| `request.voucherCode` | "SUMMER2026" | "SUMMER2026" | "SUMMER2026" | "SUMMER2026" | "SUMMER2026" |
| `request.discountType` | "PERCENT" | "PERCENT" | "PERCENT" | "PERCENT" | "PERCENT" |
| `request.discountValue` | BigDecimal(10) | null | BigDecimal(10) | BigDecimal(10) | BigDecimal(10) |
| `request.quantity` | 100 | 100 | 100 | 100 | 100 |
| `request.startDate` | Instant.now() | Instant.now() | Instant.now().plus(2 days) | Instant.now() | Instant.now() |
| `request.endDate` | Instant.now().plus(30 days) | Instant.now().plus(30 days) | Instant.now() | Instant.now().plus(30 days) | Instant.now().plus(30 days) |
| **Mock / Context Setup** | | | | | |
| `SecurityContext` | logged in as "cust@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" |
| `userRepository.findByEmail` | returns User(role=Role(name="CUSTOMER")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) |
| `voucherRepository.existsByCodeIgnoreCase`| not called | not called | not called | returns true | returns false |
| `voucherRepository.save` | not called | not called | not called | not called | returns Voucher(id="1111...", code="SUMMER2026", status="ACTIVE") |
| **Expected Output** | BusinessException(403, "Only business admin can perform this action") | BusinessException(400, "Required field is missing") | BusinessException(400, "End date must be after start date") | BusinessException(409, "Voucher code already exists") | resVoucherDTO.message = "Voucher created successfully", code = "SUMMER2026" |
