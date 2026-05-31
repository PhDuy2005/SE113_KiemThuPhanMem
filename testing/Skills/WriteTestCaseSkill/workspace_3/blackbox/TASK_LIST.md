# Danh sách 25 Chức năng (Use Case) cốt lõi cho Black-box UI Testing

Dựa trên tài liệu SRS, đây là 25 module cốt lõi có giao diện tương tác người dùng quan trọng nhất, bao phủ toàn bộ luồng từ khách hàng đến quản trị viên, cần được ưu tiên viết UI Test:

## 1. Module Xác thực & Tài khoản (Authentication & Profile)
- [x] 1. **UC-01 - Đăng ký (Sign Up)**: Test form đăng ký, validate định dạng email/password.
- [ ] 2. **UC-02 - Đăng nhập (Sign In)**: Test form đăng nhập, giới hạn số lần sai, hiển thị lỗi.
- [ ] 3. **UC-03 - Quên mật khẩu (Forget Password)**: Test luồng gửi email khôi phục.
- [ ] 4. **UC-04 - Cập nhật thông tin cá nhân**: Test form cập nhật User Profile.
- [ ] 5. **UC-05 - Chủ động đổi mật khẩu**: Test form đổi mật khẩu với pass cũ/mới.

## 2. Module Sản phẩm & Mua sắm (Shopping & Cart)
- [ ] 6. **UC-09 - Tìm kiếm sản phẩm bằng từ khóa**: Test thanh tìm kiếm, hiển thị kết quả.
- [ ] 7. **UC-10 - Lọc theo danh mục**: Test các checkbox/dropdown lọc sản phẩm.
- [ ] 8. **UC-12 - Xem thông tin chi tiết của sản phẩm**: Test giao diện chi tiết, hiển thị giá cả.
- [ ] 9. **UC-14 - Thêm sản phẩm vào giỏ hàng**: Test nút Add to Cart và thông báo phản hồi.
- [ ] 10. **UC-15 - Thay đổi số lượng trong giỏ**: Test nút tăng/giảm, nhập tay (min/max) số lượng.
- [ ] 11. **UC-16 - Xóa sản phẩm khỏi giỏ hàng**: Test popup xác nhận xóa.

## 3. Module Thanh toán & Đơn hàng (Checkout & Order - Customer)
- [ ] 12. **UC-08 - Thêm địa chỉ giao hàng mới**: Test form nhập liệu địa chỉ, số điện thoại.
- [ ] 13. **UC-18 - Nhập mã giảm giá**: Test validate voucher hợp lệ/sai/hết hạn.
- [ ] 14. **UC-20 - Xác nhận đơn hàng**: Test màn hình review tổng tiền, phí ship và tiến hành đặt hàng.
- [ ] 15. **UC-25 - Hủy đơn hàng trạng thái "Pending"**: Test quyền thao tác hủy đơn.
- [ ] 16. **UC-27 - Đánh giá sản phẩm đã mua**: Test form rating (sao) và text comment.

## 4. Module Quản lý Đơn hàng (Order Management - Admin/Staff)
- [ ] 17. **UC-31 - Duyệt đơn hàng**: Test giao diện xác nhận duyệt đơn của Staff.
- [ ] 18. **UC-32 - Cập nhật trạng thái giao hàng**: Test dropdown cập nhật status Shipping/Delivered.
- [ ] 19. **UC-34 - Hủy đơn hàng**: Test quyền hủy đơn có ghi chú lý do.

## 5. Module Quản trị Hệ thống (Catalog & Admin)
- [ ] 20. **UC-40 - Thêm danh mục sản phẩm mới**: Test form tạo Category.
- [ ] 21. **UC-41 - Sửa hoặc xóa danh mục**: Test luồng xác nhận/cảnh báo khi xóa.
- [ ] 22. **UC-42 - Đăng sản phẩm mới**: Test form nhập liệu phức tạp (upload ảnh, giá, kho, mô tả).
- [ ] 23. **UC-43 - Cập nhật giá sản phẩm**: Test rule nhập giá tiền hợp lệ.
- [ ] 24. **UC-46 - Tạo voucher kèm điều kiện**: Test form tạo mã với rule (min order, max discount).
- [ ] 25. **UC-48 - Tạo tài khoản nhân viên mới**: Test giao diện phân quyền.
