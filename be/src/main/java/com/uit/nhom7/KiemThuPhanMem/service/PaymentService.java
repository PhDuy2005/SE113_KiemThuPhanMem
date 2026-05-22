package com.uit.nhom7.KiemThuPhanMem.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqOnlinePaymentDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOnlinePaymentDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.PaymentMethod;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.OrderRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.PaymentMethodRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

import java.util.Locale;

@Service
public class PaymentService {
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";
    private static final String CUSTOMER_ROLE = "CUSTOMER";

    private final OrderRepository orderRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    public PaymentService(
            OrderRepository orderRepository,
            PaymentMethodRepository paymentMethodRepository,
            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ResOnlinePaymentDTO initializeOnlinePayment(ReqOnlinePaymentDTO request) {
        User currentUser = getCurrentActiveUser();
        if (isCustomer(currentUser)) {
            orderRepository.findByIdAndUserId(request.getOrderId(), currentUser.getId())
                    .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order not found"));
        } else {
            orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order not found"));
        }
        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Invalid payment method"));
        if (!PaymentMethod.ONLINE_TYPE.equalsIgnoreCase(paymentMethod.getType())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Payment method is not online payment");
        }

        throw new BusinessException(HttpStatus.NOT_IMPLEMENTED,
                "Online payment is not supported yet. Current supported payment method is cash.");
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

    private boolean isCustomer(User user) {
        return user.getRole() != null
                && user.getRole().getName() != null
                && CUSTOMER_ROLE.equals(user.getRole().getName().trim().toUpperCase(Locale.ROOT));
    }
}
