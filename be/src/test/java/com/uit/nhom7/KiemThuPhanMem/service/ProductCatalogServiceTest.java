package com.uit.nhom7.KiemThuPhanMem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Sort;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResProductDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.repository.InventoryRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ReviewRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductImageRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductRepository;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

class ProductCatalogServiceTest {
    @Test
    void getProductByIdShouldReturnActiveProduct() {
        ProductRepository productRepository = Mockito.mock(ProductRepository.class);
        ProductImageRepository productImageRepository = Mockito.mock(ProductImageRepository.class);
        InventoryRepository inventoryRepository = Mockito.mock(InventoryRepository.class);
        ReviewRepository reviewRepository = Mockito.mock(ReviewRepository.class);
        ProductCatalogService productCatalogService = new ProductCatalogService(productRepository, productImageRepository, inventoryRepository, reviewRepository);
        UUID productId = UUID.randomUUID();
        Product product = Product.builder()
                .id(productId)
                .name("Mouse")
                .price(BigDecimal.valueOf(250000))
                .status(Product.ACTIVE_STATUS)
                .build();

        when(productRepository.findByIdAndStatusIgnoreCase(productId, Product.ACTIVE_STATUS))
                .thenReturn(Optional.of(product));

        ResProductDTO result = productCatalogService.getProductById(productId);

        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Mouse");
    }

    @Test
    void getProductByIdShouldThrowNotFoundWhenProductDoesNotExist() {
        ProductRepository productRepository = Mockito.mock(ProductRepository.class);
        ProductImageRepository productImageRepository = Mockito.mock(ProductImageRepository.class);
        InventoryRepository inventoryRepository = Mockito.mock(InventoryRepository.class);
        ReviewRepository reviewRepository = Mockito.mock(ReviewRepository.class);
        ProductCatalogService productCatalogService = new ProductCatalogService(productRepository, productImageRepository, inventoryRepository, reviewRepository);
        UUID productId = UUID.randomUUID();

        when(productRepository.findByIdAndStatusIgnoreCase(productId, Product.ACTIVE_STATUS))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> productCatalogService.getProductById(productId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Product not found");
    }

    @Test
    void getProductsShouldFilterByCategoriesAndSortByPriceDesc() {
        ProductRepository productRepository = Mockito.mock(ProductRepository.class);
        ProductImageRepository productImageRepository = Mockito.mock(ProductImageRepository.class);
        InventoryRepository inventoryRepository = Mockito.mock(InventoryRepository.class);
        ReviewRepository reviewRepository = Mockito.mock(ReviewRepository.class);
        ProductCatalogService productCatalogService = new ProductCatalogService(productRepository, productImageRepository, inventoryRepository, reviewRepository);
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
        ProductCatalogService productCatalogService = new ProductCatalogService(Mockito.mock(ProductRepository.class), Mockito.mock(ProductImageRepository.class), Mockito.mock(InventoryRepository.class), Mockito.mock(ReviewRepository.class));

        assertThatThrownBy(() -> productCatalogService.getProducts(null, "CHEAP_FIRST"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("sortPrice must be ASC or DESC");
    }
}
