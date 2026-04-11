package com.uit.nhom7.KiemThuPhanMem.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqCreateRoleDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateRoleDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResPermissionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResRoleDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResultPaginationDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.repository.PermissionRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.RoleRepository;
import com.uit.nhom7.KiemThuPhanMem.util.error.IdInvalidException;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    /**
     * Convert Role entity to ResRoleDTO
     */
    private ResRoleDTO convertToDTO(Role role) {
        if (role == null) {
            return null;
        }

        List<ResPermissionDTO> permissionDTOs = new ArrayList<>();
        if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
            permissionDTOs = role.getPermissions().stream()
                    .map(permission -> ResPermissionDTO.builder()
                            .id(permission.getId())
                            .name(permission.getName())
                            .apiPath(permission.getApiPath())
                            .method(permission.getMethod())
                            .module(permission.getModule())
                            .createdAt(permission.getCreatedAt())
                            .updatedAt(permission.getUpdatedAt())
                            .createdBy(permission.getCreatedBy())
                            .updatedBy(permission.getUpdatedBy())
                            .build())
                    .collect(Collectors.toList());
        }

        return ResRoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .active(role.isActive())
                .permissions(permissionDTOs)
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .createdBy(role.getCreatedBy())
                .updatedBy(role.getUpdatedBy())
                .build();
    }

    /**
     * Create a new role
     */
    @Transactional
    public ResRoleDTO createRole(ReqCreateRoleDTO request) {
        // Check if role with same name already exists
        if (roleRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Role with name '" + request.getName() + "' already exists");
        }

        // Create new role entity
        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .active(true) // Default: active = true
                .permissions(new HashSet<>())
                .build();

        // Save and convert to DTO
        Role savedRole = roleRepository.save(role);
        return convertToDTO(savedRole);
    }

    /**
     * Update an existing role
     */
    @Transactional
    public ResRoleDTO updateRole(ReqUpdateRoleDTO request) {
        // Validate role exists
        Role role = roleRepository.findById(request.getId())
                .orElseThrow(() -> new IdInvalidException("Role with id " + request.getId() + " does not exist"));

        // Check if new name already exists (and is different from current name)
        if (request.getName() != null && !request.getName().equals(role.getName())
                && roleRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Role with name '" + request.getName() + "' already exists");
        }

        // Update fields if provided
        if (request.getName() != null) {
            role.setName(request.getName());
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        if (request.getActive() != null) {
            role.setActive(request.getActive());
        }

        // Save and convert to DTO
        Role updatedRole = roleRepository.save(role);
        return convertToDTO(updatedRole);
    }

    /**
     * Get all roles with filter and pagination
     */
    @Transactional(readOnly = true)
    public ResultPaginationDTO handleGetAllRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRoles = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(pageRoles.getTotalPages());
        meta.setTotalItems(pageRoles.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(pageRoles.getContent());
        return rs;
    }

    /**
     * Get role by ID
     */
    @Transactional(readOnly = true)
    public ResRoleDTO handleFetchRoleById(Long id) {
        Role role = roleRepository.findById(id).orElse(null);
        return convertToDTO(role);
    }

    /**
     * Get role entity by ID (internal use)
     */
    public Role getRoleById(Long id) {
        return this.roleRepository.findById(id).orElse(null);
    }

    /**
     * Delete role by ID
     */
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Role with id " + id + " does not exist"));
        this.roleRepository.deleteById(id);
    }
}