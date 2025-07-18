# Coremods

**Coremods** is a game mods website currently under development, built with **Spring Boot 3.5**.  
It aims to be a modern platform for discovering, uploading, and managing game modifications

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

## API Endpoints

### Authentication Endpoints

#### 1. Register User

```http
POST /auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "Password@123",
  "confirmPassword": "Password@123"
}
```

#### 2. Login User

```http
POST /auth/login
Content-Type: application/json

{
  "usernameOrEmail": "johndoe",
  "password": "Password@123"
}
```

#### 3. Verify Email

```http
POST /auth/verify-email?token=ABC123...
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

#### 7. Reset Password (Authenticated)

```http
POST /auth/reset-password
Content-Type: application/json

{
  "currentPassword": "OldPassword@123",
  "newPassword": "NewPassword@123",
  "confirmPassword": "NewPassword@123"
}
```

#### 8. Forgot Password (Request Reset)

```http
POST /auth/forgot-password
Content-Type: application/json

{
  "email": "john@example.com"
}
```

#### 9. Reset Password with Token

```http
POST /auth/forgot-password/reset
Content-Type: application/json

{
  "token": "ABC123...",
  "newPassword": "NewPassword@123",
  "confirmPassword": "NewPassword@123"
}
```

## Milestones

| Date       | Milestone                                                         |
| ---------- | ----------------------------------------------------------------- |
| 2025-06-30 | ✅ Session-based auth implemented                                 |
| 2025-07-08 | ✅ Roles and permissions implemented                              |
| 2025-07-10 | ✅ Spring Session implemented                                     |
| 2025-07-11 | ✅ IGDB API integration implemented                               |
| 2025-07-15 | ✅ Mod details draft submission implemented                       |
| 2025-07-16 | ✅ Mod image upload to DO spaces and YT video linking implemented |
| 2025-07-17 | ✅ Mod comment system implemented                                 |
| 2025-07-17 | ✅ Bug tracker system implemented                                 |
| 2025-07-18 | ✅ Mod tagging and endorsement system implemented                 |
