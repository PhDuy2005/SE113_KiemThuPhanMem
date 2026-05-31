package com.uit.nhom7.KiemThuPhanMem.service.order_service;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;

import com.uit.nhom7.KiemThuPhanMem.repository.InventoryRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.NotificationRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.OrderItemRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.OrderRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.PaymentRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ShippingAddressRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.service.EmailService;
import com.uit.nhom7.KiemThuPhanMem.service.OrderService;
import com.uit.nhom7.KiemThuPhanMem.service.ShippingFeeConfigService;

public class OrderServiceTestBase {

    protected static class Fixture {
        public InventoryRepository inventoryRepository;
        public NotificationRepository notificationRepository;
        public OrderItemRepository orderItemRepository;
        public OrderRepository orderRepository;
        public PaymentRepository paymentRepository;
        public ShippingAddressRepository shippingAddressRepository;
        public ShippingFeeConfigService shippingFeeConfigService;
        public UserRepository userRepository;
        public EmailService emailService;
        
        public OrderService orderService;

        public Fixture() {
            inventoryRepository = mock(InventoryRepository.class);
            notificationRepository = mock(NotificationRepository.class);
            orderItemRepository = mock(OrderItemRepository.class);
            orderRepository = mock(OrderRepository.class);
            paymentRepository = mock(PaymentRepository.class);
            shippingAddressRepository = mock(ShippingAddressRepository.class);
            shippingFeeConfigService = mock(ShippingFeeConfigService.class);
            userRepository = mock(UserRepository.class);
            emailService = mock(EmailService.class);
            
            orderService = new OrderService(
                inventoryRepository,
                notificationRepository,
                orderItemRepository,
                orderRepository,
                paymentRepository,
                shippingAddressRepository,
                shippingFeeConfigService,
                userRepository,
                emailService
            );
        }
    }
}
