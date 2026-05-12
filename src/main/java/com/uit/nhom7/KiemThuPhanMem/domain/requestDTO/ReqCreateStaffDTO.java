package com.uit.nhom7.KiemThuPhanMem.domain.requestDTO;

import lombok.Data;

@Data
public class ReqCreateStaffDTO {
    private String email;
    private String fullName;
    private Long roleId;
}
