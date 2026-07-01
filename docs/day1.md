# Day 1: Project Foundation, Database & Authentication Security

## Muc tieu
Thiet lap nen tang cho he thong Rikkei Bank RESTful API theo SRS: cau truc project, database core, Spring Security stateless, JWT access token, refresh token va token blacklist.

## Pham vi SRS
- FR-01: Dang nhap he thong, cap phat JWT.
- FR-02: Xoay vong token bang Refresh Token.
- FR-03: Dang xuat va thu hoi token.
- Nen tang database cho FR-04 den FR-10.
- Security Requirements: Stateless Session, AccessToken, RefreshToken, RevokedToken blacklist.

## Cac nhiem vu

### RB-01: Project Foundation
- [x] Khoi tao project Spring Boot voi cac dependency:
  - Spring Web MVC
  - Spring Data JPA
  - Spring Security
  - Validation
  - Lombok
  - MySQL/H2
  - JWT
- [x] Thiet lap `application.properties`:
  - Cau hinh datasource.
  - Cau hinh JPA/Hibernate.
  - Cau hinh JWT secret va expiration.
  - Cau hinh upload file limit 5MB.
- [x] Chia package theo layer:
  - `controller`
  - `service`
  - `repository`
  - `entity`
  - `dto`
  - `security`
  - `config`
  - `exception`

### RB-02: Database Core Entities
- [x] Tao entity `User`:
  - username, password, email, phoneNumber.
  - isActive, isKyc.
  - createdAt.
  - lien ket Role, KycProfile, Account, RefreshToken, TokenBlacklist.
- [x] Tao entity `Role`:
  - ADMIN, STAFF, CUSTOMER.
- [x] Tao entity `RefreshToken`:
  - token, expiryDate, revoked, user.
- [x] Tao entity `TokenBlacklist`:
  - accessToken, expiryAt, blacklistedAt, user.
- [x] Tao entity `KycProfile`:
  - idNumber, fullName, dob, sex, address.
  - idCardFrontUrl, idCardBackUrl.
  - status: PENDING, CONFIRM, REJECT.
  - verifiedAt, user.
- [x] Tao entity `Account`:
  - accountNumber, currency, balance.
  - transactionPin da ma hoa BCrypt.
  - active, version.
  - user.
- [x] Tao entity `BankingTransaction`:
  - transactionCode, amount, description, status.
  - fromAccount, toAccount, createdAt.
- [x] Tao entity `AuditLog`:
  - action, actor, status, message, createdAt.
  - dung cho AOP logging o Day 3.

### RB-03: Repository Layer
- [x] Tao repository cho cac entity chinh:
  - `UserRepository`
  - `RoleRepository`
  - `RefreshTokenRepository`
  - `TokenBlacklistRepository`
  - `AccountRepository`
  - `BankingTransactionRepository`
  - `KycProfileRepository`
  - `AuditLogRepository`
- [x] Trong `UserRepository`, bo sung cac method:
  - `findByUsername`
  - `findByEmail`
  - `existsByUsername`
  - `existsByEmail`
- [x] Trong `TokenBlacklistRepository`, bo sung:
  - `existsByAccessToken`
- [x] Trong `RefreshTokenRepository`, bo sung:
  - `findByToken`
  - `deleteByUser`

### RB-04: Spring Security & JWT
- [x] Cau hinh `SecurityConfig`:
  - Tat CSRF.
  - Bat stateless session.
  - Permit `/api/auth/**`.
  - Bao ve cac API con lai.
  - Gan `JwtAuthenticationFilter` truoc `UsernamePasswordAuthenticationFilter`.
- [x] Tao `CustomUserDetailsService`.
- [x] Tao `UserPrincipal` anh xa user va role sang Spring Security.
- [x] Tao `JwtService`:
  - generate access token.
  - extract username.
  - extract expiration.
  - validate token.
  - lay thoi gian het han token.
- [x] Tao `JwtAuthenticationFilter`:
  - Doc `Authorization: Bearer`.
  - Kiem tra token co nam trong blacklist khong.
  - Validate token.
  - Set authentication vao `SecurityContext`.

### RB-05: Authentication APIs
- [x] API dang nhap:
  - `POST /api/auth/login`
  - Request: username, password.
  - Response: accessToken, refreshToken, tokenType, expiresIn.
  - Sai thong tin tra HTTP 401.
  - Tai khoan bi khoa tra HTTP 403.
- [x] API refresh token:
  - `POST /api/auth/refresh`
  - Request: refreshToken.
  - Kiem tra token ton tai, chua het han, chua revoked.
  - Cap accessToken moi.
  - Neu refresh token khong hop le tra HTTP 401.
- [x] API dang xuat:
  - `POST /api/auth/logout`
  - Lay access token tu Authorization header.
  - Luu access token vao `TokenBlacklist`.
  - Revoke refresh token cua user neu can.
  - Tra HTTP 200 OK.

### RB-06: Standard Response & Error Foundation
- [ ] Tao DTO response thanh cong theo chuan SRS:
  - success.
  - message.
  - data.
- [x] Tao DTO error response:
  - timestamp.
  - status.
  - error.
  - message.
  - path.
- [ ] Tao cac exception nen tang:
  - `BusinessException`
  - `UnauthorizedException`
  - `ForbiddenException`
  - `ResourceNotFoundException`
- [x] Tao `GlobalExceptionHandler` co ban:
  - Validation error: HTTP 400.
  - Unauthorized: HTTP 401.
  - Forbidden: HTTP 403.
  - Not found: HTTP 404.
  - Business conflict: HTTP 409.

## Ket qua can dat sau Day 1
- Project chay duoc va test context pass.
- Database entity phu hop SRS.
- Dang nhap tra access token va refresh token.
- Refresh token hoat dong.
- Logout dua access token vao blacklist.
- JWT filter chan token da logout.
- Cac API private yeu cau authentication.
