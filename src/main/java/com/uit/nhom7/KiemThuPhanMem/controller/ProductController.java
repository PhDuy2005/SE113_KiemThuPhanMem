package com.uit.nhom7.KiemThuPhanMem.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResProductDTO;
import com.uit.nhom7.KiemThuPhanMem.service.ProductCatalogService;
import com.uit.nhom7.KiemThuPhanMem.service.ProductSearchService;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductCatalogService productCatalogService;
    private final ProductSearchService productSearchService;

    public ProductController(
            ProductCatalogService productCatalogService,
            ProductSearchService productSearchService) {
        this.productCatalogService = productCatalogService;
        this.productSearchService = productSearchService;
    }

    @GetMapping
    @ApiMessage("Lay danh sach san pham")
    public ResponseEntity<List<ResProductDTO>> getProducts(
            @RequestParam(value = "categoryIds", required = false) List<UUID> categoryIds,
            @RequestParam(value = "sortPrice", required = false) String sortPrice) {
        return ResponseEntity.ok(productCatalogService.getProducts(categoryIds, sortPrice));
    }

    @GetMapping("/search")
    @ApiMessage("Tim kiem san pham bang tu khoa")
    public ResponseEntity<List<ResProductDTO>> searchProducts(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "limit", required = false) Integer limit) {
        return ResponseEntity.ok(productSearchService.search(keyword, limit));
    }
}
