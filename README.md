# Letter of Recommendation (LOR) Management System

A comprehensive web application for managing Letter of Recommendation requests between students and professors.

## Tech Stack

### Frontend
- React + TypeScript
- React Router for navigation
- Material UI (MUI) for styling
- Formik + Yup for form validation
- Axios for API calls

### Backend
- Java Spring Boot
- Spring Security with JWT authentication
- Spring Data JPA
- iText for PDF generation
- MySQL database

### Database
- MySQL 8.0+
- Hostname: localhost
- Port: 3306
- Username: root
- Password: Ankit

## Project Structure

```
lor-management-system/
├── frontend/                 # React + TypeScript frontend
│   ├── src/
│   │   ├── components/      # Reusable UI components
│   │   ├── pages/          # Page components
│   │   ├── services/       # API service calls
│   │   ├── types/          # TypeScript type definitions
│   │   ├── utils/          # Utility functions
│   │   └── App.tsx         # Main app component
│   ├── package.json
│   └── tsconfig.json
├── backend/                 # Spring Boot backend
│   ├── src/main/java/
│   │   └── com/lor/
│   │       ├── config/     # Configuration classes
│   │       ├── controller/ # REST controllers
│   │       ├── entity/     # JPA entities
│   │       ├── repository/ # Data repositories
│   │       ├── service/    # Business logic
│   │       ├── security/   # Security configuration
│   │       └── dto/        # Data transfer objects
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── data.sql        # Initial data
│   └── pom.xml
├── database/               # Database scripts
│   ├── schema.sql         # Database schema
│   └── initial-data.sql   # Initial admin data
└── README.md
```

## Features

### User Roles

1. **Admin**
   - Manages pre-uploaded verified student and professor data
   - Validates registrations against stored data
   - Access to admin dashboard

2. **Student**
   - Register with validated credentials
   - Submit LOR requests
   - Track request status
   - Download approved LORs

3. **Professor**
   - Register with validated credentials
   - View and manage LOR requests
   - Approve/reject requests
   - Generate PDF LORs with unique reference numbers

### Security Features
- JWT-based authentication
- Role-based access control
- Secure PDF storage and download
- Input validation and sanitization

## Getting Started

### Prerequisites
- Node.js 18+
- Java 17+
- MySQL 8.0+
- Maven 3.6+

### Database Setup
1. Create MySQL database: `lor_management`
2. Run schema.sql to create tables
3. Run initial-data.sql to populate admin data

### Backend Setup
1. Navigate to backend directory
2. Configure database connection in application.yml
3. Run: `mvn spring-boot:run`

### Frontend Setup
1. Navigate to frontend directory
2. Install dependencies: `npm install`
3. Start development server: `npm start`

## API Endpoints

### Authentication
- POST /api/auth/login
- POST /api/auth/register

### Students
- GET /api/students/profile
- POST /api/students/lor-request
- GET /api/students/requests

### Professors
- GET /api/professors/requests
- PUT /api/professors/requests/{id}/approve
- PUT /api/professors/requests/{id}/reject
- POST /api/professors/generate-pdf/{id}

### Admin
- GET /api/admin/students
- GET /api/admin/professors
- POST /api/admin/students
- POST /api/admin/professors

## System Architecture

### High-Level Architecture
The system follows a three-tier architecture:
1. **Presentation Layer**: React frontend
2. **Application Layer**: Spring Boot REST API
3. **Data Layer**: MySQL database

### Component Diagram
```
Frontend (React + TypeScript)
  ├── Components
  ├── Services
  └── Contexts
         │
         ▼
Backend (Spring Boot)
  ├── Controllers
  ├── Services
  └── Repositories
         │
         ▼
Database (MySQL)
  ├── Users
  ├── LOR Requests
  └── PDF Documents
```

## Database Schema

### Entity Relationship Diagram
```
User ──┐
       │
       ├── LOR Request ── PDF Document
       │
Professor
```

### Key Tables
- **users**: Stores user accounts with role information
- **lor_requests**: Contains all LOR requests with details
- **pdf_documents**: Stores generated PDF metadata
- **admin_students**: Pre-verified student records
- **admin_professors**: Pre-verified professor records

## Security Implementation

### Authentication Flow
1. User submits credentials
2. Server validates credentials
3. JWT token generated with user role and expiration
4. Token stored in client localStorage
5. Token included in Authorization header for API requests

### Authorization
- Role-based access control (RBAC) implemented
- Endpoints secured with Spring Security
- Frontend routes protected with route guards

## PDF Generation

### Process
1. Professor approves LOR request
2. System generates unique reference number
3. PDF created with iText library
4. Document stored in secure file system
5. Metadata saved in database
6. Download link provided to student

### Security Features
- Reference number validation
- Access control based on user role
- Secure storage with restricted access

## Deployment Guide

### Production Setup
1. Build frontend: `npm run build`
2. Build backend: `mvn package`
3. Configure production database
4. Set up secure file storage
5. Deploy with appropriate server configuration

### Environment Variables
- DATABASE_URL
- JWT_SECRET
- FILE_STORAGE_PATH
- CORS_ALLOWED_ORIGINS
