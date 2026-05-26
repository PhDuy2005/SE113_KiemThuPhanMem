---
name: unit-test-sendRegistrationVerification-condition-coverage
description: Unit tests with 100% condition coverage for EmailService.sendRegistrationVerification method.
---

## Infomation
Service Name: EmailService
Method Name: sendRegistrationVerification(String email, String verificationToken)
Mock class: 
   1. JavaMailSender mailSender

Mock Data:
   - email: "newuser@example.com"
   - verificationToken: "550e8400-e29b-41d4-a716-446655440000"
   - appBaseUrl: "http://localhost:8080"
   - fromEmail: "noreply@techsales.com"

## Testcase
### Testcase 1
Short Description: Test sendRegistrationVerification successfully sends a verification email.
Input: email = "newuser@example.com", verificationToken = "550e8400-e29b-41d4-a716-446655440000", mailSender.send() executes successfully.
Expected Output: Void (method completes normally, mailSender.send called with correct parameters)
Actual Output: Void (method completes normally, mailSender.send called with correct parameters)

### Testcase 2
Short Description: Test sendRegistrationVerification fails when the email service is unavailable.
Input: email = "newuser@example.com", verificationToken = "550e8400-e29b-41d4-a716-446655440000", mailSender.send() throws MailException.
Expected Output: BusinessException (503, "Cannot send email right now: ...")
Actual Output: BusinessException (503, "Cannot send email right now: ...")

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
