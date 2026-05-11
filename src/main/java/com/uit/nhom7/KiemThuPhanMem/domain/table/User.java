package com.uit.nhom7.KiemThuPhanMem.domain.table;

import java.time.Instant;
import java.util.UUID;

import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.UuidV7Generator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    // private Long id;

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "user_uuid", columnDefinition = "BINARY(16)")
    private UUID userUuid;

    @Column(name = "user_fullname")
    private String userFullName;

    private String phoneNumber;

    @NotBlank(message = "Không được để trống email")
    private String email;

    @NotBlank(message = "Không được để trống mật khẩu")
    private String password;

    private String accountStatus;
    // TODO: Sau này sửa thành enum

    @Min(value = 0)
    private Integer failedLoginAttempts;

    private Instant lastFailedAt;

    private Instant lockedUntil;

    private String verificationToken;

    private String resetPasswordToken;

    private Instant resetPasswordTokenExpiresAt;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;

    // // Relationship: n User -> 1 Role
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    // Audit fields
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = generateUUIDv7();
        }
        if (userUuid == null) {
            userUuid = id;
        }
        if (failedLoginAttempts == null) {
            failedLoginAttempts = 0;
        }

        createdAt = Instant.now();
        createdBy = SecurityUtil.getCurrentUserLogin().orElse("system");
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
        updatedBy = SecurityUtil.getCurrentUserLogin().orElse("system");
    }

    private UUID generateUUIDv7() {
        return UuidV7Generator.generate(); // tự implement / dùng lib
        // Ở đây dùng thư viện mã nguồn mở (chi tiết xem build.gradle.kts)
    }
}
