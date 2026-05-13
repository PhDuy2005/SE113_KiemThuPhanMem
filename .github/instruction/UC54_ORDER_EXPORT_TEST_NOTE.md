# UC54 Order Export Test Note

## Scope

Applies to:

`GET /api/v1/business/reports/orders/export`

## Required Filters

- `startDate`
- `endDate`

Optional:

- `orderStatus`

Date format:

```text
YYYY-MM-DD
```

Example:

```http
GET /api/v1/business/reports/orders/export?startDate=2026-05-01&endDate=2026-05-13&orderStatus=DELIVERED
```

## Access Control

- Only `BUSINESS_ADMIN` should be able to export.
- Unauthenticated users should receive `401`.
- Non-business-admin users should receive `403`.

## Validation Cases

- Missing `startDate` -> `400`.
- Missing `endDate` -> `400`.
- `endDate < startDate` -> `400`.
- Filter has no matching orders -> `404`.

## File Response Checks

Successful response must include:

- HTTP `200`.
- `Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`.
- `Content-Disposition: attachment; filename=Orders_Export_{Date}.xlsx`.
- Response body must be a valid `.xlsx` file.

## Workbook Content Checks

The workbook should contain one sheet named:

```text
Orders
```

Header row:

- `Order ID`
- `Order Date`
- `Customer`
- `Total Amount`
- `Status`
- `Payment Method`

Verify each exported row:

- `Order ID` matches DB order id.
- `Order Date` uses `Order.createdAt`.
- `Customer` uses full name, fallback to email when full name is empty.
- `Total Amount` matches `Order.totalAmount`.
- `Status` matches `Order.status`.
- `Payment Method` matches the order payment method.

## Filter Behavior

- Without `orderStatus`, export all orders in the created date range.
- With `orderStatus`, export only orders matching that status.
- Date range uses `Order.createdAt` from start date `00:00:00` to end date `23:59:59.999`.

## Risk Areas

- Large exports are currently built in memory before returning the response.
- Numeric currency is exported as a number, so Excel display formatting may depend on client locale.
- The file name uses the current server date, not the filter date range.
