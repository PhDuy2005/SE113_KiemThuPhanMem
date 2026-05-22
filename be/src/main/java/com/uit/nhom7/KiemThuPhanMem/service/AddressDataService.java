package com.uit.nhom7.KiemThuPhanMem.service;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResProvinceDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResWardDTO;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

@Service
public class AddressDataService {
    private static final String MSG_INVALID_PROVINCE = "Province is invalid";
    private static final String MSG_INVALID_WARD = "Ward is invalid for selected province";

    private final List<ResProvinceDTO> provinces;
    private final Map<String, ResProvinceDTO> provinceByCode;
    private final Map<String, ResProvinceDTO> provinceByAlias;
    private final Map<String, ResWardDTO> wardByCode;
    private final Map<String, List<ResWardDTO>> wardsByProvinceCode;
    private final Map<String, Map<String, ResWardDTO>> wardsByProvinceAndAlias;

    public AddressDataService() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, AddressDataItem> provinceData = objectMapper.readValue(
                    new ClassPathResource("address_data/province.json").getInputStream(),
                    new TypeReference<Map<String, AddressDataItem>>() {
                    });
            Map<String, AddressDataItem> wardData = objectMapper.readValue(
                    new ClassPathResource("address_data/ward.json").getInputStream(),
                    new TypeReference<Map<String, AddressDataItem>>() {
                    });

            this.provinceByCode = new HashMap<>();
            this.provinceByAlias = new HashMap<>();
            this.provinces = provinceData.values().stream()
                    .map(this::toProvinceDTO)
                    .sorted(Comparator.comparing(ResProvinceDTO::getName))
                    .toList();
            for (ResProvinceDTO province : provinces) {
                provinceByCode.put(province.getCode(), province);
                addProvinceAlias(province.getCode(), province);
                addProvinceAlias(province.getName(), province);
                addProvinceAlias(province.getNameWithType(), province);
                addProvinceAlias(province.getSlug(), province);
            }

            this.wardByCode = new HashMap<>();
            this.wardsByProvinceCode = new HashMap<>();
            this.wardsByProvinceAndAlias = new HashMap<>();
            for (AddressDataItem item : wardData.values()) {
                ResWardDTO ward = toWardDTO(item);
                if (!provinceByCode.containsKey(ward.getParentCode())) {
                    continue;
                }
                wardByCode.put(ward.getCode(), ward);
                wardsByProvinceCode.computeIfAbsent(ward.getParentCode(), ignored -> new ArrayList<>()).add(ward);
                Map<String, ResWardDTO> aliasMap = wardsByProvinceAndAlias.computeIfAbsent(
                        ward.getParentCode(), ignored -> new HashMap<>());
                addWardAlias(aliasMap, ward.getCode(), ward);
                addWardAlias(aliasMap, ward.getName(), ward);
                addWardAlias(aliasMap, ward.getNameWithType(), ward);
                addWardAlias(aliasMap, ward.getSlug(), ward);
            }
            for (List<ResWardDTO> wards : wardsByProvinceCode.values()) {
                wards.sort(Comparator.comparing(ResWardDTO::getName));
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot load Vietnam address data", ex);
        }
    }

    public List<ResProvinceDTO> getProvinces() {
        return provinces;
    }

    public List<ResWardDTO> getWardsByProvinceCode(String provinceCode) {
        ResProvinceDTO province = findProvince(provinceCode, null)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, MSG_INVALID_PROVINCE));
        return wardsByProvinceCode.getOrDefault(province.getCode(), List.of());
    }

    public ResProvinceDTO requireProvince(String provinceCode, String provinceName) {
        return findProvince(provinceCode, provinceName)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, MSG_INVALID_PROVINCE));
    }

    public ResolvedAddress resolveAddress(
            String provinceCode,
            String provinceName,
            String wardCode,
            String wardName,
            String detail) {
        ResProvinceDTO province = requireProvince(provinceCode, provinceName);
        ResWardDTO ward = findWard(province.getCode(), wardCode, wardName)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, MSG_INVALID_WARD));
        if (detail == null || detail.isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Detail must not be empty");
        }
        return new ResolvedAddress(
                province.getCode(),
                province.getName(),
                ward.getCode(),
                ward.getName(),
                detail.trim());
    }

    public Optional<ResProvinceDTO> findProvince(String provinceCode, String provinceName) {
        if (provinceCode != null && !provinceCode.isBlank()) {
            return Optional.ofNullable(provinceByCode.get(provinceCode.trim()));
        }
        if (provinceName == null || provinceName.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(provinceByAlias.get(normalizeKey(provinceName)));
    }

    private Optional<ResWardDTO> findWard(String provinceCode, String wardCode, String wardName) {
        if (wardCode != null && !wardCode.isBlank()) {
            ResWardDTO ward = wardByCode.get(wardCode.trim());
            if (ward != null && provinceCode.equals(ward.getParentCode())) {
                return Optional.of(ward);
            }
            return Optional.empty();
        }
        if (wardName == null || wardName.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(wardsByProvinceAndAlias.getOrDefault(provinceCode, Map.of())
                .get(normalizeKey(wardName)));
    }

    private ResProvinceDTO toProvinceDTO(AddressDataItem item) {
        return ResProvinceDTO.builder()
                .code(item.code)
                .name(item.name)
                .nameWithType(item.nameWithType)
                .slug(item.slug)
                .type(item.type)
                .build();
    }

    private ResWardDTO toWardDTO(AddressDataItem item) {
        return ResWardDTO.builder()
                .code(item.code)
                .parentCode(item.parentCode)
                .name(item.name)
                .nameWithType(item.nameWithType)
                .pathWithType(item.pathWithType)
                .slug(item.slug)
                .type(item.type)
                .build();
    }

    private void addProvinceAlias(String value, ResProvinceDTO province) {
        if (value != null && !value.isBlank()) {
            provinceByAlias.put(normalizeKey(value), province);
        }
    }

    private void addWardAlias(Map<String, ResWardDTO> aliasMap, String value, ResWardDTO ward) {
        if (value != null && !value.isBlank()) {
            aliasMap.put(normalizeKey(value), ward);
        }
    }

    public String normalizeKey(String value) {
        String normalized = Normalizer.normalize(value.trim().toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd')
                .replaceAll("\\s+", " ");
        return normalized;
    }

    public record ResolvedAddress(
            String provinceCode,
            String province,
            String wardCode,
            String ward,
            String detail) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class AddressDataItem {
        public String name;
        public String type;
        public String slug;
        public String code;

        @JsonProperty("name_with_type")
        public String nameWithType;

        @JsonProperty("path_with_type")
        public String pathWithType;

        @JsonProperty("parent_code")
        public String parentCode;
    }
}
