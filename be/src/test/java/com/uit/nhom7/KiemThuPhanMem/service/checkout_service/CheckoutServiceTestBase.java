package com.uit.nhom7.KiemThuPhanMem.service.checkout_service;

import static org.mockito.Mockito.mock;

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
import com.uit.nhom7.KiemThuPhanMem.service.CheckoutService;
import com.uit.nhom7.KiemThuPhanMem.service.EmailService;
import com.uit.nhom7.KiemThuPhanMem.service.ShippingFeeConfigService;

public abstract class CheckoutServiceTestBase {

    protected static class Fixture {
        public final CartRepository cartRepository;
        public final CartItemRepository cartItemRepository;
        public final InventoryRepository inventoryRepository;
        public final OrderItemRepository orderItemRepository;
        public final OrderRepository orderRepository;
        public final PaymentMethodRepository paymentMethodRepository;
        public final PaymentRepository paymentRepository;
        public final ShippingAddressRepository shippingAddressRepository;
        public final ShippingFeeConfigService shippingFeeConfigService;
        public final UserRepository userRepository;
        public final VoucherRepository voucherRepository;
        public final EmailService emailService;
        public final CheckoutService checkoutService;

        public Fixture() {
            this.cartRepository = mock(CartRepository.class);
            this.cartItemRepository = mock(CartItemRepository.class);
            this.inventoryRepository = mock(InventoryRepository.class);
            this.orderItemRepository = mock(OrderItemRepository.class);
            this.orderRepository = mock(OrderRepository.class);
            this.paymentMethodRepository = mock(PaymentMethodRepository.class);
            this.paymentRepository = mock(PaymentRepository.class);
            this.shippingAddressRepository = mock(ShippingAddressRepository.class);
            this.shippingFeeConfigService = mock(ShippingFeeConfigService.class);
            this.userRepository = mock(UserRepository.class);
            this.voucherRepository = mock(VoucherRepository.class);
            this.emailService = mock(EmailService.class);

            this.checkoutService = new CheckoutService(
                    this.cartRepository,
                    this.cartItemRepository,
                    this.inventoryRepository,
                    this.orderItemRepository,
                    this.orderRepository,
                    this.paymentMethodRepository,
                    this.paymentRepository,
                    this.shippingAddressRepository,
                    this.shippingFeeConfigService,
                    this.userRepository,
                    this.voucherRepository,
                    this.emailService
            );
        }
    }
}
