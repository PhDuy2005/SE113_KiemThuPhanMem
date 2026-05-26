## Infomation
Service Name: AddressDataService
Method Name: resolveAddress(String provinceCode, String provinceName, String wardCode, String wardName, String detail)
Mock class: 
   1. None

Mock Data:
- Provinces: {"79": ResProvinceDTO(code="79", name="Thành phố Hồ Chí Minh")}
- Wards for "79": {"26734": ResWardDTO(code="26734", name="Phường Bến Nghé", parentCode="79")}

## Testcase
### Testcase 1
Short Description: Resolve valid address.
Input: provinceCode = "79", provinceName = null, wardCode = "26734", wardName = null, detail = "123 Le Loi"
Expected Output: ResolvedAddress(provinceCode="79", province="Thành phố Hồ Chí Minh", wardCode="26734", ward="Phường Bến Nghé", detail="123 Le Loi")
Actual Output: ResolvedAddress(provinceCode="79", province="Thành phố Hồ Chí Minh", wardCode="26734", ward="Phường Bến Nghé", detail="123 Le Loi")

### Testcase 2
Short Description: Resolve address with invalid ward.
Input: provinceCode = "79", provinceName = null, wardCode = "99999", wardName = null, detail = "123 Le Loi"
Expected Output: BusinessException with status 400 and message "Ward is invalid for selected province".
Actual Output: BusinessException(400, "Ward is invalid for selected province")

### Testcase 3
Short Description: Resolve address with null detail.
Input: provinceCode = "79", provinceName = null, wardCode = "26734", wardName = null, detail = null
Expected Output: BusinessException with status 400 and message "Detail must not be empty".
Actual Output: BusinessException(400, "Detail must not be empty")

### Testcase 4
Short Description: Resolve address with blank detail.
Input: provinceCode = "79", provinceName = null, wardCode = "26734", wardName = null, detail = "   "
Expected Output: BusinessException with status 400 and message "Detail must not be empty".
Actual Output: BusinessException(400, "Detail must not be empty")
