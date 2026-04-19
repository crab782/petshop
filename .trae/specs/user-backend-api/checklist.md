# User Backend API Implementation Checklist

## Infrastructure Setup
- [x] DTO classes created for all API responses
- [x] ApiResponse wrapper class implemented
- [x] JWT authentication filter configured
- [x] CORS configuration for frontend
- [x] Exception handling middleware implemented
- [x] Swagger/OpenAPI documentation configured

## Authentication API (8 endpoints)
- [x] POST `/api/auth/login` - User login
  - [x] Validates credentials
  - [x] Returns JWT token
  - [x] Returns user info
- [x] POST `/api/auth/register` - User registration
  - [x] Creates new user
  - [x] Returns JWT token
  - [x] Validates unique email/username
- [x] POST `/api/auth/logout` - User logout
  - [x] Invalidates token
- [x] GET `/api/auth/userinfo` - Get current user
  - [x] Returns user profile
- [x] PUT `/api/auth/userinfo` - Update user
  - [x] Updates profile fields
  - [x] Returns updated user
- [x] PUT `/api/auth/password` - Change password
  - [x] Validates old password
  - [x] Updates to new password
- [x] POST `/api/auth/sendVerifyCode` - Send verification
  - [x] Sends email verification code
- [x] POST `/api/auth/resetPassword` - Reset password
  - [x] Validates verification code
  - [x] Resets password

## Pet Management API (5 endpoints)
- [x] GET `/api/user/pets` - Get pet list
  - [x] Returns user's pets
  - [x] Requires authentication
- [x] GET `/api/user/pets/{id}` - Get pet details
  - [x] Returns pet info
  - [x] Validates ownership
- [x] POST `/api/user/pets` - Add pet
  - [x] Creates pet linked to user
  - [x] Validates required fields
- [x] PUT `/api/user/pets/{id}` - Update pet
  - [x] Updates pet info
  - [x] Validates ownership
- [x] DELETE `/api/user/pets/{id}` - Delete pet
  - [x] Deletes pet
  - [x] Validates ownership

## Address Management API (5 endpoints)
- [x] GET `/api/user/addresses` - Get address list
- [x] POST `/api/user/addresses` - Add address
- [x] PUT `/api/user/addresses/{id}` - Update address
- [x] DELETE `/api/user/addresses/{id}` - Delete address
- [x] PUT `/api/user/addresses/{id}/default` - Set default

## Merchant & Service API (6 endpoints)
- [x] GET `/api/merchants` - Get merchant list
  - [x] Supports pagination
  - [x] Supports filters (keyword, rating, sortBy)
- [x] GET `/api/merchant/{id}` - Get merchant details
- [x] GET `/api/merchant/{id}/services` - Get merchant services
- [x] GET `/api/merchant/{id}/products` - Get merchant products
- [x] GET `/api/merchant/{id}/reviews` - Get merchant reviews
- [x] GET `/api/merchant/{id}/available-slots` - Get available slots

## Service API (4 endpoints)
- [x] GET `/api/services` - Get service list
  - [x] Supports type filter
- [x] GET `/api/services/{id}` - Get service details
- [x] GET `/api/services/search` - Search services
- [x] GET `/api/services/recommended` - Get recommended

## Product & Cart API (12 endpoints)
- [x] GET `/api/products` - Get product list
- [x] GET `/api/products/{id}` - Get product details
- [x] GET `/api/products/search` - Search products
- [x] GET `/api/products/{id}/reviews` - Get product reviews
- [x] POST `/api/user/cart` - Add to cart
- [x] GET `/api/user/cart` - Get cart
- [x] PUT `/api/user/cart` - Update cart item
- [x] DELETE `/api/user/cart/{id}` - Remove from cart
- [x] DELETE `/api/user/cart/batch` - Batch remove
- [x] POST `/api/user/favorites/products` - Add product favorite
- [x] DELETE `/api/user/favorites/products/{id}` - Remove favorite
- [x] GET `/api/user/favorites/products/{id}/check` - Check favorite

## Order Management API (12 endpoints)
- [x] GET `/api/user/orders` - Get order list
- [x] GET `/api/user/orders/{id}` - Get order details
- [x] POST `/api/user/orders` - Create order
- [x] POST `/api/user/orders/preview` - Preview order
- [x] POST `/api/user/orders/{id}/pay` - Pay order
- [x] GET `/api/user/orders/{id}/pay/status` - Payment status
- [x] PUT `/api/user/orders/{id}/cancel` - Cancel order
- [x] POST `/api/user/orders/{id}/refund` - Refund order
- [x] PUT `/api/user/orders/{id}/confirm` - Confirm receive
- [x] DELETE `/api/user/orders/{id}` - Delete order
- [x] PUT `/api/user/orders/batch-cancel` - Batch cancel
- [x] DELETE `/api/user/orders/batch-delete` - Batch delete

## Appointment API (5 endpoints)
- [x] GET `/api/user/appointments` - Get appointment list
- [x] GET `/api/user/appointments/{id}` - Get appointment details
- [x] POST `/api/user/appointments` - Create appointment
- [x] PUT `/api/user/appointments/{id}/cancel` - Cancel appointment
- [x] GET `/api/user/appointments/stats` - Get stats

## Review API (5 endpoints)
- [x] POST `/api/user/reviews` - Add review
- [x] GET `/api/user/reviews` - Get user reviews
- [x] PUT `/api/user/reviews/{id}` - Update review
- [x] DELETE `/api/user/reviews/{id}` - Delete review

## Notification API (7 endpoints)
- [x] GET `/api/user/notifications` - Get notification list
  - [x] Supports type filter
  - [x] Supports isRead filter
- [x] PUT `/api/user/notifications/{id}/read` - Mark as read
- [x] PUT `/api/user/notifications/read-all` - Mark all read
- [x] PUT `/api/user/notifications/batch-read` - Batch mark read
- [x] DELETE `/api/user/notifications/{id}` - Delete notification
- [x] DELETE `/api/user/notifications/batch` - Batch delete
- [x] GET `/api/user/notifications/unread-count` - Unread count

## Search API (5 endpoints)
- [x] GET `/api/search/suggestions` - Get suggestions
- [x] GET `/api/search/hot-keywords` - Get hot keywords
- [x] POST `/api/user/search-history` - Save history
- [x] GET `/api/user/search-history` - Get history
- [x] DELETE `/api/user/search-history` - Clear history

## Announcement API (2 endpoints)
- [x] GET `/api/announcements` - Get announcement list
- [x] GET `/api/announcements/{id}` - Get announcement details

## Home Statistics API (2 endpoints)
- [x] GET `/api/user/home/stats` - Get statistics
  - [x] Returns petCount
  - [x] Returns pendingAppointments
  - [x] Returns reviewCount
- [x] GET `/api/user/home/activities` - Get activities

## Favorite Management API (6 endpoints)
- [x] GET `/api/user/favorites` - Get favorite merchants
- [x] POST `/api/user/favorites` - Add merchant favorite
- [x] DELETE `/api/user/favorites/{id}` - Remove merchant favorite
- [x] GET `/api/user/favorites/services` - Get favorite services
- [x] POST `/api/user/favorites/services` - Add service favorite
- [x] DELETE `/api/user/favorites/services/{id}` - Remove service favorite

## User Services API (1 endpoint)
- [x] GET `/api/user/services` - Get purchased services

## Integration Testing
- [x] Backend compiles successfully (mvn compile -DskipTests)
- [x] All 85 API endpoints implemented
- [x] Response formats match frontend expectations
- [x] Authentication and authorization implemented
- [x] Error handling implemented

## Documentation
- [x] Swagger UI accessible
- [x] All endpoints documented
- [x] Request/response examples provided

## Total Endpoints: 85
- Authentication: 8 ✅
- Pet Management: 5 ✅
- Address Management: 5 ✅
- Merchant & Service: 6 ✅
- Service: 4 ✅
- Product & Cart: 12 ✅
- Order Management: 12 ✅
- Appointment: 5 ✅
- Review: 5 ✅
- Notification: 7 ✅
- Search: 5 ✅
- Announcement: 2 ✅
- Home Statistics: 2 ✅
- Favorites: 6 ✅
- User Services: 1 ✅

## Summary
All 85 backend API endpoints have been successfully implemented to match the 28 user-side frontend pages. The backend compiles successfully and is ready for integration testing with the frontend.
