package com.uit.nhom7.KiemThuPhanMem.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqChangeOrderAddressDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqStaffCancelOrderDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateShippingStatusDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOrderDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOrderDetailDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOrderItemDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOrderStatusTimelineDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResultPaginationDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Inventory;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Notification;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Order;
import com.uit.nhom7.KiemThuPhanMem.domain.table.OrderItem;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Payment;
import com.uit.nhom7.KiemThuPhanMem.domain.table.PaymentMethod;
import com.uit.nhom7.KiemThuPhanMem.domain.table.ShippingAddress;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.InventoryRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.NotificationRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.OrderItemRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.OrderRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.PaymentRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ShippingAddressRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

@Service
public class OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";
    private static final String CUSTOMER_ROLE = "CUSTOMER";
    private static final String STAFF_ROLE = "STAFF";
    private static final String BUSINESS_ADMIN_ROLE = "BUSINESS_ADMIN";
    private static final String MSG52 = "There are no new orders";
    private static final String MSG53 = "Shipping status updated successfully";
    private static final String MSG55 = "Order approved successfully";
    private static final String MSG57 = "Order delivered successfully";
    private static final String MSG59 = "Cancel reason is required";
    private static final String MSG60 = "Order not found";
    private static final String MSG61 = "Order has already been delivered or cancelled";
    private static final String MSG62 = "Refund completed successfully";
    private static final String MSG62_ORDER_STATUS = "Order cannot be cancelled in its current status";
    private static final String MSG63 = "Order cancelled successfully";
    private static final String MSG64 = "Order is not eligible for refund";
    private static final String MSG65 = "No matching orders found";
    private static final String PENDING_REFUND_STATUS = "PENDING_REFUND";
    private static final String REFUNDED_STATUS = "REFUNDED";

    private final InventoryRepository inventoryRepository;
    private final NotificationRepository notificationRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final ShippingFeeConfigService shippingFeeConfigService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public OrderService(
            InventoryRepository inventoryRepository,
            NotificationRepository notificationRepository,
            OrderItemRepository orderItemRepository,
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            ShippingAddressRepository shippingAddressRepository,
            ShippingFeeConfigService shippingFeeConfigService,
            UserRepository userRepository,
            EmailService emailService) {
        this.inventoryRepository = inventoryRepository;
        this.notificationRepository = notificationRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.shippingAddressRepository = shippingAddressRepository;
        this.shippingFeeConfigService = shippingFeeConfigService;
        this.userRepository = userRepository;
        this.emailService = emailService;
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
        result.setResult(orders.getContent().stream().map(order -> toOrderSummary(order, null)).toList());
        return result;
    }

    @Transactional(readOnly = true)
    public ResultPaginationDTO getPendingOrdersForStaff(UUID userId, int pageNumber, int pageSize) {
        getCurrentStaffOrBusinessAdmin();

        Pageable pageable = PageRequest.of(
                Math.max(pageNumber - 1, 0),
                pageSize <= 0 ? 20 : pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orders = userId == null
                ? orderRepository.findByStatusIgnoreCase(Order.PENDING_STATUS, pageable)
                : orderRepository.findByStatusIgnoreCaseAndUserId(Order.PENDING_STATUS, userId, pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(orders.getTotalPages());
        meta.setTotalItems(orders.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(orders.getContent().stream()
                .map(order -> toOrderSummary(order, null))
                .toList());
        result.setMessage(orders.isEmpty() ? MSG52 : null);
        return result;
    }

    @Transactional(readOnly = true)
    public ResOrderDetailDTO getOrderDetail(UUID orderId) {
        Order order = getAccessibleOrder(orderId);
        return toOrderDetail(order);
    }

    @Transactional(readOnly = true)
    public ResOrderDetailDTO getOrderDetailForStaff(UUID orderId) {
        getCurrentStaffOrBusinessAdmin();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order not found"));
        return toOrderDetail(order);
    }

    @Transactional
    public ResOrderDTO approveOrder(UUID orderId) {
        User staff = getCurrentStaffOrBusinessAdmin();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order not found"));
        if (!Order.PENDING_STATUS.equalsIgnoreCase(order.getStatus())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Only pending orders can be approved");
        }

        order.setStatus(Order.APPROVED_STATUS);
        Order savedOrder = orderRepository.save(order);
        notificationRepository.save(Notification.builder()
                .user(savedOrder.getUser())
                .title("Order Approved")
                .content("Your order has been approved")
                .refTo(savedOrder.getId())
                .read(false)
                .build());
        LOGGER.info("Audit action={}, staffId={}, orderId={}", "APPROVE_ORDER", staff.getId(), savedOrder.getId());

        return toOrderSummary(savedOrder, MSG55);
    }

    @Transactional
    public ResOrderDetailDTO updateShippingStatus(UUID orderId, ReqUpdateShippingStatusDTO request) {
        User staff = getCurrentStaffOrBusinessAdmin();
        if (request.getTrackingNumber() == null || request.getTrackingNumber().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Tracking number is required");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order not found"));
        if (!Order.APPROVED_STATUS.equalsIgnoreCase(order.getStatus())
                && !Order.SHIPPING_STATUS.equalsIgnoreCase(order.getStatus())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Only approved orders can be moved to shipping");
        }

        order.setTrackingNumber(request.getTrackingNumber().trim());
        order.setStatus(Order.SHIPPING_STATUS);
        Order savedOrder = orderRepository.save(order);
        notificationRepository.save(Notification.builder()
                .user(savedOrder.getUser())
                .title("Order Shipping")
                .content("Your order is being shipped")
                .refTo(savedOrder.getId())
                .read(false)
                .build());
        LOGGER.info("Audit action={}, staffId={}, orderId={}, status={}",
                "UPDATE_STATUS", staff.getId(), savedOrder.getId(), Order.SHIPPING_STATUS);

        return toOrderDetail(savedOrder, MSG53);
    }

    @Transactional
    public ResOrderDetailDTO markOrderDelivered(UUID orderId) {
        User staff = getCurrentStaffOrBusinessAdmin();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order not found"));
        if (!Order.SHIPPING_STATUS.equalsIgnoreCase(order.getStatus())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Only shipping orders can be marked as delivered");
        }

        order.setStatus(Order.DELIVERED_STATUS);
        order.setCompletedAt(Instant.now());
        Order savedOrder = orderRepository.save(order);

        paymentRepository.findByOrderId(savedOrder.getId()).ifPresent(payment -> {
            payment.setStatus(Payment.SUCCESS_STATUS);
            paymentRepository.save(payment);
        });
        notificationRepository.save(Notification.builder()
                .user(savedOrder.getUser())
                .title("Order Delivered")
                .content("Your order has been delivered")
                .refTo(savedOrder.getId())
                .read(false)
                .build());
        LOGGER.info("Audit action={}, staffId={}, orderId={}", "SET_DELIVERED", staff.getId(), savedOrder.getId());

        return toOrderDetail(savedOrder, MSG57);
    }

    @Transactional(readOnly = true)
    public ResultPaginationDTO searchOrdersForStaff(String searchKeyword, int pageNumber, int pageSize) {
        getCurrentStaffOrBusinessAdmin();
        if (searchKeyword == null || searchKeyword.isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Search keyword is required");
        }

        String keyword = searchKeyword.trim();
        Pageable pageable = PageRequest.of(
                Math.max(pageNumber - 1, 0),
                pageSize <= 0 ? 20 : pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        ResultPaginationDTO result = new ResultPaginationDTO();

        try {
            UUID orderId = UUID.fromString(keyword);
            List<ResOrderDTO> orders = orderRepository.findById(orderId)
                    .map(order -> List.of(toOrderSummary(order, null)))
                    .orElseGet(List::of);
            ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
            meta.setPage(1);
            meta.setPageSize(pageable.getPageSize());
            meta.setTotalPages(orders.isEmpty() ? 0 : 1);
            meta.setTotalItems(orders.size());
            result.setMeta(meta);
            result.setResult(orders);
            result.setMessage(orders.isEmpty() ? MSG65 : null);
            return result;
        } catch (IllegalArgumentException ignored) {
            Page<Order> orders = orderRepository.findByUserPhoneNumberContaining(keyword, pageable);
            ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
            meta.setPage(pageable.getPageNumber() + 1);
            meta.setPageSize(pageable.getPageSize());
            meta.setTotalPages(orders.getTotalPages());
            meta.setTotalItems(orders.getTotalElements());
            result.setMeta(meta);
            result.setResult(orders.getContent().stream()
                    .map(order -> toOrderSummary(order, null))
                    .toList());
            result.setMessage(orders.isEmpty() ? MSG65 : null);
            return result;
        }
    }

    @Transactional
    public ResOrderDTO cancelOrderForStaff(UUID orderId, ReqStaffCancelOrderDTO request) {
        User staff = getCurrentStaffOrBusinessAdmin();
        if (request == null || request.getCancelReason() == null || request.getCancelReason().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG59);
        }
        String cancelReason = request.getCancelReason().trim();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, MSG60));
        if (Order.DELIVERED_STATUS.equalsIgnoreCase(order.getStatus())
                || Order.CANCELLED_STATUS.equalsIgnoreCase(order.getStatus())) {
            throw new BusinessException(HttpStatus.CONFLICT, MSG61);
        }
        if (!isCancellableByStaff(order)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG62_ORDER_STATUS);
        }

        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        boolean wasShipping = Order.SHIPPING_STATUS.equalsIgnoreCase(order.getStatus());
        restoreInventory(order.getId());
        order.setStatus(Order.CANCELLED_STATUS);
        order.setCancelReason(cancelReason);
        order.setCancelledAt(Instant.now());
        if (isOnlinePaidPayment(payment)) {
            order.setRefundStatus(PENDING_REFUND_STATUS);
        }
        if (wasShipping) {
            LOGGER.info("Stop shipping requested for orderId={}", order.getId());
        }
        Order savedOrder = orderRepository.save(order);

        notificationRepository.save(Notification.builder()
                .user(savedOrder.getUser())
                .title("Order Cancelled")
                .content("Your order has been cancelled")
                .refTo(savedOrder.getId())
                .read(false)
                .build());
        sendOrderCancellationEmail(savedOrder);
        LOGGER.info("Audit action={}, staffId={}, orderId={}, cancelReason={}, cancelledAt={}",
                "CANCEL_ORDER", staff.getId(), savedOrder.getId(), cancelReason, savedOrder.getCancelledAt());

        return toOrderSummary(savedOrder, MSG63);
    }

    @Transactional
    public ResOrderDTO initiateRefund(UUID orderId) {
        User staff = getCurrentStaffOrBusinessAdmin();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order not found"));
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, MSG64));

        if (!Order.CANCELLED_STATUS.equalsIgnoreCase(order.getStatus())
                || !Payment.SUCCESS_STATUS.equalsIgnoreCase(payment.getStatus())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG64);
        }

        payment.setStatus(Payment.REFUNDED_STATUS);
        paymentRepository.save(payment);
        order.setRefundStatus(REFUNDED_STATUS);
        orderRepository.save(order);
        notificationRepository.save(Notification.builder()
                .user(order.getUser())
                .title("Payment Refunded")
                .content("Your payment has been refunded")
                .refTo(order.getId())
                .read(false)
                .build());
        LOGGER.info("Audit action={}, staffId={}, orderId={}, result={}",
                "INITIATE_REFUND", staff.getId(), order.getId(), "SUCCESS");

        return toOrderSummary(order, MSG62);
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

        restoreInventory(orderId);

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
                        .provinceCode(address.getProvinceCode())
                        .province(address.getProvince())
                        .wardCode(address.getWardCode())
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
        BigDecimal newShippingFee = shippingFeeConfigService.getShippingFeeForProvince(
                newAddress.getProvinceCode(), newAddress.getProvince());
        order.setShippingAddressSnapshot(buildShippingAddressSnapshot(newAddress));
        order.setShippingFee(newShippingFee);
        order.setTotalAmount((order.getTotalProductAmount() == null ? BigDecimal.ZERO : order.getTotalProductAmount())
                .add(newShippingFee)
                .subtract(order.getDiscountAmount() == null ? BigDecimal.ZERO : order.getDiscountAmount())
                .max(BigDecimal.ZERO));
        paymentRepository.findByOrderId(order.getId()).ifPresent(payment -> {
            payment.setAmount(order.getTotalAmount());
            paymentRepository.save(payment);
        });
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

    private ResOrderDTO toOrderSummary(Order order, String message) {
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        return ResOrderDTO.builder()
                .orderId(order.getId())
                .customerId(order.getUser().getId())
                .customerName(order.getUser().getUserFullName())
                .status(order.getStatus())
                .totalProductAmount(order.getTotalProductAmount())
                .shippingFee(order.getShippingFee())
                .discountAmount(order.getDiscountAmount())
                .totalAmount(order.getTotalAmount())
                .paymentId(payment == null ? null : payment.getId())
                .paymentMethodName(payment == null || payment.getPaymentMethod() == null ? null : payment.getPaymentMethod().getName())
                .paymentStatus(payment == null ? null : payment.getStatus())
                .trackingNumber(order.getTrackingNumber())
                .orderingTime(order.getCreatedAt())
                .completedAt(order.getCompletedAt())
                .cancelReason(order.getCancelReason())
                .cancelledAt(order.getCancelledAt())
                .refundStatus(order.getRefundStatus())
                .message(message)
                .build();
    }

    private ResOrderDetailDTO toOrderDetail(Order order) {
        return toOrderDetail(order, null);
    }

    private ResOrderDetailDTO toOrderDetail(Order order, String message) {
        Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
        List<ResOrderItemDTO> items = orderItemRepository.findByOrderIdWithProduct(order.getId()).stream()
                .map(this::toOrderItemDTO)
                .toList();
        return ResOrderDetailDTO.builder()
                .orderId(order.getId())
                .customerId(order.getUser().getId())
                .customerName(order.getUser().getUserFullName())
                .customerPhone(order.getUser().getPhoneNumber())
                .status(order.getStatus())
                .totalProductAmount(order.getTotalProductAmount())
                .shippingFee(order.getShippingFee())
                .discountAmount(order.getDiscountAmount())
                .totalAmount(order.getTotalAmount())
                .shippingAddressSnapshot(order.getShippingAddressSnapshot())
                .trackingNumber(order.getTrackingNumber())
                .paymentMethod(payment == null ? null : payment.getPaymentMethod().getName())
                .paymentStatus(payment == null ? null : payment.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .completedAt(order.getCompletedAt())
                .cancelReason(order.getCancelReason())
                .cancelledAt(order.getCancelledAt())
                .refundStatus(order.getRefundStatus())
                .items(items)
                .message(message)
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

    private boolean isCancellableByStaff(Order order) {
        return Order.PENDING_STATUS.equalsIgnoreCase(order.getStatus())
                || Order.APPROVED_STATUS.equalsIgnoreCase(order.getStatus())
                || Order.SHIPPING_STATUS.equalsIgnoreCase(order.getStatus());
    }

    private boolean isOnlinePaidPayment(Payment payment) {
        return payment != null
                && Payment.SUCCESS_STATUS.equalsIgnoreCase(payment.getStatus())
                && payment.getPaymentMethod() != null
                && PaymentMethod.ONLINE_TYPE.equalsIgnoreCase(payment.getPaymentMethod().getType());
    }

    private User getCurrentStaffOrBusinessAdmin() {
        User user = getCurrentActiveUser();
        String roleName = user.getRole() == null || user.getRole().getName() == null
                ? ""
                : user.getRole().getName().trim().toUpperCase(Locale.ROOT);
        if (!STAFF_ROLE.equals(roleName) && !BUSINESS_ADMIN_ROLE.equals(roleName)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Only staff or business admin can perform this action");
        }
        return user;
    }

    private void restoreInventory(UUID orderId) {
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
    }

    private void sendOrderCancellationEmail(Order order) {
        try {
            emailService.sendOrderCancellation(
                    order.getUser().getEmail(),
                    order.getUser().getUserFullName(),
                    order.getId(),
                    order.getCancelReason(),
                    order.getRefundStatus());
        } catch (BusinessException ex) {
            LOGGER.warn("Cannot send order cancellation email for orderId={}: {}",
                    order.getId(), ex.getMessage());
        }
    }

    private String buildShippingAddressSnapshot(ShippingAddress address) {
        return String.join(", ", address.getDetail(), address.getWard(), address.getProvince());
    }
}
