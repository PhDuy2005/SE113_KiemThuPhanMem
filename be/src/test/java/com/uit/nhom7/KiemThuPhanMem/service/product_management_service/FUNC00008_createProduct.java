package com.uit.nhom7.KiemThuPhanMem.service.product_management_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResProductDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Category;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Inventory;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.domain.table.ProductImage;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00008_createProduct extends ProductManagementServiceTestBase {

    private MockedStatic<SecurityUtil> mockedSecurityUtil;
    private Fixture fixture;

    private final UUID categoryId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
        mockedSecurityUtil = mockStatic(SecurityUtil.class);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtil.close();
    }

    private List<MultipartFile> getValidImages() {
        return List.of(new MockMultipartFile("file", "test.jpg", "image/jpeg", "dummy data".getBytes()));
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
    @DisplayName("FUNC00008_UTCID01 - Fails because user is not authenticated")
    void TC1_NoLogin() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.productManagementService.createProduct(
                "Product", null, BigDecimal.valueOf(100.0), categoryId, 10, "Apple", getValidImages()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00008_UTCID02 - Fails because email is not in DB")
    void TC2_NoSession() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.productManagementService.createProduct(
                "Product", null, BigDecimal.valueOf(100.0), categoryId, 10, "Apple", getValidImages()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00008_UTCID03 - Fails because user is not ACTIVE")
    void TC3_NotActive() {
        User user = User.builder().accountStatus("PENDING").build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.productManagementService.createProduct(
                "Product", null, BigDecimal.valueOf(100.0), categoryId, 10, "Apple", getValidImages()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00008_UTCID04 - Fails because user is not BUSINESS_ADMIN")
    void TC4_NotAdmin() {
        Role role = new Role();
        role.setName("CUSTOMER");
        User user = User.builder().accountStatus("ACTIVE").role(role).build();
        
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.productManagementService.createProduct(
                "Product", null, BigDecimal.valueOf(100.0), categoryId, 10, "Apple", getValidImages()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00008_UTCID05 - Fails because name is null")
    void TC5_MissingField() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));

        assertThatThrownBy(() -> fixture.productManagementService.createProduct(
                null, null, BigDecimal.valueOf(100.0), categoryId, 10, "Apple", getValidImages()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Required field is missing")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00008_UTCID06 - Fails because price is 0")
    void TC6_ZeroPrice() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));

        assertThatThrownBy(() -> fixture.productManagementService.createProduct(
                "Product", null, BigDecimal.valueOf(0.0), categoryId, 10, "Apple", getValidImages()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Price must be greater than 0")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00008_UTCID07 - Fails because stock is negative")
    void TC7_NegativeStock() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));

        assertThatThrownBy(() -> fixture.productManagementService.createProduct(
                "Product", null, BigDecimal.valueOf(100.0), categoryId, -1, "Apple", getValidImages()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Stock must not be negative")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00008_UTCID08 - Fails because images list is empty")
    void TC8_NoImage() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));

        assertThatThrownBy(() -> fixture.productManagementService.createProduct(
                "Product", null, BigDecimal.valueOf(100.0), categoryId, 10, "Apple", new ArrayList<>()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("At least one product image is required")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00008_UTCID09 - Fails because image extension is txt")
    void TC9_InvalidImage() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));

        List<MultipartFile> txtImage = List.of(new MockMultipartFile("file", "test.txt", "text/plain", "dummy data".getBytes()));

        assertThatThrownBy(() -> fixture.productManagementService.createProduct(
                "Product", null, BigDecimal.valueOf(100.0), categoryId, 10, "Apple", txtImage))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Product image must be jpg, png or webp and no larger than 5MB")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00008_UTCID10 - Fails because category does not exist")
    void TC10_CategoryNotFound() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));
        when(fixture.categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.productManagementService.createProduct(
                "Product", null, BigDecimal.valueOf(100.0), categoryId, 10, "Apple", getValidImages()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Category not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FUNC00008_UTCID11 - Creates product successfully")
    void TC11_Success() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));
        
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Electronics");
        when(fixture.categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        
        Product savedProduct = Product.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .name("Product")
                .price(BigDecimal.valueOf(100.0))
                .status("ACTIVE")
                .brand("Apple")
                .categoryId(categoryId)
                .build();
                
        when(fixture.productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ResProductDTO result = fixture.productManagementService.createProduct(
                "Product", null, BigDecimal.valueOf(100.0), categoryId, 10, "Apple", getValidImages());

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Product created successfully");
        assertThat(result.getId()).isEqualTo(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        assertThat(result.getName()).isEqualTo("Product");
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(100.0));
        assertThat(result.getStock()).isEqualTo(10);
        assertThat(result.getImageUrls()).hasSize(1);
        assertThat(result.getPrimaryImage()).isNotNull();
        
        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(fixture.inventoryRepository).save(inventoryCaptor.capture());
        assertThat(inventoryCaptor.getValue().getProductId()).isEqualTo(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        assertThat(inventoryCaptor.getValue().getQuantity()).isEqualTo(10);

        ArgumentCaptor<ProductImage> imageCaptor = ArgumentCaptor.forClass(ProductImage.class);
        verify(fixture.productImageRepository).save(imageCaptor.capture());
        assertThat(imageCaptor.getValue().getProduct()).isEqualTo(savedProduct);
        assertThat(imageCaptor.getValue().getImageUrl()).contains("/storage/products/");
        assertThat(imageCaptor.getValue().isPrimaryImage()).isTrue();
    }
}
