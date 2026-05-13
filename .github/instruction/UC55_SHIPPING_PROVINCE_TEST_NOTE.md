# UC55 - Test Note: Cau hinh phi giao hang theo province

## Pham vi can test

UC55 hien cau hinh phi giao hang theo `province`, luu o bang `shipping_fee_config`.

Endpoint:

- `GET /api/v1/business/shipping-fees`
- `PUT /api/v1/business/shipping-fees`

Payload cap nhat:

```json
[
  {
    "province": "Ho Chi Minh",
    "shippingFee": "25000"
  },
  {
    "province": "Ha Noi",
    "shippingFee": "30000.50"
  }
]
```

## Quyet dinh can test ky

Can quyet dinh san pham co seed san danh sach province hay khong.

### Phuong an A: Khong seed san province

Hanh vi hien tai:

- Business Admin tu them/cap nhat province bang `PUT /api/v1/business/shipping-fees`.
- `GET /api/v1/business/shipping-fees` chi tra ve nhung province da duoc cau hinh.
- Khi checkout hoac doi dia chi don hang, neu province cua dia chi chua co cau hinh phi ship thi he thong tra ve `400-BAD_REQUEST`.

Can test:

- Checkout voi province da cau hinh thanh cong va `shippingFee` dung.
- Checkout voi province chua cau hinh bi chan.
- Business Admin co the them province moi qua payload update.
- Ten province khac hoa/thuong, khac dau tieng Viet, hoac thua khoang trang van map ve cung mot cau hinh.

### Phuong an B: Seed san danh sach province

Neu chot seed san danh sach province:

- Can them danh sach tinh/thanh vao initializer hoac migration.
- `GET /api/v1/business/shipping-fees` phai tra ve day du province ngay tu lan chay dau.
- Can chot default shipping fee cho province chua duoc admin cap nhat.

Can test:

- Lan khoi dong dau co du danh sach province.
- Admin chi duoc cap nhat fee cho province hop le, khong tao province ngoai danh sach.
- Default fee co dung ky vong nghiep vu khong.
- Khi checkout, moi dia chi co province hop le deu co shipping fee.

## Validate fee

Can test cac rule tu UC55:

- `shippingFee` rong hoac null -> `400-BAD_REQUEST`.
- `shippingFee` am -> `400-BAD_REQUEST`.
- `shippingFee` sai format -> `400-BAD_REQUEST`.
- Hop le: `0`, `25000`, `25000.5`, `25000.50`.
- Khong hop le: `25000.555`, `25,000`, `abc`.

## Anh huong sang flow khac

Can regression test:

- Confirm order: `totalAmount = totalProductAmount + shippingFee - discountAmount`.
- Change shipping address UC26: doi sang province khac phai cap nhat lai `shippingFee`, `totalAmount`, va `Payment.amount`.
- Export/report doanh thu van lay theo `Order.totalAmount`, nen shipping fee moi se anh huong tong doanh thu.
