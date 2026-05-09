# KiemThuPhanMem - Project Boilerplate

Một boilerplate Spring Boot 4.0.5 với architecture đầy đủ cho hệ thống quản lý người dùng, vai trò, quyền hạn sử dụng JWT authentication.

---

## 📋 Thông tin cơ bản

- **Java Version**: Java 17
- **Spring Boot**: 4.0.5
- **Build Tool**: Gradle (Kotlin DSL)
- **Group ID**: `com.uit.nhom7`
- **Artifact ID**: `KiemThuPhanMem`
- **Package Name**: `com.uit.nhom7.KiemThuPhanMem`
- **Server Port**: 8081 (default)
- **Database**: MySQL 8.0+

---

## 📦 Dependencies

### Core Spring Boot Starters
- `spring-boot-starter-actuator` - Health checks & monitoring endpoints
- `spring-boot-starter-data-jpa` - JPA/Hibernate ORM
- `spring-boot-starter-security` - Authentication & Authorization
- `spring-boot-starter-security-oauth2-resource-server` - OAuth2 Resource Server
- `spring-boot-starter-validation` - Hibernate Validator
- `spring-boot-starter-webmvc` - REST API support

### Additional Libraries
- `springdoc-openapi-starter-webmvc-ui` (v2.8.14) - Swagger/OpenAPI documentation UI
- `uuid-creator` (v5.3.3) - UUIDv7 generation
- `lombok` - Reduce boilerplate code
- `mysql-connector-j` - MySQL JDBC driver
- `spring-boot-devtools` - Development utilities

### Testing Dependencies
- `spring-boot-starter-*-test` - Spring Boot test starters
- `junit-platform-launcher` - JUnit 5

---

## 🗄️ Database Configuration

### Database Name: `se113`
### Default Credentials:
```
Username: root
Password: 123456
```

### Connection String:
```
jdbc:mysql://localhost:3306/se113
```

### Hibernate Settings:
```
DDL Auto: update (auto-create/update tables)
Show SQL: true (for debugging)
```

---

## 🔐 Security Configuration

### JWT Authentication Setup
- **Algorithm**: HS512 (HMAC-SHA512)
- **Access Token Expiration**: 10 days (864000 seconds)
- **Refresh Token Expiration**: 10 days (864000 seconds)
- **Default Secret Key**: `noVGO4KXfRQijWLkkHTdwMZzJcsvohOLNTzXHkWOEOwwj50/QWunAGce8b6XKqUwss6ozCb5A/e++2SPZN/d2Q==`
- **Environment Variable**: `JWT_SECRET` (optional override)

### Security Features
- ✅ JWT Bearer Token authentication
- ✅ BCrypt password encoding
- ✅ Method-level security (`@Secured`, `@PreAuthorize`)
- ✅ OAuth2 Resource Server
- ✅ CORS configuration
- ✅ Stateless session (SESSION_CREATION_POLICY.STATELESS)
- ✅ Custom entry point for authentication errors

### Authentication Flow
1. User logs in with email + password
2. System authenticates and generates JWT access token + refresh token
3. Refresh token stored in HTTP-only cookie
4. Access token returned in response body
5. Subsequent requests include Bearer token in Authorization header

---

## 📊 Database Schema

### 1. **users** Table
```
- id (UUID, Primary Key)
- name (VARCHAR)
- email (VARCHAR, NOT NULL)
- password (VARCHAR, NOT NULL - Bcrypt encoded)
- accountStatus (VARCHAR) - Active/Inactive/Locked
- failedLoginAttempts (INT, default 0)
- refreshToken (MEDIUMTEXT) - JWT refresh token
- role_id (FK to roles)
- createdAt (TIMESTAMP)
- updatedAt (TIMESTAMP)
```

### 2. **roles** Table
```
- id (BIGINT, Primary Key, Auto Increment)
- name (VARCHAR, NOT NULL)
- description (VARCHAR)
- active (BOOLEAN, default true)
- createdAt (TIMESTAMP)
- updatedAt (TIMESTAMP)

Relationships:
  - 1:N with users
  - N:M with permissions (via role_permission junction table)
```

### 3. **permissions** Table
```
- id (BIGINT, Primary Key, Auto Increment)
- name (VARCHAR, NOT NULL)
- apiPath (VARCHAR) - API endpoint path
- method (VARCHAR) - HTTP method (GET, POST, PUT, DELETE, etc.)
- module (VARCHAR) - Module/Feature name
- createdAt (TIMESTAMP)
- updatedAt (TIMESTAMP)

Relationships:
  - N:M with roles (via role_permission junction table)

Unique Constraint:
  - (apiPath, method)
```

### 4. **role_permission** Junction Table
```
- role_id (FK to roles)
- permission_id (FK to permissions)
```

---

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/uit/nhom7/KiemThuPhanMem/
│   │       ├── KiemThuPhanMemApplication.java (Main entry point)
│   │       ├── config/
│   │       │   ├── OpenAPIConfig.java (Swagger/OpenAPI configuration)
│   │       │   ├── SecurityConfiguration.java (Spring Security & JWT setup)
│   │       │   ├── UserDetailsCustom.java (Custom UserDetailsService)
│   │       │   └── CustomAuthenticationEntryPoint.java (Error handling)
│   │       ├── controller/
│   │       │   └── AuthController.java (Authentication endpoints)
│   │       ├── domain/
│   │       │   ├── table/
│   │       │   │   ├── User.java (Entity)
│   │       │   │   ├── Role.java (Entity)
│   │       │   │   └── Permission.java (Entity)
│   │       │   ├── requestDTO/
│   │       │   │   ├── ReqLoginDTO.java
│   │       │   │   ├── ReqCreateRoleDTO.java
│   │       │   │   └── ReqUpdateRoleDTO.java
│   │       │   └── responseDTO/
│   │       │       ├── ResLoginDTO.java
│   │       │       ├── ResRoleDTO.java
│   │       │       ├── ResUserDTO.java
│   │       │       ├── ResPermissionDTO.java
│   │       │       ├── RestResponse.java (Generic response wrapper)
│   │       │       └── ResultPaginationDTO.java (Pagination wrapper)
│   │       ├── repository/
│   │       │   ├── UserRepository.java (JPA Repository)
│   │       │   ├── RoleRepository.java (JPA Repository)
│   │       │   └── PermissionRepository.java (JPA Repository)
│   │       ├── service/
│   │       │   ├── UserService.java
│   │       │   └── RoleService.java
│   │       └── util/
│   │           ├── FormatRestResponse.java (Response formatting utility)
│   │           ├── SecurityUtil.java (JWT creation & management)
│   │           ├── UuidV7Generator.java (UUIDv7 generation)
│   │           ├── annotation/
│   │           │   └── ApiMessage.java (Custom annotation for API docs)
│   │           ├── enums/
│   │           └── error/
│   │               └── IdInvalidException.java (Custom exception)
│   └── resources/
│       └── application.properties (Configuration)
└── test/
    └── java/
        └── com/uit/nhom7/KiemThuPhanMem/
            └── KiemThuPhanMemApplicationTests.java
```

---

## 🔌 API Endpoints

### Authentication
- **POST** `/api/v1/auth/login`
  - Body: `{ "email": "user@example.com", "password": "password123" }`
  - Response: JWT access token + refresh token (in cookie)

### Role Management (Examples)
- **POST** `/api/v1/roles` - Create role
- **GET** `/api/v1/roles` - Get all roles (paginated)
- **GET** `/api/v1/roles/{id}` - Get role details
- **PUT** `/api/v1/roles/{id}` - Update role
- **DELETE** `/api/v1/roles/{id}` - Delete role

### User Management (Examples)
- **POST** `/api/v1/users` - Create user
- **GET** `/api/v1/users` - Get all users (paginated)
- **GET** `/api/v1/users/{id}` - Get user details
- **PUT** `/api/v1/users/{id}` - Update user
- **DELETE** `/api/v1/users/{id}` - Delete user

### Permission Management (Examples)
- **POST** `/api/v1/permissions` - Create permission
- **GET** `/api/v1/permissions` - Get all permissions
- **PUT** `/api/v1/permissions/{id}` - Update permission
- **DELETE** `/api/v1/permissions/{id}` - Delete permission

---

## ⚙️ Configuration Properties

### Application Settings
```properties
spring.application.name=KiemThuPhanMem
```

### Database
```properties
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:mysql://localhost:3306/se113
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.show-sql=true
```

### JWT
```properties
se113.jwt.base64-secret=${JWT_SECRET:noVGO4KXfRQijWLkkHTdwMZzJcsvohOLNTzXHkWOEOwwj50/QWunAGce8b6XKqUwss6ozCb5A/e++2SPZN/d2Q==}
se113.jwt.access-token-validity-in-seconds=864000
se113.jwt.refresh-token-validity-in-seconds=864000
```

### File Upload
```properties
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
```

### Development Tools
```properties
spring.devtools.restart.enabled=true
spring.devtools.livereload.enabled=true
```

### Pagination
```properties
spring.data.web.pageable.default-page-size=20
spring.data.web.pageable.max-page-size=2000
spring.data.web.pageable.one-indexed-parameters=true
```

### Actuator (Monitoring)
```properties
management.endpoints.web.exposure.include=health,info,metrics,env,beans,mappings
management.endpoint.health.show-details=when-authorized
management.health.defaults.enabled=true
```

---

## 🛠️ Swagger/OpenAPI Documentation

### Access Swagger UI
- **Development**: `http://localhost:8081/swagger-ui.html`
- **Production**: `https://hoidanit.vn/swagger-ui.html`

### API Docs
- OpenAPI JSON: `/v3/api-docs`
- OpenAPI YAML: `/v3/api-docs.yaml`

### Security Scheme
- **Type**: HTTP Bearer (JWT)
- **Format**: JWT token in `Authorization: Bearer {token}` header

---

## 🚀 Build & Run Commands

### Build Project
```bash
./gradlew clean build
```

### Run Application
```bash
./gradlew bootRun
```

### Development Mode (with hot reload)
```bash
./gradlew bootRun --args='--spring.devtools.restart.enabled=true'
```

### Run Tests
```bash
./gradlew test
```

### Create Executable JAR
```bash
./gradlew bootJar
# JAR file: build/libs/KiemThuPhanMem-0.0.1-SNAPSHOT.jar
```

---

## 🔒 Key Security Features

1. **Password Hashing**: BCrypt with default strength 10
2. **JWT Tokens**: HS512 algorithm with 10-day expiration
3. **Refresh Token**: Stored in HTTP-only cookies for security
4. **Failed Login Attempts**: Tracked for account lockout logic
5. **Method-level Security**: `@Secured` and `@PreAuthorize` annotations
6. **CORS**: Configured with proper cross-origin settings
7. **Input Validation**: Hibernate Validator for DTO validation

---

## 📝 Key Utilities

### SecurityUtil
- `createAccessToken()` - Generate JWT access token
- `createRefreshToken()` - Generate JWT refresh token
- `getCurrentUserLogin()` - Get current authenticated user

### FormatRestResponse
- Standardized REST response formatting
- Pagination support
- Error response handling

### UuidV7Generator
- Generate UUIDv7 for User IDs
- Sortable UUID implementation

### ApiMessage Annotation
- Custom annotation for API documentation
- Used to describe endpoint purposes

---

## 🎯 Key Technologies & Patterns

- **Architecture**: RESTful API with JWT authentication
- **ORM**: JPA/Hibernate
- **Security**: Spring Security 6.x with OAuth2
- **Documentation**: Springdoc-OpenAPI (Swagger UI)
- **Database**: MySQL with Hibernate auto-DDL
- **Design Patterns**: Repository, Service, DTO, Custom Exceptions
- **Development**: Spring Boot DevTools with hot reload

---

## 📌 Important Notes

1. **UUIDv7 for Users**: User ID uses UUIDv7 (BINARY(16)) instead of Long
2. **Audit Fields**: All entities include `createdAt` and `updatedAt` timestamps
3. **Role-Based Access Control**: Permissions are assigned to roles, roles to users
4. **Stateless Auth**: No server-side sessions; all info in JWT token
5. **Development Database**: Uses embedded default credentials (should be changed in production)

---

## 🔧 Customization Guide

### To customize this boilerplate:

1. **Change Project Name**: Update `rootProject.name` in `build.gradle.kts` and package names
2. **Modify Database**: Update connection string in `application.properties`
3. **Add New Entities**: Create new entity classes in `domain/table/`
4. **Add New Controllers**: Create controller classes in `controller/`
5. **Extend Services**: Add business logic in `service/` layer
6. **Create Custom Exceptions**: Add in `util/error/`
7. **Update API Documentation**: Modify `OpenAPIConfig.java`

---

## 📞 Support & Contributions

This boilerplate is configured for SE113 project by Nhom 7 at UIT.
For questions or improvements, contact the development team.

---

**Created**: 2026-04-23  
**Version**: 1.0  
**Status**: Ready for production
