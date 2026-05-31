package com.uit.nhom7.KiemThuPhanMem.service.product_management_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateProductGeneralDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResProductDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Category;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Inventory;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.domain.table.ProductImage;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00009_updateProduct extends ProductManagementServiceTestBase {

    private MockedStatic<SecurityUtil> mockedSecurityUtil;
    private Fixture fixture;

    private final UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID categoryId = UUID.fromString("22222222-2222-2222-2222-222222222222");

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

    private Product getOldProduct() {
        return Product.builder()
                .id(productId)
                .name("Old")
                .description("Old")
                .categoryId(categoryId)
                .brand("Old")
                .status("ACTIVE")
                .build();
    }

    private void mockCommonSuccessContext() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));
        when(fixture.productRepository.findById(productId)).thenReturn(Optional.of(getOldProduct()));
        
        Inventory inventory = Inventory.builder().quantity(15).build();
        when(fixture.inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        
        ProductImage img = ProductImage.builder().imageUrl("/img.jpg").build();
        when(fixture.productImageRepository.findByProductIdOrderByPrimaryImageDescCreatedAtAsc(productId)).thenReturn(List.of(img));
        
        when(fixture.productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    @DisplayName("FUNC00009_UTCID01 - Fails because user is not authenticated")
    void TC1_NoLogin() {
        ReqUpdateProductGeneralDTO request = new ReqUpdateProductGeneralDTO();
        request.setName("New Name");
        request.setDescription("New Desc");
        request.setCategoryId(categoryId);
        request.setBrand("New Brand");
        request.setStatus("ACTIVE");

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.productManagementService.updateProduct(productId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00009_UTCID02 - Fails because email is not in DB")
    void TC2_NoSession() {
        ReqUpdateProductGeneralDTO request = new ReqUpdateProductGeneralDTO();
        request.setName("New Name");

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.productManagementService.updateProduct(productId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00009_UTCID03 - Fails because user is not ACTIVE")
    void TC3_NotActive() {
        ReqUpdateProductGeneralDTO request = new ReqUpdateProductGeneralDTO();
        request.setName("New Name");

        User user = User.builder().accountStatus("PENDING").build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.productManagementService.updateProduct(productId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00009_UTCID04 - Fails because user is not BUSINESS_ADMIN")
    void TC4_NotAdmin() {
        ReqUpdateProductGeneralDTO request = new ReqUpdateProductGeneralDTO();
        request.setName("New Name");

        Role role = new Role();
        role.setName("CUSTOMER");
        User user = User.builder().accountStatus("ACTIVE").role(role).build();
        
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.productManagementService.updateProduct(productId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00009_UTCID05 - Fails because productId does not exist")
    void TC5_ProductNotFound() {
        ReqUpdateProductGeneralDTO request = new ReqUpdateProductGeneralDTO();
        request.setName("New Name");
        
        UUID invalidProductId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));
        when(fixture.productRepository.findById(invalidProductId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.productManagementService.updateProduct(invalidProductId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Product not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FUNC00009_UTCID06 - Fails because categoryId does not exist")
    void TC6_CategoryNotFound() {
        ReqUpdateProductGeneralDTO request = new ReqUpdateProductGeneralDTO();
        request.setName("New Name");
        UUID invalidCategoryId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        request.setCategoryId(invalidCategoryId);

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));
        when(fixture.productRepository.findById(productId)).thenReturn(Optional.of(getOldProduct()));
        when(fixture.categoryRepository.findById(invalidCategoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.productManagementService.updateProduct(productId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Category not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FUNC00009_UTCID07 - Updates all provided valid fields successfully")
    void TC7_UpdateAllFieldsValid() {
        ReqUpdateProductGeneralDTO request = new ReqUpdateProductGeneralDTO();
        request.setName("New Name");
        request.setDescription("New Desc");
        request.setCategoryId(categoryId);
        request.setBrand("New Brand");
        request.setStatus("ACTIVE");

        mockCommonSuccessContext();
        Category category = new Category();
        category.setId(categoryId);
        category.setName("New Category");
        when(fixture.categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        ResProductDTO result = fixture.productManagementService.updateProduct(productId, request);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Product details updated successfully");
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getDescription()).isEqualTo("New Desc");
        assertThat(result.getCategoryId()).isEqualTo(categoryId);
        assertThat(result.getBrand()).isEqualTo("New Brand");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        assertThat(result.getStock()).isEqualTo(15);
        assertThat(result.getImageUrls()).containsExactly("/img.jpg");
        assertThat(result.getPrimaryImage()).isEqualTo("/img.jpg");
    }

    @Test
    @DisplayName("FUNC00009_UTCID08 - Updates with blank strings to trigger cleanNullableText")
    void TC8_UpdateBlankStrings() {
        ReqUpdateProductGeneralDTO request = new ReqUpdateProductGeneralDTO();
        request.setName("   ");
        request.setDescription("   ");
        request.setBrand("   ");
        request.setStatus("   ");
        // CategoryId is null according to MD

        mockCommonSuccessContext();

        ResProductDTO result = fixture.productManagementService.updateProduct(productId, request);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Product details updated successfully");
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Old"); // Blank string shouldn't override Name since if name.isBlank(), it skips it
        assertThat(result.getDescription()).isNull(); // cleanNullableText turns "   " to null
        assertThat(result.getBrand()).isNull(); // cleanNullableText turns "   " to null
        assertThat(result.getStatus()).isEqualTo("ACTIVE"); // if request.status.isBlank(), it skips it
        assertThat(result.getCategoryId()).isEqualTo(categoryId);
    }

    @Test
    @DisplayName("FUNC00009_UTCID09 - Updates with all null fields, making no changes")
    void TC9_UpdateNullFields() {
        ReqUpdateProductGeneralDTO request = new ReqUpdateProductGeneralDTO();
        // all fields null

        mockCommonSuccessContext();

        ResProductDTO result = fixture.productManagementService.updateProduct(productId, request);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Product details updated successfully");
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Old");
        assertThat(result.getDescription()).isEqualTo("Old");
        assertThat(result.getBrand()).isEqualTo("Old");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        assertThat(result.getCategoryId()).isEqualTo(categoryId);
    }
}
