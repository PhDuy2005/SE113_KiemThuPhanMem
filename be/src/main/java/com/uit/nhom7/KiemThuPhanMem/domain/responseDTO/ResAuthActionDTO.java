package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResAuthActionDTO {
    private String message;
    private String token;
}
