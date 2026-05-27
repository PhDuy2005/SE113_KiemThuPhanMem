## Information
Service Name: AddressDataService
Method Name: findProvince(String provinceCode, String provinceName)
Mock class: 
   1. None

Mock Data:
- None

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Valid Code) | TC 2 (Non-existent Code) | TC 3 (Null Code, Valid Name) | TC 4 (Blank Code, Valid Name) | TC 5 (Null Code & Name) | TC 6 (Null Code, Blank Name) | TC 7 (Null Code, Mismatch Name) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Search by valid province code | Search by non-existent province code | Null province code, valid province name | Blank province code, valid province name | Null province code & null name | Null province code & blank name | Null province code & non-existent name |
| **Inputs** | | | | | | | |
| `provinceCode` | "79" | "999" | null | "   " | null | null | null |
| `provinceName` | null | null | "TP. Hồ Chí Minh" | "TP. Hồ Chí Minh" | null | "   " | "Ha Noi" |
| **Mock / Context Setup** | | | | | | | |
| `SecurityContext` | null | null | null | null | null | null | null |
| **Expected Output** | resProvinceDTO.code = "79" | resProvinceDTO = null | resProvinceDTO.code = "79" | resProvinceDTO.code = "79" | resProvinceDTO = null | resProvinceDTO = null | resProvinceDTO = null |
