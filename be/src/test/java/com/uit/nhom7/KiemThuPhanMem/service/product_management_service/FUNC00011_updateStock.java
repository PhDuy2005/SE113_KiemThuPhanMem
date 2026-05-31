package com.uit.nhom7.KiemThuPhanMem.service.product_management_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResProductDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Inventory;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00011_updateStock extends ProductManagementServiceTestBase {

    private MockedStatic<SecurityUtil> mockedSecurityUtil;
    private Fixture fixture;

    private final UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");

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
    @DisplayName("FUNC00011_UTCID01 - Fails because user is not authenticated")
    void TC1_NoLogin() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.productManagementService.updateStock(productId, 50))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00011_UTCID02 - Fails because email is not in DB")
    void TC2_NoSession() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.productManagementService.updateStock(productId, 50))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00011_UTCID03 - Fails because user is not ACTIVE")
    void TC3_NotActive() {
        User user = User.builder().accountStatus("PENDING").build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.productManagementService.updateStock(productId, 50))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00011_UTCID04 - Fails because user is not BUSINESS_ADMIN")
    void TC4_NotAdmin() {
        Role role = new Role();
        role.setName("CUSTOMER");
        User user = User.builder().accountStatus("ACTIVE").role(role).build();
        
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.productManagementService.updateStock(productId, 50))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00011_UTCID05 - Fails because newStock is null")
    void TC5_MissingStock() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));

        assertThatThrownBy(() -> fixture.productManagementService.updateStock(productId, null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Required field is missing")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00011_UTCID06 - Fails because newStock is negative")
    void TC6_NegativeStock() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));

        assertThatThrownBy(() -> fixture.productManagementService.updateStock(productId, -1))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Stock must not be negative")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00011_UTCID07 - Fails because productId does not exist")
    void TC7_ProductNotFound() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));
        when(fixture.productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.productManagementService.updateStock(productId, 50))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Product not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FUNC00011_UTCID08 - Updates successfully: empty inventory created, status becomes OUT_OF_STOCK")
    void TC8_SuccessEmptyInv() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));
        
        Product product = Product.builder().id(productId).status("ACTIVE").build();
        when(fixture.productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(fixture.inventoryRepository.findById(productId)).thenReturn(Optional.empty());
        
        when(fixture.productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));
        when(fixture.productImageRepository.findByProductIdOrderByPrimaryImageDescCreatedAtAsc(productId)).thenReturn(new ArrayList<>());

        ResProductDTO result = fixture.productManagementService.updateStock(productId, 0);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Product stock updated successfully");
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getStatus()).isEqualTo("OUT_OF_STOCK");
        assertThat(result.getStock()).isNull(); // As per the MD because findById returns empty for toProductDTO

        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(fixture.inventoryRepository).save(inventoryCaptor.capture());
        assertThat(inventoryCaptor.getValue().getProductId()).isEqualTo(productId);
        assertThat(inventoryCaptor.getValue().getQuantity()).isEqualTo(0);
        assertThat(inventoryCaptor.getValue().getReservedQuantity()).isEqualTo(0);
    }

    @Test
    @DisplayName("FUNC00011_UTCID09 - Updates successfully: reserved null becomes 0, status becomes ACTIVE")
    void TC9_SuccessNullReserved() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));
        
        Product product = Product.builder().id(productId).status("OUT_OF_STOCK").build();
        when(fixture.productRepository.findById(productId)).thenReturn(Optional.of(product));
        
        Inventory inventory = Inventory.builder()
                .productId(productId)
                .quantity(0)
                .reservedQuantity(null)
                .build();
        when(fixture.inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        
        when(fixture.productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));
        when(fixture.productImageRepository.findByProductIdOrderByPrimaryImageDescCreatedAtAsc(productId)).thenReturn(new ArrayList<>());

        ResProductDTO result = fixture.productManagementService.updateStock(productId, 50);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Product stock updated successfully");
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        assertThat(result.getStock()).isEqualTo(50); // The mutated inventory object will be returned by findById

        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(fixture.inventoryRepository).save(inventoryCaptor.capture());
        assertThat(inventoryCaptor.getValue().getProductId()).isEqualTo(productId);
        assertThat(inventoryCaptor.getValue().getQuantity()).isEqualTo(50);
        assertThat(inventoryCaptor.getValue().getReservedQuantity()).isEqualTo(0);
    }

    @Test
    @DisplayName("FUNC00011_UTCID10 - Updates successfully: valid inventory updated, status remains ACTIVE")
    void TC10_SuccessValidInv() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(fixture.userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(getAdminUser()));
        
        Product product = Product.builder().id(productId).status("ACTIVE").build();
        when(fixture.productRepository.findById(productId)).thenReturn(Optional.of(product));
        
        Inventory inventory = Inventory.builder()
                .productId(productId)
                .quantity(10)
                .reservedQuantity(5)
                .build();
        when(fixture.inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        
        when(fixture.productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));
        when(fixture.productImageRepository.findByProductIdOrderByPrimaryImageDescCreatedAtAsc(productId)).thenReturn(new ArrayList<>());

        ResProductDTO result = fixture.productManagementService.updateStock(productId, 50);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Product stock updated successfully");
        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
        assertThat(result.getStock()).isEqualTo(50);

        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(fixture.inventoryRepository).save(inventoryCaptor.capture());
        assertThat(inventoryCaptor.getValue().getProductId()).isEqualTo(productId);
        assertThat(inventoryCaptor.getValue().getQuantity()).isEqualTo(50);
        assertThat(inventoryCaptor.getValue().getReservedQuantity()).isEqualTo(5);
    }
}
