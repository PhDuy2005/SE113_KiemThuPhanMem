---
name: template-for-writing-unit-test-with-conditions-coverage
description: This file is a template for writing unit test with conditions coverage (100% condition coverage) for a given code snippet.
---

# The template is

## Infomation
Service Name: XXXService
Method Name: for example: login(string, string)
Mock class: 
   1. Mock repository class for 
   2. Mock for file_repository_class
   3. Mock for service_class

Mock Data:

## Testcase
### Testcase 1
Short Description:
Input:
Expected Output:
Actual Output:
### Testcase 2
Short Description:
Input:
Expected Output:
Actual Output:
### Testcase n:
....

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"

