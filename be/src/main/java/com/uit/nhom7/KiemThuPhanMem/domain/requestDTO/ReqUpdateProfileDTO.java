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
public class ReqUpdateProfileDTO {
    @NotBlank(message = "Full name must not be empty")
    private String fullName;

    @NotBlank(message = "Phone number must not be empty")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Phone number must contain 10 to 11 digits")
    private String phoneNumber;
}
