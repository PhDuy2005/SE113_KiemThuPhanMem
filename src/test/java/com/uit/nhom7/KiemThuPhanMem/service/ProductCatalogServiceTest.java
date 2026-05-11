package com.uit.nhom7.KiemThuPhanMem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResProductDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductRepository;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

class ProductCatalogServiceTest {
    @Test
    void getProductsShouldFilterByCategoriesAndSortByPriceDesc() {
        ProductRepository productRepository = Mockito.mock(ProductRepository.class);
        ProductCatalogService productCatalogService = new ProductCatalogService(productRepository);
        UUID categoryId = UUID.randomUUID();
        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name("Laptop")
                .price(BigDecimal.valueOf(15000000))
                .status(Product.ACTIVE_STATUS)
                .categoryId(categoryId)
                .build();

        when(productRepository.findByStatusIgnoreCaseAndCategoryIdIn(
                eq(Product.ACTIVE_STATUS),
                eq(List.of(categoryId)),
                Mockito.any(Sort.class)))
                .thenReturn(List.of(product));

        List<ResProductDTO> results = productCatalogService.getProducts(List.of(categoryId), "DESC");

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(productRepository).findByStatusIgnoreCaseAndCategoryIdIn(
                eq(Product.ACTIVE_STATUS),
                eq(List.of(categoryId)),
                sortCaptor.capture());

        Sort.Order priceOrder = sortCaptor.getValue().getOrderFor("price");
        assertThat(priceOrder).isNotNull();
        assertThat(priceOrder.getDirection()).isEqualTo(Sort.Direction.DESC);
        assertThat(results).extracting(ResProductDTO::getName).containsExactly("Laptop");
    }

    @Test
    void getProductsShouldRejectInvalidPriceSort() {
        ProductCatalogService productCatalogService = new ProductCatalogService(Mockito.mock(ProductRepository.class));

        assertThatThrownBy(() -> productCatalogService.getProducts(null, "CHEAP_FIRST"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("sortPrice must be ASC or DESC");
    }
}
