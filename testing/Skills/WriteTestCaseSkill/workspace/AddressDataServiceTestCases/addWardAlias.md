## Infomation
Service Name: AddressDataService
Method Name: addWardAlias(Map<String, ResWardDTO> aliasMap, String value, ResWardDTO ward)
Mock class: 
   1. None

Mock Data:
- aliasMap: empty map
- ward: ResWardDTO(code="26734", name="Phường Bến Nghé")

## Testcase
### Testcase 1
Short Description: Add alias with valid value.
Input: aliasMap = {}, value = "Phường Bến Nghé", ward = ResWardDTO(code="26734")
Expected Output: aliasMap contains key "phuong ben nghe" with value ward.
Actual Output: aliasMap.get("phuong ben nghe") == ward

### Testcase 2
Short Description: Add alias with null value.
Input: aliasMap = {}, value = null, ward = ResWardDTO(code="26734")
Expected Output: aliasMap remains unchanged.
Actual Output: aliasMap is empty.

### Testcase 3
Short Description: Add alias with blank value.
Input: aliasMap = {}, value = "   ", ward = ResWardDTO(code="26734")
Expected Output: aliasMap remains unchanged.
Actual Output: aliasMap is empty.
