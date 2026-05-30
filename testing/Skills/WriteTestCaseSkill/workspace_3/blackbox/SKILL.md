# UI / FE Black-box Testing Rules
When performing black-box testing on the UI/FE, present the test cases in the following format:

## 1. Information:
- Test Requirement: [Name of the feature or requirement being tested]

## 2. Test case:
Present test cases in a table format where each row is a single testcase. The table MUST strictly have the following 5 columns:
1. **ID**: Unique identifier for the test case (e.g., `TC-02.01`).
2. **Test Case Description**: e.g., Verify out of range error for Day (Above maximum).
3. **Test Case Procedure**: Step-by-step actions, e.g.,
   1. Click button "Check Date"
   2. Input "32" into Day textbox
   3. Click "Check"
4. **Expected Output**: Expected result visible on UI, e.g., An error message box appears with the text "Input data for Day is incorrect format!" and an "OK" button.
5. **Inter-test case Dependence**: State any dependencies on other test cases, or "None" if independent (e.g., `Requires TC-02.01 to pass`).

## 3. General Principles for UI Black-box Testing
1. **Frontend Source Code (`fe/`)**: ALWAYS read the frontend files (e.g., React components/pages) to determine the exact `Test case Procedure`. Use the actual IDs, labels, placeholders, and interactive elements found in the code (e.g., `Click button "Sign In"`, `Input to "Email address" textbox`).
2. **Business Rules (`be\instruction\UC_BR_FROM_SRS.md`)**: ALWAYS read this document to determine the `Testcase Description` and `Expected Output`. Extract the specific error messages, validations (like regex, min/max lengths), and logical constraints directly from the SRS.
3. **Testing Techniques**: Comprehensively apply black-box techniques to generate test cases:
   - *Equivalence Partitioning*: Test representative valid and invalid data groups.
   - *Boundary Value Analysis (BVA)*: Test edge cases for lengths, numeric limits, and states (e.g., password length = 7, 8; failed logins = 4, 5).
   - *Error Guessing*: Anticipate common user errors (e.g., empty fields, whitespace).
   - *Decision Tables*: Cover combinations of multiple inputs and conditions.
4. **Test Data Management**: If the user provides real test data (e.g., existing accounts, valid passwords), explicitly list them in a "Pre-conditions" section within "1. Information" and use them directly in the "Test case Procedure" steps to make the test executable. If no real data is provided, use clear assumed placeholders (e.g., `[Valid_Email]`, `[Wrong_Password]`).
