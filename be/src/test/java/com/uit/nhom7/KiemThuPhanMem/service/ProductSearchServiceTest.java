package com.uit.nhom7.KiemThuPhanMem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResProductDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductImageRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductRepository;

class ProductSearchServiceTest {
    @Test
    void searchShouldMatchBrandTokens() {
        ProductRepository productRepository = Mockito.mock(ProductRepository.class);
        ProductImageRepository productImageRepository = Mockito.mock(ProductImageRepository.class);
        ProductSearchService productSearchService = new ProductSearchService(productRepository, productImageRepository);
        Product expectedProduct = Product.builder()
                .id(UUID.randomUUID())
                .name("Body wash shampoo 3 in 1")
                .description("X-Men 3 in 1")
                .price(BigDecimal.valueOf(99000))
                .status(Product.ACTIVE_STATUS)
                .brand("X-Men")
                .build();
        Product otherProduct = Product.builder()
                .id(UUID.randomUUID())
                .name("Mechanical keyboard")
                .price(BigDecimal.valueOf(450000))
                .status(Product.ACTIVE_STATUS)
                .brand("Keychron")
                .build();

        when(productRepository.findByStatusIgnoreCase(Product.ACTIVE_STATUS))
                .thenReturn(List.of(otherProduct, expectedProduct));

        List<ResProductDTO> results = productSearchService.search("x men", 10);

        assertThat(results).extracting(ResProductDTO::getName)
                .containsExactly("Body wash shampoo 3 in 1");
    }

    @Test
    void searchShouldMatchVietnameseTypoAndTokenQuery() {
        ProductRepository productRepository = Mockito.mock(ProductRepository.class);
        ProductImageRepository productImageRepository = Mockito.mock(ProductImageRepository.class);
        ProductSearchService productSearchService = new ProductSearchService(productRepository, productImageRepository);
        Product expectedProduct = Product.builder()
                .id(UUID.randomUUID())
                .name("sữa tắm gội toàn thân 3 trong 1 X-Men")
                .description("X-Men 3 in 1")
                .price(BigDecimal.valueOf(99000))
                .status(Product.ACTIVE_STATUS)
                .brand("X-Men")
                .build();
        Product otherProduct = Product.builder()
                .id(UUID.randomUUID())
                .name("bàn phím cơ gaming")
                .price(BigDecimal.valueOf(450000))
                .status(Product.ACTIVE_STATUS)
                .build();

        when(productRepository.findByStatusIgnoreCase(Product.ACTIVE_STATUS))
                .thenReturn(List.of(otherProduct, expectedProduct));

        List<ResProductDTO> results = productSearchService.search("sứa gồi thân", 10);

        assertThat(results).extracting(ResProductDTO::getName)
                .containsExactly("sữa tắm gội toàn thân 3 trong 1 X-Men");
    }
}
