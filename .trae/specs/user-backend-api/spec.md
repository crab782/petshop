# User Backend API Implementation Spec

## Why
The frontend has 85 API endpoints defined for 28 user-side pages, but the Spring Boot backend only implements a small subset of these endpoints. This gap prevents proper frontend-backend communication. We need to implement all missing backend APIs to ensure the frontend can function correctly with real backend services.

## What Changes
- Implement 85 REST API endpoints in Spring Boot backend
- Create new controllers for: Auth, User, Notification, Search, Announcement
- Implement corresponding services and repositories
- Ensure API contracts match frontend TypeScript definitions
- Add proper authentication/authorization
- Implement data validation and error handling

## Impact
- Affected specs: user-api-audit, user-mock-services
- Affected code:
  - `src/main/java/com/petshop/controller/api/` - REST Controllers
  - `src/main/java/com/petshop/service/` - Business Logic
  - `src/main/java/com/petshop/repository/` - Data Access
  - `src/main/java/com/petshop/entity/` - Data Models
  - `src/main/java/com/petshop/dto/` - Data Transfer Objects

## ADDED Requirements

### Requirement: Authentication API
The system SHALL provide complete authentication REST API endpoints.

#### Scenario: User Login
- **WHEN** user submits login credentials to `/api/auth/login`
- **THEN** system validates credentials and returns JWT token
- **AND** returns user info in response

#### Scenario: User Registration
- **WHEN** user submits registration data to `/api/auth/register`
- **THEN** system creates new user account
- **AND** returns JWT token

#### Scenario: Get User Info
- **WHEN** authenticated user requests `/api/auth/userinfo`
- **THEN** system returns current user profile data

#### Scenario: Update User Info
- **WHEN** authenticated user submits update to `/api/auth/userinfo`
- **THEN** system updates user profile
- **AND** returns updated user data

#### Scenario: Change Password
- **WHEN** authenticated user submits password change to `/api/auth/password`
- **THEN** system validates old password
- **AND** updates to new password

### Requirement: Pet Management API
The system SHALL provide complete pet management REST API endpoints.

#### Scenario: Get User Pets
- **WHEN** authenticated user requests `/api/user/pets`
- **THEN** system returns list of user's pets

#### Scenario: Create Pet
- **WHEN** authenticated user submits pet data to `/api/user/pets`
- **THEN** system creates new pet record linked to user

#### Scenario: Update Pet
- **WHEN** authenticated user submits update to `/api/user/pets/{id}`
- **THEN** system updates pet if user owns it

#### Scenario: Delete Pet
- **WHEN** authenticated user requests DELETE `/api/user/pets/{id}`
- **THEN** system deletes pet if user owns it

### Requirement: Address Management API
The system SHALL provide complete address management REST API endpoints.

#### Scenario: CRUD Operations
- **WHEN** user performs address operations
- **THEN** system manages addresses with proper authorization
- **AND** supports setting default address

### Requirement: Merchant & Service API
The system SHALL provide merchant and service related REST API endpoints.

#### Scenario: Get Merchants
- **WHEN** user requests `/api/merchants`
- **THEN** system returns paginated merchant list with filters

#### Scenario: Get Merchant Details
- **WHEN** user requests `/api/merchant/{id}`
- **THEN** system returns merchant info with services and products

#### Scenario: Get Services
- **WHEN** user requests `/api/services`
- **THEN** system returns service list with optional filters

### Requirement: Product & Cart API
The system SHALL provide product and shopping cart REST API endpoints.

#### Scenario: Product Operations
- **WHEN** user requests product endpoints
- **THEN** system returns product data with reviews

#### Scenario: Cart Operations
- **WHEN** user manages cart
- **THEN** system handles add, update, delete operations

### Requirement: Order Management API
The system SHALL provide complete order management REST API endpoints.

#### Scenario: Order CRUD
- **WHEN** user performs order operations
- **THEN** system manages orders with status transitions
- **AND** supports payment integration

### Requirement: Appointment API
The system SHALL provide appointment management REST API endpoints.

#### Scenario: Appointment Operations
- **WHEN** user manages appointments
- **THEN** system handles booking, cancellation, status updates

### Requirement: Review API
The system SHALL provide review management REST API endpoints.

#### Scenario: Review Operations
- **WHEN** user manages reviews
- **THEN** system handles CRUD with proper authorization

### Requirement: Notification API
The system SHALL provide notification management REST API endpoints.

#### Scenario: Notification Operations
- **WHEN** user manages notifications
- **THEN** system handles read/unread status and deletion

### Requirement: Search API
The system SHALL provide search functionality REST API endpoints.

#### Scenario: Search Operations
- **WHEN** user performs search
- **THEN** system returns matching results across entities
- **AND** manages search history

### Requirement: Announcement API
The system SHALL provide announcement REST API endpoints.

#### Scenario: Get Announcements
- **WHEN** user requests announcements
- **THEN** system returns published announcements

### Requirement: Home Statistics API
The system SHALL provide user home page statistics REST API endpoints.

#### Scenario: Get Home Stats
- **WHEN** user requests `/api/user/home/stats`
- **THEN** system returns pet count, pending appointments, review count

#### Scenario: Get Recent Activities
- **WHEN** user requests `/api/user/home/activities`
- **THEN** system returns recent user activities

### Requirement: Favorite API
The system SHALL provide favorite management REST API endpoints.

#### Scenario: Favorite Operations
- **WHEN** user manages favorites
- **THEN** system handles merchant and service favorites

## MODIFIED Requirements
None - This is new functionality.

## REMOVED Requirements
None - No features are being removed.

## API Endpoint Summary

| Category | Count | Endpoints |
|----------|-------|-----------|
| Authentication | 8 | login, register, logout, userinfo, password, verifyCode, resetPassword |
| Pet Management | 5 | CRUD operations |
| Address Management | 5 | CRUD + setDefault |
| Merchant & Service | 6 | list, detail, services, products, reviews, slots |
| Service | 4 | list, detail, search, recommended |
| Product & Cart | 12 | products, reviews, favorites, cart operations |
| Order Management | 12 | CRUD, payment, status operations |
| Appointment | 5 | CRUD, stats |
| Review | 5 | CRUD operations |
| Notification | 7 | CRUD, read operations, unread count |
| Search | 5 | suggestions, keywords, history |
| Announcement | 2 | list, detail |
| Home Statistics | 2 | stats, activities |
| User Services | 1 | purchased services |
| Favorites | 6 | merchant and service favorites |
| **Total** | **85** | |
