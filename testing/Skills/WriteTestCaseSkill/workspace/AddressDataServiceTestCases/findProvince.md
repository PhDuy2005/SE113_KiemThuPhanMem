## Infomation
Service Name: AddressDataService
Method Name: findProvince(String provinceCode, String provinceName)
Mock class: 
   1. None

Mock Data:
- provinceByCode: {"79": ResProvinceDTO(code="79", name="Thành phố Hồ Chí Minh")}
- provinceByAlias: {"thanh pho ho chi minh": ResProvinceDTO(code="79", name="Thành phố Hồ Chí Minh")}

## Testcase
### Testcase 1
Short Description: Find province by valid code.
Input: provinceCode = "79", provinceName = null
Expected Output: Optional(ResProvinceDTO(code="79"))
Actual Output: Optional(ResProvinceDTO(code="79"))

### Testcase 2
Short Description: Find province by blank code and valid name.
Input: provinceCode = " ", provinceName = "Thành phố Hồ Chí Minh"
Expected Output: Optional(ResProvinceDTO(code="79"))
Actual Output: Optional(ResProvinceDTO(code="79"))

### Testcase 3
Short Description: Find province by null code and null name.
Input: provinceCode = null, provinceName = null
Expected Output: Optional.empty()
Actual Output: Optional.empty()

### Testcase 4
Short Description: Find province by null code and blank name.
Input: provinceCode = null, provinceName = "   "
Expected Output: Optional.empty()
Actual Output: Optional.empty()

### Testcase 5
Short Description: Find province by invalid code.
Input: provinceCode = "999", provinceName = null
Expected Output: Optional.empty()
Actual Output: Optional.empty()

### Testcase 6
Short Description: Find province by invalid name (code is null).
Input: provinceCode = null, provinceName = "Unknown"
Expected Output: Optional.empty()
Actual Output: Optional.empty()
