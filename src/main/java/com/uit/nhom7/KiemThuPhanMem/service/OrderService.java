package com.uit.nhom7.KiemThuPhanMem.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqChangeOrderAddressDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOrderDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOrderDetailDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOrderItemDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOrderStatusTimelineDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResultPaginationDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Inventory;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Order;
import com.uit.nhom7.KiemThuPhanMem.domain.table.OrderItem;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Payment;
import com.uit.nhom7.KiemThuPhanMem.domain.table.ShippingAddress;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.InventoryRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.OrderItemRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.OrderRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.PaymentRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ShippingAddressRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

@Service
public class OrderService {
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";
    private static final String CUSTOMER_ROLE = "CUSTOMER";

    private final InventoryRepository inventoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final UserRepository userRepository;

    public OrderService(
            InventoryRepository inventoryRepository,
            OrderItemRepository orderItemRepository,
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            ShippingAddressRepository shippingAddressRepository,
            UserRepository userRepository) {
        this.inventoryRepository = inventoryRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.shippingAddressRepository = shippingAddressRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ResultPaginationDTO getOrderHistory(UUID userId, int pageNumber, int pageSize) {
        User currentUser = getCurrentActiveUser();
        UUID effectiveUserId = isCustomer(currentUser) ? currentUser.getId() : userId;
        if (effectiveUserId == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "User id is required");
        }

        Pageable pageable = PageRequest.of(
                Math.max(pageNumber - 1, 0),
                pageSize <= 0 ? 10 : pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orders = orderRepository.findByUserId(effectiveUserId, pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(orders.getTotalPages());
        meta.setTotalItems(orders.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(orders.getContent().stream().map(this::toOrderSummary).toList());
        return result;
    }

    @Transactional(readOnly = true)
    public ResOrderDetailDTO getOrderDetail(UUID orderId) {
        Order order = getAccessibleOrder(orderId);
        return toOrderDetail(order);
    }

    @Transactional(readOnly = true)
    public ResOrderStatusTimelineDTO getOrderStatusTimeline(UUID orderId) {
        Order order = getAccessibleOrder(orderId);
        return ResOrderStatusTimelineDTO.builder()
                .orderId(order.getId())
                .currentStatus(order.getStatus())
                .timeline(List.of(ResOrderStatusTimelineDTO.StatusStep.builder()
                        .statusName(order.getStatus())
                        .updatedAt(order.getUpdatedAt() == null ? order.getCreatedAt() : order.getUpdatedAt())
                        .description("Current order status")
                        .build()))
                .build();
    }

    @Transactional
    public ResOrderDTO cancelPendingOrder(UUID orderId) {
        Order order = getAccessibleOrder(orderId);
        if (!Order.PENDING_STATUS.equalsIgnoreCase(order.getStatus())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Order cannot be cancelled because it has been processed");
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrderIdWithProduct(orderId);
        for (OrderItem item : orderItems) {
            Inventory inventory = inventoryRepository.findById(item.getProduct().getId())
                    .orElse(Inventory.builder()
                            .productId(item.getProduct().getId())
                            .quantity(0)
                            .reservedQuantity(0)
                            .build());
            inventory.setQuantity((inventory.getQuantity() == null ? 0 : inventory.getQuantity()) + item.getQuantity());
            inventoryRepository.save(inventory);
        }

        order.setStatus(Order.CANCELLED_STATUS);
        Order savedOrder = orderRepository.save(order);

        return ResOrderDTO.builder()
                .orderId(savedOrder.getId())
                .status(savedOrder.getStatus())
                .totalProductAmount(savedOrder.getTotalProductAmount())
                .shippingFee(savedOrder.getShippingFee())
                .discountAmount(savedOrder.getDiscountAmount())
                .totalAmount(savedOrder.getTotalAmount())
                .message("Order cancelled successfully")
                .build();
    }

    @Transactional(readOnly = true)
    public List<?> getAvailableAddressesForOrder(UUID orderId) {
        Order order = getAccessibleOrder(orderId);
        return shippingAddressRepository.findByUserIdAndDeletedAtIsNull(order.getUser().getId()).stream()
                .map(address -> com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResShippingAddressDTO.builder()
                        .id(address.getId())
                        .userId(address.getUser().getId())
                        .province(address.getProvince())
                        .ward(address.getWard())
                        .detail(address.getDetail())
                        .defaultAddress(address.isDefaultAddress())
                        .createdAt(address.getCreatedAt())
                        .updatedAt(address.getUpdatedAt())
                        .build())
                .toList();
    }

    @Transactional
    public ResOrderDetailDTO changeShippingAddress(UUID orderId, ReqChangeOrderAddressDTO request) {
        Order order = getAccessibleOrder(orderId);
        if (Order.SHIPPING_STATUS.equalsIgnoreCase(order.getStatus())
                || Order.DELIVERED_STATUS.equalsIgnoreCase(order.getStatus())
                || Order.CANCELLED_STATUS.equalsIgnoreCase(order.getStatus())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Cannot change address because order has been shipped");
        }

        ShippingAddress newAddress = shippingAddressRepository
                .findByIdAndUserIdAndDeletedAtIsNull(request.getNewAddressId(), order.getUser().getId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Shipping address not found"));
        order.setShippingAddressSnapshot(buildShippingAddressSnapshot(newAddress));
        return toOrderDetail(orderRepository.save(order));
    }

    private Order getAccessibleOrder(UUID orderId) {
        User currentUser = getCurrentActiveUser();
        if (isCustomer(currentUser)) {
            return orderRepository.findByIdAndUserId(orderId, currentUser.getId())
                    .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order not found"));
        }
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    private ResOrderDTO toOrderSummary(Order order) {
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        return ResOrderDTO.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .totalProductAmount(order.getTotalProductAmount())
                .shippingFee(order.getShippingFee())
                .discountAmount(order.getDiscountAmount())
                .totalAmount(order.getTotalAmount())
                .paymentId(payment == null ? null : payment.getId())
                .paymentStatus(payment == null ? null : payment.getStatus())
                .build();
    }

    private ResOrderDetailDTO toOrderDetail(Order order) {
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        List<ResOrderItemDTO> items = orderItemRepository.findByOrderIdWithProduct(order.getId()).stream()
                .map(this::toOrderItemDTO)
                .toList();
        return ResOrderDetailDTO.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .totalProductAmount(order.getTotalProductAmount())
                .shippingFee(order.getShippingFee())
                .discountAmount(order.getDiscountAmount())
                .totalAmount(order.getTotalAmount())
                .shippingAddressSnapshot(order.getShippingAddressSnapshot())
                .paymentMethod(payment == null ? null : payment.getPaymentMethod().getName())
                .paymentStatus(payment == null ? null : payment.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(items)
                .build();
    }

    private ResOrderItemDTO toOrderItemDTO(OrderItem item) {
        BigDecimal lineTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return ResOrderItemDTO.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .lineTotal(lineTotal)
                .build();
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

    private String buildShippingAddressSnapshot(ShippingAddress address) {
        return String.join(", ", address.getDetail(), address.getWard(), address.getProvince());
    }
}
