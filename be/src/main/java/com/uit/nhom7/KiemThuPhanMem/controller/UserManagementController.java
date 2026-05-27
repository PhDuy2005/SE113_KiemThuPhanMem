package com.uit.nhom7.KiemThuPhanMem.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateUserDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResUserDTO;
import com.uit.nhom7.KiemThuPhanMem.service.UserService;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/business/users")
public class UserManagementController {
    private final UserService userService;

    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{id}")
    @ApiMessage("Business admin cap nhat thong tin nguoi dung")
    public ResponseEntity<ResUserDTO> updateUser(@PathVariable UUID id, @RequestBody ReqUpdateUserDTO request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }
}
