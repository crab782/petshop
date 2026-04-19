# User Backend API Testing Checklist

## Test Infrastructure
- [ ] UserApiControllerTestBase.java created with common setup
- [ ] MockMvc configured for API testing
- [ ] ObjectMapper configured for JSON serialization
- [ ] Authentication helper methods implemented
- [ ] Test data fixtures created

## Authentication API Tests (8 endpoints)
- [ ] POST `/api/auth/login` tests
  - [ ] Valid credentials returns token
  - [ ] Invalid credentials returns 401
  - [ ] Missing fields returns 400
  - [ ] Non-existent user returns 401
- [ ] POST `/api/auth/register` tests
  - [ ] Valid registration creates user
  - [ ] Duplicate email returns 409
  - [ ] Duplicate username returns 409
  - [ ] Invalid email format returns 400
  - [ ] Weak password returns 400
- [ ] POST `/api/auth/logout` tests
  - [ ] Authenticated logout succeeds
  - [ ] Unauthenticated logout returns 401
- [ ] GET `/api/auth/userinfo` tests
  - [ ] Authenticated request returns user
  - [ ] Unauthenticated request returns 401
- [ ] PUT `/api/auth/userinfo` tests
  - [ ] Valid update succeeds
  - [ ] Invalid data returns 400
  - [ ] Unauthenticated request returns 401
- [ ] PUT `/api/auth/password` tests
  - [ ] Valid password change succeeds
  - [ ] Wrong old password returns 400
  - [ ] Same password returns 400
  - [ ] Weak new password returns 400
- [ ] POST `/api/auth/sendVerifyCode` tests
  - [ ] Valid email sends code
  - [ ] Invalid email format returns 400
- [ ] POST `/api/auth/resetPassword` tests
  - [ ] Valid code resets password
  - [ ] Invalid code returns 400
  - [ ] Expired code returns 400

## Pet Management API Tests (5 endpoints)
- [ ] GET `/api/user/pets` tests
  - [ ] Returns user's pets
  - [ ] Empty list for new user
  - [ ] Unauthenticated returns 401
- [ ] GET `/api/user/pets/{id}` tests
  - [ ] Returns pet details
  - [ ] Non-existent pet returns 404
  - [ ] Other user's pet returns 404
- [ ] POST `/api/user/pets` tests
  - [ ] Creates pet with valid data
  - [ ] Missing required fields returns 400
  - [ ] Invalid pet type returns 400
- [ ] PUT `/api/user/pets/{id}` tests
  - [ ] Updates pet successfully
  - [ ] Non-existent pet returns 404
  - [ ] Other user's pet returns 404
- [ ] DELETE `/api/user/pets/{id}` tests
  - [ ] Deletes pet successfully
  - [ ] Non-existent pet returns 404
  - [ ] Other user's pet returns 404

## Address Management API Tests (5 endpoints)
- [ ] GET `/api/user/addresses` tests
  - [ ] Returns user's addresses
  - [ ] Unauthenticated returns 401
- [ ] POST `/api/user/addresses` tests
  - [ ] Creates address successfully
  - [ ] Missing fields returns 400
  - [ ] Invalid phone format returns 400
- [ ] PUT `/api/user/addresses/{id}` tests
  - [ ] Updates address successfully
  - [ ] Non-existent address returns 404
  - [ ] Other user's address returns 404
- [ ] DELETE `/api/user/addresses/{id}` tests
  - [ ] Deletes address successfully
  - [ ] Non-existent address returns 404
- [ ] PUT `/api/user/addresses/{id}/default` tests
  - [ ] Sets default address
  - [ ] Clears previous default
  - [ ] Non-existent address returns 404

## Merchant & Service API Tests (6 endpoints)
- [ ] GET `/api/merchants` tests
  - [ ] Returns paginated merchants
  - [ ] Keyword filter works
  - [ ] Rating filter works
  - [ ] Sorting works
  - [ ] Pagination works
- [ ] GET `/api/merchant/{id}` tests
  - [ ] Returns merchant details
  - [ ] Non-existent returns 404
- [ ] GET `/api/merchant/{id}/services` tests
  - [ ] Returns merchant services
  - [ ] Empty list for no services
- [ ] GET `/api/merchant/{id}/products` tests
  - [ ] Returns merchant products
  - [ ] Empty list for no products
- [ ] GET `/api/merchant/{id}/reviews` tests
  - [ ] Returns merchant reviews
  - [ ] Empty list for no reviews
- [ ] GET `/api/merchant/{id}/available-slots` tests
  - [ ] Returns available slots
  - [ ] Invalid date format returns 400
  - [ ] Past date returns empty slots

## Service API Tests (4 endpoints)
- [ ] GET `/api/services` tests
  - [ ] Returns service list
  - [ ] Type filter works
- [ ] GET `/api/services/{id}` tests
  - [ ] Returns service details
  - [ ] Non-existent returns 404
- [ ] GET `/api/services/search` tests
  - [ ] Returns matching services
  - [ ] Empty keyword returns empty list
- [ ] GET `/api/services/recommended` tests
  - [ ] Returns recommended services
  - [ ] Limit parameter works

## Product API Tests (4 endpoints)
- [ ] GET `/api/products` tests
  - [ ] Returns product list
  - [ ] Pagination works
  - [ ] Category filter works
- [ ] GET `/api/products/{id}` tests
  - [ ] Returns product details
  - [ ] Non-existent returns 404
- [ ] GET `/api/products/search` tests
  - [ ] Returns matching products
  - [ ] Empty keyword returns empty list
- [ ] GET `/api/products/{id}/reviews` tests
  - [ ] Returns product reviews
  - [ ] Empty list for no reviews

## Cart API Tests (5 endpoints)
- [ ] POST `/api/user/cart` tests
  - [ ] Adds product to cart
  - [ ] Duplicate product updates quantity
  - [ ] Non-existent product returns 404
  - [ ] Unauthenticated returns 401
- [ ] GET `/api/user/cart` tests
  - [ ] Returns cart items
  - [ ] Empty cart returns empty list
- [ ] PUT `/api/user/cart` tests
  - [ ] Updates quantity
  - [ ] Non-existent item returns 404
  - [ ] Zero quantity removes item
- [ ] DELETE `/api/user/cart/{id}` tests
  - [ ] Removes item from cart
  - [ ] Non-existent item returns 404
- [ ] DELETE `/api/user/cart/batch` tests
  - [ ] Removes multiple items
  - [ ] Empty list returns 400

## Order Management API Tests (12 endpoints)
- [ ] GET `/api/user/orders` tests
  - [ ] Returns order list
  - [ ] Status filter works
  - [ ] Date range filter works
  - [ ] Pagination works
- [ ] GET `/api/user/orders/{id}` tests
  - [ ] Returns order details
  - [ ] Non-existent returns 404
  - [ ] Other user's order returns 404
- [ ] POST `/api/user/orders` tests
  - [ ] Creates order successfully
  - [ ] Empty cart returns 400
  - [ ] Invalid address returns 400
- [ ] POST `/api/user/orders/preview` tests
  - [ ] Returns order preview
  - [ ] Calculates totals correctly
- [ ] POST `/api/user/orders/{id}/pay` tests
  - [ ] Initiates payment
  - [ ] Already paid returns 400
- [ ] GET `/api/user/orders/{id}/pay/status` tests
  - [ ] Returns payment status
  - [ ] Non-existent returns 404
- [ ] PUT `/api/user/orders/{id}/cancel` tests
  - [ ] Cancels pending order
  - [ ] Cannot cancel shipped order
  - [ ] Restores stock
- [ ] POST `/api/user/orders/{id}/refund` tests
  - [ ] Initiates refund
  - [ ] Cannot refund unpaid order
- [ ] PUT `/api/user/orders/{id}/confirm` tests
  - [ ] Confirms delivery
  - [ ] Cannot confirm non-shipped order
- [ ] DELETE `/api/user/orders/{id}` tests
  - [ ] Deletes order
  - [ ] Cannot delete active order
- [ ] PUT `/api/user/orders/batch-cancel` tests
  - [ ] Cancels multiple orders
- [ ] DELETE `/api/user/orders/batch-delete` tests
  - [ ] Deletes multiple orders

## Appointment API Tests (5 endpoints)
- [ ] GET `/api/user/appointments` tests
  - [ ] Returns appointment list
  - [ ] Status filter works
  - [ ] Date range filter works
- [ ] GET `/api/user/appointments/{id}` tests
  - [ ] Returns appointment details
  - [ ] Non-existent returns 404
  - [ ] Other user's returns 404
- [ ] POST `/api/user/appointments` tests
  - [ ] Creates appointment
  - [ ] Invalid time returns 400
  - [ ] Non-existent pet returns 400
- [ ] PUT `/api/user/appointments/{id}/cancel` tests
  - [ ] Cancels appointment
  - [ ] Cannot cancel completed appointment
- [ ] GET `/api/user/appointments/stats` tests
  - [ ] Returns correct statistics

## Review API Tests (5 endpoints)
- [ ] POST `/api/user/reviews` tests
  - [ ] Creates review
  - [ ] Invalid rating returns 400
  - [ ] Duplicate review returns 400
- [ ] GET `/api/user/reviews` tests
  - [ ] Returns user reviews
  - [ ] Pagination works
  - [ ] Date filter works
- [ ] PUT `/api/user/reviews/{id}` tests
  - [ ] Updates review
  - [ ] Other user's review returns 404
- [ ] DELETE `/api/user/reviews/{id}` tests
  - [ ] Deletes review
  - [ ] Other user's review returns 404

## Notification API Tests (7 endpoints)
- [ ] GET `/api/user/notifications` tests
  - [ ] Returns notifications
  - [ ] Type filter works
  - [ ] Read status filter works
- [ ] PUT `/api/user/notifications/{id}/read` tests
  - [ ] Marks as read
  - [ ] Non-existent returns 404
- [ ] PUT `/api/user/notifications/read-all` tests
  - [ ] Marks all as read
- [ ] PUT `/api/user/notifications/batch-read` tests
  - [ ] Marks multiple as read
- [ ] DELETE `/api/user/notifications/{id}` tests
  - [ ] Deletes notification
- [ ] DELETE `/api/user/notifications/batch` tests
  - [ ] Deletes multiple notifications
- [ ] GET `/api/user/notifications/unread-count` tests
  - [ ] Returns correct count

## Search API Tests (5 endpoints)
- [ ] GET `/api/search/suggestions` tests
  - [ ] Returns suggestions
  - [ ] Empty keyword returns empty
- [ ] GET `/api/search/hot-keywords` tests
  - [ ] Returns hot keywords
  - [ ] Limit parameter works
- [ ] POST `/api/user/search-history` tests
  - [ ] Saves search history
  - [ ] Unauthenticated returns 401
- [ ] GET `/api/user/search-history` tests
  - [ ] Returns search history
  - [ ] Limit parameter works
- [ ] DELETE `/api/user/search-history` tests
  - [ ] Clears search history

## Announcement API Tests (2 endpoints)
- [ ] GET `/api/announcements` tests
  - [ ] Returns announcements
  - [ ] Ordered by date desc
- [ ] GET `/api/announcements/{id}` tests
  - [ ] Returns announcement details
  - [ ] Non-existent returns 404

## Home Statistics API Tests (2 endpoints)
- [ ] GET `/api/user/home/stats` tests
  - [ ] Returns pet count
  - [ ] Returns pending appointments
  - [ ] Returns review count
- [ ] GET `/api/user/home/activities` tests
  - [ ] Returns recent activities
  - [ ] Limit parameter works
  - [ ] Ordered by time desc

## Favorite API Tests (9 endpoints)
- [ ] GET `/api/user/favorites` tests
  - [ ] Returns favorite merchants
- [ ] POST `/api/user/favorites` tests
  - [ ] Adds merchant favorite
  - [ ] Duplicate returns 409
- [ ] DELETE `/api/user/favorites/{id}` tests
  - [ ] Removes merchant favorite
- [ ] GET `/api/user/favorites/services` tests
  - [ ] Returns favorite services
- [ ] POST `/api/user/favorites/services` tests
  - [ ] Adds service favorite
- [ ] DELETE `/api/user/favorites/services/{id}` tests
  - [ ] Removes service favorite
- [ ] POST `/api/user/favorites/products` tests
  - [ ] Adds product favorite
- [ ] DELETE `/api/user/favorites/products/{id}` tests
  - [ ] Removes product favorite
- [ ] GET `/api/user/favorites/products/{id}/check` tests
  - [ ] Returns favorite status

## User Services API Tests (1 endpoint)
- [ ] GET `/api/user/services` tests
  - [ ] Returns purchased services
  - [ ] Status filter works

## Test Report
- [ ] All tests executed successfully
- [ ] JaCoCo coverage report generated
- [ ] Coverage meets minimum threshold (80%)
- [ ] Test summary report created
- [ ] Failed tests documented

## Total Tests: 85 endpoints
- Authentication: 8 ✅
- Pet Management: 5 ✅
- Address Management: 5 ✅
- Merchant & Service: 6 ✅
- Service: 4 ✅
- Product: 4 ✅
- Cart: 5 ✅
- Order Management: 12 ✅
- Appointment: 5 ✅
- Review: 5 ✅
- Notification: 7 ✅
- Search: 5 ✅
- Announcement: 2 ✅
- Home Statistics: 2 ✅
- Favorites: 9 ✅
- User Services: 1 ✅
