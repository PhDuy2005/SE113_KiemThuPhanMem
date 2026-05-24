package com.uit.nhom7.KiemThuPhanMem.service;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqAddCartItemDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateCartItemDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResCartItemActionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Cart;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResCartDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.CartItem;
import com.uit.nhom7.KiemThuPhanMem.domain.table.CartItemId;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Inventory;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.domain.table.ProductImage;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.CartItemRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.CartRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.InventoryRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductImageRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

@Service
public class CartService {
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            InventoryRepository inventoryRepository,
            ProductImageRepository productImageRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.inventoryRepository = inventoryRepository;
        this.productImageRepository = productImageRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ResCartItemActionDTO addItem(ReqAddCartItemDTO request) {
        User currentUser = getCurrentActiveUser();
        Product product = productRepository.findByIdAndStatusIgnoreCase(request.getProductId(), Product.ACTIVE_STATUS)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Product not found"));
        int availableQuantity = inventoryRepository.findById(product.getId())
                .map(Inventory::getAvailableQuantity)
                .orElse(0);

        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder()
                        .user(currentUser)
                        .build()));

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseGet(() -> CartItem.builder()
                        .id(new CartItemId(cart.getId(), product.getId()))
                        .cart(cart)
                        .product(product)
                        .quantity(0)
                        .build());

        int newQuantity = cartItem.getQuantity() + request.getQuantity();
        if (newQuantity > availableQuantity) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "Insufficient stock. Available quantity: " + availableQuantity);
        }

        cartItem.setQuantity(newQuantity);
        CartItem savedItem = cartItemRepository.save(cartItem);
        int cartBadgeCount = cartItemRepository.getTotalItemsCount(cart.getId());

        return ResCartItemActionDTO.builder()
                .cartId(cart.getId())
                .productId(product.getId())
                .quantity(savedItem.getQuantity())
                .cartBadgeCount(cartBadgeCount)
                .availableQuantity(availableQuantity)
                .totalCartPrice(calculateTotalCartPrice(cart.getId()))
                .message("Product added to cart successfully")
                .build();
    }

    @Transactional(noRollbackFor = BusinessException.class)
    public ResCartItemActionDTO updateItemQuantity(UUID productId, ReqUpdateCartItemDTO request) {
        User currentUser = getCurrentActiveUser();
        Cart cart = getCurrentUserCart(currentUser);
        CartItem cartItem = getCartItem(cart.getId(), productId);
        int availableQuantity = inventoryRepository.findById(productId)
                .map(Inventory::getAvailableQuantity)
                .orElse(0);

        if (request.getNewQuantity() > availableQuantity) {
            if (availableQuantity > 0) {
                cartItem.setQuantity(availableQuantity);
                cartItemRepository.save(cartItem);
            } else {
                cartItemRepository.delete(cartItem);
            }
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                    "Insufficient stock. Available quantity: " + availableQuantity);
        }

        cartItem.setQuantity(request.getNewQuantity());
        CartItem savedItem = cartItemRepository.save(cartItem);

        return ResCartItemActionDTO.builder()
                .cartId(cart.getId())
                .productId(productId)
                .quantity(savedItem.getQuantity())
                .cartBadgeCount(cartItemRepository.getTotalItemsCount(cart.getId()))
                .availableQuantity(availableQuantity)
                .totalCartPrice(calculateTotalCartPrice(cart.getId()))
                .message("Cart item quantity updated successfully")
                .build();
    }

    @Transactional
    public ResCartItemActionDTO removeItem(UUID productId) {
        User currentUser = getCurrentActiveUser();
        Cart cart = getCurrentUserCart(currentUser);
        CartItem cartItem = getCartItem(cart.getId(), productId);

        cartItemRepository.delete(cartItem);

        return ResCartItemActionDTO.builder()
                .cartId(cart.getId())
                .productId(productId)
                .quantity(0)
                .cartBadgeCount(cartItemRepository.getTotalItemsCount(cart.getId()))
                .availableQuantity(inventoryRepository.findById(productId)
                        .map(Inventory::getAvailableQuantity)
                        .orElse(0))
                .totalCartPrice(calculateTotalCartPrice(cart.getId()))
                .message("Cart item removed successfully")
                .build();
    }

    private Cart getCurrentUserCart(User currentUser) {
        return cartRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Cart not found"));
    }

    private CartItem getCartItem(UUID cartId, UUID productId) {
        return cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Cart item not found"));
    }

    private BigDecimal calculateTotalCartPrice(UUID cartId) {
        return cartItemRepository.findByCartIdWithProduct(cartId).stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private User getCurrentActiveUser() {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "You must login first"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "User session is invalid"));
        if (user.getAccountStatus() == null
                || !ACTIVE_ACCOUNT_STATUS.equals(user.getAccountStatus().trim().toUpperCase(Locale.ROOT))) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "User account is not active");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public ResCartDTO getCart() {
        User currentUser = getCurrentActiveUser();
        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder()
                        .user(currentUser)
                        .build()));

        java.util.List<CartItem> dbItems = cartItemRepository.findByCartIdWithProduct(cart.getId());
        BigDecimal totalPrice = calculateTotalCartPrice(cart.getId());
        int totalItemsCount = cartItemRepository.getTotalItemsCount(cart.getId());

        java.util.List<ResCartDTO.CartItemDto> items = dbItems.stream()
                .map(item -> {
                    Product product = item.getProduct();
                    java.util.List<ProductImage> dbImages = productImageRepository.findByProductIdOrderByPrimaryImageDescCreatedAtAsc(product.getId());
                    
                    java.util.List<ResCartDTO.CartProductImageDto> images = dbImages.stream()
                            .map(img -> ResCartDTO.CartProductImageDto.builder()
                                    .id(img.getId())
                                    .imageUrl(img.getImageUrl())
                                    .isPrimary(img.isPrimaryImage())
                                    .build())
                            .toList();

                    ResCartDTO.CartProductDto productDto = ResCartDTO.CartProductDto.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .brand(product.getBrand())
                            .price(product.getPrice())
                            .images(images)
                            .build();

                    return ResCartDTO.CartItemDto.builder()
                            .productId(product.getId())
                            .quantity(item.getQuantity())
                            .createdAt(item.getCreatedAt())
                            .updatedAt(item.getUpdatedAt())
                            .product(productDto)
                            .build();
                })
                .toList();

        return ResCartDTO.builder()
                .userId(currentUser.getId())
                .items(items)
                .totalPrice(totalPrice)
                .totalItemsCount(totalItemsCount)
                .build();
    }

    @Transactional
    public void clearCart() {
        User currentUser = getCurrentActiveUser();
        Cart cart = getCurrentUserCart(currentUser);
        cartItemRepository.deleteByCartId(cart.getId());
    }
}
