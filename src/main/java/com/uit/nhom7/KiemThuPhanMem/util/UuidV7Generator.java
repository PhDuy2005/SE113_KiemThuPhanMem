package com.uit.nhom7.KiemThuPhanMem.util;

import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UuidV7Generator {
    public static UUID generate() {
        return UuidCreator.getTimeOrderedEpoch(); // UUID v7
    }
}
