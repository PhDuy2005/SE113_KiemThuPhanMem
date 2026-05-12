package com.uit.nhom7.KiemThuPhanMem.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqCreateCategoryDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateCategoryDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResCategoryDTO;
import com.uit.nhom7.KiemThuPhanMem.service.CategoryService;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/business/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @ApiMessage("Business admin lay danh sach danh muc")
    public ResponseEntity<List<ResCategoryDTO>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategories());
    }

    @GetMapping("/{categoryId}/replacements")
    @ApiMessage("Business admin lay danh sach danh muc thay the")
    public ResponseEntity<List<ResCategoryDTO>> getReplacementCategories(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(categoryService.getReplacementCategories(categoryId));
    }

    @PostMapping
    @ApiMessage("Business admin them danh muc san pham")
    public ResponseEntity<ResCategoryDTO> createCategory(@RequestBody ReqCreateCategoryDTO request) {
        ResCategoryDTO category = categoryService.createCategory(request);
        return ResponseEntity
                .created(URI.create("/api/v1/business/categories/" + category.getId()))
                .body(category);
    }

    @PutMapping("/{categoryId}")
    @ApiMessage("Business admin sua danh muc san pham")
    public ResponseEntity<ResCategoryDTO> updateCategory(
            @PathVariable UUID categoryId,
            @RequestBody ReqUpdateCategoryDTO request) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, request));
    }

    @DeleteMapping("/{categoryId}")
    @ApiMessage("Business admin xoa danh muc san pham")
    public ResponseEntity<ResCategoryDTO> deleteCategory(
            @PathVariable UUID categoryId,
            @RequestParam(value = "replacementCategoryId", required = false) UUID replacementCategoryId) {
        return ResponseEntity.ok(categoryService.deleteCategory(categoryId, replacementCategoryId));
    }
}
