## Information
Service Name: StaffManagementService
Method Name: lockStaff(UUID staffId)
Mock class: 
   1. UserRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Admin) | TC 2 (Staff Not Found) | TC 3 (Target Not Staff) | TC 4 (Success) |
| :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Try to lock staff account with CUSTOMER role | Try to lock a user ID that does not exist in DB | Try to lock a user who has CUSTOMER or BUSINESS_ADMIN role | Successfully lock a STAFF account |
| **Inputs** | | | | |
| `staffId` | "8888..." | "9999..." | "8888..." | "8888..." |
| **Mock / Context Setup** | | | | |
| `SecurityContext` | logged in as "cust@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" |
| `userRepository.findByEmail` | returns User(role=Role(name="CUSTOMER")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) |
| `userRepository.findById` | not called | returns empty | returns User(id="8888...", role=Role(name="CUSTOMER")) | returns User(id="8888...", role=Role(name="STAFF")) |
| `userRepository.save` | not called | not called | not called | returns User(id="8888...", accountStatus="LOCKED") |
| **Expected Output** | BusinessException(403, "Only business admin can perform this action") | BusinessException(404, "Staff account not found") | BusinessException(400, "Only staff accounts can be locked by this action") | resUserDTO.message = "Staff account locked successfully", status becomes "LOCKED", refreshToken is cleared |
