package com.uit.nhom7.KiemThuPhanMem.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    public void sendRegistrationVerification(String email, String verificationToken) {
        System.out.println(">>>EMAIL MODULE: Verification email queued for " + email
                + " with token " + verificationToken);
    }

    public void sendPasswordReset(String email, String resetToken) {
        System.out.println(">>>EMAIL MODULE: Password reset email queued for " + email
                + " with token " + resetToken);
    }
}
