## Information
Service Name: AddressDataService
Method Name: findWard(String provinceCode, String wardCode, String wardName)
Mock class: 
   1. None

Mock Data:
- None

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Code & Parent Match) | TC 2 (Code & Parent Mismatch) | TC 3 (Non-existent Code) | TC 4 (Null Code, Valid Name) | TC 5 (Blank Code, Valid Name) | TC 6 (Null Code & Name) | TC 7 (Null Code, Blank Name) | TC 8 (Null Code, Mismatch Name) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description**| Search valid ward code and matching parent province | Valid ward code, parent province mismatch | Non-existent ward code | Null ward code, valid ward name | Blank ward code, valid ward name | Null ward code, null ward name | Null ward code, blank ward name | Null ward code, non-existent ward name |
| **Inputs** | | | | | | | | |
| `provinceCode` | "79" | "01" | "79" | "79" | "79" | "79" | "79" | "79" |
| `wardCode` | "27382" | "27382" | "99999" | null | "   " | null | null | null |
| `wardName` | null | null | null | "Phường Bến Nghé" | "Phường Bến Nghé" | null | "   " | "Phuong X" |
| **Mock / Context Setup**| | | | | | | | |
| `SecurityContext` | null | null | null | null | null | null | null | null |
| **Expected Output** | resWardDTO.code = "27382" | resWardDTO = null | resWardDTO = null | resWardDTO.code = "27382" | resWardDTO.code = "27382" | resWardDTO = null | resWardDTO = null | resWardDTO = null |
