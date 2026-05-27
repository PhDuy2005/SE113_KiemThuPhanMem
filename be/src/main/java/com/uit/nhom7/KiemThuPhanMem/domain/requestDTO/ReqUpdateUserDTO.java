package com.uit.nhom7.KiemThuPhanMem.domain.requestDTO;

import lombok.Data;

@Data
public class ReqUpdateUserDTO {
    private String fullName;
    private String phoneNumber;
    private Long roleId;
    private String roleName;
    private String accountStatus;
}
