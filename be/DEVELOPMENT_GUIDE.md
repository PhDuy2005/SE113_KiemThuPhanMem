# KiemThuPhanMem - Development Guide & Troubleshooting

## 📖 Development Workflow

### Adding a New Feature

#### 1. Entity Enhancement
- Add new fields to relevant entity
- Add appropriate validation annotations
- Update `@PrePersist`/`@PreUpdate` if needed

#### 2. Database Migration
- Hibernate will auto-migrate if `ddl-auto=update`
- Manual migration for production recommended

#### 3. DTO Creation
- Create `ReqXxxDTO` for request
- Create `ResXxxDTO` for response
- Add `@JsonProperty` for field mapping if needed

#### 4. Repository Query
- Add custom query methods to repository interface
- Use `@Query` annotation if needed
- Test query logic

#### 5. Service Implementation
- Add service method with `@Transactional`
- Implement entity validation
- Convert entity to DTO
- Handle exceptions

#### 6. Controller Endpoint
- Add new mapping method
- Add `@ApiMessage` annotation
- Validate request body with `@Valid`
- Return appropriate HTTP status

#### 7. Security Configuration
- Update `SecurityConfiguration` if endpoint is public/protected
- Add role-based access if needed

---

## 🐛 Common Issues & Solutions

### Issue: `Could not resolve placeholder 'se100.jwt.refresh-token-validity-in-seconds'`
**Cause:** Property name mismatch  
**Solution:** Use correct prefix `se113` not `se100`

```properties
# ✅ CORRECT
se113.jwt.refresh-token-validity-in-seconds=864000

# ❌ WRONG
se100.jwt.refresh-token-validity-in-seconds=864000
```

---

### Issue: `UserService cannot be resolved`
**Cause:** Missing import or class not created  
**Solution:**
```java
// Add import
import com.uit.nhom7.KiemThuPhanMem.service.UserService;

// Or create the service file
// File: UserService.java
@Service
@Validated
public class UserService {
    // Implementation
}
```

---

### Issue: `The method setId(Long) in the type ResLoginDTO.UserLogin is not applicable for the arguments (UUID)`
**Cause:** Type mismatch between DTO and entity  
**Solution:** Update DTO field type to match entity

```java
// OLD: ❌ WRONG
public static class UserLogin {
    private Long id;  // Wrong type
    private String email;
    private String name;
}

// NEW: ✅ CORRECT
public static class UserLogin {
    private UUID id;  // Correct type
    private String email;
    private String name;
}
```

---

### Issue: `The method getFullname() is undefined for the type User`
**Cause:** User entity uses `name` not `fullname`  
**Solution:** Use correct field name

```java
// OLD: ❌ WRONG
user.getFullname()

// NEW: ✅ CORRECT
user.getName()
```

---

### Issue: `The method permissions(Set<Permission>) in the type Role.RoleBuilder is not applicable for the arguments (new ArrayList<>())`
**Cause:** Type mismatch - Role expects `Set` not `List`  
**Solution:** Use `HashSet` instead of `ArrayList`

```java
// OLD: ❌ WRONG
.permissions(new ArrayList<>())

// NEW: ✅ CORRECT
.permissions(new HashSet<>())
```

**Required Imports:**
```java
import java.util.HashSet;
import java.util.Set;
```

---

### Issue: Application won't start - Database connection failed
**Cause:** MySQL not running or credentials wrong  
**Solution:**
```bash
# 1. Verify MySQL is running
# Windows: Check Services or Task Manager

# 2. Test connection
mysql -h localhost -u root -p123456 -e "SELECT 1;"

# 3. Create database if needed
mysql -u root -p123456 -e "CREATE DATABASE se113;"

# 4. Update application.properties with correct credentials
spring.datasource.url=jdbc:mysql://localhost:3306/se113
spring.datasource.username=root
spring.datasource.password=123456
```

---

### Issue: Port 8080 already in use
**Cause:** Another application using port 8080  
**Solution:** 
```properties
# In application.properties
server.port=8081
```

Or kill existing process:
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

---

### Issue: JWT token validation fails - token expired
**Cause:** Access token lifetime exceeded  
**Solution:** Use refresh token endpoint to get new access token

```bash
# Request
GET /api/v1/auth/refresh
Cookie: refresh_token=your_refresh_token_here

# Response: New access token
```

---

### Issue: Null Pointer Exception in convertToDTO
**Cause:** Entity relationship not loaded lazily  
**Solution:** Use eager fetch or explicit loading

```java
// Option 1: Eager fetch in entity
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "role_id")
private Role role;

// Option 2: Manual fetch in repository
@Query("SELECT u FROM User u LEFT JOIN FETCH u.role WHERE u.id = :id")
Optional<User> findByIdWithRole(@Param("id") UUID id);
```

---

## 🔍 Testing & Debugging

### Unit Testing Template
```java
@SpringBootTest
public class UserServiceTests {
    
    @Autowired
    private UserService userService;
    
    @MockBean
    private UserRepository userRepository;
    
    @Test
    public void testHandleFindByEmail() {
        // Arrange
        String email = "test@example.com";
        User user = User.builder().email(email).build();
        
        // Act
        when(userRepository.findByEmail(email))
            .thenReturn(Optional.of(user));
        User result = userService.handleFindByEmail(email);
        
        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }
}
```

### Integration Testing
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTests {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    public void testLogin() {
        ReqLoginDTO loginDTO = ReqLoginDTO.builder()
            .email("user@example.com")
            .password("password123")
            .build();
        
        ResponseEntity<ResLoginDTO> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/v1/auth/login",
            loginDTO,
            ResLoginDTO.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getAccessToken());
    }
}
```

---

## 📊 API Testing with cURL

### Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "password123"
  }'
```

### Get Account
```bash
curl -X GET http://localhost:8080/api/v1/auth/account \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

### Refresh Token
```bash
curl -X GET http://localhost:8080/api/v1/auth/refresh \
  -H "Cookie: refresh_token=YOUR_REFRESH_TOKEN_HERE"
```

### Logout
```bash
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

---

## 🔨 Build Troubleshooting

### Clean Build
```bash
./gradlew clean build
```

### Skip Tests
```bash
./gradlew build -x test
```

### View Dependency Tree
```bash
./gradlew dependencies
```

### Rebuild Database Schema
```properties
# In application.properties - CAUTION: Data will be lost
spring.jpa.hibernate.ddl-auto=create-drop
# After restart, change back to:
spring.jpa.hibernate.ddl-auto=update
```

---

## 📈 Performance Optimization

### Query Optimization
```java
// ❌ N+1 Query Problem
@ManyToOne
private Role role;

// ✅ Solution 1: Eager fetch
@ManyToOne(fetch = FetchType.EAGER)
private Role role;

// ✅ Solution 2: Use JOIN FETCH
@Query("SELECT u FROM User u LEFT JOIN FETCH u.role WHERE u.id = :id")
Optional<User> findWithRole(@Param("id") UUID id);

// ✅ Solution 3: Use EntityGraph
@EntityGraph(attributePaths = {"role"})
Optional<User> findByIdWithGraph(UUID id);
```

### Pagination Optimization
```java
// Use pagination for large datasets
Page<User> page = userRepository.findAll(
    specification,
    PageRequest.of(0, 20)
);
```

### Index Creation
```sql
-- Add indexes for frequently queried columns
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_role_name ON roles(name);
CREATE INDEX idx_user_role ON users(role_id);
```

---

## 🔐 Security Checklist

- [ ] Passwords encoded with BCrypt
- [ ] JWT secrets not hardcoded in source
- [ ] HTTPS enforced in production
- [ ] CORS configured appropriately
- [ ] SQL injection prevention (using JPA)
- [ ] XSS protection enabled
- [ ] CSRF tokens implemented
- [ ] Rate limiting configured
- [ ] Audit logging enabled
- [ ] Sensitive data not logged
- [ ] Refresh tokens in HTTP-only cookies
- [ ] Access tokens have short expiration

---

## 📝 Code Style Guidelines

### Naming Conventions
- Entity classes: `User`, `Role`, `Permission` (singular, PascalCase)
- DTO classes: `ResUserDTO`, `ReqLoginDTO` (prefix with Res/Req, PascalCase)
- Service classes: `UserService`, `RoleService` (PascalCase)
- Controller classes: `AuthController` (PascalCase)
- Repository interfaces: `UserRepository`, `RoleRepository`
- Method names: `camelCase`
- Constants: `UPPERCASE_WITH_UNDERSCORES`

### Annotations
- Always add `@ApiMessage` to controller methods
- Mark read-only transactions: `@Transactional(readOnly = true)`
- Add validation: `@NotBlank`, `@Email`, `@Min`, etc.
- Map JSON properties: `@JsonProperty("snake_case")`

### Documentation
```java
/**
 * Brief description of the method.
 * 
 * @param paramName description of parameter
 * @return description of return value
 * @throws ExceptionType description of when thrown
 */
public void methodName(String paramName) {
    // Implementation
}
```

---

## 🚀 Deployment Checklist

Before deploying to production:

- [ ] All tests passing
- [ ] Code quality reviewed
- [ ] Security audit completed
- [ ] Performance tested
- [ ] Database backup created
- [ ] Rollback plan prepared
- [ ] Monitoring configured
- [ ] Logging configured
- [ ] Environment variables secured
- [ ] API documentation updated
- [ ] Load testing completed
- [ ] Error handling verified

---

## 📚 Reference Files

- **Configuration:** `PROJECT_CONFIGURATION.md`
- **Setup Guide:** `AI_SETUP_GUIDE.md`
- **This Document:** `DEVELOPMENT_GUIDE.md`

---

## 📅 Document Info

- **Created:** 2026-04-12
- **Last Updated:** 2026-04-12
- **Version:** 1.0
- **Project:** KiemThuPhanMem v0.0.1-SNAPSHOT
