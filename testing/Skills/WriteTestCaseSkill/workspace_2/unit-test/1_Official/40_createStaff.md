## Information
Service Name: StaffManagementService
Method Name: createStaff(ReqCreateStaffDTO request)
Mock class: 
   1. UserRepository
   2. RoleRepository
   3. EmailService

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Admin) | TC 2 (Invalid Email) | TC 3 (Email Exists) | TC 4 (Wrong Role) | TC 5 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Try to create staff with CUSTOMER role | Try to create staff with an invalid email format | Try to create staff using an email that already exists | Try to create staff but assign a role other than STAFF | Successfully create a new staff account |
| **Inputs** | | | | | |
| `request.email` | "newstaff@example.com" | "invalid-email" | "existing@example.com" | "newstaff@example.com" | "newstaff@example.com" |
| `request.fullName` | "Nguyen Van A" | "Nguyen Van A" | "Nguyen Van A" | "Nguyen Van A" | "Nguyen Van A" |
| `request.roleId` | "1111..." | "1111..." | "1111..." | "2222..." | "1111..." |
| **Mock / Context Setup** | | | | | |
| `SecurityContext` | logged in as "cust@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" |
| `userRepository.findByEmail` | returns User(role=Role(name="CUSTOMER")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) |
| `userRepository.existsByEmail`| not called | not called | returns true | returns false | returns false |
| `roleRepository.findById` | not called | not called | not called | returns Role(name="CUSTOMER") | returns Role(name="STAFF") |
| `userRepository.save` | not called | not called | not called | not called | returns User(id="8888...", email="newstaff@example.com") |
| `EmailService.sendStaffLoginDetails`| not called | not called | not called | not called | called successfully |
| **Expected Output** | BusinessException(403, "Only business admin can perform this action") | BusinessException(400, "Email format is invalid") | BusinessException(409, "Account already exists") | BusinessException(400, "Only STAFF role can be assigned to staff accounts") | resUserDTO.message = "Staff account created successfully", random password is generated and sent via email |
