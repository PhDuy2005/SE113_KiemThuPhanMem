## Infomation
Service Name: AddressDataService
Method Name: addProvinceAlias(String value, ResProvinceDTO province)
Mock class: 
   1. None

Mock Data:
- provinceByAlias: empty map
- province: ResProvinceDTO(code="79", name="TP. Hồ Chí Minh")

## Testcase
### Testcase 1
Short Description: Add alias with valid value.
Input: value = "TP. Hồ Chí Minh", province = ResProvinceDTO(code="79")
Expected Output: provinceByAlias contains key "tp ho chi minh" with value province.
Actual Output: provinceByAlias.get("tp ho chi minh") == province

### Testcase 2
Short Description: Add alias with null value.
Input: value = null, province = ResProvinceDTO(code="79")
Expected Output: provinceByAlias remains unchanged.
Actual Output: provinceByAlias is empty.

### Testcase 3
Short Description: Add alias with blank value.
Input: value = "   ", province = ResProvinceDTO(code="79")
Expected Output: provinceByAlias remains unchanged.
Actual Output: provinceByAlias is empty.
