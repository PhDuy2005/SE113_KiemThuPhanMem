package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResShippingAddressDTO {
    private UUID id;
    private UUID userId;
    private String province;
    private String ward;
    private String detail;

    @JsonProperty("isDefault")
    private boolean defaultAddress;

    private Instant createdAt;
    private Instant updatedAt;
}
