# Day 2: Quản lý người dùng & Tích hợp Cloud (eKYC)

## Mục tiêu (Objectives)
Xây dựng các chức năng liên quan đến quản trị khách hàng và định danh điện tử bằng hình ảnh.

## Các nhiệm vụ (Tasks)
- [ ] **UC-02: Quản trị danh mục Khách hàng**
  - Viết API `GET /api/v1/users` và `PUT /api/v1/users/{id}` dành cho Admin và Staff.
  - Bắt buộc áp dụng JPQL Constructor Projection (`new DTO(...)`) để trả về `Page<UserResponseDto>` nhằm tối ưu RAM.
- [ ] **UC-05: Định danh điện tử (eKYC) & Tích hợp lưu trữ đám mây**
  - Đăng ký và cấu hình Cloudinary / AWS S3 SDK.
  - Viết API `POST /api/v1/kyc/upload` để upload ảnh CCCD.
  - Xử lý lưu URL trả về vào Entity `KycProfile` và cập nhật trạng thái `PENDING`.
