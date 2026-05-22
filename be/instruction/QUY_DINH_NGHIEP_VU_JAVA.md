# Quy định nghiệp vụ hệ thống TechSales - Java Backend

> Tài liệu này mô tả các quy định nghiệp vụ cần áp dụng ở tầng Java Backend, chủ yếu cho `Service`, `Validator`, `Controller`, `Security` và xử lý transaction.  
> Tài liệu **không mô tả thiết kế database chi tiết** vì database đã được định nghĩa ở file `.txt` riêng.

---

## 1. Phạm vi áp dụng

Tài liệu áp dụng cho các module chính của hệ thống bán hàng TechSales:

- Tài khoản, hồ sơ người dùng, phân quyền.
- Địa chỉ giao hàng.
- Danh mục, sản phẩm, hình ảnh sản phẩm.
- Tồn kho.
- Giỏ hàng.
- Đơn hàng.
- Thanh toán.
- Voucher.
- Đánh giá sản phẩm và phản hồi đánh giá.
- Thông báo.

---

## 2. Quy ước chung khi code Java

### 2.1. Cấu trúc package gợi ý

```text
com.techsales
├── auth
├── user
├── role
├── permission
├── product
├── category
├── inventory
├── cart
├── order
├── payment
├── voucher
├── review
├── notification
├── common
│   ├── exception
│   ├── response
│   ├── security
│   ├── validation
│   └── util
```

### 2.2. Quy ước tầng xử lý

- `Controller` chỉ nhận request, validate cơ bản bằng annotation, gọi service và trả response.
- `Service` chứa toàn bộ nghiệp vụ chính.
- `Repository` chỉ truy vấn dữ liệu, không chứa business rule.
- `Mapper` chuyển đổi giữa Entity, DTO, Response.
- `Validator` dùng cho các rule phức tạp hoặc tái sử dụng nhiều nơi.
- Không để logic tính tiền, kiểm tra tồn kho, kiểm tra quyền trong Controller.

### 2.3. Quy ước transaction

Các nghiệp vụ sau bắt buộc dùng transaction:

- Tạo đơn hàng.
- Hủy đơn hàng.
- Duyệt đơn hàng.
- Cập nhật trạng thái giao hàng.
- Thanh toán online callback.
- Áp dụng voucher.
- Cập nhật tồn kho.
- Thay đổi địa chỉ mặc định.
- Tạo hoặc cập nhật review kèm phản hồi.

Gợi ý annotation:

```java
@Transactional
public OrderResponse createOrder(CreateOrderRequest request) {
    // business logic
}
```

### 2.4. Quy ước exception

Không trả lỗi bằng `null` hoặc `false`. Sử dụng exception rõ nghĩa.

Một số exception gợi ý:

```text
BusinessException
ResourceNotFoundException
UnauthorizedException
ForbiddenException
InvalidStatusException
OutOfStockException
VoucherInvalidException
PaymentException
DuplicateResourceException
```

### 2.5. Quy ước trạng thái

Các enum nghiệp vụ phải dùng enum Java, không dùng string rời rạc.

Ví dụ:

```java
public enum OrderStatus {
    PENDING,
    APPROVED,
    SHIPPING,
    DELIVERED,
    CANCELLED
}
```

---

## 3. Quy định về người dùng và xác thực

### 3.1. Trạng thái người dùng

Người dùng có 3 trạng thái:

| Trạng thái | Ý nghĩa |
|---|---|
| `PENDING` | Tài khoản mới tạo, chưa kích hoạt hoặc chưa hoàn tất xác thực. |
| `ACTIVE` | Tài khoản đang hoạt động bình thường. |
| `BLOCKED` | Tài khoản bị khóa bởi hệ thống hoặc quản trị viên. |

### 3.2. Đăng ký tài khoản

Khi đăng ký:

- Email không được trùng.
- Password phải được mã hóa trước khi lưu.
- Tài khoản mới mặc định có trạng thái `PENDING` hoặc `ACTIVE` tùy yêu cầu xác thực email của hệ thống.
- Mỗi tài khoản customer nên được gán role `CUSTOMER` mặc định.
- Sau khi tạo user, phải tạo hồ sơ người dùng tương ứng.

### 3.3. Đăng nhập

Chỉ cho phép đăng nhập khi:

- Email tồn tại.
- Password hợp lệ.
- User không bị `BLOCKED`.
- User không đang trong thời gian khóa tạm thời `lockedUntil`.
- User có trạng thái `ACTIVE`.

Khi đăng nhập sai:

- Tăng `failedLoginAttempts`.
- Cập nhật `lastFailedAt`.
- Nếu số lần sai vượt ngưỡng cấu hình, set `lockedUntil`.

Gợi ý cấu hình:

```properties
security.login.max-failed-attempts=5
security.login.lock-duration-minutes=15
```

Khi đăng nhập thành công:

- Reset `failedLoginAttempts` về `0`.
- Xóa `lockedUntil` nếu còn giá trị cũ.
- Trả access token hoặc session theo cơ chế xác thực của dự án.

### 3.4. Khóa tài khoản

Admin có thể khóa tài khoản bằng cách chuyển trạng thái user sang `BLOCKED`.

Quy định:

- User `BLOCKED` không được đăng nhập.
- User `BLOCKED` không được đặt hàng.
- User `BLOCKED` không được review.
- Không nên xóa vật lý user vì có liên quan tới đơn hàng, thanh toán và lịch sử giao dịch.

---

## 4. Quy định về vai trò và phân quyền

### 4.1. Vai trò mặc định

Hệ thống có các role chính:

| Role | Quyền tổng quát |
|---|---|
| `ADMIN` | Quản trị toàn hệ thống. |
| `STAFF` | Xử lý sản phẩm, đơn hàng, thanh toán, review tùy phân quyền. |
| `CUSTOMER` | Mua hàng, quản lý hồ sơ, địa chỉ, giỏ hàng, review. |

### 4.2. Permission

Permission được quản lý theo mã quyền, ví dụ:

```text
VIEW_ORDER
CREATE_PRODUCT
UPDATE_PRODUCT
DELETE_PRODUCT
APPROVE_ORDER
UPDATE_PAYMENT
MANAGE_VOUCHER
HIDE_REVIEW
```

Quy định:

- Không hard-code quyền bằng text rải rác trong nhiều class.
- Nên gom quyền vào constant hoặc enum.
- API quan trọng phải kiểm tra quyền bằng Spring Security hoặc custom annotation.

Ví dụ:

```java
@PreAuthorize("hasAuthority('APPROVE_ORDER')")
public OrderResponse approveOrder(UUID orderId) {
    // logic
}
```

---

## 5. Quy định về hồ sơ người dùng

### 5.1. Cập nhật hồ sơ

Customer chỉ được cập nhật hồ sơ của chính mình.

Các trường cần validate:

- `fullName`: bắt buộc, không rỗng.
- `phone`: bắt buộc, đúng định dạng số điện thoại.
- `avatarUrl`: nếu có thì phải là URL hợp lệ.
- `dateOfBirth`: nếu có thì không được lớn hơn ngày hiện tại.

### 5.2. Quyền xem hồ sơ

- Customer xem được hồ sơ của chính mình.
- Staff/Admin xem được hồ sơ khách hàng nếu có quyền phù hợp.

---

## 6. Quy định về địa chỉ giao hàng

### 6.1. Thêm địa chỉ

Khi thêm địa chỉ:

- User phải tồn tại và đang `ACTIVE`.
- `province`, `ward`, `detail` là bắt buộc.
- Nếu là địa chỉ đầu tiên của user thì tự động set `isDefault = true`.

### 6.2. Địa chỉ mặc định

Mỗi user chỉ được có tối đa 1 địa chỉ mặc định.

Khi set một địa chỉ là mặc định:

- Kiểm tra địa chỉ thuộc về user hiện tại.
- Set tất cả địa chỉ khác của user về `isDefault = false`.
- Set địa chỉ được chọn về `isDefault = true`.
- Thực hiện trong transaction.

### 6.3. Xóa địa chỉ

- Không xóa vật lý, dùng soft delete nếu hệ thống đã hỗ trợ `deletedAt`.
- Không cho xóa địa chỉ đang được sử dụng trong đơn hàng đang xử lý nếu cần tham chiếu trực tiếp.
- Vì đơn hàng lưu snapshot địa chỉ, việc xóa địa chỉ không được làm thay đổi địa chỉ trong đơn hàng cũ.

---

## 7. Quy định về danh mục sản phẩm

### 7.1. Tạo danh mục

- Tên danh mục không được rỗng.
- Không nên cho phép trùng tên danh mục sau khi chuẩn hóa khoảng trắng và chữ hoa/thường.

### 7.2. Xóa danh mục

- Không cho xóa danh mục nếu còn sản phẩm đang thuộc danh mục đó.
- Nếu cần ẩn danh mục, nên bổ sung trạng thái thay vì xóa vật lý.

---

## 8. Quy định về sản phẩm

### 8.1. Trạng thái sản phẩm

| Trạng thái | Ý nghĩa |
|---|---|
| `ACTIVE` | Sản phẩm đang được bán. |
| `DISCONTINUED` | Sản phẩm đã ngừng kinh doanh. |

### 8.2. Tạo sản phẩm

Khi tạo sản phẩm:

- `name` bắt buộc.
- `description` bắt buộc.
- `price` phải lớn hơn `0`.
- `brand` bắt buộc.
- Nếu có `categoryId`, danh mục phải tồn tại.
- Trạng thái mặc định nên là `ACTIVE`.
- Sau khi tạo sản phẩm, nên tạo bản ghi inventory với `quantity = 0` và `reservedQuantity = 0`.

### 8.3. Cập nhật sản phẩm

- Không cho cập nhật giá trực tiếp ảnh hưởng tới đơn hàng cũ.
- Giá trong đơn hàng phải là snapshot tại thời điểm mua.
- Nếu sản phẩm đã có trong đơn hàng, thay đổi tên, mô tả, ảnh chỉ ảnh hưởng hiển thị hiện tại, không thay đổi giá snapshot.

### 8.4. Ngừng bán sản phẩm

Khi chuyển sản phẩm sang `DISCONTINUED`:

- Không cho thêm vào giỏ hàng.
- Không cho tạo đơn hàng mới với sản phẩm đó.
- Không bắt buộc xóa khỏi giỏ hàng cũ, nhưng khi checkout phải báo lỗi sản phẩm đã ngừng bán.

---

## 9. Quy định về hình ảnh sản phẩm

### 9.1. Ảnh chính

Mỗi sản phẩm chỉ nên có tối đa 1 ảnh chính.

Khi set một ảnh là ảnh chính:

- Kiểm tra ảnh thuộc sản phẩm.
- Set toàn bộ ảnh khác của sản phẩm về `isPrimary = false`.
- Set ảnh được chọn về `isPrimary = true`.
- Thực hiện trong transaction.

### 9.2. Xóa ảnh

- Không cho xóa ảnh chính nếu sản phẩm vẫn còn ảnh khác mà chưa chỉ định ảnh chính mới.
- Nếu xóa ảnh chính, cần chọn ảnh khác làm ảnh chính hoặc cho phép sản phẩm không có ảnh chính theo quy định UI.

---

## 10. Quy định về tồn kho

### 10.1. Khái niệm tồn kho khả dụng

Tồn kho khả dụng được tính như sau:

```text
availableQuantity = quantity - reservedQuantity
```

Trong đó:

- `quantity`: số lượng tồn kho thật.
- `reservedQuantity`: số lượng đang được giữ cho các đơn hàng chưa hoàn tất hoặc chưa hủy.

### 10.2. Kiểm tra tồn kho

Khi thêm vào giỏ hàng hoặc checkout:

- Product phải `ACTIVE`.
- Số lượng yêu cầu phải lớn hơn `0`.
- Số lượng yêu cầu không được vượt quá `availableQuantity`.

### 10.3. Giữ hàng khi tạo đơn

Khi tạo đơn hàng:

- Kiểm tra tồn kho khả dụng cho từng sản phẩm.
- Tăng `reservedQuantity` tương ứng với số lượng trong đơn.
- Nếu có bất kỳ sản phẩm nào không đủ tồn kho thì rollback toàn bộ giao dịch.

### 10.4. Trừ kho thật

Khi đơn hàng hoàn tất thành công:

- Giảm `quantity` theo số lượng đã mua.
- Giảm `reservedQuantity` tương ứng.

Thời điểm trừ kho có thể chọn một trong hai cách:

1. Trừ khi đơn hàng được staff duyệt.
2. Trừ khi đơn hàng giao thành công.

Dự án cần thống nhất một cách. Khuyến nghị: giữ hàng khi tạo đơn, trừ kho thật khi đơn được `APPROVED` hoặc khi thanh toán online thành công và staff duyệt.

### 10.5. Hoàn tồn kho khi hủy đơn

Khi đơn hàng bị hủy:

- Nếu đơn đã giữ hàng thì giảm `reservedQuantity`.
- Không giảm `quantity` nếu hàng chưa bị trừ thật.
- Nếu hàng đã bị trừ thật, cần cộng lại `quantity` tùy chính sách hủy/hoàn hàng.

---

## 11. Quy định về giỏ hàng

### 11.1. Tạo giỏ hàng

- Mỗi user chỉ có một giỏ hàng.
- Khi user đăng ký hoặc lần đầu thêm sản phẩm, hệ thống tạo cart nếu chưa có.

### 11.2. Thêm sản phẩm vào giỏ hàng

Điều kiện:

- User phải `ACTIVE`.
- Product phải tồn tại và `ACTIVE`.
- Quantity phải lớn hơn `0`.
- Tổng quantity trong giỏ không được vượt quá tồn kho khả dụng.

Nếu sản phẩm đã có trong giỏ:

- Cộng dồn số lượng.
- Validate lại tổng số lượng sau khi cộng.

### 11.3. Cập nhật số lượng

- Quantity mới phải lớn hơn `0`.
- Nếu quantity bằng `0`, nên dùng API xóa item thay vì update.
- Không cho cập nhật vượt quá tồn kho khả dụng.

### 11.4. Xóa sản phẩm khỏi giỏ hàng

- Chỉ user sở hữu cart mới được xóa item.
- Xóa item không ảnh hưởng tới tồn kho vì giỏ hàng chưa giữ hàng.

---

## 12. Quy định về đơn hàng

### 12.1. Trạng thái đơn hàng

| Trạng thái | Ý nghĩa |
|---|---|
| `PENDING` | Đơn vừa tạo, chưa được xác nhận đầy đủ. |
| `APPROVED` | Đơn đã được xác nhận hoặc đã thanh toán hợp lệ và được staff duyệt. |
| `SHIPPING` | Đơn đang giao hàng. |
| `DELIVERED` | Đơn đã giao thành công. |
| `CANCELLED` | Đơn đã bị hủy. |

### 12.2. Tạo đơn hàng

Khi tạo đơn hàng:

- User phải `ACTIVE`.
- Giỏ hàng không được rỗng.
- Mỗi sản phẩm trong giỏ phải còn `ACTIVE`.
- Mỗi sản phẩm phải đủ tồn kho khả dụng.
- Địa chỉ giao hàng phải tồn tại và thuộc về user.
- Hệ thống phải lưu snapshot địa chỉ giao hàng vào đơn hàng.
- Hệ thống phải lưu snapshot giá sản phẩm vào từng order item.
- Tính tổng tiền sản phẩm, phí vận chuyển, giảm giá và tổng thanh toán.
- Nếu có voucher, validate voucher trước khi tạo đơn.
- Sau khi tạo đơn, tăng `reservedQuantity` cho từng sản phẩm.
- Sau khi tạo đơn thành công, có thể xóa giỏ hàng hoặc xóa các item đã checkout.

### 12.3. Công thức tính tiền

```text
totalProductAmount = sum(orderItem.price * orderItem.quantity)
discountAmount = voucherDiscount hoặc 0
shippingFee = phí giao hàng theo cấu hình hoặc rule vận chuyển
totalAmount = totalProductAmount + shippingFee - discountAmount
```

Quy định:

- `totalAmount` không được nhỏ hơn `0`.
- Không tin giá từ client gửi lên.
- Giá phải lấy từ product tại thời điểm tạo đơn.
- Discount phải được tính ở backend.

### 12.4. Staff xác nhận đơn hàng

Nhân viên luôn phải xác nhận đơn hàng.

Quy định:

- Đơn COD: staff có thể duyệt khi kiểm tra thông tin đơn hợp lệ.
- Đơn thanh toán online: staff chỉ duyệt khi payment đã `SUCCESS` và đủ số tiền.
- Chỉ staff/admin có quyền phù hợp mới được duyệt đơn.
- Chỉ đơn `PENDING` mới được chuyển sang `APPROVED`.

### 12.5. Luồng trạng thái hợp lệ

```text
PENDING -> APPROVED
APPROVED -> SHIPPING
SHIPPING -> DELIVERED
PENDING -> CANCELLED
APPROVED -> CANCELLED
```

Không cho phép:

```text
DELIVERED -> CANCELLED
CANCELLED -> APPROVED
CANCELLED -> SHIPPING
CANCELLED -> DELIVERED
PENDING -> SHIPPING
```

### 12.6. Hủy đơn hàng

Customer được hủy đơn khi:

- Đơn thuộc về customer đó.
- Đơn đang ở trạng thái `PENDING`.

Staff/Admin có thể hủy đơn khi:

- Đơn đang ở trạng thái `PENDING` hoặc `APPROVED`.
- Có lý do hủy hợp lệ nếu hệ thống yêu cầu.

Khi hủy đơn:

- Chuyển trạng thái order sang `CANCELLED`.
- Hoàn reserved inventory.
- Nếu đã thanh toán online thành công, cần tạo quy trình hoàn tiền hoặc đánh dấu cần hoàn tiền.
- Nếu có voucher đã tăng `usedCount`, cần giảm lại nếu chính sách cho phép.

### 12.7. Giao hàng thành công

Khi đơn chuyển sang `DELIVERED`:

- Đơn trước đó phải là `SHIPPING`.
- Nếu payment là COD/CASH, payment chuyển sang `SUCCESS`.
- Nếu còn giữ reserved inventory thì xử lý giảm reserved theo chính sách tồn kho đã thống nhất.
- Tạo thông báo cho customer.

---

## 13. Quy định về thanh toán

### 13.1. Phương thức thanh toán

Hệ thống có 2 loại phương thức:

| Type | Ý nghĩa |
|---|---|
| `ONLINE` | Thanh toán qua cổng thanh toán. |
| `CASH` | Thanh toán tiền mặt khi nhận hàng. |

### 13.2. Trạng thái thanh toán

| Trạng thái | Ý nghĩa |
|---|---|
| `PENDING` | Vừa tạo, chưa xử lý xong. |
| `SUCCESS` | Thanh toán thành công. |
| `FAILED` | Thanh toán thất bại. |
| `CANCELLED` | User hủy hoặc giao dịch timeout. |

### 13.3. Tạo payment

Khi tạo order, hệ thống tạo payment tương ứng.

Quy định:

- Amount của payment phải bằng `order.totalAmount`.
- Payment method phải tồn tại.
- Với CASH/COD, payment ban đầu là `PENDING`.
- Với ONLINE, payment ban đầu là `PENDING` cho tới khi nhận callback thành công từ cổng thanh toán.

### 13.4. Callback thanh toán online

Khi nhận callback:

- Verify chữ ký hoặc mã xác thực từ cổng thanh toán.
- Kiểm tra `transactionRef` không bị xử lý trùng.
- Kiểm tra amount từ cổng thanh toán khớp với payment amount.
- Nếu hợp lệ, cập nhật payment sang `SUCCESS`.
- Không tự động chuyển order sang `APPROVED` nếu quy định yêu cầu staff phải duyệt.
- Nếu thất bại, cập nhật payment sang `FAILED`.

### 13.5. Thanh toán COD

Với COD/CASH:

- Payment giữ trạng thái `PENDING` trong quá trình giao hàng.
- Khi staff cập nhật order sang `DELIVERED`, payment tự chuyển sang `SUCCESS`.
- Nếu order bị hủy trước khi giao, payment chuyển sang `CANCELLED`.

---

## 14. Quy định về voucher

### 14.1. Loại voucher

| Type | Ý nghĩa |
|---|---|
| `FIXED` | Giảm số tiền cố định. |
| `PERCENT` | Giảm theo phần trăm. |

### 14.2. Điều kiện voucher hợp lệ

Voucher chỉ được áp dụng khi:

- Voucher tồn tại.
- `isActive = true`.
- Thời gian hiện tại nằm trong khoảng `startDate` và `endDate` nếu có cấu hình.
- `usedCount < maxUsage` nếu có giới hạn lượt dùng.
- Tổng tiền sản phẩm đạt `minOrderAmount` nếu có cấu hình.
- Voucher chưa bị áp dụng trùng cho cùng một đơn.

### 14.3. Tính tiền giảm

Với `FIXED`:

```text
discountAmount = voucher.value
```

Với `PERCENT`:

```text
discountAmount = totalProductAmount * voucher.value / 100
```

Quy định:

- Discount không được lớn hơn tổng tiền sản phẩm.
- Không để tổng thanh toán âm.
- Nên làm tròn tiền theo chuẩn `BigDecimal` trong Java.

### 14.4. Tăng lượt dùng voucher

Khi đơn hàng tạo thành công và voucher được áp dụng:

- Tăng `usedCount`.
- Cần xử lý transaction để tránh vượt `maxUsage` khi nhiều user đặt cùng lúc.
- Có thể dùng pessimistic lock hoặc optimistic lock tùy thiết kế.

---

## 15. Quy định về review

### 15.1. Trạng thái review

| Trạng thái | Ý nghĩa |
|---|---|
| `VISIBLE` | Review hiển thị công khai. |
| `HIDDEN` | Review bị ẩn bởi staff/admin. |
| `DELETED` | Review đã bị xóa mềm. |

### 15.2. Tạo review

Customer được review khi:

- User đang `ACTIVE`.
- Product tồn tại.
- Rating nằm trong khoảng `1` đến `5`.
- Customer đã mua sản phẩm và đơn hàng đã `DELIVERED`.
- Mỗi customer chỉ nên review một lần cho mỗi sản phẩm, trừ khi hệ thống cho phép cập nhật review cũ.

Khi tạo review:

- Trạng thái mặc định là `VISIBLE`.
- Comment có thể rỗng nếu rating đã có.

### 15.3. Ẩn review

Staff/Admin có thể ẩn review khi:

- Có quyền quản lý review.
- Review đang `VISIBLE`.

Không xóa vật lý review nếu cần lưu lịch sử.

### 15.4. Phản hồi review

- Chỉ staff/admin hoặc user có quyền phù hợp được phản hồi review với tư cách cửa hàng.
- Nội dung phản hồi không được rỗng.
- Không phản hồi review đã `DELETED`.

---

## 16. Quy định về thông báo

### 16.1. Tạo thông báo

Thông báo nên được tạo trong các trường hợp:

- Đơn hàng được tạo thành công.
- Đơn hàng được duyệt.
- Đơn hàng chuyển sang giao hàng.
- Đơn hàng giao thành công.
- Đơn hàng bị hủy.
- Thanh toán thành công hoặc thất bại.
- Review được phản hồi.

### 16.2. Đọc thông báo

- User chỉ được đọc thông báo của chính mình.
- Khi user đánh dấu đã đọc, set `isRead = true`.
- API danh sách thông báo nên hỗ trợ phân trang.

---

## 17. Quy định bảo mật API

### 17.1. API public

Các API có thể public:

- Đăng ký.
- Đăng nhập.
- Xem danh sách sản phẩm active.
- Xem chi tiết sản phẩm active.
- Xem danh mục.

### 17.2. API yêu cầu đăng nhập

Các API yêu cầu user authenticated:

- Xem/cập nhật hồ sơ cá nhân.
- Quản lý địa chỉ giao hàng.
- Quản lý giỏ hàng.
- Tạo đơn hàng.
- Xem đơn hàng của chính mình.
- Thanh toán.
- Tạo review.
- Xem thông báo cá nhân.

### 17.3. API dành cho staff/admin

Các API cần kiểm tra role hoặc permission:

- Quản lý sản phẩm.
- Quản lý danh mục.
- Quản lý tồn kho.
- Duyệt đơn hàng.
- Cập nhật trạng thái giao hàng.
- Quản lý thanh toán.
- Quản lý voucher.
- Ẩn review.
- Xem danh sách user.

---

## 18. Quy định validate DTO

### 18.1. Request tạo sản phẩm

```java
public class CreateProductRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal price;

    @NotBlank
    private String brand;

    private UUID categoryId;
}
```

### 18.2. Request thêm vào giỏ hàng

```java
public class AddCartItemRequest {
    @NotNull
    private UUID productId;

    @Min(1)
    private int quantity;
}
```

### 18.3. Request tạo review

```java
public class CreateReviewRequest {
    @NotNull
    private UUID productId;

    @Min(1)
    @Max(5)
    private int rating;

    private String comment;
}
```

---

## 19. Quy định xử lý tiền trong Java

- Luôn dùng `BigDecimal` để xử lý tiền.
- Không dùng `float` hoặc `double` cho giá tiền.
- Khi nhân/chia phần trăm, phải set scale và rounding mode rõ ràng.

Ví dụ:

```java
BigDecimal discount = totalProductAmount
        .multiply(voucherValue)
        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
```

---

## 20. Quy định logging

Nên log ở các nghiệp vụ quan trọng:

- Đăng nhập sai nhiều lần.
- Tạo đơn hàng.
- Hủy đơn hàng.
- Thanh toán thành công/thất bại.
- Callback thanh toán không hợp lệ.
- Cập nhật tồn kho.
- Staff duyệt hoặc hủy đơn.

Không log:

- Password.
- Token.
- Thông tin nhạy cảm của payment gateway.

---

## 21. Checklist triển khai service

### 21.1. UserService

- Đăng ký user.
- Kích hoạt user nếu có xác thực.
- Cập nhật profile.
- Khóa/mở khóa user.
- Kiểm tra trạng thái user trước nghiệp vụ quan trọng.

### 21.2. ProductService

- Tạo/cập nhật sản phẩm.
- Chuyển trạng thái sản phẩm.
- Kiểm tra sản phẩm active.
- Quản lý ảnh sản phẩm.

### 21.3. InventoryService

- Kiểm tra tồn kho khả dụng.
- Tăng/giảm tồn kho thật.
- Giữ hàng khi tạo đơn.
- Hoàn giữ hàng khi hủy đơn.

### 21.4. CartService

- Tạo cart nếu chưa có.
- Thêm item.
- Cập nhật quantity.
- Xóa item.
- Clear cart sau checkout.

### 21.5. OrderService

- Tạo order từ cart.
- Tính tiền.
- Áp dụng voucher.
- Duyệt order.
- Hủy order.
- Cập nhật trạng thái giao hàng.
- Tạo notification theo trạng thái.

### 21.6. PaymentService

- Tạo payment theo order.
- Xử lý callback online.
- Cập nhật payment COD khi delivered.
- Kiểm tra payment success trước khi staff duyệt đơn online.

### 21.7. VoucherService

- Validate voucher.
- Tính discount.
- Tăng/giảm used count.
- Kiểm tra giới hạn lượt dùng.

### 21.8. ReviewService

- Tạo review sau khi mua hàng.
- Ẩn review.
- Xóa mềm review.
- Phản hồi review.

---

## 22. Quy định response API

Response nên thống nhất dạng:

```json
{
  "success": true,
  "message": "Success",
  "data": {}
}
```

Response lỗi:

```json
{
  "success": false,
  "message": "Voucher is expired",
  "errorCode": "VOUCHER_EXPIRED"
}
```

Một số error code gợi ý:

```text
USER_NOT_FOUND
USER_BLOCKED
INVALID_CREDENTIALS
PRODUCT_NOT_FOUND
PRODUCT_INACTIVE
OUT_OF_STOCK
CART_EMPTY
ORDER_NOT_FOUND
INVALID_ORDER_STATUS
PAYMENT_FAILED
PAYMENT_AMOUNT_MISMATCH
VOUCHER_NOT_FOUND
VOUCHER_EXPIRED
VOUCHER_USAGE_EXCEEDED
REVIEW_NOT_ALLOWED
FORBIDDEN
```

---

## 23. Quy định test nghiệp vụ

Cần có unit test hoặc integration test cho các case sau:

- Đăng nhập sai quá số lần cho phép thì khóa tạm thời.
- User bị blocked không được đăng nhập hoặc đặt hàng.
- Thêm sản phẩm vào giỏ vượt tồn kho thì lỗi.
- Checkout với sản phẩm discontinued thì lỗi.
- Tạo order giữ hàng thành công.
- Tạo order thất bại thì không thay đổi tồn kho.
- Voucher hết hạn không được áp dụng.
- Voucher vượt max usage không được áp dụng.
- Staff không được duyệt đơn online nếu payment chưa success.
- COD chuyển payment sang success khi order delivered.
- Customer không được hủy đơn đã shipping hoặc delivered.
- Customer chỉ được review sản phẩm đã mua và delivered.

---

## 24. Luồng nghiệp vụ chính

### 24.1. Luồng đặt hàng COD

```text
Customer thêm sản phẩm vào giỏ
-> Customer checkout
-> Backend kiểm tra user, cart, product, inventory, voucher
-> Backend tạo order PENDING
-> Backend tạo payment CASH/COD PENDING
-> Backend giữ hàng bằng reservedQuantity
-> Staff kiểm tra và duyệt order
-> Order chuyển APPROVED
-> Staff chuyển SHIPPING
-> Giao hàng thành công
-> Staff chuyển DELIVERED
-> Payment chuyển SUCCESS
-> Backend gửi notification cho customer
```

### 24.2. Luồng đặt hàng online

```text
Customer thêm sản phẩm vào giỏ
-> Customer checkout
-> Backend tạo order PENDING
-> Backend tạo payment ONLINE PENDING
-> Customer thanh toán qua cổng thanh toán
-> Payment gateway callback
-> Backend verify callback
-> Payment chuyển SUCCESS
-> Staff kiểm tra và duyệt order
-> Order chuyển APPROVED
-> Staff chuyển SHIPPING
-> Staff chuyển DELIVERED khi giao thành công
-> Backend gửi notification cho customer
```

### 24.3. Luồng hủy đơn

```text
User hoặc staff yêu cầu hủy đơn
-> Backend kiểm tra quyền hủy
-> Backend kiểm tra trạng thái đơn
-> Order chuyển CANCELLED
-> Hoàn reserved inventory
-> Payment chuyển CANCELLED hoặc tạo quy trình refund nếu đã SUCCESS
-> Hoàn usedCount voucher nếu chính sách cho phép
-> Gửi notification cho customer
```

---

## 25. Ghi chú triển khai

- Tất cả nghiệp vụ thay đổi nhiều bảng phải đặt trong transaction.
- Không tin dữ liệu tiền, quyền, trạng thái do client gửi lên.
- Các rule trạng thái nên gom vào method riêng để dễ test.
- Nên tách các rule tính tiền, voucher, inventory thành service riêng để tránh `OrderService` quá lớn.
- Cần thống nhất thời điểm trừ kho thật trong team trước khi code.
- Cần thống nhất chính sách hoàn voucher và hoàn tiền khi hủy đơn đã thanh toán.
