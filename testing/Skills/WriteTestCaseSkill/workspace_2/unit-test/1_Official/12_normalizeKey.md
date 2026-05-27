## Information
Service Name: AddressDataService
Method Name: normalizeKey(String value)
Mock class: 
   1. None

Mock Data:
- None

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Normal Accent & Spaces) | TC 2 (Character đ) | TC 3 (Multiple Spaces) | TC 4 (Null Value) |
| :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Normal string with accents, uppercase, and spaces | String containing lowercase/uppercase 'đ' | String with multiple consecutive spaces | Null string input |
| **Inputs** | | | | |
| `value` | "  TP. Hồ Chí Minh  " | "Đồng Nai" | "Quận   1" | null |
| **Mock / Context Setup** | | | | |
| `SecurityContext` | null | null | null | null |
| **Expected Output** | "tp. ho chi minh" | "dong nai" | "quan 1" | NullPointerException |
