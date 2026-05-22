# KiemThuPhanMem - AI Agent Setup Guide

## 🎯 Quick Setup Instructions for AI Agents

Use this guide to replicate the KiemThuPhanMem project setup when requested by user.

---

## 1️⃣ Project Initialization

### Step 1: Create Project Structure
```bash
gradle-project
├── build.gradle.kts
├── settings.gradle.kts
├── src/
│   ├── main/
│   │   ├── java/com/uit/nhom7/KiemThuPhanMem/
│   │   └── resources/
│   └── test/
│       └── java/com/uit/nhom7/KiemThuPhanMem/
```

### Step 2: Configure build.gradle.kts
```kotlin
plugins {
    java
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.uit.nhom7"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    
    // Libraries
    implementation("com.github.f4b6a3:uuid-creator:5.3.3")
    
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    
    // Database
    runtimeOnly("com.mysql:mysql-connector-j")
    
    // Development
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

---

## 2️⃣ Create core Application Class

**File:** `src/main/java/com/uit/nhom7/KiemThuPhanMem/KiemThuPhanMemApplication.java`

```java
package com.uit.nhom7.KiemThuPhanMem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KiemThuPhanMemApplication {
    public static void main(String[] args) {
        SpringApplication.run(KiemThuPhanMemApplication.class, args);
    }
}
```

---

## 3️⃣ Create application.properties

**File:** `src/main/resources/application.properties`

```properties
spring.application.name=KiemThuPhanMem

# Database Configuration
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://localhost:3306/se113
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.show-sql=true

# JWT Configuration
se113.jwt.base64-secret=${JWT_SECRET:noVGO4KXfRQijWLkkHTdwMZzJcsvohOLNTzXHkWOEOwwj50/QWunAGce8b6XKqUwss6ozCb5A/e++2SPZN/d2Q==}
se113.jwt.access-token-validity-in-seconds=864000
se113.jwt.refresh-token-validity-in-seconds=864000

# File Upload Configuration
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB

# DevTools
spring.devtools.restart.enabled=true
spring.devtools.livereload.enabled=true

# Pagination
spring.data.web.pageable.default-page-size=20
spring.data.web.pageable.max-page-size=2000
spring.data.web.pageable.one-indexed-parameters=true

# Actuator
management.endpoints.web.exposure.include=health,info,metrics,env,beans,mappings
management.endpoint.health.show-details=when-authorized
management.health.defaults.enabled=true
```

---

## 4️⃣ Create Entity Classes

### User Entity
**File:** `src/main/java/com/uit/nhom7/KiemThuPhanMem/domain/table/User.java`

```java
package com.uit.nhom7.KiemThuPhanMem.domain.table;

import java.time.Instant;
import java.util.UUID;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.UuidV7Generator;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    private String name;

    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    private String accountStatus;

    @Min(value = 0)
    private Integer failedLoginAttempts;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = generateUUIDv7();
        }
        createdAt = Instant.now();
        createdBy = SecurityUtil.getCurrentUserLogin().orElse("system");
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
        updatedBy = SecurityUtil.getCurrentUserLogin().orElse("system");
    }

    private UUID generateUUIDv7() {
        return UuidV7Generator.generate();
    }
}
```

### Role Entity
**File:** `src/main/java/com/uit/nhom7/KiemThuPhanMem/domain/table/Role.java`

```java
package com.uit.nhom7.KiemThuPhanMem.domain.table;

import java.time.Instant;
import java.util.Set;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String description;

    private boolean active;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permission",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        createdBy = "system";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
        updatedBy = "system";
    }
}
```

### Permission Entity
**File:** `src/main/java/com/uit/nhom7/KiemThuPhanMem/domain/table/Permission.java`

```java
package com.uit.nhom7.KiemThuPhanMem.domain.table;

import java.time.Instant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String apiPath;
    private String method;
    private String module;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        createdBy = "system";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
        updatedBy = "system";
    }
}
```

---

## 5️⃣ Create DTOs

### Request DTOs
- `ReqLoginDTO` - Login credentials
- `ReqCreateRoleDTO` - Create role request
- `ReqUpdateRoleDTO` - Update role request

### Response DTOs
- `ResLoginDTO` - Login response with tokens
- `ResUserDTO` - User information
- `ResRoleDTO` - Role information
- `ResPermissionDTO` - Permission information
- `ResultPaginationDTO` - Paginated results

---

## 6️⃣ Create Repositories

```java
// UserRepository.java
@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByEmailAndRefreshToken(String email, String refreshToken);
}

// RoleRepository.java
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    boolean existsByName(String name);
    Optional<Role> findByName(String name);
}

// PermissionRepository.java
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    // Add custom queries as needed
}
```

---

## 7️⃣ Create Services

- **UserService** - User management, login, token refresh
- **RoleService** - Role management (CRUD, permissions)

Key features:
- ✅ Transactional operations
- ✅ Entity-to-DTO conversion
- ✅ Pagination support
- ✅ Audit field handling

---

## 8️⃣ Create Security Configuration

### UserDetailsCustom
- Implement `UserDetailsService`
- Load user by email

### SecurityConfiguration
- Configure Spring Security beans
- Setup JWT validation
- Define public/protected endpoints

### CustomAuthenticationEntryPoint
- Handle 401 unauthorized responses
- Return custom JSON error

---

## 9️⃣ Create Utilities

### SecurityUtil
```java
public class SecurityUtil {
    public static Optional<String> getCurrentUserLogin() {
        // Implementation using Spring Security
    }
    
    public static String createAccessToken(String email, ResLoginDTO resLoginDTO) {
        // JWT creation logic
    }
    
    public static String createRefreshToken(String email, ResLoginDTO resLoginDTO) {
        // JWT creation logic
    }
    
    public static Jwt checkValidRefreshToken(String token) {
        // Token validation
    }
}
```

### UuidV7Generator
```java
public class UuidV7Generator {
    public static UUID generate() {
        return UuidCreator.getTimeOrderedEpoch();
    }
}
```

---

## 🔟 Create AuthController

**Endpoints:**
- `POST /api/v1/auth/login` - User login
- `GET /api/v1/auth/account` - Get current account
- `GET /api/v1/auth/refresh` - Refresh access token
- `POST /api/v1/auth/logout` - User logout

---

## 1️⃣1️⃣ Setup Database

```bash
# 1. Start MySQL
# 2. Create database
mysql -u root -p
CREATE DATABASE se113;

# 3. Application will auto-create tables on startup via Hibernate DDL
```

---

## 1️⃣2️⃣ Build & Run

```bash
# Build project
./gradlew clean build

# Run application
./gradlew bootRun

# Application will start on http://localhost:8080
```

---

## ✅ Verification Checklist

- [ ] Java 17+ installed
- [ ] Gradle wrapper configured
- [ ] MySQL running on localhost:3306
- [ ] Database `se113` created
- [ ] `application.properties` configured
- [ ] All entities have `@Entity` and `@Table` annotations
- [ ] Repositories extend `JpaRepository`
- [ ] Services have `@Service` and `@Transactional`
- [ ] Controllers have `@RestController` and route mappings
- [ ] Security configuration bean is created
- [ ] JWT secret is configured
- [ ] Application starts without errors
- [ ] API endpoints respond to requests

---

## 📚 Important Notes

1. **Prefix Consistency:** Use `se113` for all JWT properties (not `se100`)
2. **UUID Storage:** User IDs stored as `BINARY(16)` for efficiency
3. **Refresh Token:** Stored in HTTP-only cookie (secure by default)
4. **Audit Fields:** All entities track creation/update user and timestamp
5. **Pagination:** One-indexed (starts at 1, not 0)
6. **Transaction:** Use `@Transactional(readOnly = true)` for queries
7. **Password Encoding:** Use Spring Security's `PasswordEncoder`

---

## 🎓 Learning Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security JWT](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Lombok Documentation](https://projectlombok.org)
- [UUID Creator Library](https://github.com/f4b6a3/uuid-creator)

---

## 📞 Support

For additional configuration questions, refer to `PROJECT_CONFIGURATION.md`
