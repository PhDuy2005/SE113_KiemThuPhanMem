## Infomation
Service Name: AddressDataService
Method Name: normalizeKey(String value)
Mock class: 
   1. None

Mock Data: None

## Testcase
### Testcase 1
Short Description: Normalize string with accents and uppercase.
Input: value = "TP. Hồ Chí Minh"
Expected Output: "tp. ho chi minh" (Note: the method replaces accents, but keeps punctuation unless it's a combining mark)
Wait, let's check the code:
183:         String normalized = Normalizer.normalize(value.trim().toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
184:                 .replaceAll("\\p{M}", "")
185:                 .replace('đ', 'd')
186:                 .replaceAll("\\s+", " ");

"TP. Hồ Chí Minh" -> "tp. ho chi minh"

### Testcase 2
Short Description: Normalize string with 'đ' character.
Input: value = "Đà Nẵng"
Expected Output: "da nang"

### Testcase 3
Short Description: Normalize string with multiple spaces.
Input: value = "  Hà    Nội  "
Expected Output: "ha noi"

### Testcase 4
Short Description: Normalize string with combining marks.
Input: value = "Hà Nội"
Expected Output: "ha noi"
