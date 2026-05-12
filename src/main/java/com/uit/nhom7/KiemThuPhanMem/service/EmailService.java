package com.uit.nhom7.KiemThuPhanMem.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${techsales.app.base-url:http://localhost:8080}")
    private String appBaseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendRegistrationVerification(String email, String verificationToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Verify Registration TechSales Account");
        message.setText("""
                Welcome,

                Follow this link to verify your email address to finish your registration step.

                %s/api/v1/auth/verify?token=%s

                Thanks.

                The TechSale team
                """.formatted(appBaseUrl, verificationToken));
        send(message);
    }

    public void sendPasswordReset(String email, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Reset TechSales Account Password");
        message.setText("""
                Dear customer,

                Follow this link to reset your TechSales account password.

                %s/api/v1/auth/reset-password/validate?token=%s

                Thanks.

                The TechSale team
                """.formatted(appBaseUrl, resetToken));
        send(message);
    }

    public void sendOrderConfirmation(
            String email,
            String fullName,
            UUID orderId,
            String orderItems,
            BigDecimal totalAmount,
            String shippingAddress,
            String paymentMethod) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Confirm your Order");
        message.setText("""
                Dear %s,

                You have just confirmed an order. This is detail:

                Order ID: %s
                Items:
                %s

                Total amount: %s
                The order will be shipped to %s

                Payment Method: %s

                Thanks.

                The TechSale team
                """.formatted(
                fullName == null || fullName.isBlank() ? "customer" : fullName,
                orderId,
                orderItems,
                totalAmount,
                shippingAddress,
                paymentMethod));
        send(message);
    }

    public void sendOrderCancellation(
            String email,
            String fullName,
            UUID orderId,
            String cancelReason,
            String refundStatus) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Your order has been cancelled");
        message.setText("""
                Dear %s,

                Your order has been cancelled.

                Order ID: %s
                Cancel reason: %s
                Refund status: %s

                Thanks.

                The TechSale team
                """.formatted(
                fullName == null || fullName.isBlank() ? "customer" : fullName,
                orderId,
                cancelReason,
                refundStatus == null || refundStatus.isBlank() ? "N/A" : refundStatus));
        send(message);
    }

    private void send(SimpleMailMessage message) {
        try {
            mailSender.send(message);
        } catch (MailException ex) {
            throw new BusinessException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Cannot send email right now: " + ex.getMessage());
        }
    }
}
