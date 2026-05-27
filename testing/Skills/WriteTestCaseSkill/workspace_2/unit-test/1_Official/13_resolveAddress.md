## Information
Service Name: AddressDataService
Method Name: resolveAddress(String provinceCode, String provinceName, String wardCode, String wardName, String detail)
Mock class: 
   1. None

Mock Data:
- None

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Invalid Province) | TC 2 (Invalid Ward) | TC 3 (Null Detail) | TC 4 (Blank Detail) | TC 5 (Success Normal) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Resolve address with non-existent province | Valid province, invalid/non-existent ward | Valid province & ward, null detail | Valid province & ward, blank detail | Resolve completely valid inputs successfully |
| **Inputs** | | | | | |
| `provinceCode` | "999" | "79" | "79" | "79" | "79" |
| `provinceName` | null | null | null | null | null |
| `wardCode` | "27382" | "99999" | "27382" | "27382" | "27382" |
| `wardName` | null | null | null | null | null |
| `detail` | "123 Lý Tự Trọng" | "123 Lý Tự Trọng" | null | "   " | " 123 Lý Tự Trọng " |
| **Mock / Context Setup** | | | | | |
| `SecurityContext` | null | null | null | null | null |
| **Expected Output** | BusinessException(400, "Province is invalid") | BusinessException(400, "Ward is invalid for selected province") | BusinessException(400, "Detail must not be empty") | BusinessException(400, "Detail must not be empty") | ResolvedAddress(provinceCode="79", province="TP. Hồ Chí Minh", wardCode="27382", ward="Phường Bến Nghé", detail="123 Lý Tự Trọng") |
