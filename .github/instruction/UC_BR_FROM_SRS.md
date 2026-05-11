# Danh sách Use Case và Business Rule từ SRS TechSales

> Nguồn: `TechSaleManagement SRS.docx`.

- Tổng số Use Case trích xuất: **61**
- Tổng số Business Rule trích xuất: **330**
- Ghi chú: Mã BR trong SRS có thể được đánh lại từ BR01 ở từng use case hoặc đánh số liên tục tùy phần tài liệu. File này giữ nguyên mã theo SRS.

## Mục lục UC

- [UC-01 - Đăng ký (Sign Up)](#uc-01-đăng-ký-sign-up) — 8 BR
- [UC-02 - Đăng nhập (Sign In)](#uc-02-đăng-nhập-sign-in) — 8 BR
- [UC-03 - Quên mật khẩu (Forget Password)](#uc-03-quên-mật-khẩu-forget-password) — 12 BR
- [UC-04 - Cập nhật thông tin cá nhân](#uc-04-cập-nhật-thông-tin-cá-nhân) — 7 BR
- [UC-05 - Chủ động đổi mật khẩu](#uc-05-chủ-động-đổi-mật-khẩu) — 7 BR
- [UC-06 - Đặt địa chỉ mặc định](#uc-06-đặt-địa-chỉ-mặc-định) — 2 BR
- [UC-07 - Cập nhật địa chỉ mặc định](#uc-07-cập-nhật-địa-chỉ-mặc-định) — 5 BR
- [UC-08 - Thêm địa chỉ giao hàng mới](#uc-08-thêm-địa-chỉ-giao-hàng-mới) — 5 BR
- [UC-09 - Tìm kiếm sản phẩm bằng từ khóa](#uc-09-tìm-kiếm-sản-phẩm-bằng-từ-khóa) — 4 BR
- [UC-10 - Lọc theo danh mục](#uc-10-lọc-theo-danh-mục) — 5 BR
- [UC-11 - Sắp xếp sản phẩm theo giá](#uc-11-sắp-xếp-sản-phẩm-theo-giá) — 2 BR
- [UC-12 - Xem thông tin chi tiết của sản phẩm](#uc-12-xem-thông-tin-chi-tiết-của-sản-phẩm) — 4 BR
- [UC-13 - Kiểm tra trạng thái tồn kho](#uc-13-kiểm-tra-trạng-thái-tồn-kho) — 3 BR
- [UC-14 - Thêm sản phẩm vào giỏ hàng](#uc-14-thêm-sản-phẩm-vào-giỏ-hàng) — 4 BR
- [UC-15 - Thay đổi số lượng trong giỏ](#uc-15-thay-đổi-số-lượng-trong-giỏ) — 6 BR
- [UC-16 - Xóa sản phẩm khỏi giỏ hàng](#uc-16-xóa-sản-phẩm-khỏi-giỏ-hàng) — 4 BR
- [UC-17 - Chọn mặt hàng để thanh toán từ giỏ hàng](#uc-17-chọn-mặt-hàng-để-thanh-toán-từ-giỏ-hàng) — 5 BR
- [UC-18 - Nhập mã giảm giá](#uc-18-nhập-mã-giảm-giá) — 5 BR
- [UC-19 - Chọn phương thức thanh toán](#uc-19-chọn-phương-thức-thanh-toán) — 3 BR
- [UC-20 - Xác nhận đơn hàng](#uc-20-xác-nhận-đơn-hàng) — 7 BR
- [UC-61 - Thanh toán trực tuyến](#uc-61-thanh-toán-trực-tuyến) — 9 BR
- [UC-21 - Nhận email xác nhận đơn hàng](#uc-21-nhận-email-xác-nhận-đơn-hàng) — 5 BR
- [UC-22 - Xem lịch sử đơn hàng](#uc-22-xem-lịch-sử-đơn-hàng) — 4 BR
- [UC-23 - Xem chi tiết đơn hàng cũ](#uc-23-xem-chi-tiết-đơn-hàng-cũ) — 4 BR
- [UC-24 - Theo dõi trạng thái đơn hàng](#uc-24-theo-dõi-trạng-thái-đơn-hàng) — 4 BR
- [UC-25 - Hủy đơn hàng trạng thái "Pending"](#uc-25-hủy-đơn-hàng-trạng-thái-pending) — 7 BR
- [UC-26 - Thay đổi địa chỉ giao hàng](#uc-26-thay-đổi-địa-chỉ-giao-hàng) — 6 BR
- [UC-27 - Đánh giá sản phẩm đã mua](#uc-27-đánh-giá-sản-phẩm-đã-mua) — 5 BR
- [UC-28 - Đọc đánh giá từ người khác](#uc-28-đọc-đánh-giá-từ-người-khác) — 5 BR
- [UC-29 - Xem danh sách đơn hàng "Pending"](#uc-29-xem-danh-sách-đơn-hàng-pending) — 4 BR
- [UC-30 - Xem chi tiết đơn hàng](#uc-30-xem-chi-tiết-đơn-hàng) — 3 BR
- [UC-31 - Duyệt đơn hàng](#uc-31-duyệt-đơn-hàng) — 5 BR
- [UC-32 - Cập nhật trạng thái giao hàng](#uc-32-cập-nhật-trạng-thái-giao-hàng) — 5 BR
- [UC-33 - Xác nhận trạng thái "Delivered"](#uc-33-xác-nhận-trạng-thái-delivered) — 4 BR
- [UC-34 - Hủy đơn hàng](#uc-34-hủy-đơn-hàng) — 9 BR
- [UC-35 - Khởi tạo hoàn tiền](#uc-35-khởi-tạo-hoàn-tiền) — 8 BR
- [UC-36 - Tìm kiếm đơn hàng](#uc-36-tìm-kiếm-đơn-hàng) — 4 BR
- [UC-37 - Xem phản hồi mới nhất](#uc-37-xem-phản-hồi-mới-nhất) — 5 BR
- [UC-38 - Phản hồi đánh giá](#uc-38-phản-hồi-đánh-giá) — 5 BR
- [UC-39 - Ẩn đánh giá vi phạm](#uc-39-ẩn-đánh-giá-vi-phạm) — 6 BR
- [UC-40 - Thêm danh mục sản phẩm mới](#uc-40-thêm-danh-mục-sản-phẩm-mới) — 6 BR
- [UC-41 - Sửa hoặc xóa danh mục](#uc-41-sửa-hoặc-xóa-danh-mục) — 8 BR
- [UC-42 - Đăng sản phẩm mới](#uc-42-đăng-sản-phẩm-mới) — 7 BR
- [UC-43 - Cập nhật giá sản phẩm](#uc-43-cập-nhật-giá-sản-phẩm) — 5 BR
- [UC-44 - Cập nhật số lượng tồn kho](#uc-44-cập-nhật-số-lượng-tồn-kho) — 6 BR
- [UC-45 - Ngừng kinh doanh sản phẩm](#uc-45-ngừng-kinh-doanh-sản-phẩm) — 6 BR
- [UC-46 - Tạo voucher kèm điều kiện](#uc-46-tạo-voucher-kèm-điều-kiện) — 6 BR
- [UC-47 - Dừng voucher khẩn cấp](#uc-47-dừng-voucher-khẩn-cấp) — 5 BR
- [UC-48 - Tạo tài khoản nhân viên mới](#uc-48-tạo-tài-khoản-nhân-viên-mới) — 6 BR
- [UC-49 - Khóa tài khoản của nhân viên cũ](#uc-49-khóa-tài-khoản-của-nhân-viên-cũ) — 6 BR
- [UC-50 - Quản lý danh sách khách hàng](#uc-50-quản-lý-danh-sách-khách-hàng) — 4 BR
- [UC-51 - Chặn tài khoản đặt hàng giả mạo](#uc-51-chặn-tài-khoản-đặt-hàng-giả-mạo) — 6 BR
- [UC-52 - Xem biểu đồ doanh thu theo thời gian](#uc-52-xem-biểu-đồ-doanh-thu-theo-thời-gian) — 6 BR
- [UC-53 - Xem báo cáo sản phẩm bán chạy](#uc-53-xem-báo-cáo-sản-phẩm-bán-chạy) — 6 BR
- [UC-54 - Xuất đơn hàng ra Excel](#uc-54-xuất-đơn-hàng-ra-excel) — 6 BR
- [UC-55 - Cấu hình phí giao hàng](#uc-55-cấu-hình-phí-giao-hàng) — 6 BR
- [UC-56 - Định nghĩa vai trò người dùng](#uc-56-định-nghĩa-vai-trò-người-dùng) — 6 BR
- [UC-57 - Tạo tài khoản Business Admin ban đầu](#uc-57-tạo-tài-khoản-business-admin-ban-đầu) — 5 BR
- [UC-58 - Giám sát Audit Log](#uc-58-giám-sát-audit-log) — 5 BR
- [UC-59 - Bật chế độ bảo trì và tự động sao lưu](#uc-59-bật-chế-độ-bảo-trì-và-tự-động-sao-lưu) — 6 BR
- [UC-60 - Cập nhật cấu hình hệ thống không bảo mật](#uc-60-cập-nhật-cấu-hình-hệ-thống-không-bảo-mật) — 0 BR

---

## Chi tiết UC và BR

### UC-01 - Đăng ký (Sign Up)

#### BR01 — Activity (2)

Loading Rules:
Tải màn hình signUpScreen.
Yêu cầu các trường dữ liệu: [email], [password], [confirmPassword].

#### BR02 — Activity (4)

Validate Format Rules:
1. If any in [email], [password], [confirmPassword] is empty then returns 400-BAD_REQUEST error with MSG1.
2. If pattern.compile('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$').notMatch([email]) then returns 400-BAD_REQUEST error with MSG2.
3. If pattern.compile('^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\S+$).{8,}$').notMatch([password]) then returns 400-BAD_REQUEST error with MSG3.
4. If [password] != [confirmPassword] then returns 400-BAD_REQUEST error with MSG4.

#### BR03 — Activity (5)

Message Rules:
Hiển thị thông báo lỗi định dạng không hợp lệ tương ứng: MSG1, MSG2, MSG3, hoặc MSG4.

#### BR04 — Activity (6)

Check Existence Rules:
1. [existingUser] = UserRepository.findByEmail([email])
2. If [existingUser] != null then returns 400-BAD_REQUEST error with MSG5.

#### BR05 — Activity (7)

Message Rules:
Hiển thị thông báo lỗi tài khoản đã tồn tại: MSG5.

#### BR06 — Activity (8)

Saving Rules:
[hashedPassword] = hash([password])
[newUser] = UserRepository.createNewUser()
[newUser.email] = [email]
[newUser.password] = [hashedPassword]
[newUser.status] = 'PENDING'
UserRepository.save([newUser])

#### BR07 — Activity (9)

Email Template & Message Rules:
1. Email được gửi với mẫu sau:
From | techsales@gmail.com
To | [email]
Cc | N/A
Subject | Lấy [Subject] của “Email Template” với [Keyword] = “Sign Up”
Body | Lấy [Body] của “Email Template” với [Keyword] = “Sign Up”
- Ví dụ:
Subject | "Verify Registration TechSales Account"
Body | [Body] = “Welcome,” | [Body] = [Body] + 1 space lines | [Body] = [Body] + “Follow this link to verify your email address to finish your registration step.” | [Body] = [Body] + 1 space lines | [Body] = [Body] + <Link to verify email> | [Body] = [Body] + 1 space lines | [Body] = [Body] + "Thanks." | [Body] = [Body] + 1 space lines | [Body] = [Body] + "The TechSale team"
2. Hiển thị thông báo yêu cầu kiểm tra email: MSG6.

#### BR08 — Activity (11)

Activation & Message Rules:
Khi người dùng nhấn vào [verificationLink]:
1. [isValid] = verifyToken([verificationToken])
2. If [isValid] == true then
- [user.status] = 'ACTIVE'
- UserRepository.save([user])
- returns 200-OK response with MSG7.
- Hiển thị thông báo đăng ký thành công: MSG7.
3. Else
- returns 400-BAD_REQUEST error with MSG8.
- Hiển thị thông báo lỗi: MSG8.

---

### UC-02 - Đăng nhập (Sign In)

#### BR01 — Activity (1)

Loading Rules:
Tải màn hình signInScreen.
Hệ thống yêu cầu các trường dữ liệu: [email], [password].

#### BR02 — Activity (2)

Validate Format Rules:
1. If any in [email], [password] is empty then returns 400-BAD_REQUEST error with MSG1.
2. If pattern.compile('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$').notMatch([email]) then returns 400-BAD_REQUEST error with MSG2.

#### BR03 — Activity (3)

Message Rules:
Hiển thị thông báo lỗi định dạng không hợp lệ tương ứng: MSG1 hoặc MSG2.

#### BR04 — Activity (4)

Check Credentials Rules:
1. [user] = UserRepository.findByEmail([email])
2. [hashedPassword] = hash([password])
3. If [user] == null OR [user.password] != [hashedPassword] then proceeds to Activity (5)
4. Else proceeds to Activity (8)

#### BR05 — Activity (5)

Check Failed Attempts Rules:
(Luồng xử lý khi sai thông tin đăng nhập)
1. [user.failedLoginAttempts] = [user.failedLoginAttempts] + 1
2. UserRepository.save([user])
3. If [user.failedLoginAttempts] >= 5 then returns 403-FORBIDDEN error with MSG9.
4. Else returns 401-UNAUTHORIZED error with MSG10.

#### BR06 — Activity (6)

Message Rules (Account Locked):
Hiển thị thông báo tài khoản bị khóa tạm thời: MSG9.

#### BR07 — Activity (7)

Message Rules (Wrong Credentials):
Hiển thị thông báo sai thông tin tài khoản hoặc mật khẩu: MSG10.

#### BR08 — Activity (8)

Success & Redirect Rules:
(Luồng xử lý khi thông tin chính xác)
1. [user.failedLoginAttempts] = 0
2. [sessionToken] = generateJWT([user.id])
3. UserRepository.save([user])
4. returns 200-OK response with MSG11.
5. Message & Redirect Rules: Hiển thị thông báo đăng nhập thành công MSG11 và chuyển hướng người dùng về trang chủ.

---

### UC-03 - Quên mật khẩu (Forget Password)

#### BR01 — Activity (1)

Loading Rules:
Tải màn hình forgotPasswordScreen.
Hệ thống yêu cầu trường dữ liệu: [email].

#### BR02 — Activity (2)

Check Existence Rules:
1. [user] = UserRepository.findByEmail([email])
2. If [user] == null then returns 404-NOT_FOUND error with MSG12.

#### BR03 — Activity (3)

Message Rules (Not Found):
Hiển thị thông báo lỗi email không tồn tại trên giao diện: MSG12.

#### BR04 — Activity (4)

Email Template & Success Rules:
(Luồng khi email tồn tại)
1. Gửi email theo mẫu:
From | techsales@gmail.com
To | [email]
Cc | N/A
Subject | Lấy [Subject] của “Email Template” với [Keyword] = “Reset_Password”
Body | Lấy [Body] của “Email Template” với [Keyword] = “Reset_Password”
- Ví dụ:
[fullName] = UserProfile.getFullNameOfUserWithEmail([email])
Subject | "Verify Registration TechSales Account"
Body | [Body] = “Dear ” + [FullName] + “,” | [Body] = [Body] + 1 space lines | [Body] = [Body] + “Follow this link to verify your email address to finish your registration step.” | [Body] = [Body] + 1 space lines | [Body] = [Body] + <Link to verify email> | [Body] = [Body] + 1 space lines | [Body] = [Body] + "Thanks." | [Body] = [Body] + 1 space lines | [Body] = [Body] + "The TechSale team"
2. Message Rules: Hiển thị thông báo đã gửi email khôi phục: MSG13.

#### BR05 — Activity (5)

Trigger Rules:
Hệ thống nhận request từ khách hàng khi truy cập vào [resetLink] từ email.

#### BR06 — Activity (6)

Validate Token Rules:
1. [isValid] = verifyToken([resetToken])
2. If [isValid] == false OR isExpired([resetToken]) then returns 400-BAD_REQUEST error with MSG8.

#### BR07 — Activity (7)

Message Rules (Invalid Link):
Hiển thị thông báo lỗi liên kết không hợp lệ hoặc đã hết hạn: MSG8.

#### BR08 — Activity (8)

Loading Rules:
(Luồng khi token hợp lệ)
Tải màn hình resetPasswordScreen.
Hệ thống yêu cầu nhập: [newPassword], [confirmPassword].

#### BR09 — Activity (9)

Input Rules:
Người dùng nhập mật khẩu mới và xác nhận lưu.

#### BR10 — Activity (10)

Validate Password Rules:
1. If pattern.compile('^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\S+$).{8,}$').notMatch([newPassword]) then returns 400-BAD_REQUEST error with MSG3.
2. If [newPassword] != [confirmPassword] then returns 400-BAD_REQUEST error with MSG4.

#### BR11 — Activity (11)

Message Rules (Invalid Password):
Hiển thị thông báo lỗi mật khẩu không khớp hoặc sai định dạng tương ứng: MSG3 hoặc MSG4.

#### BR12 — Activity (12)

Update & Message Rules:
(Luồng khi mật khẩu hợp lệ)
1. [hashedPassword] = hash([newPassword])
2. [user.password] = [hashedPassword]
3. UserRepository.save([user])
4. returns 200-OK response with MSG14.
5. Message Rules: Cập nhật mật khẩu mới và hiển thị thông báo thành công: MSG14.

---

### UC-04 - Cập nhật thông tin cá nhân

#### BR13 — Activity (1) & (2)

Loading Rules:
1. Hệ thống lấy thông tin người dùng hiện tại: [user] = getCurrentUserProfile().
2. Tải màn hình editProfileScreen và hiển thị biểu mẫu với dữ liệu hiện tại vào các trường: [fullName], [phoneNumber].

#### BR14 — Activity (3)

Input Rules:
Khách hàng sửa đổi thông tin [fullName], [phoneNumber] và nhấn nút "Save".

#### BR15 — Activity (4)

Confirmation Rules:
Hệ thống yêu cầu xác nhận lưu thay đổi: Hiển thị hộp thoại xác nhận MSG15.

#### BR16 — Activity (7)

Validate Data Rules:
Khi khách hàng chọn "Đồng ý" (Confirm):
1. If [fullName] is empty then returns 400-BAD_REQUEST error with MSG1.
2. If pattern.compile('^[0-9]{10,11}$').notMatch([phoneNumber]) then returns 400-BAD_REQUEST error with MSG16.

#### BR17 — Activity (8)

Message Rules (Invalid Data):
Hiển thị cảnh báo lỗi dữ liệu tương ứng trên giao diện: MSG1 hoặc MSG16.

#### BR18 — Activity (9)

Update Rules:
(Luồng khi dữ liệu hợp lệ)
1. [user.fullName] = [fullName]
2. [user.phoneNumber] = [phoneNumber]
3. UserProfileRepository.save([user])
4. returns 200-OK response with MSG17.

#### BR19 — Activity (9)

Message Rules (Success):
Hiển thị thông báo cập nhật thông tin thành công trên giao diện: MSG17.

---

### UC-05 - Chủ động đổi mật khẩu

#### BR20 — Activity (2)

Loading Rules:
Tải màn hình changePasswordScreen và hiển thị form cập nhật mật khẩu.
Hệ thống yêu cầu các trường: [currentPassword], [newPassword], [confirmPassword].

#### BR21 — Activity (5)

Check Current Password Rules:
(Luồng khi người dùng nhấn "Update")
1. [user] = getCurrentUser()
2. [hashedCurrentPassword] = hash([currentPassword])
3. If [user.password] != [hashedCurrentPassword] then returns 400-BAD_REQUEST error with MSG18.

#### BR22 — Activity (10)

Message Rules (Wrong Current Password):
Hiển thị thông báo lỗi mật khẩu hiện tại không chính xác: MSG18.

#### BR23 — Activity (6)

Validate New Password Rules:
1. If any in [currentPassword], [newPassword], [confirmPassword] is empty then returns 400-BAD_REQUEST error with MSG1.
2. If [newPassword] == [currentPassword] then returns 400-BAD_REQUEST error with MSG19.
3. If pattern.compile('^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\S+$).{8,}$').notMatch([newPassword]) then returns 400-BAD_REQUEST error with MSG3.
4. If [newPassword] != [confirmPassword] then returns 400-BAD_REQUEST error with MSG4.

#### BR24 — Activity (9)

Message Rules (Invalid New Password):
Hiển thị thông báo lỗi mật khẩu mới không hợp lệ hoặc trùng mật khẩu cũ tương ứng: MSG1, MSG3, MSG4, hoặc MSG19.

#### BR25 — Activity (7)

Update Rules:
(Luồng khi mật khẩu hợp lệ)
1. [hashedNewPassword] = hash([newPassword])
2. [user.password] = [hashedNewPassword]
3. UserRepository.save([user])
4. returns 200-OK response with MSG20.

#### BR26 — Activity (8)

Message Rules (Success):
Hiển thị thông báo đổi mật khẩu thành công: MSG20.

---

### UC-06 - Đặt địa chỉ mặc định

#### BR27 — Activity (3) & (4)

Update Default Address Rules:
(Luồng xử lý khi người dùng chọn một địa chỉ và yêu cầu đặt làm mặc định)
1. [oldDefaultAddress] = AddressRepository.findDefaultAddress(getCurrentUser().id)
2. If [oldDefaultAddress] != null then [oldDefaultAddress.isDefault] = false
3. [selectedAddress] = AddressRepository.findById([selectedAddressId])
4. [selectedAddress.isDefault] = true
5. AddressRepository.save([oldDefaultAddress], [selectedAddress])
6. returns 200-OK response with MSG21.

#### BR28 — Activity (5)

Message Rules (Success):
Hiển thị thông báo thiết lập địa chỉ mặc định thành công trên giao diện: MSG21.

---

### UC-07 - Cập nhật địa chỉ mặc định

#### BR29 — Activity (2)

Loading Rules:
1. Hệ thống nhận yêu cầu khi người dùng nhấn "Edit" trên địa chỉ mặc định.
2. Tải màn hình editAddressScreen và hiển thị biểu mẫu điền sẵn dữ liệu hiện tại của địa chỉ đó.

#### BR30 — Activity (4)

Validate Data Rules:
(Luồng xử lý khi người dùng sửa đổi thông tin và nhấn "Save")
1. Kiểm tra định dạng dữ liệu đầu vào.
2. If anyIsEmpty([houseNumber], [ward], [province]) then returns 400-BAD_REQUEST error with MSG1.

#### BR31 — Activity (7)

Message Rules (Invalid Data):
Nếu dữ liệu không hợp lệ: Hệ thống trả về lỗi và hiển thị thông báo lỗi tương ứng là MSG1.

#### BR32 — Activity (5)

Update Rules:
(Luồng khi dữ liệu hợp lệ)
1. AddressRepository.save([address]).
2. returns 200-OK response with MSG22.

#### BR33 — Activity (6)

Message Rules (Success):
Hiển thị thông báo cập nhật địa chỉ thành công trên giao diện: MSG22.

---

### UC-08 - Thêm địa chỉ giao hàng mới

#### BR34 — Activity (2)

Loading Rules:
1. Hệ thống nhận yêu cầu khi người dùng nhấn chọn thêm địa chỉ mới.
2. Tải màn hình addAddressScreen và hiển thị biểu mẫu địa chỉ trống để người dùng nhập liệu.

#### BR35 — Activity (4)

Validate Data Rules:
(Luồng xử lý khi người dùng nhấn lưu)
1. Kiểm tra tính hợp lệ và định dạng của dữ liệu đầu vào.
2. If anyIsEmpty([houseNumber], [ward], [province]) then returns 400-BAD_REQUEST error with MSG1.

#### BR36 — Activity (7)

Message Rules (Invalid Data):
Nếu dữ liệu không hợp lệ: Hệ thống trả về lỗi và hiển thị thông báo cảnh báo các trường bỏ trống hoặc sai định dạng tương ứng là MSG1.

#### BR37 — Activity (5)

Storage Rules:
(Luồng khi dữ liệu hợp lệ)
1. Hệ thống thực hiện lưu thông tin địa chỉ mới vào CSDL và liên kết với tài khoản người dùng hiện tại.
2. AddressRepository.create([addressData])
3. returns 201-CREATED response with MSG23.

#### BR38 — Activity (6)

Message Rules (Success):
Hiển thị thông báo lưu địa chỉ thành công trên giao diện người dùng: MSG23.

---

### UC-09 - Tìm kiếm sản phẩm bằng từ khóa

#### BR39 — Activity (2)

Autocomplete Rules:
1. Hệ thống tiếp nhận chuỗi ký tự khi người dùng gõ từ khóa vào thanh tìm kiếm.
2. Truy vấn nhanh và cung cấp danh sách gợi ý tự động (Autocomplete) trên giao diện.

#### BR40 — Activity (4)

Search Query Rules:
(Luồng xử lý khi người dùng gửi yêu cầu tìm kiếm)
1. Hệ thống nhận từ khóa hoàn chỉnh và thực hiện truy vấn tìm các sản phẩm khớp.
2. [searchResults] = ProductRepository.searchByKeyword([keyword])
3. If [searchResults].isEmpty() then proceed to BR41 else proceed to BR42

#### BR41 — Activity (6)

Message Rules (No Result):
Nếu truy vấn không có sản phẩm nào khớp: Trả về trạng thái phản hồi hợp lệ (200-OK kèm mảng rỗng) và hiển thị thông báo không tìm thấy kết quả: MSG24.

#### BR42 — Activity (5)

Display Rules (Success):
Nếu truy vấn có sản phẩm khớp: Trả về dữ liệu và hiển thị danh sách sản phẩm phù hợp lên màn hình kết quả tìm kiếm searchResultsScreen.

---

### UC-10 - Lọc theo danh mục

#### BR43 — Activity (1) & (2)

Interaction Rules:
1. Hệ thống tiếp nhận hành động của người dùng khi tương tác với bộ lọc danh mục.
2. Xác định loại tương tác là tích chọn thêm hay bỏ tích để điều hướng luồng xử lý tương ứng.
3. Hệ thống nhận mã của các danh mục cần lọc [activeCategoryIds]

#### BR44 — Activity (3)

Add Filter Rules:
(Luồng tích chọn thêm)
Hệ thống xử lý thêm điều kiện lọc danh mục vừa chọn vào bộ tham số truy vấn hiện tại.

#### BR45 — Activity (4)

Remove Filter Rules:
(Luồng bỏ tích)
Hệ thống xử lý gỡ bỏ điều kiện lọc danh mục vừa bỏ chọn khỏi bộ tham số truy vấn hiện tại.

#### BR46 — Activity (5)

Query Rules:
Hệ thống thực hiện truy xuất cơ sở dữ liệu để tìm các sản phẩm khớp với tập hợp điều kiện lọc hiện tại. (ProductRepository.filterByCategory([activeCategoryIds]))

#### BR47 — Activity (6)

Display Rules:
Hệ thống tải lại dữ liệu và cập nhật danh sách sản phẩm trên giao diện theo đúng kết quả vừa truy xuất.

---

### UC-11 - Sắp xếp sản phẩm theo giá

#### BR48 — Activity (2)

Sort Processing Rules:
1. Hệ thống tiếp nhận tiêu chí sắp xếp theo giá từ yêu cầu của khách hàng: [sortOrder] (có thể là 'ASC' cho tăng dần hoặc 'DESC' cho giảm dần).
2. Thực hiện truy xuất và sắp xếp danh sách sản phẩm dựa trên tiêu chí hiện tại: [sortedProductList] = ProductRepository.getProductsSortedByPrice([sortOrder]).

#### BR49 — Activity (3)

Display Rules:
1. Hệ thống làm mới giao diện và hiển thị dữ liệu [sortedProductList] lên màn hình danh sách sản phẩm theo đúng thứ tự mới.

---

### UC-12 - Xem thông tin chi tiết của sản phẩm

#### BR50 — Activity (2)

Check Product Existence Rules:
1. Hệ thống tiếp nhận yêu cầu xem chi tiết với tham số [productId].
2. [product] = ProductRepository.findById([productId])
3. If [product] != null thì chuyển sang Activity (3).
4. Else chuyển sang Activity (5).

#### BR51 — Activity (3)

Check Stock Rules:
1. [stockStatus] = InventoryRepository.getStockStatus([productId])
2. Gắn thông tin tồn kho vào dữ liệu sản phẩm: [product.stockStatus] = [stockStatus].

#### BR52 — Activity (4)

Display Product Details Rules:
1. Trả về phản hồi 200-OK.
2. Tải màn hình productDetailsScreen và hiển thị toàn bộ thông tin [product] (bao gồm tên, hình ảnh, giá, mô tả, và [stockStatus]).

#### BR53 — Activity (5)

Message Rules (Not Found):
1. Trả về lỗi 404-NOT_FOUND.
2. Hiển thị thông báo không tìm thấy sản phẩm trên giao diện: MSG25.

---

### UC-13 - Kiểm tra trạng thái tồn kho

#### BR54 — Activity (2) & (3)

Stock Calculation Rules:
1. Hệ thống nhận yêu cầu kiểm tra tồn kho cho sản phẩm hiện tại với mã sản phẩm [productId].
2. [availableQuantity] = InventoryRepository.getRealTimeStock([productId]).
3. If [availableQuantity] > 0 thì chuyển sang Activity (4).
4. Else chuyển sang Activity (6).

#### BR55 — Activity (4) & (5)

In-Stock UI Rules:
1. Cập nhật trạng thái sản phẩm: [product.stockStatus] = 'IN_STOCK'.
2. Hiển thị nhãn trạng thái còn hàng trên giao diện.
3. Kích hoạt (enable) các nút mua sắm: [addToCartButton].setEnabled(true) và [buyNowButton].setEnabled(true).

#### BR56 — Activity (6) & (7)

Out-Of-Stock UI Rules:
1. Cập nhật trạng thái sản phẩm: [product.stockStatus] = 'OUT_OF_STOCK'.
2. Hiển thị thông báo/nhãn hết hàng trên giao diện: MSG26.
3. Vô hiệu hóa (disable) hoặc ẩn các nút mua sắm: [addToCartButton].setEnabled(false) và [buyNowButton].setEnabled(false).

---

### UC-14 - Thêm sản phẩm vào giỏ hàng

#### BR57 — Activity (2)

Quantity Validation Rules:
1. Hệ thống tiếp nhận mã sản phẩm [productId] và số lượng yêu cầu [requestedQuantity].
2. Lấy số lượng hàng khả dụng: [availableQuantity] = InventoryRepository.getRealTimeStock([productId]).
3. If [requestedQuantity] > [availableQuantity] thì chuyển sang Activity (3).
4. Else chuyển sang Activity (4).

#### BR58 — Activity (3)

Insufficient Stock Handling Rules:
1. Trả về thông báo lỗi MSG27 để cảnh báo khách hàng về việc thiếu hàng.
2. Tự động điều chỉnh số lượng trên giao diện về mức tối đa có thể đáp ứng: [inputQuantityField].setValue([availableQuantity]).

#### BR59 — Activity (4) & (5)

Cart Storage & Counter Rules:
1. Thêm sản phẩm vào giỏ hàng hiện tại của người dùng (trong Session hoặc Database): CartRepository.addToCart([productId], [requestedQuantity]).
2. Tính toán lại tổng số lượng trong giỏ hàng: [cartBadgeCount] = CartRepository.getTotalItemsCount().
3. Cập nhật số hiển thị trên biểu tượng giỏ hàng ở Header.

#### BR60 — Activity (6)

Success Notification Rules:
1. Trả về phản hồi thành công và hiển thị thông báo MSG28 trên giao diện.

---

### UC-15 - Thay đổi số lượng trong giỏ

#### BR62 — Activity (1)

Loading Rules:
1. Hệ thống tiếp nhận tương tác tại cartScreen.
2. Các trường dữ liệu yêu cầu từ client: [productId], [newQuantity], [oldQuantity].

#### BR63 — Activity (2)

Data Format Validation Rules:
1. Kiểm tra định dạng của [newQuantity].
2. If [newQuantity] không phải là số nguyên dương OR isEmpty([newQuantity]) then returns 400-BAD_REQUEST kèm MSG29.
3. Else chuyển sang Activity (4).

#### BR64 — Activity (3)

Message Rules (Invalid Format):
Hiển thị thông báo lỗi định dạng không hợp lệ: MSG29 và thực hiện [quantityInput].setValue([oldQuantity]) trên giao diện.

#### BR65 — Activity (4)

Stock Validation Rules:
1. Truy vấn tồn kho thực tế: [availableQuantity] = InventoryRepository.getRealTimeStock([productId]).
2. If [newQuantity] > [availableQuantity] then returns 400-BAD_REQUEST kèm MSG27.
3. Else chuyển sang Activity (6).

#### BR66 — Activity (5)

Insufficient Stock Handling Rules:
Hiển thị thông báo lỗi vượt quá tồn kho: MSG27. Đồng thời cập nhật CartRepository.updateQuantity([productId], [availableQuantity]) và hiển thị mức tối đa lên giao diện.

#### BR67 — Activity (6) & (7)

Update & Recalculate Rules:
1. CartRepository.updateQuantity([productId], [newQuantity]).
2. [totalCartPrice] = CartRepository.calculateTotal().
3. Returns 200-OK kèm dữ liệu giỏ hàng đã cập nhật.
4. Hệ thống cập nhật lại tổng tiền hiển thị trên cartScreen.

---

### UC-16 - Xóa sản phẩm khỏi giỏ hàng

#### BR68 — Activity (1) & (2)

Loading & Confirmation Rules:
1. Hệ thống tiếp nhận tương tác nhấn nút xóa tại cartScreen.
2. Trường dữ liệu yêu cầu từ client: [productId].
3. Hệ thống hiển thị hộp thoại xác nhận với nội dung: MSG30.

#### BR69 — Activity (3)

Decision Rules:
1. Nếu khách hàng chọn "Có" (Confirm): Chuyển sang Activity (4).
2. Nếu khách hàng chọn "Không" (Cancel): Chuyển sang Activity (7).

#### BR70 — Activity (4), (5) & (6)

Deletion & Recalculation Rules:
1. Hệ thống thực hiện xóa sản phẩm khỏi cơ sở dữ liệu: CartRepository.removeItem([productId]).
2. Tính toán lại tổng giá trị giỏ hàng: [totalCartPrice] = CartRepository.calculateTotal().
3. Trả về phản hồi 200-OK kèm thông báo thành công MSG31.
4. Cập nhật lại giao diện cartScreen với danh sách sản phẩm và tổng tiền mới.

#### BR71 — Activity (7)

Cancellation Rules:
1. Hệ thống đóng hộp thoại xác nhận.
2. Không thực hiện bất kỳ thay đổi nào đối với dữ liệu giỏ hàng trong CSDL.

---

### UC-17 - Chọn mặt hàng để thanh toán từ giỏ hàng

#### BR72 — Activity (1)

Loading Rules:
1. Hệ thống tiếp nhận tương tác tích chọn sản phẩm tại cartScreen.
2. Trường dữ liệu yêu cầu từ client: [selectedProductIds] (mảng chứa mã các sản phẩm được chọn).

#### BR73 — Activity (2)

Calculation Rules:
1. Hệ thống thực hiện tính toán tổng tiền dựa trên danh sách sản phẩm được chọn: [tempTotalPrice] = CartRepository.calculateSelectedItems([selectedProductIds]).
2. Trả về phản hồi 200-OK kèm giá trị [tempTotalPrice] để hiển thị thời gian thực trên giao diện.

#### BR74 — Activity (4)

Selection Validation Rules:
1. Khi khách hàng nhấn "Thanh toán", hệ thống kiểm tra danh sách đã chọn.
2. If [selectedProductIds].isEmpty() then returns 400-BAD_REQUEST kèm MSG32.
3. Else chuyển sang Activity (6).

#### BR75 — Activity (5)

Message Rules (Empty Selection):
Hiển thị thông báo lỗi yêu cầu chọn sản phẩm: MSG32.

#### BR76 — Activity (6) & (7)

Redirect Rules:
1. Ghi nhận danh sách sản phẩm vào phiên làm việc thanh toán: CheckoutSession.saveSelection([selectedProductIds]).
2. Trả về phản hồi 302-FOUND để chuyển hướng người dùng sang màn hình checkoutScreen.

---

### UC-18 - Nhập mã giảm giá

#### BR77 — Activity (1)

Loading Rules:
1. Hệ thống tiếp nhận tương tác tại màn hình checkoutScreen.
2. Trường dữ liệu yêu cầu từ client: [voucherCode].

#### BR78 — Activity (2)

Voucher Validation Rules:
1. Hệ thống thực hiện truy xuất thông tin: [voucher] = VoucherRepository.findByCode([voucherCode]).
2. Kiểm tra tính hợp lệ: Nếu [voucher] == null HOẶC [voucher].status != 'ACTIVE' HOẶC isExpired([voucher]) then returns 400-BAD_REQUEST kèm MSG33.
3. Else chuyển sang Activity (3).

#### BR79 — Activity (3) & (4)

Discount Calculation Rules:
1. Tính toán số tiền được giảm: [discountAmount] = calculateDiscountValue([voucher], [totalOrderAmount]).
2. Cập nhật lại tổng giá trị hóa đơn: [finalTotal] = [totalOrderAmount] - [discountAmount].
3. Trả về phản hồi 200-OK kèm dữ liệu [discountAmount] và [finalTotal] đã cập nhật.

#### BR80 — Activity (5)

Message Rules (Success):
Hiển thị thông báo áp dụng mã giảm giá thành công: MSG34.

#### BR81 — Activity (6)

Message Rules (Invalid Voucher):
Hiển thị thông báo lỗi mã không hợp lệ hoặc đã hết hạn: MSG33.

---

### UC-19 - Chọn phương thức thanh toán

#### BR82 — Activity (1)

Loading Rules:
1. Hệ thống tiếp nhận tương tác tại phần chọn phương thức thanh toán trên màn hình checkoutScreen.
2. Trường dữ liệu yêu cầu từ client: [paymentMethodId].

#### BR83 — Activity (2)

Instruction Retrieval Rules:
1. Hệ thống truy xuất thông tin: [paymentMethod] = PaymentRepository.findById([paymentMethodId]).
2. If [paymentMethod] == null then returns 400-BAD_REQUEST kèm MSG35.
3. Else return 200-OK, hiện hướng dẫn thanh toán lên màn hình

#### BR84 — Activity (3)

Session Persistence & Display Rules:
1. Lưu phương thức đã chọn vào phiên làm việc: CheckoutSession.savePaymentMethod([paymentMethod]).
2. Returns 200-OK

---

### UC-20 - Xác nhận đơn hàng

#### BR85 — Activity (1)

Loading Rules:
1. Hệ thống tiếp nhận yêu cầu chốt đơn tại checkoutScreen.
2. Các trường dữ liệu yêu cầu từ client: [selectedProductIds], [shippingAddressId], [paymentMethodId], [voucherCode] (nếu có).

#### BR86 — Activity (2)

Real-time Stock Check Rules:
1. Hệ thống duyệt qua danh sách [selectedProductIds] để kiểm tra tồn kho thực tế từng mặt hàng: [isAvailable] = InventoryRepository.checkBatchStock([selectedProductIds]).
2. Nếu có bất kỳ sản phẩm nào không đủ số lượng, returns 400-BAD_REQUEST kèm MSG36.
3. Else chuyển sang Activity (4).

#### BR87 — Activity (3)

Insufficient Stock Message Rules:
Hiển thị thông báo lỗi thiếu hàng và yêu cầu khách hàng kiểm tra lại giỏ hàng: MSG36.

#### BR88 — Activity (4) & (5)

Order Creation & Inventory Deduction Rules:
1. Thực hiện trừ số lượng trong kho: InventoryRepository.deductStock([selectedProductIds]).
2. Khởi tạo đơn hàng mới: [newOrder] = OrderRepository.createOrder([orderData]).
3. Trạng thái đơn hàng mặc định: [newOrder.status] = 'PENDING'.

#### BR89 — Activity (6) & (7)

Payment Processing Rules:
1. Kiểm tra [paymentMethodId].
2. Nếu phương thức là 'Online Payment' then gọi dịch vụ thanh toán: PaymentGateway.process([newOrder.totalAmount]).
3. Nếu thanh toán thất bại, trả về lỗi và dừng quy trình.

#### BR90 — Activity (8)

Cart Clearing Rules:
Sau khi tạo đơn hàng thành công, hệ thống thực hiện xóa các sản phẩm đã mua khỏi giỏ hàng của khách hàng: CartRepository.removeItems([selectedProductIds]).

#### BR91 — Activity (9)

Success Notification & Email Rules:
1. Trả về phản hồi 200-OK kèm MSG37.
2. Gửi email xác nhận đơn hàng (Chi tiết ở Usecase 21)

---

### UC-61 - Thanh toán trực tuyến

#### BR92 — Activity (1)

Loading Rules:
1. Hệ thống tiếp nhận yêu cầu thanh toán trực tuyến từ checkoutScreen.
2. Các trường dữ liệu yêu cầu từ client: [orderId], [paymentMethodId], [totalAmount].

#### BR93 — Activity (2)

Encryption & Checksum Rules:
1. Hệ thống khởi tạo yêu cầu giao dịch: [paymentRequest] = PaymentService.initialize([orderId], [totalAmount]).
2. Thực hiện mã hóa dữ liệu đơn hàng và tạo mã băm bảo mật: [checksum] = SHA512([paymentRequest.data] + [secretKey]).

#### BR94 — Activity (3) & (4)

Gateway Connection Rules:
1. Kiểm tra kết nối đến cổng thanh toán.
2. If kết nối thất bại then returns 503-SERVICE_UNAVAILABLE kèm MSG38.
3. Else chuyển sang Activity (5).

#### BR95 — Activity (5) & (6)

Redirection Rules:
1. Gửi yêu cầu kèm [checksum] sang cổng thanh toán.
2. Trả về phản hồi 302-FOUND kèm [paymentUrl] để điều hướng khách hàng sang giao diện của bên thứ ba.

#### BR96 — Activity (9) & (10)

Cancellation Handling Rules:
1. Tiếp nhận kết quả từ URL phản hồi (callback URL): [gatewayResponse.status] == 'CANCEL'.
2. Cập nhật trạng thái giao dịch trong CSDL: [transaction.status] = 'CANCELLED'.
3. Returns 200-OK kèm MSG39.

#### BR97 — Activity (14) & (16)

Failure Handling Rules:
1. Tiếp nhận kết quả từ URL phản hồi: [gatewayResponse.status] == 'FAILED'.
2. Cập nhật trạng thái giao dịch: [transaction.status] = 'FAILED'.
3. Returns 400-BAD_REQUEST kèm MSG40.

#### BR98 — Activity (18) & (19)

Success Verification Rules:
1. Tiếp nhận kết quả thành công từ URL phản hồi.
2. Hệ thống kiểm tra tính hợp lệ của chữ ký số (Digital Signature): [isValid] = SecurityService.verifySignature([gatewayResponse], [checksum]).
3. If [isValid] == false then returns 403-FORBIDDEN và ghi nhật ký cảnh báo bảo mật.

#### BR99 — Activity (20) & (21)

Data Persistence Rules:
1. Lưu trữ thông tin giao dịch thành công vào CSDL: TransactionRepository.save([gatewayResponse]).
2. Cập nhật trạng thái đơn hàng: [order.paymentStatus] = 'PAID' và [order.status] = 'CONFIRMED'.

#### BR100 — Activity (22) & (23)

Finalization Rules:
1. Kích hoạt quy trình gửi email thông báo xác nhận (UC-21).
2. Returns 200-OK kèm MSG41.
3. Điều hướng khách hàng về website và hiển thị thông báo thành công.

---

### UC-21 - Nhận email xác nhận đơn hàng

#### BR101 — Activity (2)

Loading Rules:
1. Hệ thống tiếp nhận sự kiện hoàn tất từ UC-20 hoặc UC-61.
2. Các nguồn dữ liệu cần thiết để tổng hợp: [orderId], [userEmail], [orderItems], [totalAmount], [shippingAddress], [paymentMethod].

#### BR102 — Activity (3)

Email Compilation Rules:
Hệ thống thực hiện gửi email theo mẫu:
From | techsales@gmail.com
To | [userEmail]
Cc | N/A
Subject | Lấy [Subject] của “Email Template” với [Keyword] = “Confirm_Order”
Body | Lấy [Body] của “Email Template” với [Keyword] = “Confirm_Order”
Ví dụ
[fullName] = UserProfileRepository.getFullNameByUserId([userId])
Subject | "Confirm your Order"
Body | [Body] = “Dear ” + [fullName] + “,” | [Body] = [Body] + 1 space lines | [Body] = [Body] + “You have just confirmed an order. This is detail” | [Body] = [Body] + [orderItems] | [Body] = [Body] + 1 space lines | [Body] = [Body] + “Total amount: “ + [totalAmount] | [Body] = [Body] + “The order will be shipped to “ + [shippingAddress] | [Body] = [Body] + 1 space lines | [Body] = [Body] + “Payment Method: “ + [paymentMethod] | [Body] = [Body] + "Thanks." | [Body] = [Body] + 1 space lines | [Body] = [Body] + "The TechSale team"

#### BR103 — Activity (4)

SMTP Connection Rules:
1. Hệ thống thực hiện kết nối tới dịch vụ gửi email: [connectionStatus] = SmtpService.connect().
2. If [connectionStatus] == SUCCESS then chuyển sang Activity (5).
3. Else chuyển sang Activity (6).

#### BR104 — Activity (5)

Email Dispatch Rules:
1. Thực hiện gửi email: SmtpService.send([userEmail], [finalEmailBody]).
2. Ghi nhận trạng thái gửi thành công vào nhật ký đơn hàng: OrderLog.info([orderId], 'Confirmation email sent').

#### BR105 — Activity (6)

Failure & Queue Rules:
1. Ghi nhận mã lỗi kết nối: SystemLog.error('SMTP_CONNECTION_FAILED', [orderId]).

---

### UC-22 - Xem lịch sử đơn hàng

#### BR106 — Activity (1)

Loading Rules:
1. Hệ thống tiếp nhận yêu cầu truy cập lịch sử đơn hàng từ giao diện người dùng.
2. Các trường dữ liệu yêu cầu từ client: [userId], [pageNumber] (mặc định là 1), [pageSize] (mặc định là 10).

#### BR107 — Activity (2)

Order Query Rules:
1. Hệ thống thực hiện truy vấn danh sách đơn hàng của người dùng: [orderHistoryList] = OrderRepository.findByUserId([userId]).
2. If [orderHistoryList].isEmpty() then chuyển sang Activity (3).
3. Else chuyển sang Activity (4).

#### BR108 — Activity (3)

Empty State & Suggestion Rules:
1. Trả về phản hồi 200-OK kèm mảng rỗng và MSG42.
2. Hiển thị thông báo danh sách trống và các sản phẩm gợi ý trên giao diện.

#### BR109 — Activity (4) & (5)

Sorting, Pagination & Display Rules:
1. Hệ thống thực hiện sắp xếp danh sách theo thời gian tạo giảm dần (mới nhất lên đầu): [sortedOrders] = [orderHistoryList].sortBy('createdAt', 'DESC').
2. Thực hiện phân trang dữ liệu dựa trên [pageNumber] và [pageSize].
3. Trả về phản hồi 200-OK kèm dữ liệu [paginatedOrders].
4. Hiển thị danh sách lịch sử đơn hàng lên màn hình orderHistoryScreen.

---

### UC-23 - Xem chi tiết đơn hàng cũ

#### BR110 — Activity (1)

Loading Rules:
1. Hệ thống tiếp nhận yêu cầu xem chi tiết của một đơn hàng cụ thể từ màn hình orderHistoryScreen.
2. Trường dữ liệu yêu cầu từ client: [orderId].

#### BR111 — Activity (2)

Order Detail Query Rules:
1. Hệ thống thực hiện truy vấn dữ liệu chi tiết: [orderDetails] = OrderRepository.findDetailsByOrderId([orderId]).
2. If [orderDetails] == null then returns 404-NOT_FOUND kèm MSG43.
3. Else chuyển sang Activity (4).

#### BR112 — Activity (3)

Message & Redirect Rules (Not Found):
1. Hiển thị thông báo lỗi truy xuất: MSG43.
2. Thực hiện điều hướng người dùng quay trở lại màn hình danh sách tổng orderHistoryScreen.

#### BR113 — Activity (4)

Success Display Rules:
1. Trả về phản hồi 200-OK kèm dữ liệu [orderDetails].
2. Hệ thống tải màn hình orderDetailScreen và hiển thị đầy đủ thông tin đơn hàng bao gồm: mã đơn hàng, trạng thái, danh sách sản phẩm, đơn giá, phí vận chuyển và tổng tiền.

---

### UC-24 - Theo dõi trạng thái đơn hàng

#### BR114 — Activity (1)

Loading Rules:
1. Hệ thống tiếp nhận yêu cầu theo dõi trạng thái đơn hàng thông qua việc kế thừa dữ liệu từ UC-23.
2. Nguồn dữ liệu yêu cầu: [orderId].

#### BR115 — Activity (2)

Status History Retrieval Rules:
1. Hệ thống thực hiện truy xuất toàn bộ lịch sử thay đổi trạng thái của đơn hàng: [statusHistory] = OrderRepository.getStatusHistory([orderId]).
2. Nếu [statusHistory] == null then returns 404-NOT_FOUND kèm MSG43 (Thông báo lỗi truy xuất dữ liệu).

#### BR116 — Activity (3)

Data Mapping Rules:
1. Hệ thống thực hiện ánh xạ (map) dữ liệu từ danh sách lịch sử sang cấu trúc biểu đồ dòng thời gian: [timelineData] = mapToTimeline([statusHistory]).
2. Mỗi mốc trạng thái phải bao gồm: [statusName], [updatedAt], và [description].

#### BR117 — Activity (4)

Timeline Display Rules:
1. Xác định trạng thái mới nhất để làm nổi bật: [currentStatus] = [statusHistory].latest().status.
2. Trả về phản hồi 200-OK kèm dữ liệu [timelineData] và [currentStatus].
3. Hệ thống hiển thị các mốc thời gian và làm nổi bật trạng thái hiện tại của đơn hàng trên giao diện orderDetailScreen.

---

### UC-25 - Hủy đơn hàng trạng thái "Pending"

#### BR118 — Activity (1) & (2)

Loading Rules:
1. Hệ thống tiếp nhận yêu cầu hủy đơn từ màn hình chi tiết đơn hàng.
2. Trường dữ liệu yêu cầu từ client: [orderId].
3. Hệ thống hiển thị hộp thoại xác nhận hủy đơn với nội dung: MSG44.

#### BR119 — Activity (3)

Decision Rules:
1. Nếu khách hàng chọn "Đồng ý" (Confirm): Chuyển sang Activity (4).
2. Nếu khách hàng chọn "Từ chối" (Cancel): Chuyển sang Activity (9).

#### BR120 — Activity (4)

Status Validation Rules:
1. Hệ thống truy vấn trạng thái thực tế của đơn hàng trong CSDL: [currentStatus] = OrderRepository.getStatus([orderId]).
2. If [currentStatus] != 'PENDING' then returns 400-BAD_REQUEST kèm MSG45.
3. Else chuyển sang Activity (5).

#### BR121 — Activity (5) & (6)

Cancellation & Inventory Rules:
1. Cập nhật trạng thái đơn hàng sang "Canceled": OrderRepository.updateStatus([orderId], 'CANCELED').
2. Hoàn trả số lượng sản phẩm vào kho: InventoryRepository.restoreStock([orderId]).
3. Trả về phản hồi 200-OK kèm dữ liệu trạng thái mới.

#### BR122 — Activity (7)

Success Message Rules:
Hiển thị thông báo hủy đơn hàng thành công: MSG46 và cập nhật giao diện hiển thị trạng thái mới.

#### BR123 — Activity (8)

Error Message Rules:
Hiển thị thông báo lỗi đơn hàng không thể hủy do đã được xử lý: MSG45.

#### BR124 — Activity (9)

Cancellation Rules:
Hệ thống đóng hộp thoại xác nhận và giữ nguyên trạng thái hiện tại của đơn hàng.

---

### UC-26 - Thay đổi địa chỉ giao hàng

#### BR125 — Activity (1) & (2)

Loading Rules:
1. Hệ thống tiếp nhận yêu cầu thay đổi địa chỉ từ màn hình chi tiết đơn hàng.
2. Các trường dữ liệu yêu cầu từ client: [orderId], [userId].
3. Hệ thống thực hiện truy xuất danh sách địa chỉ: [savedAddressList] = AddressRepository.findByUserId([userId]).
4. Returns 200-OK kèm dữ liệu [savedAddressList] để hiển thị lên giao diện.

#### BR126 — Activity (3)

Input Rules:
Khách hàng thực hiện chọn một địa chỉ mới từ danh sách và nhấn xác nhận: Hệ thống nhận trường dữ liệu [newAddressId].

#### BR127 — Activity (4)

Status Verification Rules:
1. Hệ thống kiểm tra trạng thái vận chuyển thực tế của đơn hàng: [shippingStatus] = OrderRepository.getShippingStatus([orderId]).
2. If [shippingStatus] == 'SHIPPED' (Đã được giao cho đơn vị vận chuyển) then returns 400-BAD_REQUEST kèm MSG48.
3. Else chuyển sang Activity (5).

#### BR128 — Activity (5)

Update Rules:
1. Thực hiện cập nhật thông tin địa chỉ mới vào bản ghi đơn hàng: OrderRepository.updateAddress([orderId], [newAddressId]).
2. Returns 200-OK kèm dữ liệu đơn hàng đã cập nhật.

#### BR129 — Activity (6)

Success Message Rules:
Hiển thị thông báo thay đổi địa chỉ thành công: MSG47.

#### BR130 — Activity (7)

Error Message Rules:
Hiển thị thông báo lỗi không thể thay đổi địa chỉ do đơn hàng đã được gửi đi: MSG48.

---

### UC-27 - Đánh giá sản phẩm đã mua

#### BR131 — Activity (1) & (2)

Loading Rules:
1. Hệ thống tiếp nhận yêu cầu viết đánh giá từ màn hình chi tiết đơn hàng.
2. Các trường dữ liệu yêu cầu từ client: [productId], [orderId].
3. Danh sách các trường nhập liệu trong biểu mẫu: [ratingStars] (từ 1 đến 5 sao), [reviewComment].

#### BR132 — Activity (4)

Validation Rules:
1. Hệ thống kiểm tra giá trị của trường [ratingStars].
2. If [ratingStars] chưa được chọn (null) hoặc bằng 0 then returns 400-BAD_REQUEST kèm MSG49.
3. Else chuyển sang Activity (6).

#### BR133 — Activity (5)

Message Rules (Error):
Hiển thị thông báo yêu cầu khách hàng phải chọn số sao tối thiểu trước khi gửi: MSG49.

#### BR134 — Activity (6)

Storage Rules:
1. Hệ thống thực hiện lưu bài đánh giá vào CSDL: ReviewRepository.save([userId], [productId], [ratingStars], [reviewComment]).
2. Liên kết bài đánh giá với sản phẩm tương ứng để cập nhật điểm đánh giá trung bình.
3. Trả về phản hồi 200-OK kèm thông tin xác nhận lưu trữ thành công.

#### BR135 — Activity (7)

Success Message Rules:
Hiển thị thông báo gửi đánh giá thành công trên giao diện: MSG50.

---

### UC-28 - Đọc đánh giá từ người khác

#### BR136 — Activity (1)

Loading Rules:
1. Hệ thống tiếp nhận tương tác cuộn xuống khu vực đánh giá tại màn hình productDetailsScreen.
2. Trường dữ liệu yêu cầu từ client: [productId].

#### BR137 — Activity (2)

Review Query Rules:
1. Hệ thống thực hiện truy vấn danh sách các bài đánh giá của sản phẩm: [reviewList] = ReviewRepository.findByProductId([productId]).
2. If [reviewList].isEmpty() then chuyển sang Activity (5).
3. Else chuyển sang Activity (3).

#### BR138 — Activity (3)

Average Rating Calculation Rules:
1. Hệ thống thực hiện tính toán điểm trung bình: [averageRating] = calculateAverage([reviewList].stars).
2. Kết quả [averageRating] được làm tròn đến 1 chữ số thập phân.

#### BR139 — Activity (4)

Success Display Rules:
1. Trả về phản hồi 200-OK kèm dữ liệu [averageRating] và [reviewList].
2. Hệ thống hiển thị điểm trung bình kèm biểu đồ phân bổ sao và danh sách các bình luận chi tiết lên giao diện.

#### BR140 — Activity (5)

Empty State Message Rules:
1. Trả về phản hồi 200-OK kèm mảng rỗng.
2. Hiển thị thông báo sản phẩm chưa có đánh giá nào: MSG51.

---

### UC-29 - Xem danh sách đơn hàng "Pending"

#### BR141 — Activity (1) & (2)

Loading Rules:
1. Hệ thống tiếp nhận yêu cầu từ Nhân viên Sales tại trang quản trị.
2. Thiết lập bộ lọc mặc định: [statusFilter] = 'PENDING'.
3. Các trường dữ liệu yêu cầu từ client: [userId], [pageNumber] (mặc định là 1), [pageSize] (mặc định là 20).

#### BR142 — Activity (3)

Data Fetching Rules:
1. Hệ thống thực hiện truy vấn danh sách đơn hàng theo trạng thái: [pendingOrders] = OrderRepository.findByStatus('PENDING').
2. If [pendingOrders].isEmpty() then chuyển sang Activity (4).
3. Else chuyển sang Activity (5).

#### BR143 — Activity (4)

Empty Result Rules:
1. Trả về phản hồi 200-OK kèm mảng rỗng.
2. Hiển thị thông báo không có đơn hàng mới: MSG52.
3. Hiển thị bảng dữ liệu trống trên giao diện quản lý đơn hàng.

#### BR144 — Activity (5)

Display Rules:
1. Trả về phản hồi 200-OK kèm dữ liệu [pendingOrders] đã được phân trang.
2. Hiển thị danh sách dưới dạng bảng với các cột: Order Id, Customer Name, Total Amount, Ordering Time, Status.

---

### UC-30 - Xem chi tiết đơn hàng

#### BR145 — Activity (1)

Loading Rules:
1. Hệ thống tiếp nhận yêu cầu xem chi tiết từ Nhân viên Sales tại danh sách quản lý đơn hàng.
2. Trường dữ liệu yêu cầu từ client: [orderId].

#### BR146 — Activity (2)

Detailed Query Rules:
1. Hệ thống thực hiện truy vấn thông tin toàn diện của đơn hàng: [orderFullDetails] = OrderRepository.findFullDetails([orderId]).
2. If [orderFullDetails] == null then returns 404-NOT_FOUND kèm MSG43.
3. Else chuyển sang Activity (3).

#### BR147 — Activity (3)

Display Rules:
1. Trả về phản hồi 200-OK kèm toàn bộ dữ liệu đơn hàng [orderFullDetails]
2. Hiển thị màn hình chi tiết bao gồm các phân khu thông tin:
- Thông tin giao hàng: Full Name, Phone Number, Shipping Address.
- Danh sách sản phẩm: Tên sản phẩm, Số lượng, Đơn giá, Thành tiền.
- Thanh toán: Payment Method, Shipping Fee, Voucher, Total Amount.
- Trạng thái: Order Status

---

### UC-31 - Duyệt đơn hàng

#### BR153 — Activity (1) & (2)

Loading & Confirmation Rules:
1. Hệ thống tiếp nhận tương tác tại màn hình chi tiết đơn hàng.
2. Các trường dữ liệu yêu cầu từ client: [orderId].
3. Hệ thống hiển thị hộp thoại xác nhận với nội dung: MSG54.

#### BR154 — Activity (3)

Decision Rules:
1. Nếu Nhân viên Sales chọn "Đồng ý" (Confirm): Chuyển sang Activity (4).
2. Nếu Nhân viên Sales chọn "Hủy" (Cancel): Chuyển sang Activity (8).

#### BR155 — Activity (4), (5) & (6)

Approval & Notification Rules:
1. Hệ thống cập nhật trạng thái đơn hàng: OrderRepository.updateStatus([orderId], 'APPROVED').
2. Ghi nhận hành động vào nhật ký hệ thống: AuditLogger.log([userId], 'APPROVE_ORDER', [orderId]).
3. Kích hoạt dịch vụ thông báo (Push Notification/In-app): NotificationService.notify([userId], 'Order Approved', [orderId]).

#### BR156 — Activity (7)

Success Message Rules:
1. Trả về phản hồi 200-OK kèm thông báo MSG55.
2. Hệ thống cập nhật lại trạng thái hiển thị trên giao diện quản trị.

#### BR157 — Activity (8)

Cancellation Rules:
Đóng hộp thoại xác nhận và giữ nguyên trạng thái đơn hàng hiện tại.

---

### UC-32 - Cập nhật trạng thái giao hàng

#### BR148 — Activity (1) & (2)

Loading Rules:
1. Hệ thống tiếp nhận yêu cầu từ Nhân viên Sales tại màn hình chi tiết đơn hàng.
2. Các trường dữ liệu yêu cầu từ client: [orderId].
3. Hệ thống hiển thị form nhập liệu bao gồm trường: [trackingNumber] (Mã vận đơn).

#### BR149 — Activity (4)

Tracking Number Validation Rules:
1. Kiểm tra tính hợp lệ của mã vận đơn.
2. If isEmpty([trackingNumber]) then returns 400-BAD_REQUEST kèm MSG1.
3. Else chuyển sang Activity (5).

#### BR150 — Activity (5) & (6)

Update & Audit Rules:
1. Hệ thống thực hiện cập nhật trạng thái đơn hàng: OrderRepository.updateStatus([orderId], 'SHIPPING').
2. Lưu mã vận đơn vào bản ghi đơn hàng: OrderRepository.setTrackingNumber([orderId], [trackingNumber]).
3. Ghi nhận thao tác vào hệ thống giám sát: AuditLogger.log([userId], 'UPDATE_STATUS', [orderId], 'SHIPPING').

#### BR151 — Activity (7)

Success Notification Rules:
1. Trả về phản hồi 200-OK kèm thông báo MSG53.
2. Cập nhật lại giao diện hiển thị trạng thái và mã vận đơn mới trên màn hình chi tiết.

#### BR152 — Activity (8)

Error Message Rules:
Hiển thị cảnh báo lỗi yêu cầu nhân viên nhập đầy đủ thông tin mã vận đơn: MSG1.

---

### UC-33 - Xác nhận trạng thái "Delivered"

#### BR158 — Activity (1) & (2)

Loading & Confirmation Rules:
1. Hệ thống tiếp nhận tương tác từ Nhân viên Sales tại màn hình chi tiết đơn hàng đang ở trạng thái 'Shipping'.
2. Các trường dữ liệu yêu cầu từ client: [orderId].
3. Hệ thống hiển thị hộp thoại xác nhận hoàn tất giao hàng với nội dung: MSG56.

#### BR159 — Activity (3)

Completion Confirmation Rules:
Hệ thống chỉ thực hiện các bước tiếp theo khi người dùng nhấn nút xác nhận trên hộp thoại. Nếu người dùng đóng hoặc hủy hộp thoại, hệ thống giữ nguyên trạng thái đơn hàng hiện tại.

#### BR160 — Activity (4) & (5)

Processing & Audit Rules:
1. Hệ thống cập nhật trạng thái đơn hàng trong CSDL: OrderRepository.updateStatus([orderId], 'DELIVERED').
2. Cập nhật thời gian hoàn thành thực tế: [order.completedAt] = DateTime.Now.
3. Ghi nhận hành động vào nhật ký hệ thống để phục vụ kiểm tra: AuditLogger.log([userId], 'SET_DELIVERED', [orderId]).

#### BR161 — Activity (6)

Finalization Notification Rules:
1. Trả về phản hồi 200-OK kèm thông báo thành công MSG57.
2. Hệ thống cập nhật lại giao diện người dùng, chuyển trạng thái đơn hàng sang 'Delivered' và vô hiệu hóa (disable) các nút tương tác thay đổi trạng thái khác.

---

### UC-34 - Hủy đơn hàng

#### BR162 — Activity (1) & (2)

Loading Rules:
1. Hệ thống tiếp nhận yêu cầu hủy đơn và hiển thị popup CancelOrder_Popup.
2. Hiển thị danh sách lý do [reasonList] và một trường nhập văn bản [cancelDescription] (mặc định bị ẩn hoặc vô hiệu hóa).

#### BR163 — Activity (3)

Dynamic UI Rules:
1. Nếu nhân viên chọn reasonID == 'OTHER', hệ thống tự động kích hoạt (enable) trường [cancelDescription].
2. Nếu chọn các lý do khác, trường này có thể để trống hoặc bị vô hiệu hóa.

#### BR164 — Activity (4)

Input Submission:
Hệ thống truyền bộ tham số (orderID, reasonId, cancelDescription) xuống tầng xử lý Order_Controller.

#### BR165 — Activity (13)

Complex Validation Rules:
1. Check 1: Nếu reasonID trống then trả về MSG59.
2. Check 2: Nếu reasonID == 'OTHER' VÀ cancelDescription trống then trả về MSG60.
3. Nếu thỏa mãn cả hai, chuyển sang Activity (5).

#### BR166 — Activity (5) & (6)

Status Update Rules:
Hệ thống cập nhật trạng thái đơn hàng thành "Canceled" trong CSDL.

#### BR167 — Activity (7) & (8)

Inventory Rules:
Hoàn trả số lượng sản phẩm vào tồn kho thực tế: InventoryRepository.restoreStock(orderID).

#### BR168 — Activity (9) & (10)

Audit Log Rules:
Ghi nhật ký hành động: AuditLog.write(staffID, "CANCEL_ORDER", orderID, reasonId, cancelDescription). Nội dung mô tả sẽ được lưu cùng để phục vụ hậu kiểm.

#### BR169 — Activity (11) & (12)

Success Message Rules:
Trả về phản hồi 200-OK kèm MSG58 và đóng popup.

#### BR170 — Activity (14)

Error Display Rules:
Hiển thị cảnh báo lỗi tương ứng MSG59 hoặc MSG60 ngay trên popup để nhân viên bổ sung thông tin.

---

### UC-35 - Khởi tạo hoàn tiền

#### BR171 — Activity (1) & (2)

Loading Rules:
1. Hệ thống tiếp nhận yêu cầu từ Nhân viên Sales tại màn hình chi tiết đơn hàng.
2. Các nguồn dữ liệu cần thiết: [orderId], [totalPaidAmount], [originalPaymentMethod].
3. Hệ thống hiển thị thông tin tóm tắt khoản tiền cần hoàn và phương thức gốc để nhân viên rà soát.

#### BR172 — Activity (3)

Interaction Rules:
Nhân viên nhấn nút xác nhận gửi lệnh. Hệ thống hiển thị hộp thoại yêu cầu xác nhận cuối cùng: MSG61.

#### BR173 — Activity (4)

Refund Condition Rules:
1. Hệ thống kiểm tra điều kiện: [order.paymentStatus] == 'PAID' VÀ [order.status] == 'CANCELED'.
2. If điều kiện không thỏa mãn then returns 400-BAD_REQUEST kèm MSG64.
3. Else chuyển sang Activity (5).

#### BR174 — Activity (5)

Bank API Integration Rules:
1. Hệ thống gọi phương thức hoàn tiền từ cổng thanh toán: BankAPI.refund([transactionId], [amount]).
2. If API trả về mã thành công then chuyển sang Activity (7).
3. Else (bị ngân hàng từ chối/lỗi kết nối) then chuyển sang Activity (10).

#### BR175 — Activity (7) & (8)

Data Persistence & Audit Rules:
1. Cập nhật trạng thái thanh toán đơn hàng: [order.paymentStatus] = 'REFUNDED'.
2. Ghi nhận hành động vào nhật ký: AuditLogger.log([userId], 'INITIATE_REFUND', [orderId], 'SUCCESS').

#### BR176 — Activity (9)

Success Notification Rules:
Trả về phản hồi 200-OK kèm thông báo MSG62 và cập nhật giao diện hiển thị trạng thái hoàn tiền thành công.

#### BR177 — Activity (10)

Bank Error Handling Rules:
Hiển thị cảnh báo lỗi chi tiết từ phía ngân hàng hoặc cổng thanh toán: MSG63.

#### BR178 — Activity (11)

Validation Error Rules:
Hiển thị thông báo đơn hàng không đủ điều kiện để hoàn tiền (ví dụ: đơn hàng chưa thanh toán hoặc chưa bị hủy): MSG64.

---

### UC-36 - Tìm kiếm đơn hàng

#### BR179 — Activity (1) & (2)

Loading & Input Rules:
1. Hệ thống tiếp nhận từ khóa tìm kiếm từ ô nhập liệu tại trang quản trị đơn hàng.
2. Tham số yêu cầu từ client: [searchKeyword].

#### BR180 — Activity (3)

Query Logic Rules:
1. Hệ thống thực hiện truy vấn trong CSDL theo cơ chế so khớp chính xác hoặc gần đúng (Like query).
2. Điều kiện tìm kiếm: [order.id] == [searchKeyword] OR [customer.phoneNumber] == [searchKeyword].

#### BR181 — Activity (5)

Search Success Rules:
1. Trả về phản hồi 200-OK kèm danh sách [matchingOrders].
2. Hiển thị danh sách kết quả dưới dạng bảng, làm nổi bật từ khóa tìm kiếm trong kết quả (nếu có).

#### BR182 — Activity (6)

Empty Result Rules:
1. Trả về phản hồi 200-OK kèm mảng dữ liệu rỗng.
2. Hiển thị thông báo không tìm thấy kết quả phù hợp: MSG65.

---

### UC-37 - Xem phản hồi mới nhất

#### BR183 — Activity (1)

Loading Rules:
1. Hệ thống tiếp nhận yêu cầu truy cập từ menu "Quản lý đánh giá/Phản hồi" tại Dashboard quản trị.
2. Tham số yêu cầu mặc định: [pageNumber] = 1, [pageSize] = 20, [sortBy] = 'createdAt', [sortOrder] = 'DESC'.

#### BR184 — Activity (2)

Review Retrieval Rules:
1. Hệ thống thực hiện truy vấn: [feedbackList] = ReviewRepository.findAllLatest().
2. If [feedbackList].isEmpty() then chuyển sang Activity (7).
3. Else chuyển sang Activity (4).

#### BR185 — Activity (4)

Chronological Display Rules:
1. Trả về phản hồi 200-OK kèm danh sách đã sắp xếp.
2. Hiển thị danh sách phản hồi bao gồm: Full Name, Product ID, Product Name, Rating, Content và Created At.

#### BR186 — Activity (5) & (6)

Star Filtering Rules:
1. Khi nhân viên thay đổi bộ lọc [starFilter], hệ thống thực hiện truy vấn lại: ReviewRepository.findByStars([starFilter]).
2. Cập nhật lại giao diện danh sách mà không cần tải lại toàn bộ trang (Partial Update).

#### BR187 — Activity (7)

Empty State Rules:
1. Trả về phản hồi 200-OK kèm mảng rỗng.
2. Hiển thị thông báo trạng thái chưa có dữ liệu: MSG66.

---

### UC-38 - Phản hồi đánh giá

#### BR188 — Activity (1) & (2)

Loading Rules:
1. Hệ thống tiếp nhận lệnh phản hồi từ Nhân viên Sales đối với một bài đánh giá cụ thể.
2. Tham số yêu cầu từ phía Client: [reviewId].
3. Hệ thống kích hoạt và hiển thị khung soạn thảo văn bản replyTextArea trên giao diện.

#### BR189 — Activity (4)

Content Validation Rules:
1. Hệ thống thực hiện kiểm tra tính hợp lệ của nội dung: [replyContent].
2. If isEmpty([replyContent]) hoặc chỉ chứa khoảng trắng then trả về lỗi kèm MSG68.
3. Else chuyển sang Activity (5) để thực hiện lưu trữ.

#### BR190 — Activity (5) & (6)

Data Persistence & Linking Rules:
1. Hệ thống thực hiện lưu nội dung vào cơ sở dữ liệu: ReviewReplyRepository.save([staffId], [reviewId], [replyContent]).
2. Thiết lập mối quan hệ phụ thuộc giữa phản hồi mới và bài đánh giá gốc để đảm bảo tính toàn vẹn của luồng hội thoại.

#### BR191 — Activity (7)

Success Notification Rules:
1. Trả về phản hồi mã 200-OK kèm thông báo thành công MSG67.
2. Hệ thống tự động cập nhật trạng thái hiển thị của bài đánh giá thành "Đã phản hồi" và tải lại vùng dữ liệu tương ứng.

#### BR192 — Activity (8)

Error Warning Rules:
Hiển thị cảnh báo trực quan yêu cầu nhân viên phải nhập nội dung trước khi gửi: MSG68.

---

### UC-39 - Ẩn đánh giá vi phạm

#### BR193 — Activity (1) & (2)

Loading Rules:
1. Hệ thống hiển thị hộp thoại HideReview_Dialog.
2. Các trường dữ liệu: [violationReason] (Danh sách chọn sẵn) và [violationDescription] (Trường nhập văn bản, mặc định bị ẩn hoặc vô hiệu hóa).

#### BR194 — Activity (3)

Dynamic UI Rules:
1. Nếu violationReason == 'OTHER', hệ thống tự động kích hoạt (enable) trường [violationDescription].
2. Nếu chọn các lý do định nghĩa sẵn khác, trường mô tả sẽ bị vô hiệu hóa để tránh dữ liệu thừa.

#### BR195 — Activity (4)

Complex Validation Rules:
1. Check 1: Nếu [violationReason] chưa được chọn then trả về MSG70.
2. Check 2: Nếu [violationReason] == 'OTHER' VÀ [violationDescription] trống then trả về MSG71.
3. Nếu vượt qua các kiểm tra, chuyển sang Activity (5).

#### BR196 — Activity (5) & (6)

Status & Audit Rules:
1. Cập nhật trạng thái đánh giá thành 'HIDDEN'.
2. Ghi nhận vào Audit Log: [staffId], [reviewId], [violationReason], [violationDescription] (nếu có).

#### BR197 — Activity (7)

Success Notification Rules:
Trả về mã 200-OK kèm thông báo MSG69 và cập nhật giao diện quản trị.

#### BR198 — Activity (8)

Error Display Rules:
Hiển thị cảnh báo tương ứng (MSG70 hoặc MSG71) ngay trên hộp thoại để nhân viên bổ sung.

---

### UC-40 - Thêm danh mục sản phẩm mới

#### BR199 — Activity (1) & (2)

Loading Rules:
1. Hệ thống tiếp nhận yêu cầu từ Business Admin tại trang quản lý kho/sản phẩm.
2. Hiển thị form AddCategory_Form bao gồm các trường: [categoryName], [categoryImage], [categoryDescription].

#### BR200 — Activity (3)

Field Validation Rules:
1. [categoryName] là trường bắt buộc (Mandatory).
2. If [categoryName] is null or empty, trả về MSG1.

#### BR201 — Activity (4)

Duplicate Check Rules:
1. Hệ thống thực hiện truy vấn: [isExist] = CategoryRepository.existsByName([categoryName]).
2. If [isExist] = true (Tên đã tồn tại) then chuyển sang Activity (7).
3. Else chuyển sang Activity (5).

#### BR202 — Activity (5)

Persistence Rules:
1. Lưu bản ghi mới vào CSDL: CategoryRepository.insert([categoryName], [categoryImage], [categoryDescription]).
2. Hệ thống tự động sinh mã định danh duy nhất (CategoryId) và gán ngày tạo.

#### BR203 — Activity (6)

Success Notification Rules:
Trả về mã 201-CREATED kèm thông báo MSG72 và làm mới danh sách danh mục trên giao diện.

#### BR204 — Activity (7)

Conflict Error Rules:
Trả về mã 409-CONFLICT kèm thông báo MSG73 để yêu cầu người dùng đổi tên khác.

---

### UC-41 - Sửa hoặc xóa danh mục

#### BR205 — Activity (2)

Operation Branching:
1. If [actionType] == 'DELETE' then chuyển sang quy trình Xóa tại Activity (3).
2. If [actionType] == 'EDIT' then chuyển sang quy trình Sửa tại Activity (10).

#### BR206 — Activity (3) & (4)

Migration Setup:
Hệ thống load danh sách danh mục thay thế: CategoryRepo.GetAll().Where(c => c.Id != targetId).

#### BR207 — Activity (5)

Selection Validation:
If [replacementCategoryId] == null then return 400-BAD_REQUEST với MSG75 và chuyển đến Activity (9).

#### BR208 — Activity (6) & (7)

Data Transaction:
Thực hiện Transaction: { ProductRepo.UpdateCategory(oldId, newId); CategoryRepo.Delete(oldId) } để đảm bảo tính toàn vẹn dữ liệu.

#### BR209 — Activity (8)

Delete Success:
Return 200-OK kèm MSG74 và cập nhật lại Grid dữ liệu.

#### BR210 — Activity (10) & (11)

Edit Validation:
1. Hiển thị thông tin danh mục hiện tại.
2. If isEmpty([categoryName]) then return 400-BAD_REQUEST với MSG1.

#### BR211 — Activity (12)

Update Processing:
1. If CategoryRepo.Exists(newName, id != currentId) then return 409-CONFLICT với MSG73.
2. Else thực hiện CategoryRepo.Update([categoryData]).

#### BR212 — Activity (13)

Update Success:
Return 200-OK kèm MSG76.

---

### UC-42 - Đăng sản phẩm mới

#### BR214 — Activity (1) & (2)

Form Initialization:
Hệ thống hiển thị AddProduct_Form. Các trường dữ liệu bắt buộc (Mandatory) bao gồm: [name], [price], [categoryId], [stock], và ít nhất một [image].

#### BR215 — Activity (3)

Request Submission:
Hệ thống tiếp nhận bộ tham số Multipart/Form-Data bao gồm thông tin chi tiết sản phẩm và các tệp hình ảnh.

#### BR216 — Activity (4)

Server-side Validation:
1. if isEmpty(name, price, categoryId) then return 400-BAD_REQUEST with MSG1.
2. if price <= 0 then return 400-BAD_REQUEST with MSG79.
3. if imageFile.Count == 0 then return 400-BAD_REQUEST with MSG80.

#### BR217 — Activity (5)

Image Storage Rules:
1. Hệ thống thực hiện kiểm tra định dạng (.jpg, .png, .webp) và dung lượng tệp (max 5MB): [fileInvalid] = validateImageFile([image])
2. if [fileInvalid] = false then return 400 with MSG78.
3. else: Lưu trữ tệp vào hệ thống (Cloud Storage/Folder) và lấy URL.

#### BR218 — Activity (6)

Data Persistence:
Thực hiện lưu trữ file ảnh: [imageUrls] = FileStorageService.store([image]);
Thực hiện ProductRepo.Insert([productData], [imageUrls]) vào CSDL SQL Server/PostgreSQL. Hệ thống tự động gán createdAt và status = 'Active'.

#### BR219 — Activity (7)

Success Feedback:
Return 201-CREATED kèm MSG77 và chuyển hướng về trang danh sách sản phẩm.

#### BR220 — Activity (8)

Error Handling:
Trả về danh sách các lỗi cụ thể (Field-level errors) để hiển thị cảnh báo tương ứng trên giao diện.

---

### UC-43 - Cập nhật giá sản phẩm

#### BR221 — Activity (1) & (2)

Data Loading & Input:
1. Hệ thống truy vấn giá hiện tại của sản phẩm theo [productId].
2. Tiếp nhận giá trị [newPrice] từ giao diện chỉnh sửa nhanh hoặc trang chi tiết.

#### BR222 — Activity (4)

Price Validation Logic:
1. if isEmpty([newPrice]) then return 400-BAD_REQUEST with MSG1.
2. if [newPrice] <= 0 then return 400-BAD_REQUEST with MSG79.
3. if [newPrice] == [currentPrice] then return 400-BAD_REQUEST with MSG82.

#### BR223 — Activity (5)

Database Update:
Thực hiện ProductRepo.UpdatePrice([productId], [newPrice]). Hệ thống đồng thời cập nhật trường updatedAt để phục vụ đối soát.

#### BR224 — Activity (6)

Success Notification:
Return 200-OK kèm MSG81 và cập nhật hiển thị giá mới trên giao diện quản trị.

#### BR225 — Activity (7)

Error Display:
Hiển thị thông báo lỗi tương ứng (MSG1, MSG79 hoặc MSG82) ngay tại ô nhập liệu để yêu cầu nhân viên điều chỉnh.

---

### UC-44 - Cập nhật số lượng tồn kho

#### BR226 — Activity (1) & (2)

Search & Input:
1. Hệ thống tiếp nhận mã sản phẩm [productId] qua thanh tìm kiếm.
2. Tiếp nhận giá trị số lượng mới [newStock] từ giao diện kiểm kê.

#### BR227 — Activity (4)

Data Validation Logic:
1. if isEmpty([newStock]) then return 400-BAD_REQUEST with MSG1.
2. if [newStock] < 0 then return 400-BAD_REQUEST with MSG84.
3. if [newStock] không phải số nguyên then return 400-BAD_REQUEST với MSG29.

#### BR228 — Activity (5)

Database Persistence:
Thực hiện ProductRepo.UpdateStock([productId], [newStock]) và cập nhật dấu thời gian updatedAt.

#### BR229 — Activity (6)

Automatic Status Evaluation:
1. if [newStock] == 0 then set productStatus = 'OUT_OF_STOCK'.
2. if [newStock] > 0 and currentStatus == 'OUT_OF_STOCK' then set productStatus = 'ACTIVE'.

#### BR230 — Activity (7)

Success Notification:
Return 200-OK kèm MSG83 và làm mới số lượng hiển thị trên Dashboard.

#### BR231 — Activity (8)

Error Handling:
Hiển thị cảnh báo lỗi định dạng (MSG29, MSG84) ngay tại trường nhập liệu để nhân viên điều chỉnh.

---

### UC-45 - Ngừng kinh doanh sản phẩm

#### BR232 — Activity (1) & (2)

Action Initiation:
1. Hệ thống tiếp nhận yêu cầu ngừng kinh doanh từ Business Admin cho sản phẩm cụ thể qua [productId].

#### BR233 — Activity (3)

Confirmation Request:
1. Hệ thống hiển thị hộp thoại xác nhận ConfirmationModal kèm nội dung thông báo MSG85.

#### BR234 — Activity (4)

Response Branching:
1. if phản hồi là "Đồng ý" then chuyển sang Activity (5).
2. if phản hồi là "Từ chối" then chuyển sang Activity (7).

#### BR235 — Activity (5)

Status Persistence:
1. Thực hiện lệnh ProductRepo.UpdateStatus([productId], 'DISCONTINUED') vào Cơ sở dữ liệu.

#### BR236 — Activity (6)

Success Notification:
1. return 200-OK kèm MSG86 và cập nhật lại danh sách sản phẩm trên giao diện.

#### BR237 — Activity (7)

Cancellation Logic:
1. Đóng hộp thoại xác nhận, giữ nguyên trạng thái sản phẩm hiện tại và hủy bỏ mọi giao dịch đang chờ.

---

### UC-46 - Tạo voucher kèm điều kiện

#### BR238 — Activity (1) & (2)

Form Initialization:
1. Hệ thống hiển thị CreateVoucher_Form.
2. Các trường dữ liệu bắt buộc: [voucherCode], [discountValue], [discountType], [quantity], [startDate], [endDate].

#### BR239 — Activity (3) & (4)

Data Submission:
1. Hệ thống tiếp nhận thông tin voucher và các điều kiện áp dụngtừ Business Admin.

#### BR240 — Activity (5)

Validation Logic:
1. if isEmpty([voucherCode], [discountValue], [discountType], [quantity], [startDate], [endDate]) then return 400-BAD_REQUEST with MSG1.
2. if VoucherRepo.Exists([voucherCode]) then return 409-CONFLICT with MSG88.
3. if [endDate] <= [startDate] then return 400-BAD_REQUEST with MSG89.
4. if [discountValue] <= 0 or [quantity] <= 0 then return 400-BAD_REQUEST with MSG90.

#### BR241 — Activity (6)

Database Persistence:
1. Thực hiện VoucherRepo.Insert([voucherData]) vào Cơ sở dữ liệu.
2. Hệ thống tự động thiết lập trạng thái isActive = true nếu startDate trùng với ngày hiện tại.

#### BR242 — Activity (7)

Success Notification:
1. return 201-CREATED kèm MSG87 và làm mới danh sách voucher trên giao diện.

#### BR243 — Activity (8)

Error Handling:
1. Hiển thị cảnh báo lỗi chi tiết cho từng trường dữ liệu không hợp lệ để người dùng điều chỉnh.

---

### UC-47 - Dừng voucher khẩn cấp

#### BR244 — Activity (1) & (2)

Action Initiation & Warning:
1. Hệ thống tiếp nhận [voucherId] từ yêu cầu dừng khẩn cấp của Business Admin.
2. Hiển thị hộp thoại EmergencyStop_Dialog kèm cảnh báo MSG91 để xác nhận hành động vô hiệu hóa.

#### BR245 — Activity (3)

Confirmation Branching:
1. if phản hồi là "Confirm" then chuyển sang Activity (4).
2. if phản hồi là "Cancel" then chuyển sang Activity (6).

#### BR246 — Activity (4)

Status Persistence:
1. Thực hiện VoucherRepo.UpdateStatus([voucherId], 'STOPPED') trong Cơ sở dữ liệu.
2. Mã giảm giá sẽ bị vô hiệu hóa ngay lập tức và không thể áp dụng cho các đơn hàng mới.

#### BR247 — Activity (5)

Success Notification:
1. return 200-OK kèm MSG92 và làm mới trạng thái hiển thị trên giao diện quản trị.

#### BR248 — Activity (6)

Cancellation Logic:
1. Đóng hộp thoại xác nhận, giữ nguyên trạng thái Active của voucher và hủy bỏ thao tác.

---

### UC-48 - Tạo tài khoản nhân viên mới

#### BR249 — Activity (1) & (2)

Loading Rules:
1. Tải màn hình CreateStaff_Form.
2. Hệ thống yêu cầu các trường dữ liệu: [email], [fullName], [roleId].

#### BR250 — Activity (3) & (4)

Validate Format Rules:
1. If any in [email], [fullName], [roleId] is empty then returns 400-BAD_REQUEST error with MSG1.
2. If pattern.compile('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$').notMatch([email]) then returns 400-BAD_REQUEST error with MSG2.

#### BR251 — Activity (5)

Check Conflict Rules:
1. [account] = AccountRepository.findByEmail([email]).
2. If [account] != null then proceeds to Activity (9).
3. Else proceeds to Activity (6).

#### BR252 — Activity (6) & (7)

Account Creation & Email Rules:
1. [tempPassword] = generateRandomPassword().
2. [hashedPassword] = hash([tempPassword]).
3. UserRepository.save([email], [roleId], [hashedPassword]).
4. UserProfileRepository.save([fullName])
4. EmailService.sendLoginDetails([email], [tempPassword]).

#### BR253 — Activity (8)

Success & Notification Rules:
1. returns 201-CREATED response with MSG93.
2. Hiển thị thông báo thành công và làm mới danh sách nhân viên.

#### BR254 — Activity (9)

Conflict Handling Rules:
1. returns 409-CONFLICT error with MSG94.
2. Hiển thị thông báo tài khoản đã tồn tại: MSG94.

---

### UC-49 - Khóa tài khoản của nhân viên cũ

#### BR255 — Activity (1) & (2)

Selection Rules:
1. Hệ thống tiếp nhận [staffId] từ yêu cầu khóa tài khoản của Business Admin.
2. [account] = AccountRepository.findById([staffId]).

#### BR256 — Activity (3)

Confirmation Rules:
1. Hiển thị hộp thoại LockAccount_Dialog kèm thông báo xác nhận MSG96.

#### BR257 — Activity (4)

Response Branching Rules:
1. if phản hồi là "Đồng ý" then chuyển sang Activity (5).
2. if phản hồi là "Từ chối" then chuyển sang Activity (8).

#### BR258 — Activity (5) & (6)

Lock & Session Cleanup Rules:
1. [account.status] = 'LOCKED'.
2. AccountRepository.save([account]).
3. SessionManager.revokeAllActiveSessions([staffId]): Hệ thống thực hiện thu hồi tất cả JWT/RefreshToken hiện có của nhân viên để buộc đăng xuất ngay lập tức.

#### BR259 — Activity (7)

Success & Message Rules:
1. returns 200-OK response với MSG97.
2. Hiển thị thông báo thành công và cập nhật trạng thái trên Dashboard.

#### BR260 — Activity (8)

Cancellation Rules:
1. Đóng hộp thoại xác nhận, giữ nguyên trạng thái tài khoản và hủy bỏ thao tác.

---

### UC-50 - Quản lý danh sách khách hàng

#### BR261 — Activity (1) & (2)

Data Retrieval Rules:
1. Tải màn hình CustomerManagement_Screen.
2. Thực hiện truy vấn: [customerList] = CustomerRepository.findAll().

#### BR262 — Activity (3)

Processing Rules:
1. Hệ thống thực hiện sắp xếp danh sách: [customerList].OrderByDescending(c => c.CreatedAt).
2. Áp dụng phân trang (Pagination): [pagedList] = [customerList].ToPagedList(pageIndex, pageSize) (mặc định 10 bản ghi/trang).

#### BR263 — Activity (4)

Display Rules (Has Data):
1. if [pagedList].Count > 0 then render dữ liệu lên giao diện Grid kèm các cột thông tin: fullName, email, phoneNumber, status.

#### BR264 — Activity (5)

Empty State Rules:
1. if [pagedList].Count == 0 then hiển thị bảng trống kèm thông báo MSG98.

---

### UC-51 - Chặn tài khoản đặt hàng giả mạo

#### BR265 — Activity (1)

Selection Rules:
1. Hệ thống tiếp nhận [customerId] từ yêu cầu chặn tài khoản của Business Admin.
2. [account] = CustomerRepository.findById([customerId]).

#### BR266 — Activity (2)

Warning Rules:
1. Hệ thống hiển thị hộp thoại FraudMitigation_Dialog kèm thông báo MSG99.
2. Cảnh báo nhấn mạnh việc tất cả đơn hàng đang ở trạng thái PENDING sẽ bị hủy tự động.

#### BR267 — Activity (3)

Confirmation Rules:
1. if phản hồi là "Đồng ý" then chuyển sang Activity (4).
2. if phản hồi là "Từ chối" then chuyển sang Activity (8).

#### BR268 — Activity (4), (5) & (6)

Fraud Processing Transaction:
Thực hiện một Transaction duy nhất để đảm bảo tính toàn vẹn dữ liệu:
1. [account.status] = 'LOCKED'.
2. [pendingOrders] = OrderRepository.findPendingByCustomer([customerId]).
3. foreach order in [pendingOrders]:
begin
a. order.status = 'CANCELLED'.
b. InventoryService.restock(order.items) (Hoàn trả số lượng sản phẩm vào kho theo từng mã hàng).
4. AccountRepository.save([account]) và OrderRepository.saveAll([pendingOrders]).

#### BR269 — Activity (7)

Success Notification:
1. returns 200-OK kèm MSG100.
2. Làm mới giao diện và cập nhật số lượng tồn kho trên Dashboard.

#### BR270 — Activity (8)

Cancellation Rules:
1. Đóng hộp thoại, giữ nguyên trạng thái tài khoản và đơn hàng, hủy bỏ mọi xử lý logic.

---

### UC-52 - Xem biểu đồ doanh thu theo thời gian

#### BR271 — Activity (1) & (2)

Loading & Filter Rules:
1. Tải màn hình RevenueReport_Screen.
2. Hệ thống yêu cầu bộ lọc khoảng thời gian: [startDate], [endDate].
3. Mặc định: startDate là ngày đầu tháng hiện tại, endDate là ngày hiện tại.

#### BR272 — Activity (2)

Validate Date Rules:
1. If isEmpty([startDate]) OR isEmpty([endDate]) then returns 400-BAD_REQUEST error with MSG1.
2. If [endDate] < [startDate] then returns 400-BAD_REQUEST error with MSG101.

#### BR273 — Activity (3)

Data Retrieval Rules:
1. [orderList] = OrderRepository.findOrdersByStatusAndDate('DELIVERED', [startDate], [endDate]).
2. Chỉ các đơn hàng đã giao thành công (Delivered) mới được tính vào doanh thu thực tế.

#### BR274 — Activity (4)

Aggregation Rules:
1. [totalRevenue] = sum([orderList.totalPrice]).
2. [chartData] = [orderList].GroupBy(o => o.OrderDate.Date).Select(g => new { Date = g.Key, Value = g.Sum(x => x.TotalPrice) }).

#### BR275 — Activity (5)

Display Rules (Has Data):
1. if [orderList].Count > 0 then kết xuất dữ liệu lên biểu đồ (Line/Bar Chart) kèm các chỉ số tóm tắt: Tổng doanh thu, Số đơn hàng hoàn tất.

#### BR276 — Activity (6)

Empty State Rules:
1. if [orderList].Count == 0 then returns 200-OK với biểu đồ rỗng và thông báo MSG102.

---

### UC-53 - Xem báo cáo sản phẩm bán chạy

#### BR277 — Activity (1) & (2)

Loading & Filter Rules:
1. Tải màn hình BestSellingProducts_Report.
2. Hệ thống yêu cầu bộ lọc khoảng thời gian: [startDate], [endDate].

#### BR278 — Activity (2)

Validate Date Rules:
1. If isEmpty([startDate]) OR isEmpty([endDate]) then returns 400-BAD_REQUEST error with MSG1.
2. If [endDate] < [startDate] then returns 400-BAD_REQUEST error with MSG101.

#### BR279 — Activity (3)

Data Retrieval Rules:
1. [orderItems] = OrderRepository.findDeliveredOrderItems([startDate], [endDate]).
2. Chỉ truy vấn các sản phẩm thuộc đơn hàng đã hoàn tất (trạng thái Delivered/Completed) để đảm bảo tính chính xác của dữ liệu thực thu.

#### BR280 — Activity (4)

Calculation & Ranking Rules:
1. [rankingList] = [orderItems].GroupBy(i => i.ProductId).Select(g => new { ProductName = g.First().Name, TotalSold = g.Sum(x => x.Quantity), Revenue = g.Sum(x => x.UnitPrice * x.Quantity) }).
2. Thực hiện sắp xếp giảm dần: [rankingList].OrderByDescending(x => x.TotalSold).

#### BR281 — Activity (5)

Display Rules (Has Data):
1. if [rankingList].Count > 0 then hiển thị danh sách Top sản phẩm lên giao diện bảng kèm các cột: Hạng, Tên sản phẩm, Số lượng đã bán, Doanh thu đóng góp.

#### BR282 — Activity (6)

Empty State Rules:
1. if [rankingList].Count == 0 then trả về 200-OK với bảng trống và thông báo MSG103.

---

### UC-54 - Xuất đơn hàng ra Excel

#### BR283 — Activity (1)

Input & Filter Rules:
1. Hệ thống tiếp nhận bộ lọc dữ liệu từ giao diện: [startDate], [endDate], [orderStatus].
2. if isEmpty([startDate]) OR isEmpty([endDate]) then returns 400-BAD_REQUEST error with MSG1.

#### BR284 — Activity (2)

Data Aggregation Rules:
1. Thực hiện truy vấn: [exportData] = OrderRepository.findForExport([startDate], [endDate], [orderStatus]).
2. Dữ liệu bao gồm các trường: Mã đơn hàng, Ngày đặt, Khách hàng, Tổng tiền, Trạng thái, Phương thức thanh toán.

#### BR285 — Activity (3)

Excel Initialization Rules:
1. if [exportData].Count == 0 then proceeds to Activity (7).
2. else sử dụng thư viện (như EPPlus hoặc ClosedXML) để khởi tạo Workbook và ánh xạ dữ liệu vào các ô (Cells) theo định dạng bảng chuẩn.

#### BR286 — Activity (4) & (5)

File Stream Rules:
1. Chuyển đổi Workbook thành MemoryStream.
2. Thiết lập Header cho Response: Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.
3. Thiết lập Content-Disposition: attachment; filename=Orders_Export_{Date}.xlsx để trình duyệt tự động kích hoạt tiến trình tải xuống.

#### BR287 — Activity (6)

Success Notification:
1. returns 200-OK kèm MSG104.
2. Hiển thị thông báo kết xuất thành công trên giao diện quản trị.

#### BR288 — Activity (7)

Error Handling Rules:
1. returns 404-NOT_FOUND error với MSG105.
2. Hiển thị thông báo lỗi không thể tạo tệp do không có dữ liệu phù hợp với bộ lọc.

---

### UC-55 - Cấu hình phí giao hàng

#### BR289 — Activity (1)

Loading Rules:
1. Tải màn hình ShippingConfiguration_Screen.
2. Thực hiện truy vấn: [areaList] = ShippingRepository.findAllAreas().

#### BR290 — Activity (2) & (3)

Input & Submission Rules:
1. Hệ thống tiếp nhận giá trị phí vận chuyển mới [shippingFee] cho từng khu vực từ Business Admin.
2. Nhấn nút xác nhận để gửi danh sách cập nhật.

#### BR291 — Activity (4)

Validate Format Rules:
1. if isEmpty([shippingFee]) then returns 400-BAD_REQUEST error with MSG1.
2. if [shippingFee] < 0 then returns 400-BAD_REQUEST error with MSG107.
3. if pattern.compile('^[0-9]+(.[0-9]{1,2})?$').notMatch([shippingFee]) then returns 400-BAD_REQUEST error with MSG108.

#### BR292 — Activity (5)

Persistence Rules:
1. Thực hiện: ShippingRepository.UpdateFees([updatedList]).
2. Cập nhật dấu thời gian updatedAt cho các bản ghi thay đổi.

#### BR293 — Activity (6)

Success & Notification Rules:
1. returns 200-OK response với MSG106.
2. Làm mới giao diện và hiển thị mức phí mới nhất.

#### BR294 — Activity (7)

Error Handling Rules:
1. Hiển thị thông báo lỗi tương ứng (MSG107, MSG108) ngay tại dòng dữ liệu bị sai để người dùng điều chỉnh.

---

### UC-56 - Định nghĩa vai trò người dùng

#### BR295 — Activity (1) & (2)

Loading Matrix Rules:
1. Hệ thống truy vấn danh sách vai trò (Roles) và danh sách quyền (Permissions).
2. Hiển thị ma trận Role-Permission_Matrix cho phép Technical Admin đánh dấu chọn các quyền tương ứng cho từng vai trò.

#### BR296 — Activity (3)

Setting Rules:
1. Hệ thống tiếp nhận danh sách các cặp giá trị [roleId, permissionId] được thiết lập.

#### BR297 — Activity (4)

Data Structure Validation:
1. if [roleId] không tồn tại hoặc [permissionId] không hợp lệ then returns 400-BAD_REQUEST kèm MSG110.

#### BR298 — Activity (5) & (6)

Transactional Update Rules:
1. Việc cập nhật phải được thực hiện trong một Transaction để đảm bảo tính toàn vẹn dữ liệu.
2. Thực hiện: PermissionRepo.SyncRolePermissions([roleId], [selectedPermissions]).
3. if quá trình cập nhật gặp lỗi (Deadlock, Connection Timeout, v.v.) then GOTO Activity (8).

#### BR299 — Activity (7)

Success Notification:
1. returns 200-OK kèm MSG109.
2. Làm mới (Refresh) bộ nhớ đệm phân quyền (Authorization Cache) để các thay đổi có hiệu lực ngay lập tức.

#### BR300 — Activity (8)

Rollback & Error Rules:
1. Thực hiện Rollback toàn bộ dữ liệu về trạng thái trước khi thay đổi.
2. returns 500-INTERNAL_SERVER_ERROR kèm MSG110.

---

### UC-57 - Tạo tài khoản Business Admin ban đầu

#### BR301 — Activity (1) & (2)

Input & Role Selection Rules:
1. Hệ thống tiếp nhận các thông tin từ Technical Admin: [email], [fullName], [password].
2. Vai trò mặc định được gán cho tài khoản này là BUSINESS_ADMIN.

#### BR302 — Activity (3)

Validate Format Rules:
1. If any in [email], [fullName], [password] is empty then returns 400-BAD_REQUEST error with MSG1.
2. If pattern.compile('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$').notMatch([email]) then returns 400-BAD_REQUEST error with MSG2.

#### BR303 — Activity (4) & (5)

Security & Persistence Rules:
1. Password Encryption: Hệ thống thực hiện mã hóa mật khẩu: [hashedPassword] = hash([password]) trước khi lưu trữ.
2. Database Save: Thực hiện ConfigRepository.save(new Account([email], [hashedPassword], 'BUSINESS_ADMIN')) vào CSDL cấu hình hệ thống.

#### BR304 — Activity (6)

Success Notification Rules:
1. returns 201-CREATED response với MSG111.
2. Hiển thị thông báo khởi tạo thành công và cho phép đăng nhập bằng tài khoản quản trị mới.

#### BR305 — Activity (7)

Error Handling Rules:
1. returns 400-BAD_REQUEST kèm MSG2 để cảnh báo lỗi định dạng email.

---

### UC-58 - Giám sát Audit Log

#### BR306 — Activity (1) & (2)

Loading & Filter Rules:
1. Tải màn hình AuditLog_Monitor.
2. Hệ thống cung cấp bộ lọc tra cứu bao gồm: [userId], [actionType] (Ví dụ: CREATE, UPDATE, DELETE, LOGIN), và [timestampRange].

#### BR307 — Activity (3)

Data Retrieval Rules:
1. Hệ thống thực hiện truy vấn: [logEntries] = AuditRepository.findLogsByFilters([filters]).
2. Dữ liệu nhật ký phải được truy xuất theo chế độ Read-Only để đảm bảo tính toàn vẹn, không được phép chỉnh sửa hoặc xóa.

#### BR308 — Activity (4)

Pagination Rules:
1. Hệ thống thực hiện sắp xếp: [logEntries].OrderByDescending(l => l.Timestamp).
2. Áp dụng phân trang phía Server (Server-side Pagination): [pagedLogs] = [logEntries].Skip(offset).Take(limit) để tối ưu hiệu năng khi số lượng bản ghi nhật ký lớn.

#### BR309 — Activity (5)

Display Rules (Has Data):
1. if [pagedLogs].Count > 0 then render dữ liệu lên màn hình theo các cột: Thời gian, Người thực hiện, Hành động, Đối tượng tác động, và Chi tiết thay đổi (Old Value/New Value).

#### BR310 — Activity (6)

Empty State Rules:
1. if [pagedLogs].Count == 0 then hiển thị giao diện bảng trống kèm thông báo MSG112.

---

### UC-59 - Bật chế độ bảo trì và tự động sao lưu

#### BR311 — Activity (1)

Maintenance Initiation Rules:
1. Hệ thống tiếp nhận lệnh kích hoạt bảo trì từ Technical Admin.
2. Yêu cầu xác nhận lần cuối qua hộp thoại Maintenance_Confirm_Modal.

#### BR312 — Activity (2)

UI Redirection Rules:
1. Hệ thống thực hiện chuyển hướng toàn bộ traffic của khách hàng sang Maintenance_Page (Màn hình 503 Service Unavailable).
2. Chỉ cho phép các địa chỉ IP thuộc danh sách Admin_Whitelisted_IPs truy cập vào hệ thống quản trị.

#### BR313 — Activity (3)

Backup Execution Rules:
1. Hệ thống tự động kích hoạt script sao lưu toàn bộ Cơ sở dữ liệu: DatabaseService.BackupToCloudStorage().
2. Tệp sao lưu phải được định dạng theo chuẩn .sql.gz hoặc .bak kèm theo dấu thời gian.

#### BR314 — Activity (4)

Integrity Check Rules:
1. Hệ thống thực hiện: [isValid] = VerifyChecksum([backupFile]).
2. Kiểm tra dung lượng tệp sao lưu phải lớn hơn 0 và cấu trúc tệp không bị lỗi.
3. if [isValid] == true then proceeds to Activity (5).
4. else proceeds to Activity (6).

#### BR315 — Activity (5)

Success Notification Rules:
1. returns 200-OK kèm MSG113.
2. Ghi nhận trạng thái System_Status = 'MAINTENANCE' vào bảng cấu hình hệ thống.

#### BR316 — Activity (6)

Emergency Rollback Rules:
1. Hủy bỏ chế độ bảo trì và khôi phục giao diện khách hàng về trạng thái hoạt động bình thường.
2. returns 500-INTERNAL_SERVER_ERROR kèm cảnh báo khẩn cấp MSG114.

---

### UC-60 - Cập nhật cấu hình hệ thống không bảo mật

_Không tìm thấy bảng Business Rules tương ứng trong file SRS._
