# User Backend API Testing Tasks

## Task Breakdown

### Task 1: Create Test Infrastructure
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - Create UserApiControllerTestBase.java with common setup
  - Configure MockMvc, ObjectMapper, and test data
  - Create authentication helper methods for tests
  - Create test data fixtures and builders
- **Files to create**:
  - `src/test/java/com/petshop/controller/api/UserApiControllerTestBase.java`
  - `src/test/java/com/petshop/fixtures/TestDataBuilder.java`

### Task 2: Implement Authentication API Tests
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - Test POST `/api/auth/login` - valid/invalid credentials, missing fields
  - Test POST `/api/auth/register` - valid registration, duplicates, validation
  - Test POST `/api/auth/logout` - authenticated/unauthenticated
  - Test GET `/api/auth/userinfo` - authenticated access
  - Test PUT `/api/auth/userinfo` - update validation
  - Test PUT `/api/auth/password` - password change scenarios
  - Test POST `/api/auth/sendVerifyCode` - email validation
  - Test POST `/api/auth/resetPassword` - code validation
- **Files to create**:
  - `src/test/java/com/petshop/controller/api/AuthApiControllerTest.java`

### Task 3: Implement Pet Management API Tests
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - Test GET `/api/user/pets` - list user's pets
  - Test GET `/api/user/pets/{id}` - pet details, ownership validation
  - Test POST `/api/user/pets` - create pet, validation
  - Test PUT `/api/user/pets/{id}` - update pet, ownership
  - Test DELETE `/api/user/pets/{id}` - delete pet, ownership
- **Files to create**:
  - `src/test/java/com/petshop/controller/api/UserApiControllerPetTest.java`

### Task 4: Implement Address Management API Tests
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - Test GET `/api/user/addresses` - list addresses
  - Test POST `/api/user/addresses` - create address
  - Test PUT `/api/user/addresses/{id}` - update address
  - Test DELETE `/api/user/addresses/{id}` - delete address
  - Test PUT `/api/user/addresses/{id}/default` - set default
- **Files to create**:
  - `src/test/java/com/petshop/controller/api/UserApiControllerAddressTest.java`

### Task 5: Implement Merchant & Service API Tests
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - Test GET `/api/merchants` - pagination, filtering, sorting
  - Test GET `/api/merchant/{id}` - merchant details
  - Test GET `/api/merchant/{id}/services` - merchant services
  - Test GET `/api/merchant/{id}/products` - merchant products
  - Test GET `/api/merchant/{id}/reviews` - merchant reviews
  - Test GET `/api/merchant/{id}/available-slots` - available slots
- **Files to create**:
  - `src/test/java/com/petshop/controller/api/PublicApiControllerMerchantTest.java`

### Task 6: Implement Service API Tests
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - Test GET `/api/services` - list services, type filter
  - Test GET `/api/services/{id}` - service details
  - Test GET `/api/services/search` - keyword search
  - Test GET `/api/services/recommended` - recommended services
- **Files to create**:
  - `src/test/java/com/petshop/controller/api/ServiceApiControllerTest.java`

### Task 7: Implement Product API Tests
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - Test GET `/api/products` - list products, pagination
  - Test GET `/api/products/{id}` - product details
  - Test GET `/api/products/search` - keyword search
  - Test GET `/api/products/{id}/reviews` - product reviews
- **Files to create**:
  - `src/test/java/com/petshop/controller/api/ProductApiControllerTest.java`

### Task 8: Implement Cart API Tests
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - Test POST `/api/user/cart` - add to cart
  - Test GET `/api/user/cart` - get cart
  - Test PUT `/api/user/cart` - update cart item
  - Test DELETE `/api/user/cart/{id}` - remove from cart
  - Test DELETE `/api/user/cart/batch` - batch remove
- **Files to create**:
  - `src/test/java/com/petshop/controller/api/UserApiControllerCartTest.java`

### Task 9: Implement Order Management API Tests
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - Test GET `/api/user/orders` - list orders, filtering
  - Test GET `/api/user/orders/{id}` - order details
  - Test POST `/api/user/orders` - create order
  - Test POST `/api/user/orders/preview` - preview order
  - Test POST `/api/user/orders/{id}/pay` - pay order
  - Test GET `/api/user/orders/{id}/pay/status` - payment status
  - Test PUT `/api/user/orders/{id}/cancel` - cancel order
  - Test POST `/api/user/orders/{id}/refund` - refund order
  - Test PUT `/api/user/orders/{id}/confirm` - confirm receive
  - Test DELETE `/api/user/orders/{id}` - delete order
  - Test PUT `/api/user/orders/batch-cancel` - batch cancel
  - Test DELETE `/api/user/orders/batch-delete` - batch delete
- **Files to create**:
  - `src/test/java/com/petshop/controller/api/UserApiControllerOrderTest.java`

### Task 10: Implement Appointment API Tests
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - Test GET `/api/user/appointments` - list appointments
  - Test GET `/api/user/appointments/{id}` - appointment details
  - Test POST `/api/user/appointments` - create appointment
  - Test PUT `/api/user/appointments/{id}/cancel` - cancel appointment
  - Test GET `/api/user/appointments/stats` - appointment stats
- **Files to create**:
  - `src/test/java/com/petshop/controller/api/UserApiControllerAppointmentTest.java`

### Task 11: Implement Review API Tests
- **Priority**: P1
- **Depends On**: Task 1
- **Description**:
  - Test POST `/api/user/reviews` - add review, rating validation
  - Test GET `/api/user/reviews` - list user reviews
  - Test PUT `/api/user/reviews/{id}` - update review
  - Test DELETE `/api/user/reviews/{id}` - delete review
- **Files to create**:
  - `src/test/java/com/petshop/controller/api/UserApiControllerReviewTest.java`

### Task 12: Implement Notification API Tests
- **Priority**: P1
- **Depends On**: Task 1
- **Description**:
  - Test GET `/api/user/notifications` - list notifications, filtering
  - Test PUT `/api/user/notifications/{id}/read` - mark as read
  - Test PUT `/api/user/notifications/read-all` - mark all read
  - Test PUT `/api/user/notifications/batch-read` - batch mark read
  - Test DELETE `/api/user/notifications/{id}` - delete notification
  - Test DELETE `/api/user/notifications/batch` - batch delete
  - Test GET `/api/user/notifications/unread-count` - unread count
- **Files to create**:
  - `src/test/java/com/petshop/controller/api/UserApiControllerNotificationTest.java`

### Task 13: Implement Search API Tests
- **Priority**: P1
- **Depends On**: Task 1
- **Description**:
  - Test GET `/api/search/suggestions` - search suggestions
  - Test GET `/api/search/hot-keywords` - hot keywords
  - Test POST `/api/user/search-history` - save history
  - Test GET `/api/user/search-history` - get history
  - Test DELETE `/api/user/search-history` - clear history
- **Files to create**:
  - `src/test/java/com/petshop/controller/api/SearchApiControllerTest.java`

### Task 14: Implement Announcement API Tests
- **Priority**: P1
- **Depends On**: Task 1
- **Description**:
  - Test GET `/api/announcements` - list announcements
  - Test GET `/api/announcements/{id}` - announcement details
- **Files to create**:
  - `src/test/java/com/petshop/controller/api/AnnouncementApiControllerTest.java`

### Task 15: Implement Home Statistics API Tests
- **Priority**: P1
- **Depends On**: Task 1
- **Description**:
  - Test GET `/api/user/home/stats` - home statistics
  - Test GET `/api/user/home/activities` - recent activities
- **Files to create**:
  - `src/test/java/com/petshop/controller/api/UserApiControllerHomeTest.java`

### Task 16: Implement Favorite API Tests
- **Priority**: P1
- **Depends On**: Task 1
- **Description**:
  - Test GET `/api/user/favorites` - favorite merchants
  - Test POST `/api/user/favorites` - add merchant favorite
  - Test DELETE `/api/user/favorites/{id}` - remove merchant favorite
  - Test GET `/api/user/favorites/services` - favorite services
  - Test POST `/api/user/favorites/services` - add service favorite
  - Test DELETE `/api/user/favorites/services/{id}` - remove service favorite
  - Test POST `/api/user/favorites/products` - add product favorite
  - Test DELETE `/api/user/favorites/products/{id}` - remove product favorite
  - Test GET `/api/user/favorites/products/{id}/check` - check favorite
- **Files to create**:
  - `src/test/java/com/petshop/controller/api/UserApiControllerFavoriteTest.java`

### Task 17: Implement User Services API Tests
- **Priority**: P2
- **Depends On**: Task 1
- **Description**:
  - Test GET `/api/user/services` - purchased services
- **Files to create**:
  - `src/test/java/com/petshop/controller/api/UserApiControllerServiceTest.java`

### Task 18: Generate Test Report
- **Priority**: P0
- **Depends On**: Task 2-17
- **Description**:
  - Run all tests with coverage
  - Generate JaCoCo coverage report
  - Create test summary report
  - Document any failing tests
- **Files to create**:
  - `docs/test-report/user-api-test-report.md`

## Task Dependencies

```
Task 1 (Test Infrastructure)
  ├── Task 2 (Auth Tests)
  ├── Task 3 (Pet Tests)
  ├── Task 4 (Address Tests)
  ├── Task 5 (Merchant Tests)
  ├── Task 6 (Service Tests)
  ├── Task 7 (Product Tests)
  ├── Task 8 (Cart Tests)
  ├── Task 9 (Order Tests)
  ├── Task 10 (Appointment Tests)
  ├── Task 11 (Review Tests)
  ├── Task 12 (Notification Tests)
  ├── Task 13 (Search Tests)
  ├── Task 14 (Announcement Tests)
  ├── Task 15 (Home Stats Tests)
  ├── Task 16 (Favorite Tests)
  └── Task 17 (User Services Tests)

All Tasks
  └── Task 18 (Test Report)
```

## Parallelizable Tasks
- Tasks 2-17 can run in parallel after Task 1
- Task 18 depends on all test tasks

## Test Requirements

### Each test class must include:
1. **Normal operation tests** - Happy path scenarios
2. **Boundary condition tests** - Edge cases
3. **Error handling tests** - Invalid inputs
4. **Authentication tests** - Auth required endpoints
5. **Authorization tests** - Ownership validation
6. **Response format tests** - JSON structure validation

### Test naming convention:
- `test[MethodName]_[Scenario]_[ExpectedResult]`
- Example: `testLogin_ValidCredentials_ReturnsToken`

### Test structure (AAA pattern):
1. **Arrange** - Set up test data and mocks
2. **Act** - Execute the API call
3. **Assert** - Verify response and side effects
