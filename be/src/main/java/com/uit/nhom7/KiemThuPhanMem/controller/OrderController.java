package com.uit.nhom7.KiemThuPhanMem.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqChangeOrderAddressDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqStaffCancelOrderDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateShippingStatusDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOrderDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOrderDetailDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOrderStatusTimelineDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResultPaginationDTO;
import com.uit.nhom7.KiemThuPhanMem.service.OrderService;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @ApiMessage("Xem lich su don hang")
    public ResponseEntity<ResultPaginationDTO> getOrderHistory(
            @RequestParam(value = "userId", required = false) UUID userId,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(orderService.getOrderHistory(userId, pageNumber, pageSize));
    }

    @GetMapping("/staff/pending")
    @ApiMessage("Staff xem danh sach don hang pending")
    public ResponseEntity<ResultPaginationDTO> getPendingOrdersForStaff(
            @RequestParam(value = "userId", required = false) UUID userId,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(orderService.getPendingOrdersForStaff(userId, pageNumber, pageSize));
    }

    @GetMapping("/staff/search")
    @ApiMessage("Staff tim kiem don hang")
    public ResponseEntity<ResultPaginationDTO> searchOrdersForStaff(
            @RequestParam("searchKeyword") String searchKeyword,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(orderService.searchOrdersForStaff(searchKeyword, pageNumber, pageSize));
    }

    @GetMapping("/staff/{orderId}")
    @ApiMessage("Staff xem chi tiet don hang")
    public ResponseEntity<ResOrderDetailDTO> getOrderDetailForStaff(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderDetailForStaff(orderId));
    }

    @PatchMapping("/staff/{orderId}/approve")
    @ApiMessage("Staff duyet don hang")
    public ResponseEntity<ResOrderDTO> approveOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.approveOrder(orderId));
    }

    @PatchMapping("/staff/{orderId}/shipping")
    @ApiMessage("Staff cap nhat trang thai giao hang")
    public ResponseEntity<ResOrderDetailDTO> updateShippingStatus(
            @PathVariable UUID orderId,
            @RequestBody ReqUpdateShippingStatusDTO request) {
        return ResponseEntity.ok(orderService.updateShippingStatus(orderId, request));
    }

    @PatchMapping("/staff/{orderId}/delivered")
    @ApiMessage("Staff xac nhan don hang delivered")
    public ResponseEntity<ResOrderDetailDTO> markOrderDelivered(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.markOrderDelivered(orderId));
    }

    @PatchMapping("/staff/{orderId}/cancel")
    @ApiMessage("Staff huy don hang")
    public ResponseEntity<ResOrderDTO> cancelOrderForStaff(
            @PathVariable UUID orderId,
            @RequestBody ReqStaffCancelOrderDTO request) {
        return ResponseEntity.ok(orderService.cancelOrderForStaff(orderId, request));
    }

    @PatchMapping("/staff/{orderId}/refund")
    @ApiMessage("Staff khoi tao hoan tien")
    public ResponseEntity<ResOrderDTO> initiateRefund(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.initiateRefund(orderId));
    }

    @GetMapping("/{orderId}")
    @ApiMessage("Xem chi tiet don hang")
    public ResponseEntity<ResOrderDetailDTO> getOrderDetail(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderDetail(orderId));
    }

    @GetMapping("/{orderId}/status")
    @ApiMessage("Theo doi trang thai don hang")
    public ResponseEntity<ResOrderStatusTimelineDTO> getOrderStatus(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderStatusTimeline(orderId));
    }

    @PostMapping("/{orderId}/cancel")
    @ApiMessage("Huy don hang pending")
    public ResponseEntity<ResOrderDTO> cancelPendingOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.cancelPendingOrder(orderId));
    }

    @GetMapping("/{orderId}/addresses")
    @ApiMessage("Lay danh sach dia chi cho don hang")
    public ResponseEntity<List<?>> getAvailableAddressesForOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getAvailableAddressesForOrder(orderId));
    }

    @PatchMapping("/{orderId}/address")
    @ApiMessage("Thay doi dia chi giao hang")
    public ResponseEntity<ResOrderDetailDTO> changeShippingAddress(
            @PathVariable UUID orderId,
            @Valid @RequestBody ReqChangeOrderAddressDTO request) {
        return ResponseEntity.ok(orderService.changeShippingAddress(orderId, request));
    }
}
