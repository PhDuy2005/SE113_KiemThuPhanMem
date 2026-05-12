# UC42 Image Storage Test Note

## Scope

Applies to `POST /api/v1/business/products`.

The endpoint receives product images through `multipart/form-data` field `images`.

## Expected Behavior

- At least one image is required.
- Accepted file extensions: `.jpg`, `.jpeg`, `.png`, `.webp`.
- Maximum file size: `5MB` per image.
- Valid images are stored under `storage/products`.
- Response returns image URLs using `/storage/products/{filename}`.
- The first uploaded image is saved as the primary image.
- Static image URLs should be publicly accessible through `/storage/**`.

## Database Checks

After a successful product creation, verify these records:

- `products`: new product exists with status `ACTIVE`.
- `inventory`: stock record exists for the product.
- `product_image`: one row per uploaded image.
- `product_image.is_primary`: exactly one image is primary, and it is the first uploaded image.

## Negative Test Cases

- No `images` field.
- Empty `images` field.
- File size greater than `5MB`.
- Unsupported extension, for example `.gif`, `.bmp`, `.pdf`, `.txt`.
- Mixed upload where one file is valid and another file is invalid.
- Missing required product fields together with images.

## Risk Areas

- File-system writes are not automatically rolled back with DB transactions.
  - If DB save fails after image files are copied, orphan files may remain in `storage/products`.
- Current validation checks extension and size.
  - It does not deeply validate actual image content.
  - A renamed fake image such as `file.jpg` with non-image content may pass validation.
- Tester should verify that generated filenames are unique and do not overwrite existing files.
