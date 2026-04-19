# User Backend API Testing Spec

## Why
The backend has implemented 85 REST API endpoints for 28 user-side frontend pages, but comprehensive test coverage is missing. Without proper tests, we cannot ensure API reliability, catch regressions, or validate that the implementation matches the frontend expectations. This spec defines the testing strategy and implementation for all user backend APIs.

## What Changes
- Create comprehensive test classes for all 85 API endpoints
- Implement test base class with common setup and utilities
- Add test data fixtures for consistent testing
- Cover normal requests, boundary conditions, and error handling
- Test parameter validation, response status codes, and data formats
- Implement authentication and authorization tests
- Generate test coverage reports

## Impact
- Affected specs: user-backend-api
- Affected code:
  - `src/test/java/com/petshop/controller/api/` - Test classes
  - `src/test/java/com/petshop/` - Test utilities and fixtures
  - `pom.xml` - Test dependencies and configuration

## ADDED Requirements

### Requirement: Test Infrastructure
The system SHALL provide a comprehensive test infrastructure for API testing.

#### Scenario: Test Base Class
- **WHEN** writing API tests
- **THEN** a base test class provides common setup, authentication, and utility methods

#### Scenario: Test Data Fixtures
- **WHEN** running tests
- **THEN** consistent test data is available for all test scenarios

### Requirement: Authentication API Tests
The system SHALL provide complete test coverage for authentication endpoints.

#### Scenario: Login Tests
- **WHEN** testing POST `/api/auth/login`
- **THEN** tests cover valid credentials, invalid credentials, missing fields, and account lockout

#### Scenario: Registration Tests
- **WHEN** testing POST `/api/auth/register`
- **THEN** tests cover valid registration, duplicate email/username, validation errors

#### Scenario: User Info Tests
- **WHEN** testing GET/PUT `/api/auth/userinfo`
- **THEN** tests cover authenticated access, unauthenticated access, update validation

#### Scenario: Password Tests
- **WHEN** testing PUT `/api/auth/password`
- **THEN** tests cover valid password change, wrong old password, validation errors

### Requirement: Pet Management API Tests
The system SHALL provide complete test coverage for pet management endpoints.

#### Scenario: Pet CRUD Tests
- **WHEN** testing pet endpoints
- **THEN** tests cover create, read, update, delete with ownership validation

#### Scenario: Pet Ownership Tests
- **WHEN** accessing another user's pet
- **THEN** tests verify 403/404 responses

### Requirement: Address Management API Tests
The system SHALL provide complete test coverage for address management endpoints.

#### Scenario: Address CRUD Tests
- **WHEN** testing address endpoints
- **THEN** tests cover all CRUD operations and default address handling

### Requirement: Merchant & Service API Tests
The system SHALL provide complete test coverage for merchant and service endpoints.

#### Scenario: Merchant List Tests
- **WHEN** testing GET `/api/merchants`
- **THEN** tests cover pagination, filtering, and sorting

#### Scenario: Merchant Detail Tests
- **WHEN** testing merchant detail endpoints
- **THEN** tests cover services, products, reviews, and available slots

### Requirement: Service API Tests
The system SHALL provide complete test coverage for service endpoints.

#### Scenario: Service List Tests
- **WHEN** testing GET `/api/services`
- **THEN** tests cover type filtering and pagination

#### Scenario: Service Search Tests
- **WHEN** testing GET `/api/services/search`
- **THEN** tests cover keyword search and empty results

### Requirement: Product & Cart API Tests
The system SHALL provide complete test coverage for product and cart endpoints.

#### Scenario: Product Tests
- **WHEN** testing product endpoints
- **THEN** tests cover list, detail, search, and reviews

#### Scenario: Cart Tests
- **WHEN** testing cart endpoints
- **THEN** tests cover add, update, remove, batch operations

### Requirement: Order Management API Tests
The system SHALL provide complete test coverage for order management endpoints.

#### Scenario: Order CRUD Tests
- **WHEN** testing order endpoints
- **THEN** tests cover create, read, update, delete with status transitions

#### Scenario: Order Payment Tests
- **WHEN** testing payment endpoints
- **THEN** tests cover payment flow and status checking

#### Scenario: Order Batch Tests
- **WHEN** testing batch operations
- **THEN** tests cover batch cancel and delete

### Requirement: Appointment API Tests
The system SHALL provide complete test coverage for appointment endpoints.

#### Scenario: Appointment CRUD Tests
- **WHEN** testing appointment endpoints
- **THEN** tests cover create, read, cancel with validation

#### Scenario: Appointment Stats Tests
- **WHEN** testing GET `/api/user/appointments/stats`
- **THEN** tests verify correct statistics calculation

### Requirement: Review API Tests
The system SHALL provide complete test coverage for review endpoints.

#### Scenario: Review CRUD Tests
- **WHEN** testing review endpoints
- **THEN** tests cover create, read, update, delete with rating validation

### Requirement: Notification API Tests
The system SHALL provide complete test coverage for notification endpoints.

#### Scenario: Notification Tests
- **WHEN** testing notification endpoints
- **THEN** tests cover list, read, delete, and unread count

### Requirement: Search API Tests
The system SHALL provide complete test coverage for search endpoints.

#### Scenario: Search Tests
- **WHEN** testing search endpoints
- **THEN** tests cover suggestions, hot keywords, and history

### Requirement: Announcement API Tests
The system SHALL provide complete test coverage for announcement endpoints.

#### Scenario: Announcement Tests
- **WHEN** testing announcement endpoints
- **THEN** tests cover list and detail endpoints

### Requirement: Home Statistics API Tests
The system SHALL provide complete test coverage for home statistics endpoints.

#### Scenario: Stats Tests
- **WHEN** testing GET `/api/user/home/stats`
- **THEN** tests verify correct statistics for pets, appointments, and reviews

#### Scenario: Activity Tests
- **WHEN** testing GET `/api/user/home/activities`
- **THEN** tests verify activity aggregation and ordering

### Requirement: Favorite API Tests
The system SHALL provide complete test coverage for favorite endpoints.

#### Scenario: Favorite Tests
- **WHEN** testing favorite endpoints
- **THEN** tests cover merchant, service, and product favorites

### Requirement: Test Coverage Report
The system SHALL generate comprehensive test coverage reports.

#### Scenario: Coverage Report
- **WHEN** tests complete
- **THEN** a report shows coverage percentage, passed/failed tests, and details

## MODIFIED Requirements
None - This is new functionality.

## REMOVED Requirements
None - No features are being removed.

## Test Categories Summary

| Category | Endpoints | Test Class |
|----------|-----------|------------|
| Authentication | 8 | AuthApiControllerTest |
| Pet Management | 5 | UserApiControllerPetTest |
| Address Management | 5 | UserApiControllerAddressTest |
| Merchant & Service | 6 | PublicApiControllerMerchantTest |
| Service | 4 | ServiceApiControllerTest |
| Product & Cart | 12 | ProductApiControllerTest, UserApiControllerCartTest |
| Order Management | 12 | UserApiControllerOrderTest |
| Appointment | 5 | UserApiControllerAppointmentTest |
| Review | 5 | UserApiControllerReviewTest |
| Notification | 7 | UserApiControllerNotificationTest |
| Search | 5 | SearchApiControllerTest |
| Announcement | 2 | AnnouncementApiControllerTest |
| Home Statistics | 2 | UserApiControllerHomeTest |
| Favorites | 6 | UserApiControllerFavoriteTest |
| User Services | 1 | UserApiControllerServiceTest |
| **Total** | **85** | **15 test classes** |
