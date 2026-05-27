package com.uit.nhom7.KiemThuPhanMem.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqCreateVoucherDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResVoucherDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResultPaginationDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Voucher;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.VoucherRepository;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

@Service
public class VoucherManagementService {
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";
    private static final String BUSINESS_ADMIN_ROLE = "BUSINESS_ADMIN";
    private static final String MSG1 = "Required field is missing";
    private static final String MSG87 = "Voucher created successfully";
    private static final String MSG88 = "Voucher code already exists";
    private static final String MSG89 = "End date must be after start date";
    private static final String MSG90 = "Discount value and quantity must be greater than 0";
    private static final String MSG92 = "Voucher stopped successfully";

    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;

    public VoucherManagementService(UserRepository userRepository, VoucherRepository voucherRepository) {
        this.userRepository = userRepository;
        this.voucherRepository = voucherRepository;
    }

    @Transactional
    public ResVoucherDTO createVoucher(ReqCreateVoucherDTO request) {
        getCurrentBusinessAdmin();
        validateCreateVoucherRequest(request);

        String voucherCode = request.getVoucherCode().trim().toUpperCase(Locale.ROOT);
        if (voucherRepository.existsByCodeIgnoreCase(voucherCode)) {
            throw new BusinessException(HttpStatus.CONFLICT, MSG88);
        }

        Voucher voucher = Voucher.builder()
                .code(voucherCode)
                .type(normalizeDiscountType(request.getDiscountType()))
                .value(request.getDiscountValue())
                .maxUsage(request.getQuantity())
                .usedCount(0)
                .minOrderAmount(request.getMinOrderAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .active(startsToday(request.getStartDate()))
                .status(startsToday(request.getStartDate()) ? Voucher.ACTIVE_STATUS : "SCHEDULED")
                .build();

        return toDTO(voucherRepository.save(voucher), MSG87);
    }

    @Transactional
    public ResVoucherDTO emergencyStopVoucher(UUID voucherId) {
        getCurrentBusinessAdmin();
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Voucher not found"));
        voucher.setActive(false);
        voucher.setStatus(Voucher.STOPPED_STATUS);
        return toDTO(voucherRepository.save(voucher), MSG92);
    }

    @Transactional(readOnly = true)
    public ResultPaginationDTO getAllVouchersForAdmin(int pageNumber, int pageSize) {
        getCurrentBusinessAdmin();
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                Math.max(pageNumber - 1, 0),
                pageSize <= 0 ? 20 : pageSize,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        org.springframework.data.domain.Page<Voucher> vouchers = voucherRepository.findAll(pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(vouchers.getTotalPages());
        meta.setTotalItems(vouchers.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(vouchers.getContent().stream()
                .map(voucher -> toDTO(voucher, null))
                .toList());
        return result;
    }

    @Transactional(readOnly = true)
    public ResultPaginationDTO getPublicVouchers(int pageNumber, int pageSize) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                Math.max(pageNumber - 1, 0),
                pageSize <= 0 ? 20 : pageSize,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        org.springframework.data.domain.Page<Voucher> vouchers = voucherRepository
                .findByActiveTrueAndStatusIgnoreCaseAndEndDateAfter(Voucher.ACTIVE_STATUS, Instant.now(), pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(vouchers.getTotalPages());
        meta.setTotalItems(vouchers.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(vouchers.getContent().stream()
                .filter(v -> v.getMaxUsage() == null || v.getUsedCount() == null || v.getUsedCount() < v.getMaxUsage())
                .map(voucher -> toDTO(voucher, null))
                .toList());
        return result;
    }

    private void validateCreateVoucherRequest(ReqCreateVoucherDTO request) {
        if (request == null
                || request.getVoucherCode() == null || request.getVoucherCode().isBlank()
                || request.getDiscountValue() == null
                || request.getDiscountType() == null || request.getDiscountType().isBlank()
                || request.getQuantity() == null
                || request.getStartDate() == null
                || request.getEndDate() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG1);
        }
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG89);
        }
        if (request.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0 || request.getQuantity() <= 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG90);
        }
        normalizeDiscountType(request.getDiscountType());
    }

    private String normalizeDiscountType(String discountType) {
        String normalizedType = discountType.trim().toUpperCase(Locale.ROOT);
        if (!Voucher.FIXED_TYPE.equals(normalizedType) && !Voucher.PERCENT_TYPE.equals(normalizedType)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "discountType must be FIXED or PERCENT");
        }
        return normalizedType;
    }

    private boolean startsToday(Instant startDate) {
        LocalDate start = startDate.atZone(ZoneId.systemDefault()).toLocalDate();
        return LocalDate.now(ZoneId.systemDefault()).equals(start);
    }

    private User getCurrentBusinessAdmin() {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "You must login first"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "User session is invalid"));
        if (user.getAccountStatus() == null
                || !ACTIVE_ACCOUNT_STATUS.equals(user.getAccountStatus().trim().toUpperCase(Locale.ROOT))) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "User account is not active");
        }
        String roleName = user.getRole() == null || user.getRole().getName() == null
                ? ""
                : user.getRole().getName().trim().toUpperCase(Locale.ROOT);
        if (!BUSINESS_ADMIN_ROLE.equals(roleName)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Only business admin can perform this action");
        }
        return user;
    }

    private ResVoucherDTO toDTO(Voucher voucher, String message) {
        return ResVoucherDTO.builder()
                .id(voucher.getId())
                .voucherCode(voucher.getCode())
                .discountType(voucher.getType())
                .discountValue(voucher.getValue())
                .quantity(voucher.getMaxUsage())
                .usedCount(voucher.getUsedCount())
                .minOrderAmount(voucher.getMinOrderAmount())
                .startDate(voucher.getStartDate())
                .endDate(voucher.getEndDate())
                .active(voucher.isActive())
                .status(voucher.getStatus())
                .createdAt(voucher.getCreatedAt())
                .updatedAt(voucher.getUpdatedAt())
                .message(message)
                .build();
    }
}
