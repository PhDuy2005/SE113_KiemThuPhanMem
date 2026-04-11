package com.uit.nhom7.KiemThuPhanMem.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResLoginDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResUserDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResultPaginationDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.error.IdInvalidException;

@Service
@Validated
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Convert User entity to ResUserDTO
     */
    private ResUserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }

        return ResUserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .accountStatus(user.getAccountStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdBy(user.getCreatedBy())
                .updatedBy(user.getUpdatedBy())
                .build();
    }

    /**
     * Convert User entity to ResLoginDTO
     */
    private ResLoginDTO convertToLoginDTO(User user) {
        if (user == null) {
            return null;
        }

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                user.getId(),
                user.getEmail(),
                user.getName());

        ResLoginDTO.Role roleDTO = null;
        if (user.getRole() != null) {
            roleDTO = new ResLoginDTO.Role(user.getRole().getId(), user.getRole().getName());
        }

        return ResLoginDTO.builder()
                .user(userLogin)
                .role(roleDTO)
                .build();
    }

    /**
     * Find user by email
     */
    @Transactional(readOnly = true)
    public User handleFindByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Find user by email and refresh token
     */
    @Transactional(readOnly = true)
    public User handleFindByEmailAndRefreshToken(String email, String refreshToken) {
        return userRepository.findByEmailAndRefreshToken(email, refreshToken).orElse(null);
    }

    /**
     * Get user by ID and convert to DTO
     */
    @Transactional(readOnly = true)
    public ResUserDTO handleFetchUserById(UUID id) {
        User user = userRepository.findById(id).orElse(null);
        return convertToDTO(user);
    }

    /**
     * Get user entity by ID (internal)
     */
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Get all users with filter and pagination
     */
    @Transactional(readOnly = true)
    public ResultPaginationDTO handleGetAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUsers = userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(pageUsers.getTotalPages());
        meta.setTotalItems(pageUsers.getTotalElements());

        rs.setMeta(meta);

        List<ResUserDTO> userDTOs = pageUsers.getContent().stream()
                .map(this::convertToDTO)
                .toList();
        rs.setResult(userDTOs);

        return rs;
    }

    /**
     * Update user refresh token
     */
    @Transactional
    public void updateUserRefreshToken(String refreshToken, String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
        }
    }

    /**
     * Handle user logout
     */
    @Transactional
    public void handleLogOutUser(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            user.setRefreshToken(null);
            userRepository.save(user);
        }
    }

    /**
     * Delete user by ID
     */
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("User with id " + id + " does not exist"));
        userRepository.deleteById(id);
    }
}