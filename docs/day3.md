# Day 3: Transfer, Audit Logging, Redis, PIN/Password & Testing

## Muc tieu
Hoan thien phan nghiep vu tai chinh nang cao va chat luong theo SRS: chuyen tien an toan, AOP audit log, Redis token blacklist, doi PIN/quen mat khau, error handling day du va test.

## Pham vi SRS
- FR-07: Chuyen tien noi bo/lien ngan hang.
- FR-10: Doi ma PIN / Quen mat khau.
- Non-functional Security: Chong double-spending, BCrypt, JWT secret 256-bit.
- Logging: Bat buoc ghi log moi thay doi so du bang AOP.
- Day 3 yeu cau them: AOP Logging, Redis, Unit Test va Controller Test.

## Cac nhiem vu

### RB-13: Transfer Money
- [ ] Tao `TransferRequest`:
  - fromAccountNumber hoac fromAccountId.
  - toAccountNumber hoac targetAccountId.
  - amount.
  - description.
  - transactionPin.
- [ ] Tao `TransferResponse`:
  - transactionId.
  - transactionCode.
  - fromAccountNumber.
  - toAccountNumber.
  - amount.
  - status.
  - createdAt.
- [ ] API chuyen tien:
  - `POST /api/v1/transactions/transfer`
  - CUSTOMER thuc hien chuyen tien tu tai khoan cua minh.
  - Validate token va ownership cua source account.
- [ ] Xu ly nghiep vu transfer trong service:
  - Kiem tra tai khoan nguon ton tai.
  - Kiem tra tai khoan dich ton tai.
  - Khong cho chuyen vao chinh tai khoan nguon.
  - Kiem tra account dang active.
  - Kiem tra user da KYC neu SRS yeu cau.
  - Kiem tra amount > 0.
  - Kiem tra so du du.
  - Kiem tra transaction PIN bang BCrypt.
  - Tru tien tai khoan nguon.
  - Cong tien tai khoan dich.
  - Tao `BankingTransaction` status SUCCESS.
  - Tra HTTP 200 OK hoac 201 Created.
- [ ] Xu ly loi transfer:
  - Khong du so du: HTTP 409 Conflict.
  - Tai khoan khong ton tai: HTTP 404.
  - Sai PIN: HTTP 403.
  - Sai quyen so huu account: HTTP 403.
  - Validation fail: HTTP 400.

### RB-14: Double-spending Protection
- [ ] Bo sung query khoa account khi transfer:
  - Dung `@Lock(LockModeType.PESSIMISTIC_WRITE)` hoac optimistic locking voi `@Version`.
  - Lay source account va target account trong cung transaction.
- [ ] Dat `@Transactional` tai method service transfer.
- [ ] Dam bao transfer rollback khi co loi:
  - Khong tao transaction SUCCESS neu tru/cong tien that bai.
  - Neu loi thi ghi status FAILED neu can theo thiet ke.
- [ ] Viet test case chong chuyen tien vuot so du.

### RB-15: AOP Audit Logging
- [ ] Tao entity `AuditLog` neu chua co:
  - action.
  - actor.
  - status.
  - message.
  - createdAt.
- [ ] Tao enum/action cho audit:
  - TRANSFER.
  - KYC_APPROVE.
  - KYC_REJECT.
  - ACCOUNT_LOCK.
  - ACCOUNT_UNLOCK.
- [ ] Tao annotation `@LogAudit`.
- [ ] Tao `FinancialAuditAspect`:
  - Bat cac method co `@LogAudit`.
  - Ghi log SUCCESS sau khi method thanh cong.
  - Ghi log FAILED khi method throw exception.
  - Ghi input/output/error o muc du can thiet.
- [ ] Dam bao AOP tach rieng khoi logic business:
  - Service transfer khong tu viet code audit truc tiep.
  - Aspect phu trach log.
- [ ] Khong log thong tin nhay cam:
  - password.
  - transactionPin.
  - token.
  - refreshToken.
  - cardNumber.
  - idNumber neu can mask.
- [ ] Tao ham sanitize/mask data truoc khi luu audit log.

### RB-16: Redis Token Blacklist
- [ ] Them dependency Redis:
  - `spring-boot-starter-data-redis`.
- [ ] Cau hinh Redis trong `application.properties`.
- [ ] Tao interface `TokenBlacklistService`:
  - `blacklist(token, ttl)`.
  - `isBlacklisted(token)`.
- [ ] Tao `RedisTokenBlacklistService`:
  - Luu token voi key prefix `auth:blacklist:`.
  - Set TTL bang thoi gian con lai cua access token.
- [ ] Cap nhat `AuthService.logout`:
  - Khong luu blacklist vao DB neu da chuyen sang Redis.
  - Luu token vao Redis voi TTL.
- [ ] Cap nhat `JwtAuthenticationFilter`:
  - Kiem tra blacklist qua `TokenBlacklistService`.
  - Neu token bi revoke thi request khong duoc authenticate.
- [ ] Giai thich trong README hoac report:
  - Redis phu hop blacklist token vi truy van nhanh, co TTL tu dong, giam tai DB.

### RB-17: Change PIN & Forgot Password
- [ ] API doi transaction PIN:
  - `PATCH /api/v1/accounts/{id}/pin`
  - CUSTOMER chi doi PIN tai khoan cua minh.
  - Request: oldPin, newPin, confirmNewPin.
  - Verify oldPin bang BCrypt.
  - Hash newPin bang BCrypt.
  - Khong luu PIN plain text.
- [ ] API yeu cau quen mat khau:
  - `POST /api/auth/forgot-password`
  - Public endpoint.
  - Nhan username/email.
  - Neu user ton tai, tao reset token hoac mock flow theo yeu cau du an.
- [ ] API reset mat khau:
  - `POST /api/auth/reset-password`
  - Request: resetToken, newPassword, confirmPassword.
  - Hash password bang BCrypt.
- [ ] Dam bao response khong tiet lo user co ton tai hay khong trong forgot-password neu can bao mat.

### RB-18: Global Exception Handler Completion
- [ ] Hoan thien `GlobalExceptionHandler` theo SRS:
  - 400 Bad Request.
  - 401 Unauthorized.
  - 403 Forbidden.
  - 404 Not Found.
  - 409 Conflict.
  - 500 Internal Server Error.
- [ ] Chuan hoa error body:
  - timestamp.
  - status.
  - error.
  - message.
  - path.
- [ ] Them handler cho:
  - `MethodArgumentNotValidException`.
  - `BadCredentialsException`.
  - `AccessDeniedException`.
  - `InsufficientBalanceException`.
  - `ResourceNotFoundException`.
  - Exception chung.

### RB-19: Unit Tests
- [ ] Viet toi thieu 5 unit test cho service bang JUnit 5 + Mockito:
  - Login thanh cong.
  - Logout blacklist token.
  - Transfer thanh cong.
  - Transfer that bai vi khong du so du.
  - Transfer that bai vi account khong active hoac sai ownership.
- [ ] Viet test cho Redis blacklist service:
  - Blacklist token thanh cong.
  - Check token da blacklist.
  - TTL duoc set.
- [ ] Viet test cho eKYC service:
  - Upload thanh cong.
  - Sai format file.
  - Cloud upload loi.

### RB-20: Controller Tests
- [ ] Viet toi thieu 5 controller test bang MockMvc:
  - `POST /api/auth/login`.
  - `POST /api/auth/refresh`.
  - `POST /api/auth/logout`.
  - `GET /api/v1/users`.
  - `POST /api/v1/transactions/transfer`.
- [ ] Test validation:
  - Request body thieu field.
  - Amount <= 0.
  - File upload qua 5MB.
- [ ] Test authorization:
  - Khong co token: HTTP 401.
  - Sai role: HTTP 403.
  - CUSTOMER truy cap account khong thuoc minh: HTTP 403.

### RB-21: Final SRS Verification
- [ ] Doi chieu toan bo FR-01 den FR-10:
  - FR-01 Login.
  - FR-02 Refresh Token.
  - FR-03 Logout/Revoke Token.
  - FR-04 Register/open account + eKYC upload.
  - FR-05 User/Account CRUD pagination.
  - FR-06 Balance inquiry.
  - FR-07 Transfer money.
  - FR-08 Transaction statement.
  - FR-09 eKYC approval.
  - FR-10 Change PIN/Forgot password.
- [ ] Doi chieu non-functional requirements:
  - Password va PIN dung BCrypt.
  - JWT secret 256-bit.
  - API stateless.
  - Transfer co co che chong double-spending.
  - Upload file gioi han 5MB.
  - AOP audit log cho thay doi so du.
- [ ] Chay `./gradlew test`.
- [ ] Cap nhat README hoac report:
  - Cach chay project.
  - Danh sach API.
  - Redis va Cloudinary/AWS S3 config.
  - Ly do dung Redis thay DB cho token blacklist.

## Ket qua can dat sau Day 3
- Chuyen tien an toan, transactional, co chong double-spending.
- Moi thay doi so du duoc audit bang AOP.
- Token blacklist dung Redis TTL.
- Co API doi PIN va reset password.
- Global exception handler tra dung format SRS.
- Co unit test va controller test thuc te.
- Toan bo FR-01 den FR-10 trong SRS duoc cover.
