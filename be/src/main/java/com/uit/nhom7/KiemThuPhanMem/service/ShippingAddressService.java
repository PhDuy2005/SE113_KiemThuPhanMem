package com.uit.nhom7.KiemThuPhanMem.service;

import java.util.Locale;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqShippingAddressDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResShippingAddressDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.ShippingAddress;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.service.AddressDataService.ResolvedAddress;
import com.uit.nhom7.KiemThuPhanMem.repository.ShippingAddressRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

@Service
public class ShippingAddressService {
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";

    private final ShippingAddressRepository shippingAddressRepository;
    private final AddressDataService addressDataService;
    private final UserRepository userRepository;

    public ShippingAddressService(
            ShippingAddressRepository shippingAddressRepository,
            AddressDataService addressDataService,
            UserRepository userRepository) {
        this.shippingAddressRepository = shippingAddressRepository;
        this.addressDataService = addressDataService;
        this.userRepository = userRepository;
    }

    @Transactional
    public ResShippingAddressDTO createAddress(ReqShippingAddressDTO request) {
        User currentUser = getCurrentActiveUser();
        boolean firstAddress = !shippingAddressRepository.existsByUserIdAndDeletedAtIsNull(currentUser.getId());
        ResolvedAddress resolvedAddress = resolveAddress(request);

        ShippingAddress address = ShippingAddress.builder()
                .user(currentUser)
                .provinceCode(resolvedAddress.provinceCode())
                .province(resolvedAddress.province())
                .wardCode(resolvedAddress.wardCode())
                .ward(resolvedAddress.ward())
                .detail(resolvedAddress.detail())
                .defaultAddress(firstAddress)
                .build();

        return convertToDTO(shippingAddressRepository.save(address));
    }

    @Transactional
    public ResShippingAddressDTO updateDefaultAddress(ReqShippingAddressDTO request) {
        User currentUser = getCurrentActiveUser();
        ShippingAddress defaultAddress = shippingAddressRepository
                .findByUserIdAndDefaultAddressTrueAndDeletedAtIsNull(currentUser.getId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Default shipping address not found"));
        ResolvedAddress resolvedAddress = resolveAddress(request);

        defaultAddress.setProvinceCode(resolvedAddress.provinceCode());
        defaultAddress.setProvince(resolvedAddress.province());
        defaultAddress.setWardCode(resolvedAddress.wardCode());
        defaultAddress.setWard(resolvedAddress.ward());
        defaultAddress.setDetail(resolvedAddress.detail());

        return convertToDTO(shippingAddressRepository.save(defaultAddress));
    }

    @Transactional
    public ResShippingAddressDTO setDefaultAddress(UUID addressId) {
        User currentUser = getCurrentActiveUser();
        ShippingAddress selectedAddress = shippingAddressRepository
                .findByIdAndUserIdAndDeletedAtIsNull(addressId, currentUser.getId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Shipping address not found"));

        if (!selectedAddress.isDefaultAddress()) {
            shippingAddressRepository.clearDefaultByUserId(currentUser.getId());
            selectedAddress.setDefaultAddress(true);
        }

        return convertToDTO(shippingAddressRepository.save(selectedAddress));
    }

    private User getCurrentActiveUser() {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "You must login first"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "User session is invalid"));
        if (user.getAccountStatus() == null
                || !ACTIVE_ACCOUNT_STATUS.equals(user.getAccountStatus().trim().toUpperCase(Locale.ROOT))) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "User account is not active");
        }
        return user;
    }

    private ResolvedAddress resolveAddress(ReqShippingAddressDTO request) {
        return addressDataService.resolveAddress(
                request.getProvinceCode(),
                request.getProvince(),
                request.getWardCode(),
                request.getWard(),
                request.getDetail());
    }

    private ResShippingAddressDTO convertToDTO(ShippingAddress address) {
        return ResShippingAddressDTO.builder()
                .id(address.getId())
                .userId(address.getUser().getId())
                .provinceCode(address.getProvinceCode())
                .province(address.getProvince())
                .wardCode(address.getWardCode())
                .ward(address.getWard())
                .detail(address.getDetail())
                .defaultAddress(address.isDefaultAddress())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}
