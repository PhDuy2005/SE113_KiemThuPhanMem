## Infomation
Service Name: UserService
Method Name: matchesPassword(String, User)
Mock class: 
   1. PasswordEncoder passwordEncoder

Mock Data:
   - rawPassword: "password123"
   - encodedPassword: "encoded_password123"
   - User user: {password: "encoded_password123"}

## Testcase
### Testcase 1
Short Description: Test returns false when the user object is null.
Input: rawPassword = "password123", user = null
Expected Output: false
Actual Output: false

### Testcase 2
Short Description: Test returns false when the raw password is null.
Input: rawPassword = null, user = {password: "encoded_password123"}
Expected Output: false
Actual Output: false

### Testcase 3
Short Description: Test returns false when the user's stored password is null.
Input: rawPassword = "password123", user = {password: null}
Expected Output: false
Actual Output: false

### Testcase 4
Short Description: Test returns false when the password encoder matches returns false.
Input: rawPassword = "wrongPassword", user = {password: "encoded_password123"}
Expected Output: false
Actual Output: false

### Testcase 5
Short Description: Test returns true when all conditions are met and the password matches.
Input: rawPassword = "password123", user = {password: "encoded_password123"}
Expected Output: true
Actual Output: true
