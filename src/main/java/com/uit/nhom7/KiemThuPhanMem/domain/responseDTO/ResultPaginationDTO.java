package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ResultPaginationDTO {
    private Meta meta;
    private Object result;
    private String message;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Meta {
        private int page;
        private int pageSize;
        private int totalPages;
        private long totalItems;
    }
}
