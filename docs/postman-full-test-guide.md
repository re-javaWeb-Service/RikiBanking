# Huong dan test Postman full chuc nang - Rikkei Banking API

Tai lieu nay dung cho project `RikkeiBankingv2`, chay local tai `http://localhost:8080`.

## 0. Chuan bi moi truong

### 0.1. Dieu kien truoc khi test

- May da co Java 17.
- MySQL dang chay local.
- Database se duoc tao tu dong theo config:
  - URL: `jdbc:mysql://localhost:3306/rikkei_banking_dev?createDatabaseIfNotExist=true`
  - Username: `root`
  - Password: `loc123456`
- Profile mac dinh dang la `dev-test`, nen du lieu seed se duoc tao khi app start.

### 0.2. Chay server

Trong thu muc `RikkeiBankingv2`:

```bash
./gradlew bootRun
```

Neu dung Windows PowerShell:

```powershell
.\gradlew.bat bootRun
```

Server chay tai:

```text
http://localhost:8080
```

### 0.3. Tai khoan seed co san

| Role | Username | Password | Ghi chu |
|---|---|---|---|
| Admin | `admin` | `123456` | Quan ly user, KYC, account, audit log |
| Staff | `staff` | `123456` | Quan ly user, KYC, account, audit log |
| Customer | `customer1` | `123456` | Co san KYC va account |
| Customer | `customer2` | `123456` | Co san KYC va account |

Account seed:

| User | Account number | Balance | PIN |
|---|---:|---:|---:|
| `customer1` | `1000000001` | `10000000.00` | `123456` |
| `customer2` | `1000000002` | `5000000.00` | `123456` |

Luu y: account number on dinh hon account id. Neu database da co du lieu cu, account id co the khong phai `1`, `2`.

## 1. Tao Postman Environment

Tao environment ten `Rikkei Banking - Local` voi cac bien sau:

| Variable | Initial value |
|---|---|
| `baseUrl` | `http://localhost:8080` |
| `adminToken` | de trong |
| `adminRefreshToken` | de trong |
| `staffToken` | de trong |
| `staffRefreshToken` | de trong |
| `customerToken` | de trong |
| `customerRefreshToken` | de trong |
| `newCustomerToken` | de trong |
| `newCustomerRefreshToken` | de trong |
| `newCustomerId` | de trong |
| `newCustomerAccountId` | de trong |
| `newCustomerAccountNumber` | de trong |
| `customer1AccountId` | de trong |
| `customer1AccountNumber` | `1000000001` |
| `customer2AccountNumber` | `1000000002` |
| `kycId` | de trong |
| `resetToken` | de trong |

Tat ca request can JSON thi them header:

```text
Content-Type: application/json
```

Tat ca request can dang nhap thi them header theo token phu hop:

```text
Authorization: Bearer {{adminToken}}
Authorization: Bearer {{staffToken}}
Authorization: Bearer {{customerToken}}
Authorization: Bearer {{newCustomerToken}}
```

## 2. Mau response chung

Response thanh cong dung wrapper:

```json
{
  "success": true,
  "message": "optional message",
  "data": {}
}
```

Response loi dung wrapper:

```json
{
  "timestamp": "2026-07-01T07:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Thong bao loi",
  "path": "/api/..."
}
```

Phan trang tra ve trong `data` theo format Spring Page, gom cac field nhu `content`, `pageable`, `totalElements`, `totalPages`, `size`, `number`.

## 3. Folder Authentication

### 3.1. Login Admin

- Method: `POST`
- URL: `{{baseUrl}}/api/auth/login`
- Body:

```json
{
  "username": "admin",
  "password": "123456"
}
```

- Expected: `200 OK`

Tests script:

```javascript
const res = pm.response.json();
pm.environment.set("adminToken", res.data.accessToken);
pm.environment.set("adminRefreshToken", res.data.refreshToken);
```

### 3.2. Login Staff

- Method: `POST`
- URL: `{{baseUrl}}/api/auth/login`
- Body:

```json
{
  "username": "staff",
  "password": "123456"
}
```

- Expected: `200 OK`

Tests script:

```javascript
const res = pm.response.json();
pm.environment.set("staffToken", res.data.accessToken);
pm.environment.set("staffRefreshToken", res.data.refreshToken);
```

### 3.3. Login Customer1

- Method: `POST`
- URL: `{{baseUrl}}/api/auth/login`
- Body:

```json
{
  "username": "customer1",
  "password": "123456"
}
```

- Expected: `200 OK`

Tests script:

```javascript
const res = pm.response.json();
pm.environment.set("customerToken", res.data.accessToken);
pm.environment.set("customerRefreshToken", res.data.refreshToken);
```

### 3.4. Login sai mat khau

- Method: `POST`
- URL: `{{baseUrl}}/api/auth/login`
- Body:

```json
{
  "username": "admin",
  "password": "wrong-password"
}
```

- Expected: `401 Unauthorized`
- Message: `Invalid username or password`

### 3.5. Register customer moi

- Method: `POST`
- URL: `{{baseUrl}}/api/auth/register`
- Body:

```json
{
  "username": "newcustomer",
  "password": "password123",
  "email": "newcustomer@example.com",
  "phoneNumber": "0901234567"
}
```

- Expected: `201 Created`
- Message: `User registered successfully`

Neu username/email da ton tai:

- Expected: `400 Bad Request`
- Message: `Username already exists` hoac `Email already exists`

### 3.6. Login customer moi

- Method: `POST`
- URL: `{{baseUrl}}/api/auth/login`
- Body:

```json
{
  "username": "newcustomer",
  "password": "password123"
}
```

- Expected: `200 OK`

Tests script:

```javascript
const res = pm.response.json();
pm.environment.set("newCustomerToken", res.data.accessToken);
pm.environment.set("newCustomerRefreshToken", res.data.refreshToken);
```

### 3.7. Refresh token

- Method: `POST`
- URL: `{{baseUrl}}/api/auth/refresh`
- Body:

```json
{
  "refreshToken": "{{customerRefreshToken}}"
}
```

- Expected: `200 OK`

Tests script:

```javascript
const res = pm.response.json();
pm.environment.set("customerToken", res.data.accessToken);
pm.environment.set("customerRefreshToken", res.data.refreshToken);
```

### 3.8. Forgot password

- Method: `POST`
- URL: `{{baseUrl}}/api/auth/forgot-password`
- Body:

```json
{
  "usernameOrEmail": "customer1"
}
```

- Expected: `200 OK`

Response khong tra reset token vi ly do bao mat:

```json
{
  "success": true,
  "data": {
    "message": "If the account exists, a password reset email has been sent",
    "expiresInMinutes": 15
  }
}
```

Luu y: endpoint nay can mail config hoat dong. Neu SMTP loi, request co the tra `500 Internal Server Error`.

### 3.9. Reset password

- Method: `POST`
- URL: `{{baseUrl}}/api/auth/reset-password`
- Body:

```json
{
  "resetToken": "{{resetToken}}",
  "newPassword": "newpassword123",
  "confirmPassword": "newpassword123"
}
```

- Expected thanh cong: `200 OK`
- Message: `Password reset successfully`

Case loi:

- Token sai/het han: `401 Unauthorized`
- Confirm password khong khop: `400 Bad Request`

### 3.10. Logout

- Method: `POST`
- URL: `{{baseUrl}}/api/auth/logout`
- Header: `Authorization: Bearer {{customerToken}}`
- Body:

```json
{
  "refreshToken": "{{customerRefreshToken}}"
}
```

- Expected: `200 OK`
- Message: `Logout successful`

Sau logout, dung lai access token cu de goi API protected se bi tu choi.

## 4. Folder User - Admin/Staff

Tat ca request trong folder nay can token Admin hoac Staff, tru `/me` co the dung moi role.

### 4.1. Lay danh sach users

- Method: `GET`
- URL: `{{baseUrl}}/api/v1/users?page=0&size=10&sort=id,asc`
- Header: `Authorization: Bearer {{adminToken}}`
- Expected: `200 OK`

### 4.2. Lay user theo id

- Method: `GET`
- URL: `{{baseUrl}}/api/v1/users/1`
- Header: `Authorization: Bearer {{adminToken}}`
- Expected: `200 OK`

### 4.3. Lay profile cua minh

- Method: `GET`
- URL: `{{baseUrl}}/api/v1/users/me`
- Header: `Authorization: Bearer {{customerToken}}`
- Expected: `200 OK`

Tests script de lay `newCustomerId` sau khi login bang customer moi:

```javascript
const res = pm.response.json();
pm.environment.set("newCustomerId", res.data.id);
```

### 4.4. Cap nhat user

- Method: `PUT`
- URL: `{{baseUrl}}/api/v1/users/{{newCustomerId}}`
- Header: `Authorization: Bearer {{adminToken}}`
- Body:

```json
{
  "email": "newcustomer.updated@example.com",
  "phoneNumber": "0909999999",
  "isActive": true,
  "role": "ROLE_CUSTOMER"
}
```

- Expected: `200 OK`
- Message: `User updated successfully`

### 4.5. Khoa user - Admin only

- Method: `PATCH`
- URL: `{{baseUrl}}/api/v1/users/{{newCustomerId}}/status`
- Header: `Authorization: Bearer {{adminToken}}`
- Body:

```json
{
  "isActive": false
}
```

- Expected: `200 OK`

### 4.6. Mo khoa user - Admin only

- Method: `PATCH`
- URL: `{{baseUrl}}/api/v1/users/{{newCustomerId}}/status`
- Header: `Authorization: Bearer {{adminToken}}`
- Body:

```json
{
  "isActive": true
}
```

- Expected: `200 OK`

### 4.7. Customer goi API danh sach users

- Method: `GET`
- URL: `{{baseUrl}}/api/v1/users`
- Header: `Authorization: Bearer {{customerToken}}`
- Expected: `403 Forbidden`

## 5. Folder KYC

### 5.1. Customer nop ho so KYC

- Method: `POST`
- URL: `{{baseUrl}}/api/v1/kyc/upload`
- Header: `Authorization: Bearer {{newCustomerToken}}`
- Body type: `form-data`

| Key | Type | Value |
|---|---|---|
| `fullName` | Text | `Nguyen Van A` |
| `idNumber` | Text | `001099123456` |
| `dob` | Text | `1999-01-15` |
| `sex` | Text | `MALE` |
| `address` | Text | `123 Nguyen Trai, Ha Noi` |
| `idCardFront` | File | Chon file jpg/png/pdf nho hon 5MB |
| `idCardBack` | File | Optional, jpg/png/pdf nho hon 5MB |

- Expected: `201 Created`
- Message: `KYC profile uploaded successfully`
- Status trong data: `PENDING`

Tests script:

```javascript
const res = pm.response.json();
pm.environment.set("kycId", res.data.id);
```

Case loi:

- Khong upload `idCardFront`: `400 Bad Request`
- File qua 5MB: `400 Bad Request`
- File khong phai jpg/png/pdf: `400 Bad Request`
- Dung Admin/Staff token de upload: `403 Forbidden`

### 5.2. Customer xem KYC cua minh

- Method: `GET`
- URL: `{{baseUrl}}/api/v1/kyc/me`
- Header: `Authorization: Bearer {{newCustomerToken}}`
- Expected: `200 OK`

### 5.3. Admin/Staff lay danh sach KYC

- Method: `GET`
- URL: `{{baseUrl}}/api/v1/kyc?page=0&size=10&sort=id,desc`
- Header: `Authorization: Bearer {{adminToken}}`
- Expected: `200 OK`

Loc theo status:

```text
{{baseUrl}}/api/v1/kyc?status=PENDING&page=0&size=10
{{baseUrl}}/api/v1/kyc?status=CONFIRM&page=0&size=10
{{baseUrl}}/api/v1/kyc?status=REJECT&page=0&size=10
```

### 5.4. Admin/Staff xem chi tiet KYC

- Method: `GET`
- URL: `{{baseUrl}}/api/v1/kyc/{{kycId}}`
- Header: `Authorization: Bearer {{adminToken}}`
- Expected: `200 OK`

### 5.5. Admin/Staff approve KYC

- Method: `PATCH`
- URL: `{{baseUrl}}/api/v1/kyc/{{kycId}}/approve`
- Header: `Authorization: Bearer {{adminToken}}`
- Body: empty
- Expected: `200 OK`
- Message: `KYC profile approved`
- Status trong data: `CONFIRM`

Sau approve, user se co `isKyc = true`.

### 5.6. Admin/Staff reject KYC

- Method: `PATCH`
- URL: `{{baseUrl}}/api/v1/kyc/{{kycId}}/reject`
- Header: `Authorization: Bearer {{adminToken}}`
- Body: empty
- Expected: `200 OK`
- Message: `KYC profile rejected`
- Status trong data: `REJECT`

Sau reject, user se co `isKyc = false`.

## 6. Folder Account

### 6.1. Customer lay danh sach account cua minh

- Method: `GET`
- URL: `{{baseUrl}}/api/v1/accounts?page=0&size=10`
- Header: `Authorization: Bearer {{customerToken}}`
- Expected: `200 OK`

Tests script de luu account id customer1:

```javascript
const res = pm.response.json();
const first = res.data.content && res.data.content[0];
if (first) {
  pm.environment.set("customer1AccountId", first.id);
  pm.environment.set("customer1AccountNumber", first.accountNumber);
}
```

### 6.2. Admin/Staff lay tat ca accounts

- Method: `GET`
- URL: `{{baseUrl}}/api/v1/accounts?page=0&size=10&sort=id,asc`
- Header: `Authorization: Bearer {{adminToken}}`
- Expected: `200 OK`

### 6.3. Admin/Staff loc account theo userId

- Method: `GET`
- URL: `{{baseUrl}}/api/v1/accounts?userId={{newCustomerId}}&page=0&size=10`
- Header: `Authorization: Bearer {{adminToken}}`
- Expected: `200 OK`

### 6.4. Xem chi tiet account

- Method: `GET`
- URL: `{{baseUrl}}/api/v1/accounts/{{customer1AccountId}}`
- Header: `Authorization: Bearer {{customerToken}}`
- Expected: `200 OK`

Neu customer xem account cua user khac:

- Expected: `403 Forbidden`
- Message: `You do not have permission to access this account`

### 6.5. Van tin so du

- Method: `GET`
- URL: `{{baseUrl}}/api/v1/accounts/{{customer1AccountId}}/balance`
- Header: `Authorization: Bearer {{customerToken}}`
- Expected: `200 OK`

Response mau:

```json
{
  "success": true,
  "data": {
    "accountNumber": "1000000001",
    "currency": "VND",
    "balance": 10000000.00
  }
}
```

### 6.6. Tao account moi cho customer da KYC - Admin/Staff

Chi thanh cong neu user co `isKyc = true`.

- Method: `POST`
- URL: `{{baseUrl}}/api/v1/accounts`
- Header: `Authorization: Bearer {{adminToken}}`
- Body:

```json
{
  "userId": "{{newCustomerId}}",
  "currency": "VND",
  "transactionPin": "123456"
}
```

- Expected: `201 Created`
- Message: `Account created successfully`

Tests script:

```javascript
const res = pm.response.json();
pm.environment.set("newCustomerAccountId", res.data.id);
pm.environment.set("newCustomerAccountNumber", res.data.accountNumber);
```

Case loi neu user chua approve KYC:

- Expected: `400 Bad Request`
- Message: `User must complete KYC before opening account`

### 6.7. Doi PIN giao dich - Customer

- Method: `PATCH`
- URL: `{{baseUrl}}/api/v1/accounts/{{customer1AccountId}}/pin`
- Header: `Authorization: Bearer {{customerToken}}`
- Body:

```json
{
  "oldPin": "123456",
  "newPin": "654321",
  "confirmNewPin": "654321"
}
```

- Expected: `200 OK`
- Message: `Transaction PIN changed successfully`

Nen doi lai PIN ve `123456` sau khi test:

```json
{
  "oldPin": "654321",
  "newPin": "123456",
  "confirmNewPin": "123456"
}
```

Case loi:

- Old PIN sai: `403 Forbidden`
- Confirm PIN khong khop: `400 Bad Request`
- New PIN khong du 6 so: `400 Bad Request`
- New PIN trung old PIN: `400 Bad Request`

### 6.8. Xem sao ke giao dich

- Method: `GET`
- URL: `{{baseUrl}}/api/v1/accounts/{{customer1AccountId}}/transactions?page=0&size=10&sort=createdAt,desc`
- Header: `Authorization: Bearer {{customerToken}}`
- Expected: `200 OK`

### 6.9. Khoa account - Admin/Staff

- Method: `PATCH`
- URL: `{{baseUrl}}/api/v1/accounts/{{customer1AccountId}}/status`
- Header: `Authorization: Bearer {{adminToken}}`
- Body:

```json
{
  "active": false
}
```

- Expected: `200 OK`
- Message: `Account status updated`

### 6.10. Mo khoa account - Admin/Staff

- Method: `PATCH`
- URL: `{{baseUrl}}/api/v1/accounts/{{customer1AccountId}}/status`
- Header: `Authorization: Bearer {{adminToken}}`
- Body:

```json
{
  "active": true
}
```

- Expected: `200 OK`

## 7. Folder Transfer

### 7.1. Chuyen tien thanh cong

- Method: `POST`
- URL: `{{baseUrl}}/api/v1/transactions/transfer`
- Header: `Authorization: Bearer {{customerToken}}`
- Body:

```json
{
  "fromAccountNumber": "{{customer1AccountNumber}}",
  "toAccountNumber": "{{customer2AccountNumber}}",
  "amount": 500000,
  "transactionPin": "123456",
  "description": "Chuyen tien test Postman"
}
```

- Expected: `200 OK`
- Message: `Transfer successful`
- Status trong data: `SUCCESS`

### 7.2. Loi khong du so du

- Method: `POST`
- URL: `{{baseUrl}}/api/v1/transactions/transfer`
- Header: `Authorization: Bearer {{customerToken}}`
- Body:

```json
{
  "fromAccountNumber": "{{customer1AccountNumber}}",
  "toAccountNumber": "{{customer2AccountNumber}}",
  "amount": 999999999999,
  "transactionPin": "123456",
  "description": "Test khong du so du"
}
```

- Expected: `409 Conflict`
- Message: `Insufficient balance`

### 7.3. Loi sai PIN

- Method: `POST`
- URL: `{{baseUrl}}/api/v1/transactions/transfer`
- Header: `Authorization: Bearer {{customerToken}}`
- Body:

```json
{
  "fromAccountNumber": "{{customer1AccountNumber}}",
  "toAccountNumber": "{{customer2AccountNumber}}",
  "amount": 100000,
  "transactionPin": "999999",
  "description": "Test sai PIN"
}
```

- Expected: `403 Forbidden`
- Message: `Invalid transaction PIN`

### 7.4. Loi chuyen cho chinh minh

- Method: `POST`
- URL: `{{baseUrl}}/api/v1/transactions/transfer`
- Header: `Authorization: Bearer {{customerToken}}`
- Body:

```json
{
  "fromAccountNumber": "{{customer1AccountNumber}}",
  "toAccountNumber": "{{customer1AccountNumber}}",
  "amount": 100000,
  "transactionPin": "123456",
  "description": "Test same account"
}
```

- Expected: `400 Bad Request`
- Message: `Cannot transfer to the same account`

### 7.5. Loi customer chuyen tien tu account khong phai cua minh

- Method: `POST`
- URL: `{{baseUrl}}/api/v1/transactions/transfer`
- Header: `Authorization: Bearer {{customerToken}}`
- Body:

```json
{
  "fromAccountNumber": "{{customer2AccountNumber}}",
  "toAccountNumber": "{{customer1AccountNumber}}",
  "amount": 100000,
  "transactionPin": "123456",
  "description": "Test account ownership"
}
```

- Expected: `403 Forbidden`
- Message: `You do not have permission to transfer from this account`

### 7.6. Loi account bi khoa

Buoc test:

1. Admin khoa account nguon bang endpoint `PATCH /api/v1/accounts/{id}/status`.
2. Customer goi transfer.

- Expected: `409 Conflict`
- Message co dang: `Account is not active: 1000000001`

Sau do Admin mo khoa lai account de tiep tuc test.

## 8. Folder Audit Log - Admin/Staff

### 8.1. Lay audit logs

- Method: `GET`
- URL: `{{baseUrl}}/api/v1/audit-logs?page=0&size=20&sort=createdAt,desc`
- Header: `Authorization: Bearer {{adminToken}}`
- Expected: `200 OK`

Audit log se co khi goi cac action co `@LogAudit`, vi du:

- `KYC_UPLOAD`
- `KYC_APPROVE`
- `KYC_REJECT`
- `ACCOUNT_CREATE`
- `ACCOUNT_STATUS_CHANGE`
- `ACCOUNT_PIN_CHANGE`
- `TRANSFER`

### 8.2. Customer xem audit logs

- Method: `GET`
- URL: `{{baseUrl}}/api/v1/audit-logs`
- Header: `Authorization: Bearer {{customerToken}}`
- Expected: `403 Forbidden`

## 9. Luong demo full nen chay theo thu tu

1. Login Admin, luu `adminToken`.
2. Login Customer1, luu `customerToken`.
3. Customer1 goi `/api/v1/accounts`, luu `customer1AccountId`.
4. Register `newcustomer`.
5. Login `newcustomer`, luu `newCustomerToken`.
6. `newcustomer` goi `/api/v1/users/me`, luu `newCustomerId`.
7. `newcustomer` nop KYC, luu `kycId`.
8. Admin xem danh sach KYC `status=PENDING`.
9. Admin approve KYC `{{kycId}}`.
10. Admin tao account cho `newcustomer`, luu `newCustomerAccountId` va `newCustomerAccountNumber`.
11. Customer1 van tin so du.
12. Customer1 chuyen tien sang customer2.
13. Customer1 xem sao ke.
14. Admin xem audit logs.
15. Test cac case loi: sai password, sai PIN, khong du so du, customer truy cap sai role.
16. Logout customer.

## 10. Bang loi thuong gap

| HTTP code | Nguyen nhan | Cach xu ly |
|---|---|---|
| `400 Bad Request` | Body sai format, validation fail, user chua KYC | Kiem tra JSON/form-data va message |
| `401 Unauthorized` | Thieu token, token sai/het han, login sai | Login lai hoac refresh token |
| `403 Forbidden` | Sai role hoac khong phai chu account | Dung token dung role/owner |
| `404 Not Found` | ID/account khong ton tai | Kiem tra lai id, account number |
| `409 Conflict` | Khong du so du, account bi khoa | Kiem tra balance/status account |
| `500 Internal Server Error` | Loi server/config, thuong gap o mail config | Xem log backend, kiem tra SMTP |

## 11. Checklist nghiem thu nhanh

- Auth:
  - Login thanh cong cho Admin/Staff/Customer.
  - Login sai tra `401`.
  - Register tao customer moi.
  - Refresh token tra access token moi.
  - Logout revoke token.
- User:
  - Admin/Staff xem va sua user.
  - Admin khoa/mo user.
  - Customer bi chan khi xem danh sach user.
- KYC:
  - Customer upload KYC bang form-data.
  - Admin/Staff xem, approve, reject KYC.
  - Upload sai file bi chan.
- Account:
  - Customer chi xem account cua minh.
  - Admin/Staff xem tat ca account.
  - Admin/Staff tao account cho user da KYC.
  - Doi PIN dung/sai.
  - Khoa/mo account.
- Transfer:
  - Chuyen tien thanh cong.
  - Khong du so du tra `409`.
  - Sai PIN tra `403`.
  - Chuyen tu account khong so huu tra `403`.
- Audit:
  - Admin/Staff xem duoc audit logs.
  - Customer bi chan.
