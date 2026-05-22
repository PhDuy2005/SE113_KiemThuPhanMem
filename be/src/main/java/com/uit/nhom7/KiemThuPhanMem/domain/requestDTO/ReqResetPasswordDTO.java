package com.uit.nhom7.KiemThuPhanMem.domain.requestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqResetPasswordDTO {
    @NotBlank(message = "Reset token must not be empty")
    private String token;

    @NotBlank(message = "New password must not be empty")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", message = "Password must contain at least 8 characters, one letter, one number, and one special character")
    private String newPassword;

    @NotBlank(message = "Confirm password must not be empty")
    private String confirmPassword;
}
