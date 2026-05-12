package com.uit.nhom7.KiemThuPhanMem.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqAddCartItemDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateCartItemDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResCartItemActionDTO;
import com.uit.nhom7.KiemThuPhanMem.service.CartService;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    @ApiMessage("Them san pham vao gio hang")
    public ResponseEntity<ResCartItemActionDTO> addItem(@Valid @RequestBody ReqAddCartItemDTO request) {
        return ResponseEntity.ok(cartService.addItem(request));
    }

    @PutMapping("/items/{productId}")
    @ApiMessage("Thay doi so luong trong gio hang")
    public ResponseEntity<ResCartItemActionDTO> updateItemQuantity(
            @PathVariable UUID productId,
            @Valid @RequestBody ReqUpdateCartItemDTO request) {
        return ResponseEntity.ok(cartService.updateItemQuantity(productId, request));
    }

    @DeleteMapping("/items/{productId}")
    @ApiMessage("Xoa san pham khoi gio hang")
    public ResponseEntity<ResCartItemActionDTO> removeItem(@PathVariable UUID productId) {
        return ResponseEntity.ok(cartService.removeItem(productId));
    }
}
