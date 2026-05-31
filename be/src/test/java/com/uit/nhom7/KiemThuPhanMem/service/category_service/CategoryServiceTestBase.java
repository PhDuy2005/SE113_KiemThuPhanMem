package com.uit.nhom7.KiemThuPhanMem.service.category_service;

import static org.mockito.Mockito.mock;

import com.uit.nhom7.KiemThuPhanMem.repository.CategoryRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.service.CategoryService;

public abstract class CategoryServiceTestBase {

    protected static class Fixture {
        public final CategoryRepository categoryRepository;
        public final ProductRepository productRepository;
        public final UserRepository userRepository;
        public final CategoryService categoryService;

        public Fixture() {
            this.categoryRepository = mock(CategoryRepository.class);
            this.productRepository = mock(ProductRepository.class);
            this.userRepository = mock(UserRepository.class);

            this.categoryService = new CategoryService(
                    this.categoryRepository,
                    this.productRepository,
                    this.userRepository
            );
        }
    }
}
