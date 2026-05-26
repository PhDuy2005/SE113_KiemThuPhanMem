## Infomation
Service Name: AddressDataService
Method Name: getWardsByProvinceCode(String provinceCode)
Mock class: 
   1. None (Constructor loads data from JSON files)

Mock Data:
- Provinces: {"79": ResProvinceDTO(code="79", name="Thành phố Hồ Chí Minh")}
- Wards for "79": [ResWardDTO(code="26734", name="Phường Bến Nghé", parentCode="79")]

## Testcase
### Testcase 1
Short Description: Get wards for a valid province code.
Input: provinceCode = "79"
Expected Output: List of wards for province "79".
Actual Output: [ResWardDTO(code="26734", name="Phường Bến Nghé", parentCode="79")]

### Testcase 2
Short Description: Get wards for an invalid province code.
Input: provinceCode = "999"
Expected Output: BusinessException with status 400 and message "Province is invalid".
Actual Output: BusinessException(400, "Province is invalid")
