## Infomation
Service Name: AddressDataService
Method Name: requireProvince(String provinceCode, String provinceName)
Mock class: 
   1. None

Mock Data:
- Provinces: {"79": ResProvinceDTO(code="79", name="Thành phố Hồ Chí Minh")}

## Testcase
### Testcase 1
Short Description: Require province with valid code.
Input: provinceCode = "79", provinceName = null
Expected Output: ResProvinceDTO(code="79", name="Thành phố Hồ Chí Minh")
Actual Output: ResProvinceDTO(code="79", name="Thành phố Hồ Chí Minh")

### Testcase 2
Short Description: Require province with valid name (code is null).
Input: provinceCode = null, provinceName = "Thành phố Hồ Chí Minh"
Expected Output: ResProvinceDTO(code="79", name="Thành phố Hồ Chí Minh")
Actual Output: ResProvinceDTO(code="79", name="Thành phố Hồ Chí Minh")

### Testcase 3
Short Description: Require province with invalid code and name.
Input: provinceCode = "999", provinceName = "Unknown Province"
Expected Output: BusinessException with status 400 and message "Province is invalid".
Actual Output: BusinessException(400, "Province is invalid")
