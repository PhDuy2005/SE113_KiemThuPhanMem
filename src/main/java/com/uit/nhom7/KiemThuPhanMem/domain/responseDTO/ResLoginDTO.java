package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class ResLoginDTO {
    @JsonProperty("access_token")
    private String accessToken;
    private UserLogin user;
    private Role role;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLogin {
        private UUID id;
        private String email;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserGetAccount {
        private UserLogin user;
        private Role role;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInsideToken {
        private UUID id;
        private String email, name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Role {
        private Long roleId;
        private String roleName;
    }
}