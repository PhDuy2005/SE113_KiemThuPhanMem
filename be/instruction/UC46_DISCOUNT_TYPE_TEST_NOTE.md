# UC46 Discount Type Test Note

## Scope

Applies to voucher creation:

`POST /api/v1/business/vouchers`

## Current Definition

`discountType` currently supports only:

- `FIXED`
- `PERCENT`

The value is normalized to uppercase before saving.

## Expected Meaning

### `FIXED`

Fixed amount discount.

Example:

```json
{
  "discountType": "FIXED",
  "discountValue": 50000
}
```

Expected interpretation: subtract `50,000` from the order total, subject to other voucher conditions.

### `PERCENT`

Percentage discount.

Example:

```json
{
  "discountType": "PERCENT",
  "discountValue": 10
}
```

Expected interpretation: discount `10%` of the eligible order amount, subject to other voucher conditions.

## Validation Cases

- Missing `discountType` -> `400`.
- Empty `discountType` -> `400`.
- Unsupported value such as `AMOUNT`, `RATE`, `CASH`, `abc` -> `400`.
- Lowercase/mixed-case values such as `fixed`, `Percent` should be accepted and stored as uppercase.
- Missing `discountValue` -> `400`.
- `discountValue <= 0` -> `400`.

## Risk Areas To Confirm

- For `PERCENT`, the current implementation validates only `discountValue > 0`.
- There is no current upper-bound validation like `discountValue <= 100`.
- If the business expects percentage vouchers to be capped at `100%`, add this rule to the SRS and backend validation.
- The DB schema has `VoucherType { FIXED, PERCENT }`, so frontend, backend, and test data should use exactly these two semantic values.
