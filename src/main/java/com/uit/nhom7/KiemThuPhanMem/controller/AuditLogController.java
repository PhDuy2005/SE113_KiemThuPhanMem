package com.uit.nhom7.KiemThuPhanMem.controller;

import java.time.Instant;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResultPaginationDTO;
import com.uit.nhom7.KiemThuPhanMem.service.AuditLogService;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/business/audit-logs")
public class AuditLogController {
    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    @ApiMessage("Business admin giam sat audit log")
    public ResponseEntity<ResultPaginationDTO> getAuditLogs(
            @RequestParam(value = "userId", required = false) UUID userId,
            @RequestParam(value = "actionType", required = false) String actionType,
            @RequestParam(value = "startTimestamp", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTimestamp,
            @RequestParam(value = "endTimestamp", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTimestamp,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(auditLogService.getAuditLogs(
                userId,
                actionType,
                startTimestamp,
                endTimestamp,
                pageNumber,
                pageSize));
    }
}
