package com.uit.nhom7.KiemThuPhanMem.domain.requestDTO;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ReqUpdateProductPriceDTO {
    private BigDecimal newPrice;
}
