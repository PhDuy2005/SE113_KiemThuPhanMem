package com.uit.nhom7.KiemThuPhanMem.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateProductGeneralDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateProductPriceDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateProductStockDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResProductDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResultPaginationDTO;
import com.uit.nhom7.KiemThuPhanMem.service.ProductManagementService;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/business/products")
public class ProductManagementController {
    private final ProductManagementService productManagementService;

    public ProductManagementController(ProductManagementService productManagementService) {
        this.productManagementService = productManagementService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiMessage("Business admin dang san pham moi")
    public ResponseEntity<ResProductDTO> createProduct(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("categoryId") UUID categoryId,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        ResProductDTO product = productManagementService.createProduct(
                name, description, price, categoryId, stock, brand, images);
        return ResponseEntity
                .created(URI.create("/api/v1/products/" + product.getId()))
                .body(product);
    }

    @PatchMapping("/{productId}/price")
    @ApiMessage("Business admin cap nhat gia san pham")
    public ResponseEntity<ResProductDTO> updatePrice(
            @PathVariable UUID productId,
            @RequestBody ReqUpdateProductPriceDTO request) {
        return ResponseEntity.ok(productManagementService.updatePrice(productId, request.getNewPrice()));
    }

    @PatchMapping("/{productId}/stock")
    @ApiMessage("Business admin cap nhat ton kho san pham")
    public ResponseEntity<ResProductDTO> updateStock(
            @PathVariable UUID productId,
            @RequestBody ReqUpdateProductStockDTO request) {
        return ResponseEntity.ok(productManagementService.updateStock(productId, request.getNewStock()));
    }

    @PatchMapping("/{productId}/discontinue")
    @ApiMessage("Business admin ngung kinh doanh san pham")
    public ResponseEntity<ResProductDTO> discontinueProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(productManagementService.discontinueProduct(productId));
    }

    @GetMapping
    @ApiMessage("Business admin lay danh sach san pham")
    public ResponseEntity<ResultPaginationDTO> getAdminProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) UUID categoryId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(productManagementService.getAdminProducts(keyword, categoryId, status, pageNumber, pageSize));
    }

    @PutMapping("/{productId}")
    @ApiMessage("Business admin cap nhat thong tin chung san pham")
    public ResponseEntity<ResProductDTO> updateProduct(
            @PathVariable UUID productId,
            @RequestBody ReqUpdateProductGeneralDTO request) {
        return ResponseEntity.ok(productManagementService.updateProduct(productId, request));
    }
}
