package com.uit.nhom7.KiemThuPhanMem.service;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResProductDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.domain.table.ProductImage;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductImageRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ProductSearchService {
    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 50;

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public ProductSearchService(ProductRepository productRepository, ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    @Transactional(readOnly = true)
    public List<ResProductDTO> search(String keyword, Integer limit) {
        String normalizedKeyword = normalizeForSearch(keyword);
        if (normalizedKeyword.isBlank()) {
            return List.of();
        }

        int resultLimit = normalizeLimit(limit);
        List<String> queryTokens = tokenize(normalizedKeyword);

        List<Product> products = productRepository.findByStatusIgnoreCase(Product.ACTIVE_STATUS).stream()
                .map(product -> new ScoredProduct(product, scoreProduct(product, normalizedKeyword, queryTokens)))
                .filter(scoredProduct -> scoredProduct.score() > 0)
                .sorted(Comparator.comparingInt(ScoredProduct::score).reversed()
                        .thenComparing(scoredProduct -> scoredProduct.product().getName()))
                .limit(resultLimit)
                .map(ScoredProduct::product)
                .toList();

        List<UUID> productIds = products.stream().map(Product::getId).toList();
        List<ProductImage> images = productIds.isEmpty() ? List.of() : productImageRepository.findByProductIdIn(productIds);
        Map<UUID, String> primaryImageMap = new HashMap<>();
        for (ProductImage img : images) {
            if (img.isPrimaryImage()) {
                primaryImageMap.put(img.getProduct().getId(), img.getImageUrl());
            } else {
                primaryImageMap.putIfAbsent(img.getProduct().getId(), img.getImageUrl());
            }
        }

        return products.stream()
                .map(product -> convertToDTO(product, primaryImageMap.get(product.getId())))
                .toList();
    }

    private int scoreProduct(Product product, String normalizedKeyword, List<String> queryTokens) {
        String productName = product.getNormalizedName();
        if (productName == null || productName.isBlank()) {
            productName = normalizeForSearch(product.getName());
        }
        String searchableText = (productName + " " + normalizeForSearch(product.getBrand())).trim();

        List<String> productTokens = tokenize(searchableText);
        if (productTokens.isEmpty()) {
            return 0;
        }

        int score = searchableText.contains(normalizedKeyword) ? 100 : 0;
        int matchedTokens = 0;

        for (String queryToken : queryTokens) {
            int tokenScore = bestTokenScore(queryToken, productTokens);
            if (tokenScore > 0) {
                matchedTokens++;
                score += tokenScore;
            }
        }

        if (matchedTokens == 0) {
            return 0;
        }
        if (queryTokens.size() > 1 && matchedTokens < Math.ceil(queryTokens.size() * 0.6)) {
            return 0;
        }
        return score + matchedTokens * 10;
    }

    private int bestTokenScore(String queryToken, List<String> productTokens) {
        int bestScore = 0;
        for (String productToken : productTokens) {
            if (productToken.equals(queryToken)) {
                bestScore = Math.max(bestScore, 40);
            } else if (productToken.startsWith(queryToken) || queryToken.startsWith(productToken)) {
                bestScore = Math.max(bestScore, 28);
            } else if (productToken.contains(queryToken)) {
                bestScore = Math.max(bestScore, 20);
            } else {
                int distance = levenshteinDistance(queryToken, productToken);
                int allowedDistance = queryToken.length() <= 4 ? 1 : 2;
                if (distance <= allowedDistance) {
                    bestScore = Math.max(bestScore, 18 - distance * 5);
                }
            }
        }
        return bestScore;
    }

    private int levenshteinDistance(String first, String second) {
        int[] previous = new int[second.length() + 1];
        int[] current = new int[second.length() + 1];

        for (int j = 0; j <= second.length(); j++) {
            previous[j] = j;
        }

        for (int i = 1; i <= first.length(); i++) {
            current[0] = i;
            for (int j = 1; j <= second.length(); j++) {
                int substitutionCost = first.charAt(i - 1) == second.charAt(j - 1) ? 0 : 1;
                current[j] = Math.min(
                        Math.min(current[j - 1] + 1, previous[j] + 1),
                        previous[j - 1] + substitutionCost);
            }

            int[] temp = previous;
            previous = current;
            current = temp;
        }

        return previous[second.length()];
    }

    private List<String> tokenize(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value.split("\\s+")).stream()
                .filter(token -> !token.isBlank())
                .toList();
    }

    private String normalizeForSearch(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd');
        return normalized.replaceAll("[^a-z0-9]+", " ").trim().replaceAll("\\s+", " ");
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private ResProductDTO convertToDTO(Product product, String primaryImage) {
        return ResProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .status(product.getStatus())
                .brand(product.getBrand())
                .categoryId(product.getCategoryId())
                .primaryImage(primaryImage)
                .build();
    }

    private record ScoredProduct(Product product, int score) {
    }
}
