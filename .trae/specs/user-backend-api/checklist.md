# User Backend API Implementation Checklist

## Infrastructure Setup
- [ ] DTO classes created for all API responses
- [ ] ApiResponse wrapper class implemented
- [ ] JWT authentication filter configured
- [ ] CORS configuration for frontend
- [ ] Exception handling middleware implemented
- [ ] Swagger/OpenAPI documentation configured

## Authentication API (8 endpoints)
- [ ] POST `/api/auth/login` - User login
  - [ ] Validates credentials
  - [ ] Returns JWT token
  - [ ] Returns user info
- [ ] POST `/api/auth/register` - User registration
  - [ ] Creates new user
  - [ ] Returns JWT token
  - [ ] Validates unique email/username
- [ ] POST `/api/auth/logout` - User logout
  - [ ] Invalidates token
- [ ] GET `/api/auth/userinfo` - Get current user
  - [ ] Returns user profile
- [ ] PUT `/api/auth/userinfo` - Update user
  - [ ] Updates profile fields
  - [ ] Returns updated user
- [ ] PUT `/api/auth/password` - Change password
  - [ ] Validates old password
  - [ ] Updates to new password
- [ ] POST `/api/auth/sendVerifyCode` - Send verification
  - [ ] Sends email verification code
- [ ] POST `/api/auth/resetPassword` - Reset password
  - [ ] Validates verification code
  - [ ] Resets password

## Pet Management API (5 endpoints)
- [ ] GET `/api/user/pets` - Get pet list
  - [ ] Returns user's pets
  - [ ] Requires authentication
- [ ] GET `/api/user/pets/{id}` - Get pet details
  - [ ] Returns pet info
  - [ ] Validates ownership
- [ ] POST `/api/user/pets` - Add pet
  - [ ] Creates pet linked to user
  - [ ] Validates required fields
- [ ] PUT `/api/user/pets/{id}` - Update pet
  - [ ] Updates pet info
  - [ ] Validates ownership
- [ ] DELETE `/api/user/pets/{id}` - Delete pet
  - [ ] Deletes pet
  - [ ] Validates ownership

## Address Management API (5 endpoints)
- [ ] GET `/api/user/addresses` - Get address list
- [ ] POST `/api/user/addresses` - Add address
- [ ] PUT `/api/user/addresses/{id}` - Update address
- [ ] DELETE `/api/user/addresses/{id}` - Delete address
- [ ] PUT `/api/user/addresses/{id}/default` - Set default

## Merchant & Service API (6 endpoints)
- [ ] GET `/api/merchants` - Get merchant list
  - [ ] Supports pagination
  - [ ] Supports filters (keyword, rating, sortBy)
- [ ] GET `/api/merchant/{id}` - Get merchant details
- [ ] GET `/api/merchant/{id}/services` - Get merchant services
- [ ] GET `/api/merchant/{id}/products` - Get merchant products
- [ ] GET `/api/merchant/{id}/reviews` - Get merchant reviews
- [ ] GET `/api/merchant/{id}/available-slots` - Get available slots

## Service API (4 endpoints)
- [ ] GET `/api/services` - Get service list
  - [ ] Supports type filter
- [ ] GET `/api/services/{id}` - Get service details
- [ ] GET `/api/services/search` - Search services
- [ ] GET `/api/services/recommended` - Get recommended

## Product & Cart API (12 endpoints)
- [ ] GET `/api/products` - Get product list
- [ ] GET `/api/products/{id}` - Get product details
- [ ] GET `/api/products/search` - Search products
- [ ] GET `/api/products/{id}/reviews` - Get product reviews
- [ ] POST `/api/user/cart` - Add to cart
- [ ] GET `/api/user/cart` - Get cart
- [ ] PUT `/api/user/cart` - Update cart item
- [ ] DELETE `/api/user/cart/{id}` - Remove from cart
- [ ] DELETE `/api/user/cart/batch` - Batch remove
- [ ] POST `/api/user/favorites/products` - Add product favorite
- [ ] DELETE `/api/user/favorites/products/{id}` - Remove favorite
- [ ] GET `/api/user/favorites/products/{id}/check` - Check favorite

## Order Management API (12 endpoints)
- [ ] GET `/api/user/orders` - Get order list
- [ ] GET `/api/user/orders/{id}` - Get order details
- [ ] POST `/api/user/orders` - Create order
- [ ] POST `/api/user/orders/preview` - Preview order
- [ ] POST `/api/user/orders/{id}/pay` - Pay order
- [ ] GET `/api/user/orders/{id}/pay/status` - Payment status
- [ ] PUT `/api/user/orders/{id}/cancel` - Cancel order
- [ ] POST `/api/user/orders/{id}/refund` - Refund order
- [ ] PUT `/api/user/orders/{id}/confirm` - Confirm receive
- [ ] DELETE `/api/user/orders/{id}` - Delete order
- [ ] PUT `/api/user/orders/batch-cancel` - Batch cancel
- [ ] DELETE `/api/user/orders/batch-delete` - Batch delete

## Appointment API (5 endpoints)
- [ ] GET `/api/user/appointments` - Get appointment list
- [ ] GET `/api/user/appointments/{id}` - Get appointment details
- [ ] POST `/api/user/appointments` - Create appointment
- [ ] PUT `/api/user/appointments/{id}/cancel` - Cancel appointment
- [ ] GET `/api/user/appointments/stats` - Get stats

## Review API (5 endpoints)
- [ ] POST `/api/user/reviews` - Add review
- [ ] GET `/api/user/reviews` - Get user reviews
- [ ] PUT `/api/user/reviews/{id}` - Update review
- [ ] DELETE `/api/user/reviews/{id}` - Delete review

## Notification API (7 endpoints)
- [ ] GET `/api/user/notifications` - Get notification list
  - [ ] Supports type filter
  - [ ] Supports isRead filter
- [ ] PUT `/api/user/notifications/{id}/read` - Mark as read
- [ ] PUT `/api/user/notifications/read-all` - Mark all read
- [ ] PUT `/api/user/notifications/batch-read` - Batch mark read
- [ ] DELETE `/api/user/notifications/{id}` - Delete notification
- [ ] DELETE `/api/user/notifications/batch` - Batch delete
- [ ] GET `/api/user/notifications/unread-count` - Unread count

## Search API (5 endpoints)
- [ ] GET `/api/search/suggestions` - Get suggestions
- [ ] GET `/api/search/hot-keywords` - Get hot keywords
- [ ] POST `/api/user/search-history` - Save history
- [ ] GET `/api/user/search-history` - Get history
- [ ] DELETE `/api/user/search-history` - Clear history

## Announcement API (2 endpoints)
- [ ] GET `/api/announcements` - Get announcement list
- [ ] GET `/api/announcements/{id}` - Get announcement details

## Home Statistics API (2 endpoints)
- [ ] GET `/api/user/home/stats` - Get statistics
  - [ ] Returns petCount
  - [ ] Returns pendingAppointments
  - [ ] Returns reviewCount
- [ ] GET `/api/user/home/activities` - Get activities

## Favorite Management API (6 endpoints)
- [ ] GET `/api/user/favorites` - Get favorite merchants
- [ ] POST `/api/user/favorites` - Add merchant favorite
- [ ] DELETE `/api/user/favorites/{id}` - Remove merchant favorite
- [ ] GET `/api/user/favorites/services` - Get favorite services
- [ ] POST `/api/user/favorites/services` - Add service favorite
- [ ] DELETE `/api/user/favorites/services/{id}` - Remove service favorite

## User Services API (1 endpoint)
- [ ] GET `/api/user/services` - Get purchased services

## Integration Testing
- [ ] Authentication tests pass
- [ ] Authorization tests pass
- [ ] All API endpoints return correct responses
- [ ] Error handling works correctly
- [ ] Response format matches frontend expectations

## Documentation
- [ ] Swagger UI accessible
- [ ] All endpoints documented
- [ ] Request/response examples provided

## Total Endpoints: 85
- Authentication: 8
- Pet Management: 5
- Address Management: 5
- Merchant & Service: 6
- Service: 4
- Product & Cart: 12
- Order Management: 12
- Appointment: 5
- Review: 5
- Notification: 7
- Search: 5
- Announcement: 2
- Home Statistics: 2
- Favorites: 6
- User Services: 1
