# Unit Test Coverage Summary

| No | Requirement Name | Class Name | Function Name | Description |
| :--- | :--- | :--- | :--- | :--- |
| 1 | User Login | UserService | handleLogin | Authenticate user and handle the login process. |
| 2 | User Registration | UserService | register | Register a new user account. |
| 3 | Account Verification | UserService | verifyRegistration | Verify a newly registered account via an email token. |
| 4 | Forgot Password | UserService | forgotPassword | Handle forgot password requests and send a reset link. |
| 5 | Reset Password | UserService | resetPassword | Set a new password using a valid reset token. |
| 6 | Change Password | UserService | changeCurrentUserPassword | Change the password of the currently logged-in user. |
| 7 | Update User Profile | UserService | updateCurrentUserProfile | Update the personal information of the current user. |
| 8 | Create Product | ProductManagementService | createProduct | Add a new product to the system. |
| 9 | Update Product Information | ProductManagementService | updateProduct | Update general details of an existing product. |
| 10 | Update Product Price | ProductManagementService | updatePrice | Modify the selling price of a product. |
| 11 | Update Product Stock | ProductManagementService | updateStock | Update the inventory stock quantity for a product. |
| 12 | Discontinue Product | ProductManagementService | discontinueProduct | Mark a product as discontinued from sales. |
| 13 | Create Category | CategoryService | createCategory | Create a new product category. |
| 14 | Delete Category | CategoryService | deleteCategory | Delete a category and handle its replacement. |
| 15 | Add Item to Cart | CartService | addItem | Add a specific product to the user's shopping cart. |
| 16 | Remove Item from Cart | CartService | removeItem | Remove a specific product from the shopping cart. |
| 17 | Update Cart Item Quantity | CartService | updateItemQuantity | Update the quantity of a product in the shopping cart. |
| 18 | Clear Cart | CartService | clearCart | Remove all items from the user's shopping cart. |
| 19 | Create Voucher | VoucherManagementService | createVoucher | Generate a new discount voucher. |
| 20 | Stop Voucher | VoucherManagementService | emergencyStopVoucher | Perform an emergency stop on an active voucher. |
| 21 | Calculate Order Total | CheckoutService | calculateSelection | Calculate the total price and shipping fees for selected items. |
| 22 | Apply Voucher | CheckoutService | applyVoucher | Apply a discount voucher to the current checkout session. |
| 23 | Confirm Order | CheckoutService | confirmOrder | Confirm and proceed with placing an order. |
| 24 | Approve Order | OrderService | approveOrder | Review and approve a pending order by staff. |
| 25 | Update Shipping Status | OrderService | updateShippingStatus | Update the shipping and delivery status of an order. |
| 26 | Mark Order as Delivered | OrderService | markOrderDelivered | Mark an order as successfully delivered to the customer. |
| 27 | Cancel Pending Order | OrderService | cancelPendingOrder | Allow customers to cancel their own pending orders. |
| 28 | Create Staff Account | StaffManagementService | createStaff | Create a new staff account by admin. |
| 29 | Lock Staff Account | StaffManagementService | lockStaff | Lock an existing staff account. |
