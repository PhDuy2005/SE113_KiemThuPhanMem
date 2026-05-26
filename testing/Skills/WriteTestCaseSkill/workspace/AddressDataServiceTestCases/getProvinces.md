## Infomation
Service Name: AddressDataService
Method Name: getProvinces()
Mock class: 
   1. None (Constructor loads data from JSON files)

Mock Data:
- provinces: [ResProvinceDTO(code="01", name="Thành phố Hà Nội"), ResProvinceDTO(code="79", name="Thành phố Hồ Chí Minh")]

## Testcase
### Testcase 1
Short Description: Get all provinces from the service.
Input: None
Expected Output: List of all provinces sorted by name.
Actual Output: [ResProvinceDTO(code="01", name="Thành phố Hà Nội"), ResProvinceDTO(code="79", name="Thành phố Hồ Chí Minh")]
