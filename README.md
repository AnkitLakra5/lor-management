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

## License

This project is licensed under the MIT License.
