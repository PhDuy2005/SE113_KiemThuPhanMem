package com.uit.nhom7.KiemThuPhanMem.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateProductGeneralDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResProductDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResultPaginationDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Inventory;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.domain.table.ProductImage;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.CategoryRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.InventoryRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductImageRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

@Service
public class ProductManagementService {
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";
    private static final String BUSINESS_ADMIN_ROLE = "BUSINESS_ADMIN";
    private static final String MSG1 = "Required field is missing";
    private static final String MSG77 = "Product created successfully";
    private static final String MSG78 = "Product image must be jpg, png or webp and no larger than 5MB";
    private static final String MSG79 = "Price must be greater than 0";
    private static final String MSG80 = "At least one product image is required";
    private static final String MSG81 = "Product price updated successfully";
    private static final String MSG82 = "New price must be different from current price";
    private static final String MSG83 = "Product stock updated successfully";
    private static final String MSG84 = "Stock must not be negative";
    private static final String MSG86 = "Product discontinued successfully";
    private static final long MAX_IMAGE_SIZE = 5L * 1024L * 1024L;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Path PRODUCT_IMAGE_DIR = Path.of("storage", "products");

    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductManagementService(
            CategoryRepository categoryRepository,
            InventoryRepository inventoryRepository,
            ProductImageRepository productImageRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.inventoryRepository = inventoryRepository;
        this.productImageRepository = productImageRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ResProductDTO createProduct(
            String name,
            String description,
            BigDecimal price,
            UUID categoryId,
            Integer stock,
            String brand,
            List<MultipartFile> images) {
        getCurrentBusinessAdmin();
        validateCreateProductInput(name, price, categoryId, stock, images);
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Category not found"));

        Product product = Product.builder()
                .name(name.trim())
                .description(cleanNullableText(description))
                .price(price)
                .status(Product.ACTIVE_STATUS)
                .brand(cleanNullableText(brand))
                .categoryId(categoryId)
                .build();
        Product savedProduct = productRepository.save(product);

        List<String> imageUrls = storeProductImages(savedProduct, images);
        inventoryRepository.save(Inventory.builder()
                .productId(savedProduct.getId())
                .quantity(stock)
                .reservedQuantity(0)
                .build());

        return toProductDTO(savedProduct, stock, imageUrls, MSG77);
    }

    @Transactional
    public ResProductDTO updatePrice(UUID productId, BigDecimal newPrice) {
        getCurrentBusinessAdmin();
        if (newPrice == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG1);
        }
        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG79);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Product not found"));
        if (product.getPrice() != null && product.getPrice().compareTo(newPrice) == 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG82);
        }

        product.setPrice(newPrice);
        Product savedProduct = productRepository.save(product);
        return toProductDTO(savedProduct, MSG81);
    }

    @Transactional
    public ResProductDTO updateStock(UUID productId, Integer newStock) {
        getCurrentBusinessAdmin();
        if (newStock == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG1);
        }
        if (newStock < 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG84);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Product not found"));
        Inventory inventory = inventoryRepository.findById(productId)
                .orElse(Inventory.builder()
                        .productId(productId)
                        .reservedQuantity(0)
                        .build());
        inventory.setQuantity(newStock);
        if (inventory.getReservedQuantity() == null) {
            inventory.setReservedQuantity(0);
        }
        inventoryRepository.save(inventory);

        if (newStock == 0) {
            product.setStatus(Product.OUT_OF_STOCK_STATUS);
        } else if (Product.OUT_OF_STOCK_STATUS.equalsIgnoreCase(product.getStatus())) {
            product.setStatus(Product.ACTIVE_STATUS);
        }

        Product savedProduct = productRepository.save(product);
        return toProductDTO(savedProduct, MSG83);
    }

    @Transactional
    public ResProductDTO discontinueProduct(UUID productId) {
        getCurrentBusinessAdmin();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Product not found"));
        product.setStatus(Product.DISCONTINUED_STATUS);
        Product savedProduct = productRepository.save(product);
        return toProductDTO(savedProduct, MSG86);
    }

    private void validateCreateProductInput(
            String name,
            BigDecimal price,
            UUID categoryId,
            Integer stock,
            List<MultipartFile> images) {
        if (name == null || name.isBlank() || price == null || categoryId == null || stock == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG1);
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG79);
        }
        if (stock < 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Stock must not be negative");
        }
        if (images == null || images.isEmpty() || images.stream().allMatch(MultipartFile::isEmpty)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG80);
        }
        images.forEach(this::validateImageFile);
    }

    private List<String> storeProductImages(Product product, List<MultipartFile> images) {
        try {
            Files.createDirectories(PRODUCT_IMAGE_DIR);
        } catch (IOException ex) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot initialize product image storage");
        }

        List<MultipartFile> validImages = images.stream()
                .filter(image -> !image.isEmpty())
                .toList();
        return validImages.stream()
                .map(image -> storeProductImage(product, image, validImages.indexOf(image) == 0))
                .toList();
    }

    private String storeProductImage(Product product, MultipartFile image, boolean primaryImage) {
        String extension = getExtension(image);
        String fileName = UUID.randomUUID() + "." + extension;
        Path target = PRODUCT_IMAGE_DIR.resolve(fileName).normalize();
        try {
            Files.copy(image.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot store product image");
        }

        String imageUrl = "/storage/products/" + fileName;
        productImageRepository.save(ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .primaryImage(primaryImage)
                .build());
        return imageUrl;
    }

    private void validateImageFile(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return;
        }
        if (image.getSize() > MAX_IMAGE_SIZE || !ALLOWED_EXTENSIONS.contains(getExtension(image))) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG78);
        }
    }

    private String getExtension(MultipartFile image) {
        String originalName = image.getOriginalFilename();
        if (originalName == null || !originalName.contains(".")) {
            return "";
        }
        return originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private ResProductDTO toProductDTO(Product product, String message) {
        Integer stock = inventoryRepository.findById(product.getId())
                .map(Inventory::getQuantity)
                .orElse(null);
        List<String> imageUrls = productImageRepository.findByProductIdOrderByPrimaryImageDescCreatedAtAsc(product.getId())
                .stream()
                .map(ProductImage::getImageUrl)
                .toList();
        return toProductDTO(product, stock, imageUrls, message);
    }

    private ResProductDTO toProductDTO(Product product, Integer stock, List<String> imageUrls, String message) {
        String primaryImage = (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.get(0) : null;
        return ResProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .status(product.getStatus())
                .brand(product.getBrand())
                .categoryId(product.getCategoryId())
                .stock(stock)
                .imageUrls(imageUrls)
                .primaryImage(primaryImage)
                .message(message)
                .build();
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

    private User getCurrentStaffOrBusinessAdmin() {
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
        if (!BUSINESS_ADMIN_ROLE.equals(roleName) && !"STAFF".equals(roleName)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Only staff or business admin can perform this action");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public ResultPaginationDTO getAdminProducts(String keyword, UUID categoryId, String status, int pageNumber, int pageSize) {
        getCurrentStaffOrBusinessAdmin();
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                Math.max(pageNumber - 1, 0),
                pageSize <= 0 ? 20 : pageSize,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));

        org.springframework.data.jpa.domain.Specification<Product> spec = (root, query, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%"));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("categoryId"), categoryId));
            }
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("status")), status.trim().toLowerCase(Locale.ROOT)));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        org.springframework.data.domain.Page<Product> page = productRepository.findAll(spec, pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(page.getTotalPages());
        meta.setTotalItems(page.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(page.getContent().stream()
                .map(product -> toProductDTO(product, null))
                .toList());
        return result;
    }

    @Transactional
    public ResProductDTO updateProduct(UUID productId, ReqUpdateProductGeneralDTO request) {
        getCurrentBusinessAdmin();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Product not found"));

        if (request.getName() != null && !request.getName().isBlank()) {
            product.setName(request.getName().trim());
        }
        if (request.getDescription() != null) {
            product.setDescription(cleanNullableText(request.getDescription()));
        }
        if (request.getCategoryId() != null) {
            categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Category not found"));
            product.setCategoryId(request.getCategoryId());
        }
        if (request.getBrand() != null) {
            product.setBrand(cleanNullableText(request.getBrand()));
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            product.setStatus(request.getStatus().trim());
        }

        Product savedProduct = productRepository.save(product);
        return toProductDTO(savedProduct, "Product details updated successfully");
    }

    private String cleanNullableText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
