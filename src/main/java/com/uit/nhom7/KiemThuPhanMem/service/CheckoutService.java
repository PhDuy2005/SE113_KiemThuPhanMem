package com.uit.nhom7.KiemThuPhanMem.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqApplyVoucherDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqCheckoutSelectionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqConfirmOrderDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqSelectPaymentMethodDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResCheckoutSelectionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOrderDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResPaymentMethodSelectionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResVoucherApplicationDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Cart;
import com.uit.nhom7.KiemThuPhanMem.domain.table.CartItem;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Inventory;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Order;
import com.uit.nhom7.KiemThuPhanMem.domain.table.OrderItem;
import com.uit.nhom7.KiemThuPhanMem.domain.table.OrderItemId;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Payment;
import com.uit.nhom7.KiemThuPhanMem.domain.table.PaymentMethod;
import com.uit.nhom7.KiemThuPhanMem.domain.table.ShippingAddress;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Voucher;
import com.uit.nhom7.KiemThuPhanMem.repository.CartItemRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.CartRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.InventoryRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.OrderItemRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.OrderRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.PaymentMethodRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.PaymentRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ShippingAddressRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.VoucherRepository;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

@Service
public class CheckoutService {
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";
    private static final BigDecimal DEFAULT_SHIPPING_FEE = BigDecimal.ZERO;

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentRepository paymentRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;
    private final EmailService emailService;

    public CheckoutService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            InventoryRepository inventoryRepository,
            OrderItemRepository orderItemRepository,
            OrderRepository orderRepository,
            PaymentMethodRepository paymentMethodRepository,
            PaymentRepository paymentRepository,
            ShippingAddressRepository shippingAddressRepository,
            UserRepository userRepository,
            VoucherRepository voucherRepository,
            EmailService emailService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentRepository = paymentRepository;
        this.shippingAddressRepository = shippingAddressRepository;
        this.userRepository = userRepository;
        this.voucherRepository = voucherRepository;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public ResCheckoutSelectionDTO calculateSelection(ReqCheckoutSelectionDTO request) {
        User currentUser = getCurrentActiveUser();
        Cart cart = getCurrentUserCart(currentUser);
        List<CartItem> selectedItems = getSelectedCartItems(cart, request.getSelectedProductIds());

        return ResCheckoutSelectionDTO.builder()
                .selectedProductIds(request.getSelectedProductIds())
                .tempTotalPrice(calculateItemsTotal(selectedItems))
                .checkoutUrl("/checkout")
                .build();
    }

    @Transactional(readOnly = true)
    public ResVoucherApplicationDTO applyVoucher(ReqApplyVoucherDTO request) {
        getCurrentActiveUser();
        Voucher voucher = getValidVoucher(request.getVoucherCode(), request.getTotalOrderAmount());
        BigDecimal discountAmount = calculateDiscountAmount(voucher, request.getTotalOrderAmount());
        BigDecimal finalTotal = request.getTotalOrderAmount().subtract(discountAmount).max(BigDecimal.ZERO);

        return ResVoucherApplicationDTO.builder()
                .voucherCode(voucher.getCode())
                .discountAmount(discountAmount)
                .finalTotal(finalTotal)
                .message("Voucher applied successfully")
                .build();
    }

    @Transactional(readOnly = true)
    public ResPaymentMethodSelectionDTO selectPaymentMethod(ReqSelectPaymentMethodDTO request) {
        getCurrentActiveUser();
        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Invalid payment method"));
        ensureCashPaymentMethod(paymentMethod);

        return ResPaymentMethodSelectionDTO.builder()
                .paymentMethodId(paymentMethod.getId())
                .name(paymentMethod.getName())
                .type(paymentMethod.getType())
                .instruction(buildPaymentInstruction(paymentMethod))
                .build();
    }

    @Transactional
    public ResOrderDTO confirmOrder(ReqConfirmOrderDTO request) {
        User currentUser = getCurrentActiveUser();
        Cart cart = getCurrentUserCart(currentUser);
        List<CartItem> selectedItems = getSelectedCartItems(cart, request.getSelectedProductIds());
        ShippingAddress shippingAddress = shippingAddressRepository
                .findByIdAndUserIdAndDeletedAtIsNull(request.getShippingAddressId(), currentUser.getId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Shipping address not found"));
        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Invalid payment method"));
        ensureCashPaymentMethod(paymentMethod);

        validateStock(selectedItems);

        BigDecimal totalProductAmount = calculateItemsTotal(selectedItems);
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getVoucherCode() != null && !request.getVoucherCode().isBlank()) {
            Voucher voucher = getValidVoucher(request.getVoucherCode(), totalProductAmount);
            discountAmount = calculateDiscountAmount(voucher, totalProductAmount);
            voucher.setUsedCount((voucher.getUsedCount() == null ? 0 : voucher.getUsedCount()) + 1);
            voucherRepository.save(voucher);
        }
        BigDecimal totalAmount = totalProductAmount.add(DEFAULT_SHIPPING_FEE).subtract(discountAmount).max(BigDecimal.ZERO);

        deductStock(selectedItems);

        Order order = orderRepository.save(Order.builder()
                .user(currentUser)
                .status(Order.PENDING_STATUS)
                .totalProductAmount(totalProductAmount)
                .shippingFee(DEFAULT_SHIPPING_FEE)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .shippingAddressSnapshot(buildShippingAddressSnapshot(shippingAddress))
                .build());

        List<OrderItem> orderItems = selectedItems.stream()
                .map(item -> OrderItem.builder()
                        .id(new OrderItemId(order.getId(), item.getProduct().getId()))
                        .order(order)
                        .product(item.getProduct())
                        .price(item.getProduct().getPrice())
                        .quantity(item.getQuantity())
                        .build())
                .toList();
        orderItemRepository.saveAll(orderItems);

        Payment payment = paymentRepository.save(Payment.builder()
                .order(order)
                .paymentMethod(paymentMethod)
                .status(resolveInitialPaymentStatus(paymentMethod))
                .amount(totalAmount)
                .build());

        cartItemRepository.deleteByCartIdAndProductIdIn(cart.getId(), request.getSelectedProductIds());
        sendOrderConfirmationEmail(currentUser, order, orderItems, paymentMethod);

        return ResOrderDTO.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .totalProductAmount(totalProductAmount)
                .shippingFee(DEFAULT_SHIPPING_FEE)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .paymentId(payment.getId())
                .paymentStatus(payment.getStatus())
                .message("Order created successfully")
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

    private Cart getCurrentUserCart(User currentUser) {
        return cartRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Cart not found"));
    }

    private List<CartItem> getSelectedCartItems(Cart cart, List<UUID> selectedProductIds) {
        if (selectedProductIds == null || selectedProductIds.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "You must select at least one product");
        }
        List<CartItem> selectedItems = cartItemRepository.findSelectedByCartIdWithProduct(cart.getId(), selectedProductIds);
        if (selectedItems.size() != selectedProductIds.stream().distinct().count()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Selected products are invalid");
        }
        return selectedItems;
    }

    private BigDecimal calculateItemsTotal(List<CartItem> items) {
        return items.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Voucher getValidVoucher(String voucherCode, BigDecimal totalOrderAmount) {
        Voucher voucher = voucherRepository.findByCodeIgnoreCase(voucherCode.trim())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Invalid or expired voucher"));
        Instant now = Instant.now();
        if (!voucher.isActive()
                || (voucher.getStartDate() != null && voucher.getStartDate().isAfter(now))
                || (voucher.getEndDate() != null && voucher.getEndDate().isBefore(now))
                || (voucher.getMaxUsage() != null && voucher.getUsedCount() != null
                        && voucher.getUsedCount() >= voucher.getMaxUsage())
                || (voucher.getMinOrderAmount() != null && totalOrderAmount.compareTo(voucher.getMinOrderAmount()) < 0)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Invalid or expired voucher");
        }
        return voucher;
    }

    private BigDecimal calculateDiscountAmount(Voucher voucher, BigDecimal totalOrderAmount) {
        if (Voucher.PERCENT_TYPE.equalsIgnoreCase(voucher.getType())) {
            BigDecimal percent = voucher.getValue() == null ? BigDecimal.ZERO : voucher.getValue();
            return totalOrderAmount.multiply(percent)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                    .min(totalOrderAmount);
        }
        if (Voucher.FIXED_TYPE.equalsIgnoreCase(voucher.getType())) {
            return (voucher.getValue() == null ? BigDecimal.ZERO : voucher.getValue()).min(totalOrderAmount);
        }
        throw new BusinessException(HttpStatus.BAD_REQUEST, "Invalid or expired voucher");
    }

    private String buildPaymentInstruction(PaymentMethod paymentMethod) {
        return "Pay in cash when the order is delivered.";
    }

    private void ensureCashPaymentMethod(PaymentMethod paymentMethod) {
        if (!PaymentMethod.CASH_TYPE.equalsIgnoreCase(paymentMethod.getType())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "Only cash payment is supported at the moment");
        }
    }

    private void validateStock(List<CartItem> selectedItems) {
        for (CartItem item : selectedItems) {
            int availableQuantity = inventoryRepository.findById(item.getProduct().getId())
                    .map(Inventory::getAvailableQuantity)
                    .orElse(0);
            if (item.getQuantity() > availableQuantity) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Insufficient stock. Please check your cart again");
            }
        }
    }

    private void deductStock(List<CartItem> selectedItems) {
        for (CartItem item : selectedItems) {
            Inventory inventory = inventoryRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST,
                            "Insufficient stock. Please check your cart again"));
            inventory.setQuantity((inventory.getQuantity() == null ? 0 : inventory.getQuantity()) - item.getQuantity());
            inventoryRepository.save(inventory);
        }
    }

    private String buildShippingAddressSnapshot(ShippingAddress address) {
        return String.join(", ", address.getDetail(), address.getWard(), address.getProvince());
    }

    private String resolveInitialPaymentStatus(PaymentMethod paymentMethod) {
        return Payment.PENDING_STATUS;
    }

    private void sendOrderConfirmationEmail(
            User user,
            Order order,
            List<OrderItem> orderItems,
            PaymentMethod paymentMethod) {
        String itemText = orderItems.stream()
                .map(item -> "- %s x %d: %s".formatted(
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))))
                .reduce("", (left, right) -> left + right + System.lineSeparator());
        try {
            emailService.sendOrderConfirmation(
                    user.getEmail(),
                    user.getUserFullName(),
                    order.getId(),
                    itemText,
                    order.getTotalAmount(),
                    order.getShippingAddressSnapshot(),
                    paymentMethod.getName());
        } catch (RuntimeException ex) {
            System.err.println("Cannot send order confirmation email for order " + order.getId() + ": " + ex.getMessage());
        }
    }
}
