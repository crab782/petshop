# User Backend API Implementation Tasks

## Task Breakdown

### Task 1: Setup Backend Infrastructure
- **Priority**: P0
- **Depends On**: None
- **Description**:
  - Create DTO classes for all API responses
  - Create ApiResponse wrapper class
  - Setup JWT authentication filter
  - Configure CORS for frontend
  - Create exception handling middleware
- **Acceptance Criteria**: Infrastructure ready for API development

### Task 2: Implement Authentication API
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - POST `/api/auth/login` - User login with JWT
  - POST `/api/auth/register` - User registration
  - POST `/api/auth/logout` - User logout
  - GET `/api/auth/userinfo` - Get current user info
  - PUT `/api/auth/userinfo` - Update user info
  - PUT `/api/auth/password` - Change password
  - POST `/api/auth/sendVerifyCode` - Send verification code
  - POST `/api/auth/resetPassword` - Reset password
- **Files to modify**:
  - `src/main/java/com/petshop/controller/api/AuthApiController.java`
  - `src/main/java/com/petshop/service/AuthService.java`

### Task 3: Implement Pet Management API
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - GET `/api/user/pets` - Get user's pet list
  - GET `/api/user/pets/{id}` - Get pet details
  - POST `/api/user/pets` - Add new pet
  - PUT `/api/user/pets/{id}` - Update pet
  - DELETE `/api/user/pets/{id}` - Delete pet
- **Files to modify**:
  - `src/main/java/com/petshop/controller/api/UserApiController.java`
  - `src/main/java/com/petshop/service/PetService.java`

### Task 4: Implement Address Management API
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - GET `/api/user/addresses` - Get address list
  - POST `/api/user/addresses` - Add address
  - PUT `/api/user/addresses/{id}` - Update address
  - DELETE `/api/user/addresses/{id}` - Delete address
  - PUT `/api/user/addresses/{id}/default` - Set default address
- **Files to create/modify**:
  - `src/main/java/com/petshop/entity/Address.java`
  - `src/main/java/com/petshop/repository/AddressRepository.java`
  - `src/main/java/com/petshop/service/AddressService.java`

### Task 5: Implement Merchant & Service API
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - GET `/api/merchants` - Get merchant list with filters
  - GET `/api/merchant/{id}` - Get merchant details
  - GET `/api/merchant/{id}/services` - Get merchant services
  - GET `/api/merchant/{id}/products` - Get merchant products
  - GET `/api/merchant/{id}/reviews` - Get merchant reviews
  - GET `/api/merchant/{id}/available-slots` - Get available slots
- **Files to modify**:
  - `src/main/java/com/petshop/controller/api/MerchantApiController.java`
  - `src/main/java/com/petshop/service/MerchantService.java`

### Task 6: Implement Service API
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - GET `/api/services` - Get service list
  - GET `/api/services/{id}` - Get service details
  - GET `/api/services/search` - Search services
  - GET `/api/services/recommended` - Get recommended services
- **Files to modify**:
  - `src/main/java/com/petshop/controller/api/ServiceApiController.java`
  - `src/main/java/com/petshop/service/ServiceService.java`

### Task 7: Implement Product & Cart API
- **Priority**: P0
- **Depends On**: Task 1
- **Description**:
  - GET `/api/products` - Get product list
  - GET `/api/products/{id}` - Get product details
  - GET `/api/products/search` - Search products
  - GET `/api/products/{id}/reviews` - Get product reviews
  - POST `/api/user/cart` - Add to cart
  - GET `/api/user/cart` - Get cart
  - PUT `/api/user/cart` - Update cart item
  - DELETE `/api/user/cart/{id}` - Remove from cart
  - DELETE `/api/user/cart/batch` - Batch remove from cart
- **Files to create/modify**:
  - `src/main/java/com/petshop/entity/Cart.java`
  - `src/main/java/com/petshop/repository/CartRepository.java`
  - `src/main/java/com/petshop/service/CartService.java`

### Task 8: Implement Product Favorite API
- **Priority**: P1
- **Depends On**: Task 1
- **Description**:
  - POST `/api/user/favorites/products` - Add product favorite
  - DELETE `/api/user/favorites/products/{id}` - Remove product favorite
  - GET `/api/user/favorites/products/{id}/check` - Check favorite status
- **Files to modify**:
  - `src/main/java/com/petshop/service/FavoriteService.java`

### Task 9: Implement Order Management API
- **Priority**: P0
- **Depends On**: Task 4, Task 7
- **Description**:
  - GET `/api/user/orders` - Get order list
  - GET `/api/user/orders/{id}` - Get order details
  - POST `/api/user/orders` - Create order
  - POST `/api/user/orders/preview` - Preview order
  - POST `/api/user/orders/{id}/pay` - Pay order
  - GET `/api/user/orders/{id}/pay/status` - Get payment status
  - PUT `/api/user/orders/{id}/cancel` - Cancel order
  - POST `/api/user/orders/{id}/refund` - Refund order
  - PUT `/api/user/orders/{id}/confirm` - Confirm receive
  - DELETE `/api/user/orders/{id}` - Delete order
  - PUT `/api/user/orders/batch-cancel` - Batch cancel
  - DELETE `/api/user/orders/batch-delete` - Batch delete
- **Files to modify**:
  - `src/main/java/com/petshop/service/ProductOrderService.java`

### Task 10: Implement Appointment API
- **Priority**: P0
- **Depends On**: Task 3, Task 6
- **Description**:
  - GET `/api/user/appointments` - Get appointment list
  - GET `/api/user/appointments/{id}` - Get appointment details
  - POST `/api/user/appointments` - Create appointment
  - PUT `/api/user/appointments/{id}/cancel` - Cancel appointment
  - GET `/api/user/appointments/stats` - Get appointment stats
- **Files to modify**:
  - `src/main/java/com/petshop/service/AppointmentService.java`

### Task 11: Implement Review API
- **Priority**: P1
- **Depends On**: Task 1
- **Description**:
  - POST `/api/user/reviews` - Add review
  - GET `/api/user/reviews` - Get user reviews
  - PUT `/api/user/reviews/{id}` - Update review
  - DELETE `/api/user/reviews/{id}` - Delete review
- **Files to modify**:
  - `src/main/java/com/petshop/service/ReviewService.java`

### Task 12: Implement Notification API
- **Priority**: P1
- **Depends On**: Task 1
- **Description**:
  - GET `/api/user/notifications` - Get notification list
  - PUT `/api/user/notifications/{id}/read` - Mark as read
  - PUT `/api/user/notifications/read-all` - Mark all as read
  - PUT `/api/user/notifications/batch-read` - Batch mark as read
  - DELETE `/api/user/notifications/{id}` - Delete notification
  - DELETE `/api/user/notifications/batch` - Batch delete
  - GET `/api/user/notifications/unread-count` - Get unread count
- **Files to create/modify**:
  - `src/main/java/com/petshop/entity/Notification.java`
  - `src/main/java/com/petshop/repository/NotificationRepository.java`
  - `src/main/java/com/petshop/service/NotificationService.java`

### Task 13: Implement Search API
- **Priority**: P1
- **Depends On**: Task 1
- **Description**:
  - GET `/api/search/suggestions` - Get search suggestions
  - GET `/api/search/hot-keywords` - Get hot keywords
  - POST `/api/user/search-history` - Save search history
  - GET `/api/user/search-history` - Get search history
  - DELETE `/api/user/search-history` - Clear search history
- **Files to create/modify**:
  - `src/main/java/com/petshop/entity/SearchHistory.java`
  - `src/main/java/com/petshop/service/SearchService.java`

### Task 14: Implement Announcement API
- **Priority**: P1
- **Depends On**: Task 1
- **Description**:
  - GET `/api/announcements` - Get announcement list
  - GET `/api/announcements/{id}` - Get announcement details
- **Files to modify**:
  - `src/main/java/com/petshop/service/AnnouncementService.java`

### Task 15: Implement Home Statistics API
- **Priority**: P1
- **Depends On**: Task 3, Task 10
- **Description**:
  - GET `/api/user/home/stats` - Get home statistics
  - GET `/api/user/home/activities` - Get recent activities
- **Files to create/modify**:
  - `src/main/java/com/petshop/service/UserHomeService.java`

### Task 16: Implement Favorite Management API
- **Priority**: P1
- **Depends On**: Task 1
- **Description**:
  - GET `/api/user/favorites` - Get favorite merchants
  - POST `/api/user/favorites` - Add merchant favorite
  - DELETE `/api/user/favorites/{id}` - Remove merchant favorite
  - GET `/api/user/favorites/services` - Get favorite services
  - POST `/api/user/favorites/services` - Add service favorite
  - DELETE `/api/user/favorites/services/{id}` - Remove service favorite
- **Files to modify**:
  - `src/main/java/com/petshop/service/FavoriteService.java`

### Task 17: Implement User Services API
- **Priority**: P2
- **Depends On**: Task 10
- **Description**:
  - GET `/api/user/services` - Get purchased services
- **Files to modify**:
  - `src/main/java/com/petshop/service/UserService.java`

### Task 18: API Integration Testing
- **Priority**: P0
- **Depends On**: Task 2-17
- **Description**:
  - Write integration tests for all API endpoints
  - Test authentication and authorization
  - Test error handling
  - Verify response formats match frontend expectations
- **Files to create**:
  - `src/test/java/com/petshop/controller/api/*Test.java`

## Task Dependencies

```
Task 1 (Infrastructure)
  ├── Task 2 (Auth API)
  ├── Task 3 (Pet API)
  ├── Task 4 (Address API)
  ├── Task 5 (Merchant API)
  ├── Task 6 (Service API)
  ├── Task 7 (Product & Cart API)
  │     └── Task 8 (Product Favorite)
  ├── Task 11 (Review API)
  ├── Task 12 (Notification API)
  ├── Task 13 (Search API)
  ├── Task 14 (Announcement API)
  └── Task 16 (Favorite API)

Task 3 + Task 6
  └── Task 10 (Appointment API)

Task 4 + Task 7
  └── Task 9 (Order API)

Task 3 + Task 10
  └── Task 15 (Home Stats)

Task 10
  └── Task 17 (User Services)

All Tasks
  └── Task 18 (Integration Testing)
```

## Parallelizable Tasks
- Tasks 2-8, 11-14, 16 can run in parallel after Task 1
- Tasks 9, 10, 15, 17 depend on earlier tasks

## Technical Requirements
- Use Spring Boot 3.x with Jakarta EE
- Use JWT for authentication
- Follow RESTful API conventions
- Use proper HTTP status codes
- Implement request validation
- Implement proper error responses
- Add Swagger/OpenAPI documentation
- Use DTOs for request/response objects
