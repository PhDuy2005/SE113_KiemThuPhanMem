package com.uit.nhom7.KiemThuPhanMem.service;

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

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResRoleDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResUserDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResultPaginationDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Inventory;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Order;
import com.uit.nhom7.KiemThuPhanMem.domain.table.OrderItem;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.InventoryRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.OrderItemRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.OrderRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

@Service
public class CustomerManagementService {
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";
    private static final String LOCKED_ACCOUNT_STATUS = "LOCKED";
    private static final String BUSINESS_ADMIN_ROLE = "BUSINESS_ADMIN";
    private static final String CUSTOMER_ROLE = "CUSTOMER";
    private static final String MSG98 = "There are no customers";
    private static final String MSG100 = "Customer account blocked and pending orders cancelled successfully";

    private final InventoryRepository inventoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public CustomerManagementService(
            InventoryRepository inventoryRepository,
            OrderItemRepository orderItemRepository,
            OrderRepository orderRepository,
            UserRepository userRepository) {
        this.inventoryRepository = inventoryRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ResultPaginationDTO getCustomers(int pageNumber, int pageSize) {
        getCurrentBusinessAdmin();
        Pageable pageable = PageRequest.of(
                Math.max(pageNumber - 1, 0),
                pageSize <= 0 ? 10 : pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> customers = userRepository.findByRoleNameIgnoreCase(CUSTOMER_ROLE, pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(customers.getTotalPages());
        meta.setTotalItems(customers.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(customers.getContent().stream()
                .map(customer -> toUserDTO(customer, null))
                .toList());
        result.setMessage(customers.isEmpty() ? MSG98 : null);
        return result;
    }

    @Transactional
    public ResUserDTO blockFraudCustomer(UUID customerId) {
        getCurrentBusinessAdmin();
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Customer account not found"));
        if (!isCustomer(customer)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Only customer accounts can be blocked by this action");
        }

        customer.setAccountStatus(LOCKED_ACCOUNT_STATUS);
        customer.setRefreshToken(null);

        List<Order> pendingOrders = orderRepository.findByUserIdAndStatusIgnoreCase(customerId, Order.PENDING_STATUS);
        for (Order order : pendingOrders) {
            restoreInventory(order.getId());
            order.setStatus(Order.CANCELLED_STATUS);
            orderRepository.save(order);
        }
        return toUserDTO(userRepository.save(customer), MSG100);
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
            if (inventory.getReservedQuantity() == null) {
                inventory.setReservedQuantity(0);
            }
            inventoryRepository.save(inventory);
        }
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

    private boolean isCustomer(User user) {
        return user.getRole() != null
                && user.getRole().getName() != null
                && CUSTOMER_ROLE.equals(user.getRole().getName().trim().toUpperCase(Locale.ROOT));
    }

    private ResUserDTO toUserDTO(User user, String message) {
        ResRoleDTO roleDTO = null;
        if (user.getRole() != null) {
            roleDTO = ResRoleDTO.builder()
                    .id(user.getRole().getId())
                    .name(user.getRole().getName())
                    .description(user.getRole().getDescription())
                    .active(user.getRole().isActive())
                    .build();
        }
        return ResUserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getUserFullName())
                .phoneNumber(user.getPhoneNumber())
                .accountStatus(user.getAccountStatus())
                .role(roleDTO)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdBy(user.getCreatedBy())
                .updatedBy(user.getUpdatedBy())
                .message(message)
                .build();
    }
}
