package com.uit.nhom7.KiemThuPhanMem.service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqCreateCategoryDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateCategoryDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResCategoryDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Category;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.CategoryRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

@Service
public class CategoryService {
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";
    private static final String BUSINESS_ADMIN_ROLE = "BUSINESS_ADMIN";
    private static final String MSG1 = "Category name is required";
    private static final String MSG72 = "Category created successfully";
    private static final String MSG73 = "Category name already exists";
    private static final String MSG74 = "Category deleted successfully";
    private static final String MSG75 = "Replacement category is required";
    private static final String MSG76 = "Category updated successfully";

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CategoryService(
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ResCategoryDTO> getCategories() {
        getCurrentBusinessAdmin();
        return categoryRepository.findAll().stream()
                .map(category -> toDTO(category, null))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResCategoryDTO> getReplacementCategories(UUID targetId) {
        getCurrentBusinessAdmin();
        categoryRepository.findById(targetId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Category not found"));
        return categoryRepository.findByIdNot(targetId).stream()
                .map(category -> toDTO(category, null))
                .toList();
    }

    @Transactional
    public ResCategoryDTO createCategory(ReqCreateCategoryDTO request) {
        getCurrentBusinessAdmin();
        String categoryName = requireCategoryName(request == null ? null : request.getCategoryName());
        if (categoryRepository.existsByNameIgnoreCase(categoryName)) {
            throw new BusinessException(HttpStatus.CONFLICT, MSG73);
        }

        Category category = Category.builder()
                .name(categoryName)
                .imageUrl(cleanNullableText(request.getCategoryImage()))
                .description(cleanNullableText(request.getCategoryDescription()))
                .build();
        return toDTO(categoryRepository.save(category), MSG72);
    }

    @Transactional
    public ResCategoryDTO updateCategory(UUID categoryId, ReqUpdateCategoryDTO request) {
        getCurrentBusinessAdmin();
        String categoryName = requireCategoryName(request == null ? null : request.getCategoryName());
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Category not found"));
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(categoryName, categoryId)) {
            throw new BusinessException(HttpStatus.CONFLICT, MSG73);
        }

        category.setName(categoryName);
        category.setImageUrl(cleanNullableText(request.getCategoryImage()));
        category.setDescription(cleanNullableText(request.getCategoryDescription()));
        return toDTO(categoryRepository.save(category), MSG76);
    }

    @Transactional
    public ResCategoryDTO deleteCategory(UUID categoryId, UUID replacementCategoryId) {
        getCurrentBusinessAdmin();
        if (replacementCategoryId == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG75);
        }
        if (categoryId.equals(replacementCategoryId)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Replacement category must be different from target category");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Category not found"));
        categoryRepository.findById(replacementCategoryId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Replacement category not found"));

        productRepository.updateCategory(categoryId, replacementCategoryId);
        categoryRepository.delete(category);
        return toDTO(category, MSG74);
    }

    private User getCurrentBusinessAdmin() {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "You must login first"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "User session is invalid"));
        if (user.getAccountStatus() == null
                || !ACTIVE_ACCOUNT_STATUS.equals(user.getAccountStatus().trim().toUpperCase(Locale.ROOT))) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "User account is not active");
        }
        String roleName = user.getRole() == null || user.getRole().getName() == null
                ? ""
                : user.getRole().getName().trim().toUpperCase(Locale.ROOT);
        if (!BUSINESS_ADMIN_ROLE.equals(roleName)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Only business admin can perform this action");
        }
        return user;
    }

    private String requireCategoryName(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG1);
        }
        return categoryName.trim();
    }

    private String cleanNullableText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private ResCategoryDTO toDTO(Category category, String message) {
        return ResCategoryDTO.builder()
                .id(category.getId())
                .categoryName(category.getName())
                .categoryImage(category.getImageUrl())
                .categoryDescription(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .message(message)
                .build();
    }
}
