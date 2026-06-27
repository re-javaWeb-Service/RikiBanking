# Day 1: Project Foundation, Database & Security

## Mục tiêu (Objectives)
Thiết lập nền tảng cơ bản cho dự án hệ thống Rikkei Bank, bao gồm cấu trúc mã nguồn, cơ sở dữ liệu và bảo mật với JWT.

## Các nhiệm vụ (Tasks)
- [ ] Khởi tạo dự án Spring Boot (Spring Web, Data JPA, Security, MySQL/PostgreSQL, Validation, Lombok).
- [ ] Thiết lập file cấu hình `application.properties` hoặc `application.yml`.
- [ ] Tạo các Class Entity định nghĩa Database:
  - `User`, `Role`, `TokenBlacklist`
  - `KycProfile`, `Account`
  - `Transaction`, `AuditLog`
- [ ] Cấu hình Spring Security:
  - Bật tính năng stateless session.
  - Viết các class tiện ích JWT (Tạo, Xác thực, Lấy thông tin).
  - Viết JWT Filter để chặn và kiểm tra token.
- [ ] Triển khai Use Case:
  - **UC-01:** Xây dựng API Đăng nhập (`POST /api/auth/login`) trả về chuỗi JWT.
  - **UC-03:** Xây dựng API Đăng xuất (`POST /api/auth/logout`) và đưa token vào TokenBlacklist.
