---
name: write-unit-test-with-conditions-coverage
description: Write unit tests with condition coverage (100% condition coverage) for a given code snippet.
---

# 1. Task Description
Write unit tests with condition coverage (100% condition coverage) for the project code.

# 2. Step
We are just need to test Service classes (which contain Business Logic).
1. First, read the code of Service classes. Location: ./SE113_KiemChungPhanMem/src/main/java/com/uit/nhom7/KiemThuPhanMem/service/

2. Read clearly the code in [XXX]Service.java
3. Write the DOCUMENT of test case for unit test with 100% condition coverage into output file. Output file location: ./Skills/WriteTestCaseSkill/workspace_3/unit-test/1_Official/[YYY].md
4. Write the Code of test case for unit test with 100% condition coverage into output file. Output file location: ./Skills/WriteTestCaseSkill/workspace_3/unit-test/1_Official/[YYY].md
    - Note:
    [XXX]: Name of service
    [YYY]: Name of method in service whose testcase is written

5. The output of testcase write with template as in file : ./Skills/WriteTestCaseSkill/workspace/example_testcase.md

# 3. New Strict Rules for Mock & Context Setup
1. ONLY list mocks/contexts that are actually called in the method. Do not include unused or extra mocks.
2. If a listed mock/context is not called in a specific test case, mark its value as `N/A`.
3. The mock values must reflect the main values focused on in that test case. You MUST write the JSON representation of the mock data DIRECTLY into the table cells. Do NOT declare variables in a separate "Mock Data" section and use variable names in the table.
4. After completing the test case matrix, add a section at the bottom of the file detailing the data variations (Tập dữ liệu) for EACH row in the table. The summary MUST be categorized explicitly under the following subheadings: `### INPUT METHOD`, `### MOCK/ CONTEXT SETUP`, and `### EXPECTED OUTPUT & MESSAGE`. Each variable/row must be a top-level bullet point under its respective category, and each of its distinct values must be listed as a nested bullet point underneath it. If a value is a JSON object in the matrix, it MUST be listed as the exact same JSON object in the summary. Do NOT use backticks (`) around JSON values or other values to make them easy to copy. For example:
5. When testing with UUID data types, you MUST use strictly valid 36-character UUID formats (e.g., "11111111-1111-1111-1111-111111111111" or "550e8400-e29b-41d4-a716-446655440000"). Do NOT use mock strings like "p1-uuid" or "c1-uuid" to avoid UUID parsing exceptions in Java test code.

### INPUT METHOD
- variableA:
  - 1
  - { "id": 1 }
  - null

### MOCK/ CONTEXT SETUP
- userRepository.existsByEmail(...):
  - N/A
  - true

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - N/A
- Expected Return:
  - N/A
  - { "token": "mock_access_token", "user": { "id": 1 } }
- Message:
  - "Email or password incorrect"
  - N/A

# 4. Workflow Rule
- I will only generate the test case file when you explicitly type the command `OK!<number_of_test>`. Do not generate test cases unprompted.

# 5. Strict 100% Condition Coverage Guarantee
- You MUST guarantee absolutely 100/100 condition coverage. This means you must read the source code meticulously line by line.
- Every single `if`, `else if`, `switch`, or ternary operator must be accounted for with a dedicated test case branch.
- Pay special attention to seemingly "minor" conditions, such as checking if an object is null before mapping its fields to a DTO (e.g., `if (user.getRole() != null)`). These require their own test cases (e.g., one where the value is present, and one where the value is null).
- Expected Output MUST be represented as an explicit JSON string (for return objects) or the specific Exception thrown. Do NOT use descriptive English sentences like "Returns AuthResult containing token".