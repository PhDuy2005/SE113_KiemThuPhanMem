package com.uit.nhom7.KiemThuPhanMem.service;

import java.math.BigDecimal;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResBestSellingProductsReportDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResCategoryDistributionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResRevenueReportDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Order;
import com.uit.nhom7.KiemThuPhanMem.domain.table.OrderItem;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Payment;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.CategoryRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.OrderItemRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.OrderRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.PaymentRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

@Service
public class ReportService {
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";
    private static final String BUSINESS_ADMIN_ROLE = "BUSINESS_ADMIN";
    private static final String MSG1 = "Start date and end date are required";
    private static final String MSG101 = "End date must not be before start date";
    private static final String MSG102 = "There is no revenue data for this period";
    private static final String MSG103 = "There is no best-selling product data for this period";
    private static final String MSG105 = "No orders match the export filter";

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ReportService(
            OrderItemRepository orderItemRepository,
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public ResRevenueReportDTO getRevenueReport(LocalDate startDate, LocalDate endDate) {
        getCurrentBusinessAdmin();
        if (startDate == null || endDate == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG1);
        }
        if (endDate.isBefore(startDate)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG101);
        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant start = startDate.atStartOfDay(zoneId).toInstant();
        Instant end = endDate.atTime(LocalTime.MAX).atZone(zoneId).toInstant();
        List<Order> deliveredOrders = orderRepository.findByStatusIgnoreCaseAndCompletedAtBetween(
                Order.DELIVERED_STATUS, start, end);

        Map<LocalDate, List<Order>> ordersByDate = deliveredOrders.stream()
                .filter(order -> order.getCompletedAt() != null)
                .collect(Collectors.groupingBy(order -> order.getCompletedAt().atZone(zoneId).toLocalDate()));

        List<ResRevenueReportDTO.RevenuePoint> chartData = ordersByDate.entrySet().stream()
                .map(entry -> ResRevenueReportDTO.RevenuePoint.builder()
                        .date(entry.getKey())
                        .value(sumRevenue(entry.getValue()))
                        .orderCount(entry.getValue().size())
                        .build())
                .sorted(Comparator.comparing(ResRevenueReportDTO.RevenuePoint::getDate))
                .toList();

        return ResRevenueReportDTO.builder()
                .totalRevenue(sumRevenue(deliveredOrders))
                .completedOrderCount(deliveredOrders.size())
                .chartData(chartData)
                .message(deliveredOrders.isEmpty() ? MSG102 : null)
                .build();
    }

    @Transactional(readOnly = true)
    public ResBestSellingProductsReportDTO getBestSellingProductsReport(
            LocalDate startDate,
            LocalDate endDate,
            Integer limit) {
        getCurrentBusinessAdmin();
        if (startDate == null || endDate == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG1);
        }
        if (endDate.isBefore(startDate)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG101);
        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant start = startDate.atStartOfDay(zoneId).toInstant();
        Instant end = endDate.atTime(LocalTime.MAX).atZone(zoneId).toInstant();
        List<OrderItem> orderItems = orderItemRepository.findByOrderStatusAndOrderCreatedAtBetweenWithProduct(
                Order.DELIVERED_STATUS, start, end);

        List<ResBestSellingProductsReportDTO.BestSellingProduct> rankingList = buildBestSellingRanking(orderItems);
        if (limit != null && limit > 0 && rankingList.size() > limit) {
            rankingList = rankingList.subList(0, limit);
        }

        return ResBestSellingProductsReportDTO.builder()
                .rankingList(rankingList)
                .message(rankingList.isEmpty() ? MSG103 : null)
                .build();
    }

    @Transactional(readOnly = true)
    public byte[] exportOrders(LocalDate startDate, LocalDate endDate, String orderStatus) {
        getCurrentBusinessAdmin();
        if (startDate == null || endDate == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG1);
        }
        if (endDate.isBefore(startDate)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG101);
        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant start = startDate.atStartOfDay(zoneId).toInstant();
        Instant end = endDate.atTime(LocalTime.MAX).atZone(zoneId).toInstant();
        List<Order> orders = orderStatus == null || orderStatus.isBlank()
                ? orderRepository.findByCreatedAtBetween(start, end)
                : orderRepository.findByStatusIgnoreCaseAndCreatedAtBetween(orderStatus.trim(), start, end);
        if (orders.isEmpty()) {
            throw new BusinessException(HttpStatus.NOT_FOUND, MSG105);
        }
        return buildOrdersWorkbook(orders, zoneId);
    }

    private BigDecimal sumRevenue(List<Order> orders) {
        return orders.stream()
                .map(Order::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private byte[] buildOrdersWorkbook(List<Order> orders, ZoneId zoneId) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Orders");
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row header = sheet.createRow(0);
            List<String> headers = List.of(
                    "Order ID",
                    "Order Date",
                    "Customer",
                    "Total Amount",
                    "Status",
                    "Payment Method");
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            for (int rowIndex = 0; rowIndex < orders.size(); rowIndex++) {
                Order order = orders.get(rowIndex);
                Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
                Row row = sheet.createRow(rowIndex + 1);
                row.createCell(0).setCellValue(order.getId().toString());
                row.createCell(1).setCellValue(order.getCreatedAt() == null
                        ? ""
                        : order.getCreatedAt().atZone(zoneId).toLocalDateTime().toString());
                row.createCell(2).setCellValue(order.getUser().getUserFullName() == null
                        ? order.getUser().getEmail()
                        : order.getUser().getUserFullName());
                row.createCell(3).setCellValue(order.getTotalAmount() == null
                        ? BigDecimal.ZERO.doubleValue()
                        : order.getTotalAmount().doubleValue());
                row.createCell(4).setCellValue(order.getStatus());
                row.createCell(5).setCellValue(payment == null ? "" : payment.getPaymentMethod().getName());
            }

            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot export orders to Excel");
        }
    }

    private List<ResBestSellingProductsReportDTO.BestSellingProduct> buildBestSellingRanking(List<OrderItem> orderItems) {
        Map<UUID, ProductSalesAccumulator> accumulatorByProduct = new HashMap<>();
        for (OrderItem item : orderItems) {
            UUID productId = item.getProduct().getId();
            ProductSalesAccumulator accumulator = accumulatorByProduct.computeIfAbsent(productId,
                    ignored -> new ProductSalesAccumulator(productId, item.getProduct().getName()));
            int quantity = item.getQuantity() == null ? 0 : item.getQuantity();
            BigDecimal price = item.getPrice() == null ? BigDecimal.ZERO : item.getPrice();
            accumulator.totalSold += quantity;
            accumulator.revenue = accumulator.revenue.add(price.multiply(BigDecimal.valueOf(quantity)));
        }

        List<ProductSalesAccumulator> sortedAccumulators = new ArrayList<>(accumulatorByProduct.values());
        sortedAccumulators.sort(Comparator
                .comparingLong(ProductSalesAccumulator::getTotalSold)
                .reversed()
                .thenComparing(ProductSalesAccumulator::getProductName));

        List<ResBestSellingProductsReportDTO.BestSellingProduct> ranking = new ArrayList<>();
        for (int i = 0; i < sortedAccumulators.size(); i++) {
            ProductSalesAccumulator accumulator = sortedAccumulators.get(i);
            ranking.add(ResBestSellingProductsReportDTO.BestSellingProduct.builder()
                    .rank(i + 1)
                    .productId(accumulator.productId)
                    .productName(accumulator.productName)
                    .totalSold(accumulator.totalSold)
                    .revenue(accumulator.revenue)
                    .build());
        }
        return ranking;
    }

    private static class ProductSalesAccumulator {
        private final UUID productId;
        private final String productName;
        private long totalSold;
        private BigDecimal revenue = BigDecimal.ZERO;

        private ProductSalesAccumulator(UUID productId, String productName) {
            this.productId = productId;
            this.productName = productName;
        }

        private long getTotalSold() {
            return totalSold;
        }

        private String getProductName() {
            return productName;
        }
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

    @Transactional(readOnly = true)
    public ResCategoryDistributionDTO getCategoryDistributionReport(LocalDate startDate, LocalDate endDate) {
        getCurrentBusinessAdmin();
        if (startDate == null || endDate == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG1);
        }
        if (endDate.isBefore(startDate)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG101);
        }

        ZoneId zoneId = ZoneId.systemDefault();
        Instant start = startDate.atStartOfDay(zoneId).toInstant();
        Instant end = endDate.atTime(LocalTime.MAX).atZone(zoneId).toInstant();
        List<OrderItem> orderItems = orderItemRepository.findByOrderStatusAndOrderCreatedAtBetweenWithProduct(
                Order.DELIVERED_STATUS, start, end);

        Map<UUID, String> categoryNameMap = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(
                        com.uit.nhom7.KiemThuPhanMem.domain.table.Category::getId,
                        com.uit.nhom7.KiemThuPhanMem.domain.table.Category::getName,
                        (existing, replacement) -> existing
                ));

        Map<String, Double> revenueByCategory = new HashMap<>();
        for (OrderItem item : orderItems) {
            UUID catId = item.getProduct().getCategoryId();
            String catName = catId != null ? categoryNameMap.getOrDefault(catId, "Other") : "Other";
            int quantity = item.getQuantity() != null ? item.getQuantity() : 0;
            BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
            double revenue = price.multiply(BigDecimal.valueOf(quantity)).doubleValue();
            
            revenueByCategory.put(catName, revenueByCategory.getOrDefault(catName, 0.0) + revenue);
        }

        List<ResCategoryDistributionDTO.CategoryPoint> distribution = revenueByCategory.entrySet().stream()
                .map(entry -> ResCategoryDistributionDTO.CategoryPoint.builder()
                        .name(entry.getKey())
                        .value(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(ResCategoryDistributionDTO.CategoryPoint::getName))
                .toList();

        return ResCategoryDistributionDTO.builder()
                .distribution(distribution)
                .message(distribution.isEmpty() ? "No category sales data for this period" : null)
                .build();
    }
}
