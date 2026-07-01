# Day 2: User Management, Account APIs, eKYC & Core Banking Queries

## Muc tieu
Hoan thien cac use case quan tri va nghiep vu co ban trong SRS: quan ly khach hang, quan ly tai khoan, dang ky/mo tai khoan eKYC, van tin so du va xem sao ke giao dich.

## Pham vi SRS
- FR-04: Dang ky mo tai khoan va tai len eKYC.
- FR-05: Quan ly nguoi dung va tai khoan CRUD, phan trang.
- FR-06: Van tin so du tai khoan.
- FR-08: Xem sao ke lich su giao dich.
- FR-09: Phe duyet ho so dinh danh eKYC.
- Mot phan Authorization Matrix cho ADMIN, STAFF, CUSTOMER.

## Cac nhiem vu

### RB-07: User & Customer Management
- [x] Tao `UserResponseDto`:
  - id, username, email, phoneNumber, isActive, isKyc, role, createdAt.
  - Khong tra password hoac thong tin nhay cam.
- [x] Tao DTO request cap nhat user:
  - email.
  - phoneNumber.
  - isActive.
  - role neu ADMIN duoc phep cap nhat.
- [x] Bo sung JPQL Constructor Projection trong `UserRepository`:
  - Tra ve `Page<UserResponseDto>`.
  - Chi select cac cot can thiet, khong load full entity.
- [x] API lay danh sach khach hang:
  - `GET /api/v1/users`
  - Chi ADMIN/STAFF duoc truy cap.
  - Ho tro page, size, sort.
  - Tra ve `Page<UserResponseDto>`.
- [x] API xem chi tiet khach hang:
  - `GET /api/v1/users/{id}`
  - Chi ADMIN/STAFF duoc truy cap.
- [x] API cap nhat thong tin khach hang:
  - `PUT /api/v1/users/{id}`
  - Chi ADMIN/STAFF duoc truy cap.
  - Validate email, phoneNumber, role, status.
- [x] API khoa/mo khoa khach hang:
  - `PATCH /api/v1/users/{id}/status`
  - Chi ADMIN duoc khoa/mo khoa.
- [x] Xu ly loi:
  - Khong tim thay user: HTTP 404.
  - Sai quyen: HTTP 403.
  - Validation fail: HTTP 400.

### RB-08: Account Management & Balance Inquiry
- [x] Tao `AccountResponseDto`:
  - id, accountNumber, currency, balance, active, createdAt.
  - Khong tra `transactionPin`.
- [x] Tao API lay danh sach tai khoan cua khach hang:
  - `GET /api/v1/accounts`
  - CUSTOMER chi xem tai khoan cua minh.
  - ADMIN/STAFF co the filter theo userId.
- [x] Tao API xem chi tiet tai khoan:
  - `GET /api/v1/accounts/{id}`
  - Kiem tra quyen so huu tai khoan voi CUSTOMER.
- [x] Tao API van tin so du:
  - `GET /api/v1/accounts/{id}/balance`
  - Chi CUSTOMER so huu tai khoan hoac ADMIN/STAFF duoc xem.
  - Tra ve accountNumber, currency, balance.
- [x] Tao API tao tai khoan ngan hang cho khach hang da KYC:
  - `POST /api/v1/accounts`
  - Chi STAFF/ADMIN duoc tao.
  - Chi tao khi user da `isKyc = true`.
  - Ma hoa transaction PIN bang BCrypt.
- [x] Tao API khoa/mo khoa tai khoan:
  - `PATCH /api/v1/accounts/{id}/status`
  - Chi STAFF/ADMIN duoc thuc hien.

### RB-09: eKYC Upload & Cloud Storage
- [ ] Tich hop Cloudinary hoac AWS S3 SDK.
- [ ] Cau hinh bien moi truong cho cloud storage:
  - cloud name/bucket.
  - api key/access key.
  - api secret/secret key.
- [ ] Tao service upload file:
  - Validate file khong rong.
  - Gioi han dung luong 5MB/file.
  - Chi chap nhan jpg, jpeg, png, pdf neu SRS cho phep.
  - Upload len cloud storage.
  - Tra ve secure URL.
- [x] API upload eKYC:
  - `POST /api/v1/kyc/upload`
  - Request multipart/form-data.
  - File mat truoc CCCD/Passport.
  - File mat sau neu can.
  - Luu URL vao `KycProfile`.
  - Cap nhat status thanh `PENDING`.
  - Tra HTTP 200 OK kem DTO.
- [x] Xu ly loi upload:
  - Sai dinh dang: HTTP 400.
  - Vuot 5MB: HTTP 400.
  - Loi cloud storage: HTTP 503 hoac 500.

> Ghi chu hien tai: `FileStorageService` dang dung mock URL `mock://kyc/...` de hoan thien flow va validation. Cloudinary/AWS S3 that van chua duoc tich hop.

### RB-10: eKYC Approval Workflow
- [x] Tao `KycProfileResponseDto`:
  - id, userId, fullName, idNumber, file URLs, status, verifiedAt.
- [x] API lay danh sach ho so KYC cho STAFF:
  - `GET /api/v1/kyc`
  - Ho tro filter theo status: PENDING, CONFIRM, REJECT.
  - Ho tro phan trang.
- [x] API xem chi tiet ho so KYC:
  - `GET /api/v1/kyc/{id}`
  - STAFF/ADMIN duoc truy cap.
- [x] API duyet ho so KYC:
  - `PATCH /api/v1/kyc/{id}/approve`
  - Chi STAFF/ADMIN duoc thuc hien.
  - Chuyen status sang `CONFIRM`.
  - Cap nhat `User.isKyc = true`.
  - Set `verifiedAt`.
- [x] API tu choi ho so KYC:
  - `PATCH /api/v1/kyc/{id}/reject`
  - Chi STAFF/ADMIN duoc thuc hien.
  - Chuyen status sang `REJECT`.
  - Khong set `User.isKyc = true`.

### RB-11: Transaction Statement Query
- [x] Tao `TransactionStatementDto`:
  - transactionId.
  - transactionCode.
  - accountNumber.
  - counterpartyAccountNumber.
  - amount.
  - transactionDirection: DEBIT hoac CREDIT.
  - description.
  - status.
  - createdAt.
- [x] Bo sung query trong `BankingTransactionRepository`:
  - Tim transaction theo `fromAccount.id = accountId OR toAccount.id = accountId`.
  - Ho tro phan trang.
  - Sap xep moi nhat truoc.
- [x] API xem sao ke giao dich:
  - `GET /api/v1/accounts/{accountId}/transactions`
  - CUSTOMER chi xem tai khoan cua minh.
  - ADMIN/STAFF co the xem theo nghiep vu.
  - Service tu tinh DEBIT neu account la fromAccount.
  - Service tu tinh CREDIT neu account la toAccount.
- [x] Xu ly loi:
  - Account khong ton tai: HTTP 404.
  - CUSTOMER xem tai khoan khong phai cua minh: HTTP 403.

### RB-12: Authorization Matrix
- [x] Cau hinh phan quyen:
  - `/api/auth/**`: Public.
  - `/api/v1/admin/**`: ADMIN.
  - `/api/v1/users/**`: ADMIN, STAFF.
  - `/api/v1/kyc/**`: CUSTOMER upload, STAFF/ADMIN approve.
  - `/api/v1/accounts/**`: CUSTOMER/STAFF/ADMIN tuy endpoint.
  - `/api/v1/transactions/**`: CUSTOMER/STAFF/ADMIN tuy endpoint.
- [x] Them method-level security neu can:
  - `@PreAuthorize`.
  - Kiem tra ownership trong service.

## Ket qua can dat sau Day 2
- Admin/Staff quan ly duoc user va account co phan trang.
- Customer upload eKYC len Cloudinary/AWS S3.
- Staff/ADMIN duyet hoac tu choi eKYC.
- Customer xem duoc so du tai khoan.
- Customer xem duoc sao ke giao dich co phan trang.
- Tat ca response khong lo password, PIN hoac thong tin nhay cam.
