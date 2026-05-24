package com.uit.nhom7.KiemThuPhanMem.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResCategoryDTO;
import com.uit.nhom7.KiemThuPhanMem.service.CategoryService;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/categories")
public class PublicCategoryController {
    private final CategoryService categoryService;

    public PublicCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @ApiMessage("Lay danh sach danh muc cho khach hang")
    public ResponseEntity<List<ResCategoryDTO>> getPublicCategories() {
        return ResponseEntity.ok(categoryService.getPublicCategories());
    }
}
