package com.uit.nhom7.KiemThuPhanMem.service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResProductDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductRepository;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

@Service
public class ProductCatalogService {
    private final ProductRepository productRepository;

    public ProductCatalogService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ResProductDTO> getProducts(List<UUID> categoryIds, String sortPrice) {
        Sort sort = buildPriceSort(sortPrice);
        List<Product> products = hasCategoryFilter(categoryIds)
                ? productRepository.findByStatusIgnoreCaseAndCategoryIdIn(Product.ACTIVE_STATUS, categoryIds, sort)
                : productRepository.findByStatusIgnoreCase(Product.ACTIVE_STATUS, sort);

        return products.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResProductDTO getProductById(UUID productId) {
        Product product = productRepository.findByIdAndStatusIgnoreCase(productId, Product.ACTIVE_STATUS)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Product not found"));
        return convertToDTO(product);
    }

    private boolean hasCategoryFilter(List<UUID> categoryIds) {
        return categoryIds != null && !categoryIds.isEmpty();
    }

    private Sort buildPriceSort(String sortPrice) {
        if (sortPrice == null || sortPrice.isBlank()) {
            return Sort.unsorted();
        }

        String normalizedSort = sortPrice.trim().toUpperCase(Locale.ROOT);
        if ("ASC".equals(normalizedSort)) {
            return Sort.by(Sort.Direction.ASC, "price");
        }
        if ("DESC".equals(normalizedSort)) {
            return Sort.by(Sort.Direction.DESC, "price");
        }

        throw new BusinessException(HttpStatus.BAD_REQUEST, "sortPrice must be ASC or DESC");
    }

    private ResProductDTO convertToDTO(Product product) {
        return ResProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .status(product.getStatus())
                .brand(product.getBrand())
                .categoryId(product.getCategoryId())
                .build();
    }
}
