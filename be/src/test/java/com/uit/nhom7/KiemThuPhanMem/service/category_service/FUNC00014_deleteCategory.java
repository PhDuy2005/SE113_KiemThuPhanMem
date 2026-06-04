package com.uit.nhom7.KiemThuPhanMem.service.category_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResCategoryDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Category;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00014_deleteCategory extends CategoryServiceTestBase {

    private MockedStatic<SecurityUtil> mockedSecurityUtil;
    private Fixture fixture;

    private final UUID categoryId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID replacementCategoryId = UUID.fromString("22222222-2222-2222-2222-222222222222");

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
    @DisplayName("FUNC00014_UTCID01 - Fails because user is not authenticated")
    void TC1_NoLogin() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.categoryService.deleteCategory(categoryId, replacementCategoryId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00014_UTCID02 - Fails because email is not in DB")
    void TC2_NoSession() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.categoryService.deleteCategory(categoryId, replacementCategoryId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00014_UTCID03 - Fails because user is not ACTIVE")
    void TC3_NotActive() {
        User user = User.builder().accountStatus("PENDING").build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.categoryService.deleteCategory(categoryId, replacementCategoryId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00014_UTCID04 - Fails because user is not BUSINESS_ADMIN")
    void TC4_NotAdmin() {
        Role role = new Role();
        role.setName("CUSTOMER");
        User user = User.builder().accountStatus("ACTIVE").role(role).build();
        
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.categoryService.deleteCategory(categoryId, replacementCategoryId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00014_UTCID05 - Fails because replacementCategoryId is null")
    void TC5_ReplacementNull() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));

        assertThatThrownBy(() -> fixture.categoryService.deleteCategory(categoryId, null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Replacement category is required")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00014_UTCID06 - Fails because target and replacement IDs are identical")
    void TC6_SameIDs() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));

        assertThatThrownBy(() -> fixture.categoryService.deleteCategory(categoryId, categoryId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Replacement category must be different from target category")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00014_UTCID07 - Fails because target categoryId does not exist")
    void TC7_TargetNotFound() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));
        when(fixture.categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.categoryService.deleteCategory(categoryId, replacementCategoryId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Category not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FUNC00014_UTCID08 - Fails because replacementCategoryId does not exist")
    void TC8_ReplacementNotFound() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));
        
        Category targetCategory = Category.builder().id(categoryId).name("Target").build();
        when(fixture.categoryRepository.findById(categoryId)).thenReturn(Optional.of(targetCategory));
        when(fixture.categoryRepository.findById(replacementCategoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.categoryService.deleteCategory(categoryId, replacementCategoryId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Replacement category not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FUNC00014_UTCID09 - Deletes category successfully and updates products")
    void TC9_Success() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));
        
        Category targetCategory = Category.builder().id(categoryId).name("Target").build();
        when(fixture.categoryRepository.findById(categoryId)).thenReturn(Optional.of(targetCategory));
        
        Category replacementCategory = Category.builder().id(replacementCategoryId).name("Replacement").build();
        when(fixture.categoryRepository.findById(replacementCategoryId)).thenReturn(Optional.of(replacementCategory));

        ResCategoryDTO result = fixture.categoryService.deleteCategory(categoryId, replacementCategoryId);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Category deleted successfully");
        assertThat(result.getId()).isEqualTo(categoryId);
        assertThat(result.getCategoryName()).isEqualTo("Target");
        assertThat(result.getParentId()).isNull();
        assertThat(result.getCategoryImage()).isNull();
        assertThat(result.getCategoryDescription()).isNull();

        verify(fixture.productRepository).updateCategory(categoryId, replacementCategoryId);
        verify(fixture.categoryRepository).delete(targetCategory);
    }
}
