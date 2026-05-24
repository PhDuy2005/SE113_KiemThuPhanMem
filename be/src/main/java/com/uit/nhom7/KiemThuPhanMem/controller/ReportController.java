package com.uit.nhom7.KiemThuPhanMem.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResBestSellingProductsReportDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResCategoryDistributionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResRevenueReportDTO;
import com.uit.nhom7.KiemThuPhanMem.service.ReportService;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/business/reports")
public class ReportController {
    private static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/revenue")
    @ApiMessage("Business admin xem bieu do doanh thu theo thoi gian")
    public ResponseEntity<ResRevenueReportDTO> getRevenueReport(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getRevenueReport(startDate, endDate));
    }

    @GetMapping("/best-selling-products")
    @ApiMessage("Business admin xem bao cao san pham ban chay")
    public ResponseEntity<ResBestSellingProductsReportDTO> getBestSellingProductsReport(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "limit", required = false) Integer limit) {
        return ResponseEntity.ok(reportService.getBestSellingProductsReport(startDate, endDate, limit));
    }

    @GetMapping("/category-distribution")
    @ApiMessage("Business admin xem phan bo doanh thu theo danh muc")
    public ResponseEntity<ResCategoryDistributionDTO> getCategoryDistributionReport(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reportService.getCategoryDistributionReport(startDate, endDate));
    }

    @GetMapping("/orders/export")
    @ApiMessage("Business admin xuat don hang ra Excel")
    public ResponseEntity<byte[]> exportOrders(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "orderStatus", required = false) String orderStatus) {
        byte[] excelBytes = reportService.exportOrders(startDate, endDate, orderStatus);
        String fileName = "Orders_Export_%s.xlsx".formatted(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType(EXCEL_CONTENT_TYPE))
                .body(excelBytes);
    }
}
