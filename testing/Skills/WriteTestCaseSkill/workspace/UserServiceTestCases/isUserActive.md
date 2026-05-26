## Infomation
Service Name: UserService
Method Name: isUserActive(User)
Mock class: None

Mock Data:
   - User activeUser: {accountStatus: "ACTIVE"}
   - User pendingUser: {accountStatus: "PENDING"}
   - User nullStatusUser: {accountStatus: null}

## Testcase
### Testcase 1
Short Description: Test returns false when the user object is null.
Input: user = null
Expected Output: false
Actual Output: false

### Testcase 2
Short Description: Test returns false when the user object exists but account status is null.
Input: user = {accountStatus: null}
Expected Output: false
Actual Output: false

### Testcase 3
Short Description: Test returns false when the account status is not ACTIVE (e.g., PENDING).
Input: user = {accountStatus: "PENDING"}
Expected Output: false
Actual Output: false

### Testcase 4
Short Description: Test returns true when the account status is ACTIVE (case-insensitive and trimmed).
Input: user = {accountStatus: "  active  "}
Expected Output: true
Actual Output: true
