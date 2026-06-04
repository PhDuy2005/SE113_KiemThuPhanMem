package com.uit.nhom7.KiemThuPhanMem.service.user_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResAuthActionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00003_verifyRegistration extends UserServiceTestBase {

    @Test
    @DisplayName("FUNC00003_UTCID01 - Fails because token is invalid or not found")
    void TC1_InvalidToken() {
        Fixture fixture = new Fixture();
        String token = "invalid_token";

        when(fixture.userRepository.findByVerificationToken(token)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.userService.verifyRegistration(token))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid verification link")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00003_UTCID02 - Verifies account successfully and sets status to ACTIVE")
    void TC2_Success() {
        Fixture fixture = new Fixture();
        String token = "valid_token";

        User mockUser = User.builder()
                .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"))
                .accountStatus("PENDING")
                .verificationToken(token)
                .build();

        when(fixture.userRepository.findByVerificationToken(token)).thenReturn(Optional.of(mockUser));

        ResAuthActionDTO result = fixture.userService.verifyRegistration(token);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Registration verified successfully");
        assertThat(result.getToken()).isNull();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(fixture.userRepository).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        assertThat(savedUser.getAccountStatus()).isEqualTo("ACTIVE");
        assertThat(savedUser.getVerificationToken()).isNull();
    }
}
