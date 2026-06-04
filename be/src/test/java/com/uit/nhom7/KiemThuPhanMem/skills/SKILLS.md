# Quy Định Viết Unit Test Ánh Xạ 1-1 Với Kịch Bản Test (Markdown)

Nhằm đảm bảo mỗi kịch bản test (file `.md`) được đối chiếu và ánh xạ chính xác với mã nguồn, khi thực hiện refactor hoặc viết mới unit test, cần tuân thủ nghiêm ngặt các quy định sau:

## 1. Cấu Trúc Thư Mục & Package
- Các file unit test đã được tách nhỏ sẽ **không** nằm chung trong một file `[ServiceName]Test.java`.
- **Tạo một thư mục con** (folder) mang tên của Service đó, nhưng phải viết theo chuẩn **snake_case** (toàn bộ chữ thường, cách nhau bởi dấu gạch dưới) để chứa các file test. 
  - Ví dụ: Các test của `CartService` sẽ được đặt trong thư mục `cart_service` (KHÔNG dùng `CartService` hay `cartservice` để tránh lỗi đụng độ tên Package và Class trong Java).
  - Khai báo package trong các file Java lúc này phải là chuẩn snake_case: `package com.uit.nhom7.KiemThuPhanMem.service.cart_service;`

## 2. Quy Tắc Đặt Tên File, Class & Hàm Test
- Đặt tên file (và class) theo cấu trúc: `FUNCxxxxx_<TênMethod>.java`
- Trong đó:
  - `FUNC` là tiền tố cố định.
  - `xxxxx` là **5 chữ số** đại diện cho số thứ tự của file `.md` tương ứng (padding thêm các số `0` ở đầu nếu cần).
  - `<TênMethod>` là tên hàm đang được test (trích xuất từ tên file `.md`).
- **Ví dụ**:
  - File `.md`: `15_addItem.md` -> Sinh ra file: `FUNC00015_addItem.java`
  - File `.md`: `2_register.md` -> Sinh ra file: `FUNC00002_register.java`
  - File `.md`: `29_lockStaff.md` -> Sinh ra file: `FUNC00029_lockStaff.java`
- **Tên hiển thị của bài test**: Bắt buộc sử dụng `@DisplayName` (của JUnit 5) trên mỗi hàm `@Test` để miêu tả rõ ràng mục đích của test case.
  - Cú pháp chuẩn: `@DisplayName("FUNCxxxxx_UTCIDyy - <Mô tả ngắn gọn>")`
  - Trong đó `xxxxx` là mã hàm, `yy` là số thứ tự test case, lấy chính xác mô tả từ file `.md`
  - Ví dụ: `@DisplayName("FUNC00001_UTCID01 - Login fails because email does not exist")`.

## 3. Tái Sử Dụng Fixture & Setup Dữ Liệu
- Không được duplicate (lặp lại) code khởi tạo mock data (`Fixture`, `authenticate`, `activeUser`, v.v.) trong từng file test.
- Phải tạo một class **Base** chứa code setup dùng chung. 
  - Ví dụ: `CartServiceTestBase.java` đặt trong thư mục chứa chung hoặc trong thư mục `CartService`.
- Tất cả các class `FUNCxxxxx_...` phải **kế thừa (extends)** từ class Base tương ứng của Service đó.

## 4. Quy trình Từng Bước Khi Tách File Test (Refactor)
1. Đọc và gom các hàm test dùng chung lại.
2. Tạo file Base (vd: `CartServiceTestBase.java`) để chứa mock repository, mock data.
3. Tạo thư mục `CartService`.
4. Lần lượt bóc tách các `@Test` ra từng file `FUNCxxxxx_<Method>.java` theo đúng logic ánh xạ với file `.md`.
5. Xóa file `[ServiceName]Test.java` (gốc) đã bị phình to sau khi tách xong.

## 5. Quy Tắc Lệnh (Trigger Command)
- Khi user gõ lệnh có cú pháp `TC!xx` (ví dụ: `TC!01`, `TC!15`), AI sẽ tự động tiến hành:
  1. Đọc kịch bản trong file `.md` có thứ tự tương ứng (ví dụ `01`, `15`).
  2. Viết/Refactor mã nguồn Java Unit Test cho kịch bản đó theo đúng các quy chuẩn về cấu trúc thư mục, tên file và base class như đã nêu ở trên.
