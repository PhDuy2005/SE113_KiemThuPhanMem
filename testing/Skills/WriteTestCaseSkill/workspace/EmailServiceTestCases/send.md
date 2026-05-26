---
name: unit-test-send-condition-coverage
description: Unit tests with 100% condition coverage for EmailService.send method.
---

## Infomation
Service Name: EmailService
Method Name: send(SimpleMailMessage message)
Mock class: 
   1. JavaMailSender mailSender

Mock Data:
   - message: SimpleMailMessage {from: "noreply@techsales.com", to: "user@example.com", subject: "Test", text: "Hello"}
   - mailException: MailException {message: "SMTP server unavailable"}

## Testcase
### Testcase 1
Short Description: Test send succeeds when JavaMailSender sends the message without error.
Input: message = SimpleMailMessage, mailSender.send(message) executes successfully.
Expected Output: Void (method completes normally)
Actual Output: Void (method completes normally)

### Testcase 2
Short Description: Test send fails and throws BusinessException when JavaMailSender throws a MailException.
Input: message = SimpleMailMessage, mailSender.send(message) throws mailException.
Expected Output: BusinessException (503, "Cannot send email right now: SMTP server unavailable")
Actual Output: BusinessException (503, "Cannot send email right now: SMTP server unavailable")

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
