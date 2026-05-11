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
import com.uit.nhom7.KiemThuPhanMem.repository.ShippingAddressRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

@Service
public class ShippingAddressService {
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";

    private final ShippingAddressRepository shippingAddressRepository;
    private final UserRepository userRepository;

    public ShippingAddressService(
            ShippingAddressRepository shippingAddressRepository,
            UserRepository userRepository) {
        this.shippingAddressRepository = shippingAddressRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ResShippingAddressDTO createAddress(ReqShippingAddressDTO request) {
        User currentUser = getCurrentActiveUser();
        boolean firstAddress = !shippingAddressRepository.existsByUserIdAndDeletedAtIsNull(currentUser.getId());

        ShippingAddress address = ShippingAddress.builder()
                .user(currentUser)
                .province(request.getProvince().trim())
                .ward(request.getWard().trim())
                .detail(request.getDetail().trim())
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

        defaultAddress.setProvince(request.getProvince().trim());
        defaultAddress.setWard(request.getWard().trim());
        defaultAddress.setDetail(request.getDetail().trim());

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

    private ResShippingAddressDTO convertToDTO(ShippingAddress address) {
        return ResShippingAddressDTO.builder()
                .id(address.getId())
                .userId(address.getUser().getId())
                .province(address.getProvince())
                .ward(address.getWard())
                .detail(address.getDetail())
                .defaultAddress(address.isDefaultAddress())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}
