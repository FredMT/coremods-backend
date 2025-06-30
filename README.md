# Coremods Authentication System

A complete Spring Boot 3.5 authentication system with email verification, session-based authentication, and CSRF protection.

## Features

- **User Registration & Login**: Username/email + password authentication
- **Email Verification**: Secure email verification with time-limited tokens
- **Session-based Authentication**: Secure session management with CSRF protection
- **PostgreSQL Integration**: Full database integration using Spring Data JPA
- **RESTful API**: Clean REST endpoints for all authentication operations
- **Security**: BCrypt password hashing, CSRF protection, secure session management
- **Email Service**: Ready for Resend integration (currently logs emails)

## Technology Stack

- **Spring Boot 3.5.3**
- **Spring Security 6**
- **Spring Data JPA**
- **PostgreSQL**
- **Flyway** (Database migrations)
- **Lombok** (Code generation)
- **Jakarta Validation** (Request validation)

## Database Setup

1. **Install PostgreSQL** and create a database:

   ```sql
   CREATE DATABASE coremods;
   CREATE USER postgres WITH PASSWORD 'postgres';
   GRANT ALL PRIVILEGES ON DATABASE coremods TO postgres;
   ```

2. **Database Configuration** (already configured in `application.properties`):

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/coremods
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   ```

3. **Database Migration**: Flyway will automatically create the required tables on startup.

## Running the Application

1. **Prerequisites**:

   - Java 21+
   - PostgreSQL running on localhost:5432
   - Database 'coremods' created with user 'postgres'

2. **Start the application**:

   ```bash
   ./mvnw spring-boot:run
   ```

3. **Application will be available at**: `http://localhost:8080`

## API Endpoints

### Authentication Endpoints

#### 1. Register User

```http
POST /auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "confirmPassword": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

#### 2. Login User

```http
POST /auth/login
Content-Type: application/json

{
  "usernameOrEmail": "johndoe",
  "password": "password123"
}
```

#### 3. Verify Email

```http
GET /auth/verify-email?token=ABC123...
```

#### 4. Resend Verification Email

```http
POST /auth/resend-verification?email=john@example.com
```

#### 5. Logout

```http
POST /auth/logout
```

#### 6. Get Current User

```http
GET /auth/me
```

### Protected Endpoints

#### Dashboard

```http
GET /dashboard
```

_Requires authentication_

## Project Structure

```
src/main/java/com/tofutracker/Coremods/
├── config/
│   ├── SecurityConfig.java          # Spring Security configuration
│   └── SchedulingConfig.java        # Scheduled tasks (token cleanup)
├── dto/
│   ├── ApiResponse.java             # Generic API response wrapper
│   ├── LoginRequest.java            # Login request DTO
│   └── RegisterRequest.java         # Registration request DTO
├── entity/
│   ├── EmailVerificationToken.java  # Email verification token entity
│   └── User.java                    # User entity
├── repository/
│   ├── EmailVerificationTokenRepository.java
│   └── UserRepository.java          # User repository
├── services/
│   ├── EmailService.java            # Email service (ready for Resend)
│   ├── EmailVerificationService.java # Email verification logic
│   └── UserService.java             # User management service
├── web/
│   ├── AuthController.java          # Authentication endpoints
│   └── DashboardController.java     # Protected dashboard endpoint
└── CoremodsApplication.java         # Main application class
```

## Testing the System

1. **Register a new user** via `/auth/register`
2. **Check application logs** for the verification email (contains the verification URL)
3. **Verify email** by calling the verification URL
4. **Login** via `/auth/login`
5. **Access protected endpoints** like `/dashboard`

Your authentication system is now fully set up and ready to use! 🎉
