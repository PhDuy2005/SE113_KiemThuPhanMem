# KiemThuPhanMem - Project Configuration Documentation

## 📋 Project Overview

**Project Name:** KiemThuPhanMem  
**Description:** Quality Testing Module - Spring Boot application for role-based access control and user authentication  
**Language:** Java  
**Build Tool:** Gradle (Kotlin DSL)  
**Framework:** Spring Boot 4.0.5  
**Java Version:** Java 17  
**Database:** MySQL 8.0+  

---

## 🛠 Technology Stack & Dependencies

### Core Dependencies
- **Spring Boot Starters:**
  - `spring-boot-starter-actuator` - Application monitoring & management
  - `spring-boot-starter-data-jpa` - Java Persistence API with Hibernate
  - `spring-boot-starter-security` - Authentication & Authorization
  - `spring-boot-starter-security-oauth2-resource-server` - OAuth2 Resource Server
  - `spring-boot-starter-validation` - Bean Validation
  - `spring-boot-starter-webmvc` - Spring MVC Web Support

### Additional Libraries
- **UUID Generation:** `com.github.f4b6a3:uuid-creator:5.3.3`
- **Lombok:** For reducing boilerplate code (@Data, @Builder, @NoArgsConstructor, @AllArgsConstructor)
- **MySQL Connector:** `com.mysql:mysql-connector-j`

### Development Tools
- **Spring Boot DevTools:** Hot reload & live reload support

---

## 📂 Project Structure

```
KiemThuPhanMem/
├── src/main/java/com/uit/nhom7/KiemThuPhanMem/
│   ├── KiemThuPhanMemApplication.java           [Main Spring Boot App]
│   ├── config/                                   [Configuration Classes]
│   │   ├── CustomAuthenticationEntryPoint.java  [Custom OAuth2 Entry Point]
│   │   ├── SecurityConfiguration.java           [Spring Security Config]
│   │   └── UserDetailsCustom.java               [Custom User Details Service]
│   ├── controller/                               [REST Controllers]
│   │   └── AuthController.java                  [Authentication Endpoints]
│   ├── domain/                                   [Domain Models & DTOs]
│   │   ├── table/                               [JPA Entities]
│   │   │   ├── User.java                        [User Entity with UUID PK]
│   │   │   ├── Role.java                        [Role Entity]
│   │   │   └── Permission.java                  [Permission Entity]
│   │   ├── requestDTO/                          [Request DTOs]
│   │   │   ├── ReqLoginDTO.java                 [Login Request]
│   │   │   ├── ReqCreateRoleDTO.java            [Create Role Request]
│   │   │   └── ReqUpdateRoleDTO.java            [Update Role Request]
│   │   └── responseDTO/                         [Response DTOs]
│   │       ├── ResLoginDTO.java                 [Login Response]
│   │       ├── ResUserDTO.java                  [User Response]
│   │       ├── ResRoleDTO.java                  [Role Response]
│   │       ├── ResPermissionDTO.java            [Permission Response]
│   │       ├── RestResponse.java                [Generic REST Response]
│   │       └── ResultPaginationDTO.java         [Pagination Response]
│   ├── repository/                               [Data Access Layer]
│   │   ├── UserRepository.java                  [User Repository]
│   │   ├── RoleRepository.java                  [Role Repository]
│   │   └── PermissionRepository.java            [Permission Repository]
│   ├── service/                                  [Business Logic Layer]
│   │   ├── UserService.java                     [User Service]
│   │   └── RoleService.java                     [Role Service]
│   └── util/                                     [Utilities]
│       ├── SecurityUtil.java                    [JWT Token Generation & Validation]
│       ├── FormatRestResponse.java              [HTTP Response Formatter]
│       ├── UuidV7Generator.java                 [UUID v7 Generator]
│       ├── annotation/
│       │   └── ApiMessage.java                  [Custom @ApiMessage Annotation]
│       ├── enums/                               [Enum Classes]
│       └── error/
│           └── IdInvalidException.java          [Custom Exception]
├── src/main/resources/
│   ├── application.properties                    [Application Configuration]
│   ├── static/                                   [Static Resources]
│   └── templates/                                [Thymeleaf Templates]
├── build.gradle.kts                             [Gradle Build Configuration]
├── settings.gradle.kts                          [Gradle Settings]
└── gradle/wrapper/                              [Gradle Wrapper]
```

---

## 📊 Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    account_status VARCHAR(50),
    failed_login_attempts INT DEFAULT 0,
    refresh_token MEDIUMTEXT,
    role_id BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);
```

### Roles Table
```sql
CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);
```

### Permissions Table
```sql
CREATE TABLE permissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    api_path VARCHAR(255),
    method VARCHAR(10),
    module VARCHAR(100),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);
```

### Role_Permission Mapping (Many-to-Many)
```sql
CREATE TABLE role_permission (
    role_id BIGINT,
    permission_id BIGINT,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (permission_id) REFERENCES permissions(id)
);
```

---

## 🔐 Key Entities

### User Entity (JPA)
```
- id: UUID (Primary Key, UUIDv7)
- name: String
- email: String (Required, Unique)
- password: String (Required)
- accountStatus: String
- failedLoginAttempts: Integer
- refreshToken: String (MEDIUMTEXT for storage)
- role: Role (Many-to-One)
- Audit Fields: createdAt, updatedAt, createdBy, updatedBy
```

### Role Entity (JPA)
```
- id: Long (Primary Key, Auto-increment)
- name: String (Required, Unique)
- description: String
- active: Boolean (Default: true)
- permissions: Set<Permission> (Many-to-Many)
- Audit Fields: createdAt, updatedAt, createdBy, updatedBy
```

### Permission Entity (JPA)
```
- id: Long (Primary Key, Auto-increment)
- name: String (Required)
- apiPath: String
- method: String (HTTP Method: GET, POST, PUT, DELETE, etc.)
- module: String
- Audit Fields: createdAt, updatedAt, createdBy, updatedBy
```

---

## 📝 Key DTOs

### Request DTOs

#### ReqLoginDTO
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

#### ReqCreateRoleDTO
```json
{
  "name": "ADMIN",
  "description": "Administrator role"
}
```

#### ReqUpdateRoleDTO
```json
{
  "id": 1,
  "name": "ADMIN",
  "description": "Administrator role",
  "active": true
}
```

### Response DTOs

#### ResLoginDTO
```json
{
  "access_token": "eyJhbGc...",
  "user": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "email": "user@example.com",
    "name": "John Doe"
  },
  "role": {
    "roleId": 1,
    "roleName": "ADMIN"
  }
}
```

#### ResUserDTO
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "email": "user@example.com",
  "name": "John Doe",
  "accountStatus": "ACTIVE",
  "role": {...},
  "created_at": "2026-04-12T01:24:16Z",
  "updated_at": "2026-04-12T01:24:16Z",
  "created_by": "admin",
  "updated_by": "admin"
}
```

#### ResultPaginationDTO
```json
{
  "meta": {
    "page": 1,
    "pageSize": 20,
    "totalPages": 5,
    "totalItems": 100
  },
  "result": [...]
}
```

---

## 🔌 API Endpoints

### Authentication Controller (`/api/v1/auth`)

#### Login
- **Endpoint:** `POST /api/v1/auth/login`
- **Request:** `ReqLoginDTO` (email, password)
- **Response:** `ResLoginDTO` (access_token, user, role)
- **Cookie:** Sets `refresh_token` (httpOnly, secure)

#### Get Account
- **Endpoint:** `GET /api/v1/auth/account`
- **Auth:** Required (Bearer token)
- **Response:** `ResLoginDTO.UserGetAccount`

#### Refresh Token
- **Endpoint:** `GET /api/v1/auth/refresh`
- **Auth:** Cookie-based (refresh_token)
- **Response:** `ResLoginDTO` (new access_token)

#### Logout
- **Endpoint:** `POST /api/v1/auth/logout`
- **Auth:** Required (Bearer token)
- **Response:** HTTP 200 (clears refresh_token cookie)

---

## 🔑 JWT Configuration

### Properties (application.properties)
```properties
se113.jwt.base64-secret=${JWT_SECRET:...}
se113.jwt.access-token-validity-in-seconds=864000    # 10 days
se113.jwt.refresh-token-validity-in-seconds=864000   # 10 days
```

### Token Structure
- **Payload:** Contains user email, name, ID, and role information
- **Signature:** HS512 algorithm with base64-encoded secret
- **Expires:** Configurable via properties

---

## 📦 Services

### UserService
**Methods:**
- `handleFindByEmail(email: String): User`
- `handleFindByEmailAndRefreshToken(email: String, refreshToken: String): User`
- `handleFetchUserById(id: UUID): ResUserDTO`
- `handleGetAllUsers(spec: Specification, pageable: Pageable): ResultPaginationDTO`
- `updateUserRefreshToken(refreshToken: String, email: String): void`
- `handleLogOutUser(email: String): void`
- `deleteUser(id: UUID): void`

### RoleService
**Methods:**
- `createRole(request: ReqCreateRoleDTO): ResRoleDTO`
- `updateRole(request: ReqUpdateRoleDTO): ResRoleDTO`
- `handleGetAllRoles(spec: Specification, pageable: Pageable): ResultPaginationDTO`
- `handleFetchRoleById(id: Long): ResRoleDTO`
- `deleteRole(id: Long): void`

---

## 🛡️ Security Configuration

### UserDetailsCustom
- Implements `UserDetailsService`
- Loads user by email address
- Returns Spring Security `UserDetails` with authorities

### SecurityConfiguration
- Configures Spring Security beans
- Sets up JWT token validation
- Configures authentication manager
- Enables CORS and CSRF settings
- Defines public/protected endpoints

### CustomAuthenticationEntryPoint
- Handles unauthorized access (401)
- Returns custom JSON error response

---

## ⚙️ Application Configuration

### application.properties Key Settings

**Database:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/se113
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update    # Auto-create/update tables
spring.jpa.show-sql=true
```

**File Upload:**
```properties
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
```

**Pagination:**
```properties
spring.data.web.pageable.default-page-size=20
spring.data.web.pageable.max-page-size=2000
spring.data.web.pageable.one-indexed-parameters=true
```

**DevTools:**
```properties
spring.devtools.restart.enabled=true
spring.devtools.livereload.enabled=true
```

**Actuator:**
```properties
management.endpoints.web.exposure.include=health,info,metrics,env,beans,mappings
management.endpoint.health.show-details=when-authorized
```

---

## 🏗️ Key Implementation Details

### UUID v7 Generation
- User IDs use UUIDv7 (time-sorted UUIDs)
- Implemented via `com.github.f4b6a3:uuid-creator` library
- Auto-generated in `@PrePersist` method
- Stored as BINARY(16) in MySQL

### Audit Fields
- All entities have: `createdAt`, `updatedAt`, `createdBy`, `updatedBy`
- `@PrePersist` sets creation timestamp & current user
- `@PreUpdate` sets update timestamp & current user
- Current user obtained from `SecurityUtil.getCurrentUserLogin()`

### Password Encoding
- Passwords encoded using Spring Security's `PasswordEncoder`
- Default: BCrypt with strength 10

### Transaction Management
- Service methods use `@Transactional` annotation
- Read-only operations: `@Transactional(readOnly = true)`

---

## 🔧 Utilities

### SecurityUtil
- `getCurrentUserLogin(): Optional<String>` - Get current authenticated user email
- `createAccessToken(email: String, resLoginDTO: ResLoginDTO): String`
- `createRefreshToken(email: String, resLoginDTO: ResLoginDTO): String`
- `checkValidRefreshToken(token: String): Jwt`

### FormatRestResponse
- Custom HTTP response formatter
- Serializes responses to REST API standard format

### UuidV7Generator
- Generates time-sorted UUIDs (UUIDv7)
- Used for `User.id` primary key

---

## 📋 Custom Annotations

### @ApiMessage
- Placed on controller methods
- Provides API endpoint description
- Example: `@ApiMessage("Đăng nhập")`

---

## ❌ Custom Exceptions

### IdInvalidException
- Thrown when entity ID is not found
- Example: "User with id {id} does not exist"

---

## 🧪 Testing Structure

```
src/test/java/com/uit/nhom7/KiemThuPhanMem/
└── KiemThuPhanMemApplicationTests.java (Spring Boot test configuration)
```

---

## 📊 Development Workflow

### Build & Run
```bash
# Build project
./gradlew build

# Run application
./gradlew bootRun

# Dev with hot reload
./gradlew bootRun --detect-property-changes
```

### Database Setup
1. Ensure MySQL is running on `localhost:3306`
2. Create database: `CREATE DATABASE se113;`
3. Hibernate will auto-create/update tables on startup

### Authentication Flow
1. User sends credentials to `/api/v1/auth/login`
2. System validates and generates JWT tokens
3. Access token returned in response
4. Refresh token stored in HTTP-only cookie
5. Client uses access token for subsequent requests
6. When access token expires, use refresh token to get new one

---

## 🚀 Future Enhancements

### Recommended Configurations
- Add API versioning strategy
- Implement rate limiting
- Add request/response logging
- Setup Swagger/OpenAPI documentation
- Add more comprehensive error handling
- Implement audit logging for sensitive operations
- Add email verification for sign-up
- Implement password reset functionality
- Add 2FA support
- Setup monitoring & alerting (Prometheus, Grafana)

---

## 📝 Notes for AI Agent Setup

When recreating this project, ensure:
1. ✅ Java 17+ is installed
2. ✅ Gradle wrapper is configured (Kotlin DSL)
3. ✅ MySQL database is accessible with credentials in properties
4. ✅ All dependencies in `build.gradle.kts` are resolved
5. ✅ Entity relationships (User→Role→Permission) are properly mapped
6. ✅ JWT secret is configured in environment or properties
7. ✅ Spring Security is configured with custom authentication entry point
8. ✅ Repositories implement custom query methods for email/refresh token
9. ✅ Services implement transactional business logic
10. ✅ Controllers follow REST conventions with proper annotation

---

## 📅 Document Version

- **Created:** 2026-04-12
- **Last Updated:** 2026-04-12
- **Project Version:** 0.0.1-SNAPSHOT
- **Spring Boot Version:** 4.0.5
- **Java Version:** 17
