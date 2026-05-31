package com.uit.nhom7.KiemThuPhanMem.service.voucher_management_service;

import static org.mockito.Mockito.mock;

import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.VoucherRepository;
import com.uit.nhom7.KiemThuPhanMem.service.VoucherManagementService;

public abstract class VoucherManagementServiceTestBase {

    protected static class Fixture {
        public final UserRepository userRepository;
        public final VoucherRepository voucherRepository;
        public final VoucherManagementService voucherManagementService;

        public Fixture() {
            this.userRepository = mock(UserRepository.class);
            this.voucherRepository = mock(VoucherRepository.class);

            this.voucherManagementService = new VoucherManagementService(
                    this.userRepository,
                    this.voucherRepository
            );
        }
    }
}
