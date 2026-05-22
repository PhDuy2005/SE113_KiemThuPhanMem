package com.uit.nhom7.KiemThuPhanMem.service;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateShippingFeeDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResProvinceDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResShippingFeeDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.ShippingFeeConfig;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.ShippingFeeConfigRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

@Service
public class ShippingFeeConfigService {
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";
    private static final String BUSINESS_ADMIN_ROLE = "BUSINESS_ADMIN";
    private static final String MSG1 = "Province and shipping fee are required";
    private static final String MSG106 = "Shipping fees updated successfully";
    private static final String MSG107 = "Shipping fee must not be negative";
    private static final String MSG108 = "Shipping fee must be a valid amount with at most 2 decimal places";
    private static final Pattern SHIPPING_FEE_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]{1,2})?$");

    private final ShippingFeeConfigRepository shippingFeeConfigRepository;
    private final AddressDataService addressDataService;
    private final UserRepository userRepository;

    public ShippingFeeConfigService(
            ShippingFeeConfigRepository shippingFeeConfigRepository,
            AddressDataService addressDataService,
            UserRepository userRepository) {
        this.shippingFeeConfigRepository = shippingFeeConfigRepository;
        this.addressDataService = addressDataService;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ResShippingFeeDTO> getShippingFees() {
        getCurrentBusinessAdmin();
        return shippingFeeConfigRepository.findAllByOrderByProvinceAsc().stream()
                .map(config -> toDTO(config, null))
                .toList();
    }

    @Transactional
    public List<ResShippingFeeDTO> updateShippingFees(List<ReqUpdateShippingFeeDTO> request) {
        getCurrentBusinessAdmin();
        if (request == null || request.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG1);
        }

        Map<String, ShippingFeeConfig> currentConfigs = shippingFeeConfigRepository.findAll().stream()
                .collect(Collectors.toMap(ShippingFeeConfig::getProvinceKey, Function.identity()));
        for (ReqUpdateShippingFeeDTO item : request) {
            ResProvinceDTO provinceDTO = requireProvince(item);
            String province = provinceDTO.getName();
            String provinceCode = provinceDTO.getCode();
            String provinceKey = normalizeProvinceKey(province);
            BigDecimal shippingFee = parseShippingFee(item == null ? null : item.getShippingFee());

            ShippingFeeConfig config = currentConfigs.get(provinceKey);
            if (config == null) {
                config = ShippingFeeConfig.builder()
                        .provinceKey(provinceKey)
                        .build();
                currentConfigs.put(provinceKey, config);
            }
            config.setProvinceCode(provinceCode);
            config.setProvince(province);
            config.setShippingFee(shippingFee);
            shippingFeeConfigRepository.save(config);
        }

        return shippingFeeConfigRepository.findAllByOrderByProvinceAsc().stream()
                .map(config -> toDTO(config, MSG106))
                .toList();
    }

    @Transactional(readOnly = true)
    public BigDecimal getShippingFeeForProvince(String province) {
        String provinceKey = normalizeProvinceKey(province);
        return shippingFeeConfigRepository.findByProvinceKey(provinceKey)
                .map(ShippingFeeConfig::getShippingFee)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST,
                        "Shipping fee is not configured for province: " + province));
    }

    @Transactional(readOnly = true)
    public BigDecimal getShippingFeeForProvince(String provinceCode, String province) {
        if (provinceCode != null && !provinceCode.isBlank()) {
            return shippingFeeConfigRepository.findByProvinceCode(provinceCode.trim())
                    .map(ShippingFeeConfig::getShippingFee)
                    .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST,
                            "Shipping fee is not configured for province: " + province));
        }
        return getShippingFeeForProvince(province);
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

    private ResProvinceDTO requireProvince(ReqUpdateShippingFeeDTO item) {
        if (item == null
                || ((item.getProvinceCode() == null || item.getProvinceCode().isBlank())
                        && (item.getProvince() == null || item.getProvince().isBlank()))) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG1);
        }
        return addressDataService.requireProvince(item.getProvinceCode(), item.getProvince());
    }

    private BigDecimal parseShippingFee(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG1);
        }
        String cleanedValue = value.trim();
        if (cleanedValue.startsWith("-")) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG107);
        }
        if (!SHIPPING_FEE_PATTERN.matcher(cleanedValue).matches()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG108);
        }
        return new BigDecimal(cleanedValue).setScale(2);
    }

    private ResShippingFeeDTO toDTO(ShippingFeeConfig config, String message) {
        return ResShippingFeeDTO.builder()
                .id(config.getId())
                .provinceCode(config.getProvinceCode())
                .province(config.getProvince())
                .shippingFee(config.getShippingFee())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .message(message)
                .build();
    }

    private String normalizeProvinceKey(String province) {
        if (province == null || province.isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG1);
        }
        String normalized = Normalizer.normalize(province.trim().toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd')
                .replaceAll("\\s+", " ");
        return normalized;
    }
}
