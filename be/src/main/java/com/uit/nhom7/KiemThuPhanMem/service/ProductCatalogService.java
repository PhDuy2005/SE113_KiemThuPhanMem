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
import com.uit.nhom7.KiemThuPhanMem.domain.table.ProductImage;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Inventory;
import com.uit.nhom7.KiemThuPhanMem.repository.InventoryRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ReviewRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductImageRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductRepository;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProductCatalogService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final InventoryRepository inventoryRepository;
    private final ReviewRepository reviewRepository;

    public ProductCatalogService(ProductRepository productRepository, ProductImageRepository productImageRepository, InventoryRepository inventoryRepository, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.inventoryRepository = inventoryRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    public List<ResProductDTO> getProducts(List<UUID> categoryIds, String sortPrice) {
        Sort sort = buildPriceSort(sortPrice);
        List<Product> products = hasCategoryFilter(categoryIds)
                ? productRepository.findByStatusIgnoreCaseAndCategoryIdIn(Product.ACTIVE_STATUS, categoryIds, sort)
                : productRepository.findByStatusIgnoreCase(Product.ACTIVE_STATUS, sort);

        List<UUID> productIds = products.stream().map(Product::getId).toList();
        List<ProductImage> images = productIds.isEmpty() ? List.of() : productImageRepository.findByProductIdIn(productIds);
        Map<UUID, String> primaryImageMap = new HashMap<>();
        for (ProductImage img : images) {
            if (img.isPrimaryImage()) {
                primaryImageMap.put(img.getProduct().getId(), img.getImageUrl());
            } else {
                primaryImageMap.putIfAbsent(img.getProduct().getId(), img.getImageUrl());
            }
        }

        List<Inventory> inventories = productIds.isEmpty() ? List.of() : inventoryRepository.findByProductIdIn(productIds);
        Map<UUID, Integer> stockMap = new HashMap<>();
        for (Inventory inv : inventories) {
            stockMap.put(inv.getProductId(), inv.getAvailableQuantity());
        }

        List<Object[]> ratings = productIds.isEmpty() ? List.of() : reviewRepository.getAverageRatingForProducts(productIds);
        Map<UUID, Double> ratingMap = new HashMap<>();
        for (Object[] r : ratings) {
            ratingMap.put((UUID) r[0], ((Number) r[1]).doubleValue());
        }

        return products.stream()
                .map(product -> convertToDTO(product, primaryImageMap.get(product.getId()), null, stockMap.getOrDefault(product.getId(), 0), ratingMap.getOrDefault(product.getId(), 5.0)))
                .toList();
    }

    @Transactional(readOnly = true)
    public ResProductDTO getProductById(UUID productId) {
        Product product = productRepository.findByIdAndStatusIgnoreCase(productId, Product.ACTIVE_STATUS)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Product not found"));
        List<ProductImage> images = productImageRepository.findByProductIdOrderByPrimaryImageDescCreatedAtAsc(productId);
        String primaryImage = null;
        if (!images.isEmpty()) {
            primaryImage = images.get(0).getImageUrl();
        }
        List<String> imageUrls = images.stream().map(ProductImage::getImageUrl).toList();
        Integer stock = inventoryRepository.findById(productId).map(Inventory::getAvailableQuantity).orElse(0);
        
        List<Object[]> ratingObj = reviewRepository.getAverageRatingForProducts(List.of(productId));
        Double rating = ratingObj.isEmpty() ? 5.0 : ((Number) ratingObj.get(0)[1]).doubleValue();

        return convertToDTO(product, primaryImage, imageUrls, stock, rating);
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

    private ResProductDTO convertToDTO(Product product, String primaryImage, List<String> imageUrls, Integer stock, Double rating) {
        return ResProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .status(product.getStatus())
                .brand(product.getBrand())
                .categoryId(product.getCategoryId())
                .primaryImage(primaryImage)
                .imageUrls(imageUrls)
                .stock(stock)
                .rating(rating)
                .build();
    }
}
