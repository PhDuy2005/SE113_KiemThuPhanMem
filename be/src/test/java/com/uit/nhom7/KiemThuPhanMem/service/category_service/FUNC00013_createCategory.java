package com.uit.nhom7.KiemThuPhanMem.service.category_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqCreateCategoryDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResCategoryDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Category;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00013_createCategory extends CategoryServiceTestBase {

    private MockedStatic<SecurityUtil> mockedSecurityUtil;
    private Fixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
        mockedSecurityUtil = mockStatic(SecurityUtil.class);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtil.close();
    }

    private User getAdminUser() {
        Role role = new Role();
        role.setName("BUSINESS_ADMIN");
        return User.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .email("admin@example.com")
                .accountStatus("ACTIVE")
                .role(role)
                .build();
    }

    @Test
    @DisplayName("FUNC00013_UTCID01 - Fails because user is not authenticated")
    void TC1_NoLogin() {
        ReqCreateCategoryDTO request = new ReqCreateCategoryDTO();
        request.setCategoryName("Electronics");

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.categoryService.createCategory(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00013_UTCID02 - Fails because email is not in DB")
    void TC2_NoSession() {
        ReqCreateCategoryDTO request = new ReqCreateCategoryDTO();
        request.setCategoryName("Electronics");

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.categoryService.createCategory(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00013_UTCID03 - Fails because user is not ACTIVE")
    void TC3_NotActive() {
        ReqCreateCategoryDTO request = new ReqCreateCategoryDTO();
        request.setCategoryName("Electronics");

        User user = User.builder().accountStatus("PENDING").build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.categoryService.createCategory(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00013_UTCID04 - Fails because user is not BUSINESS_ADMIN")
    void TC4_NotAdmin() {
        ReqCreateCategoryDTO request = new ReqCreateCategoryDTO();
        request.setCategoryName("Electronics");

        Role role = new Role();
        role.setName("CUSTOMER");
        User user = User.builder().accountStatus("ACTIVE").role(role).build();
        
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.categoryService.createCategory(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00013_UTCID05 - Fails because request object is null")
    void TC5_RequestNull() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));

        assertThatThrownBy(() -> fixture.categoryService.createCategory(null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Category name is required")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00013_UTCID06 - Fails because categoryName is null")
    void TC6_NameNull() {
        ReqCreateCategoryDTO request = new ReqCreateCategoryDTO();
        request.setCategoryName(null);

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));

        assertThatThrownBy(() -> fixture.categoryService.createCategory(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Category name is required")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00013_UTCID07 - Fails because categoryName is blank")
    void TC7_NameBlank() {
        ReqCreateCategoryDTO request = new ReqCreateCategoryDTO();
        request.setCategoryName("   ");

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));

        assertThatThrownBy(() -> fixture.categoryService.createCategory(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Category name is required")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00013_UTCID08 - Fails because categoryName already exists")
    void TC8_NameExists() {
        ReqCreateCategoryDTO request = new ReqCreateCategoryDTO();
        request.setCategoryName("Electronics");

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));
        when(fixture.categoryRepository.existsByNameIgnoreCase("Electronics")).thenReturn(true);

        assertThatThrownBy(() -> fixture.categoryService.createCategory(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Category name already exists")
                .extracting("status")
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("FUNC00013_UTCID09 - Creates category successfully with all valid fields")
    void TC9_SuccessAllFields() {
        UUID parentId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        ReqCreateCategoryDTO request = new ReqCreateCategoryDTO();
        request.setCategoryName("Electronics");
        request.setParentId(parentId);
        request.setCategoryImage("image.jpg");
        request.setCategoryDescription("Desc");

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));
        when(fixture.categoryRepository.existsByNameIgnoreCase("Electronics")).thenReturn(false);

        Category savedCategory = Category.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .name("Electronics")
                .parentId(parentId)
                .imageUrl("image.jpg")
                .description("Desc")
                .build();
        when(fixture.categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        ResCategoryDTO result = fixture.categoryService.createCategory(request);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Category created successfully");
        assertThat(result.getId()).isEqualTo(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        assertThat(result.getCategoryName()).isEqualTo("Electronics");
        assertThat(result.getParentId()).isEqualTo(parentId);
        assertThat(result.getCategoryImage()).isEqualTo("image.jpg");
        assertThat(result.getCategoryDescription()).isEqualTo("Desc");

        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(fixture.categoryRepository).save(categoryCaptor.capture());
        assertThat(categoryCaptor.getValue().getName()).isEqualTo("Electronics");
        assertThat(categoryCaptor.getValue().getParentId()).isEqualTo(parentId);
        assertThat(categoryCaptor.getValue().getImageUrl()).isEqualTo("image.jpg");
        assertThat(categoryCaptor.getValue().getDescription()).isEqualTo("Desc");
    }

    @Test
    @DisplayName("FUNC00013_UTCID10 - Creates category successfully while handling null and blank optional fields")
    void TC10_SuccessNullBlankFields() {
        ReqCreateCategoryDTO request = new ReqCreateCategoryDTO();
        request.setCategoryName("Toys");
        request.setParentId(null);
        request.setCategoryImage(null);
        request.setCategoryDescription("   ");

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));
        when(fixture.categoryRepository.existsByNameIgnoreCase("Toys")).thenReturn(false);

        Category savedCategory = Category.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .name("Toys")
                .parentId(null)
                .imageUrl(null)
                .description(null)
                .build();
        when(fixture.categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        ResCategoryDTO result = fixture.categoryService.createCategory(request);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Category created successfully");
        assertThat(result.getId()).isEqualTo(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        assertThat(result.getCategoryName()).isEqualTo("Toys");
        assertThat(result.getParentId()).isNull();
        assertThat(result.getCategoryImage()).isNull();
        assertThat(result.getCategoryDescription()).isNull();

        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(fixture.categoryRepository).save(categoryCaptor.capture());
        assertThat(categoryCaptor.getValue().getName()).isEqualTo("Toys");
        assertThat(categoryCaptor.getValue().getParentId()).isNull();
        assertThat(categoryCaptor.getValue().getImageUrl()).isNull();
        assertThat(categoryCaptor.getValue().getDescription()).isNull();
    }
}
