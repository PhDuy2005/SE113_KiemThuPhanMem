# Danh sách 29 Tính năng cốt lõi cần viết Unit Test

Đây là danh sách các phương thức chứa logic nghiệp vụ quan trọng cần được bao phủ 100% condition coverage. Các file test case sẽ được lưu trong `workspace_3/unit-test/1_Official/`.

## 1. Module Xác thực & Người dùng (`UserService`)
- [x] 1. `handleLogin`: Xử lý đăng nhập.
- [x] 2. `register`: Đăng ký tài khoản mới.
- [x] 3. `verifyRegistration`: Xác thực tài khoản bằng token qua email.
- [x] 4. `forgotPassword`: Xử lý yêu cầu quên mật khẩu.
- [x] 5. `resetPassword`: Đổi mật khẩu mới từ token.
- [x] 6. `changeCurrentUserPassword`: Đổi mật khẩu tài khoản đang đăng nhập.
- [x] 7. `updateCurrentUserProfile`: Cập nhật thông tin cá nhân.

## 2. Module Sản phẩm (`ProductManagementService`)
- [x] 8. `createProduct`: Thêm sản phẩm mới.
- [x] 9. `updateProduct`: Cập nhật thông tin chung của sản phẩm.
- [x] 10. `updatePrice`: Thay đổi giá sản phẩm.
- [x] 11. `updateStock`: Cập nhật số lượng tồn kho.
- [x] 12. `discontinueProduct`: Ngừng kinh doanh một sản phẩm.

## 3. Module Danh mục (`CategoryService`)
- [x] 13. `createCategory`: Tạo danh mục sản phẩm mới.
- [x] 14. `deleteCategory`: Xóa danh mục (kèm theo xử lý replacementCategoryId).

## 4. Module Giỏ hàng (`CartService`)
- [x] 15. `addItem`: Thêm sản phẩm vào giỏ hàng. *(Đã có bản nháp 10_addItem.md)*
- [x] 16. `removeItem`: Xóa 1 sản phẩm khỏi giỏ hàng. *(Đã có bản nháp 11_removeItem.md)*
- [x] 17. `updateItemQuantity`: Cập nhật số lượng của 1 sản phẩm trong giỏ.
- [x] 18. `clearCart`: Dọn sạch toàn bộ giỏ hàng.

## 5. Module Khuyến mãi (`VoucherManagementService`)
- [x] 19. `createVoucher`: Tạo mã giảm giá mới.
- [x] 20. `emergencyStopVoucher`: Dừng khẩn cấp một mã giảm giá.

## 6. Module Thanh toán (`CheckoutService`)
- [x] 21. `calculateSelection`: Tính toán tổng tiền, phí ship.
- [x] 22. `applyVoucher`: Áp dụng mã giảm giá.
- [x] 23. `confirmOrder`: Xác nhận tiến hành đặt hàng.

## 7. Module Đơn hàng (`OrderService`)
- [x] 24. `approveOrder`: Nhân viên xác nhận/duyệt đơn hàng.
- [x] 25. `updateShippingStatus`: Nhân viên cập nhật trạng thái vận chuyển.
- [x] 26. `markOrderDelivered`: Đánh dấu đơn hàng đã giao thành công.
- [x] 27. `cancelPendingOrder`: Khách hàng tự hủy đơn hàng đang chờ duyệt.

## 8. Module Quản trị Nhân viên (`StaffManagementService`)
- [x] 28. `createStaff`: Admin tạo tài khoản nhân viên mới.
- [x] 29. `lockStaff`: Khóa tài khoản của một nhân viên.
