# Danh sách các API và Endpoint còn thiếu (FE Required)

Dưới đây là danh sách các API và endpoint hiện đang được frontend (FE) gọi hoặc mong đợi nhưng hiện chưa được implement hoặc thiếu logic ở backend (BE). Danh sách này được trích xuất từ các bình luận `missing in BE` trong mã nguồn frontend.

## 1. Vouchers (voucherService.ts)
- **`GET /api/v1/business/vouchers`**: API để Admin quản lý danh sách khuyến mãi/voucher.
- **`GET /api/v1/vouchers`**: API công khai (Public endpoint) để khách hàng xem các voucher khả dụng.

## 2. Users / Staff (userService.ts)
- **`GET /api/v1/business/staff`** (hoặc tương đương): API để Admin xem danh sách nhân viên.
- **`PUT /api/v1/business/users/{id}`** (hoặc tương đương): API để Admin cập nhật thông tin chi tiết của một người dùng bất kỳ (hiện tại chưa có endpoint cho Admin thực hiện việc này).

## 3. Products (productService.ts)
- **`GET /api/v1/business/products`**: API dành cho Admin để lấy danh sách sản phẩm (hỗ trợ các filter dành riêng cho admin). Hiện tại FE đang phải gọi tạm API public `GET /products` để dùng cho trang quản lý.
- **`PUT /api/v1/business/products/{id}`**: API để cập nhật thông tin chung của sản phẩm (Tên, mô tả, danh mục, v.v). (BE hiện chỉ hỗ trợ cập nhật price và stock thông qua PATCH, thiếu cập nhật thông tin general).

## 4. Payments (paymentService.ts)
- **`GET /api/v1/payments/methods`**: API để lấy danh sách các phương thức thanh toán khả dụng. Hiện tại không có endpoint nào trả về danh sách này cho trang Checkout.

## 5. Orders (orderService.ts)
- **`POST /api/v1/orders/{id}/repay`**: API để thực hiện thanh toán lại (repay) cho một đơn hàng đã tạo nhưng thanh toán bị lỗi hoặc bị hủy thanh toán trước đó.

## 6. Dashboard / Reports (dashboardService.ts)
- **`GET /api/v1/reports/category-distribution`**: API báo cáo phân bổ theo danh mục sản phẩm (hiện tại FE đang dùng fallback).
- **`GET /api/v1/reports`**: Các endpoint báo cáo tổng hợp dành cho Dashboard hiện chưa đầy đủ.

## 7. Categories (categoryService.ts)
- **`GET /api/v1/categories`**: API Public để lấy danh sách danh mục cho khách hàng. Hiện tại FE đang phải gọi tạm endpoint của Admin (`GET /business/categories`).

## 8. Cart (cartService.ts)
- **`GET /api/v1/cart`**: API lấy thông tin giỏ hàng hiện tại của user.
- **`DELETE /api/v1/cart`**: API để clear (xóa toàn bộ) giỏ hàng hiện tại.

## 9. Address (addressService.ts)
- **`GET /api/v1/addresses`**: API lấy danh sách địa chỉ giao hàng của user.
- **`PUT /api/v1/addresses/{id}`**: API cập nhật một địa chỉ giao hàng.
- **`DELETE /api/v1/addresses/{id}`**: API xóa một địa chỉ giao hàng.

---

*Lưu ý: Các trường dữ liệu còn thiếu trong DTO như `rating`, `parentId`, `shippingFee`, `paymentMethodName`, `dateOfBirth`, `avatarUrl` đã được bổ sung thành công ở BE trong phiên làm việc trước đó.*
