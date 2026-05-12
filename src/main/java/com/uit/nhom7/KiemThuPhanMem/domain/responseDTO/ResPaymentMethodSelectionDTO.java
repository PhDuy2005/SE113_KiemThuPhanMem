package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResPaymentMethodSelectionDTO {
    private UUID paymentMethodId;
    private String name;
    private String type;
    private String instruction;
}
