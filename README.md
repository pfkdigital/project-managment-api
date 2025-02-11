# Project Management System - Spring Boot & PostgreSQL

## 📌 Overview
This is a **Project Management System** built using **Spring Boot** and **PostgreSQL**. The application allows users to **create projects, assign tasks, track progress, manage issues, and collaborate** with team members. It also includes features like **JWT-based authentication, real-time notifications, email alerts (AWS SES), and file storage (AWS S3).**

## 🚀 Features
- **User Authentication & Authorization** (JWT, Role-Based Access Control)
- **Project Management** (Create, Update, Delete projects)
- **Task Management** (Kanban-style tasks, assignments, due dates, statuses)
- **Issue Tracking** (Bug reports, prioritization, and resolution tracking)
- **Notifications** (Real-time WebSockets & Email alerts via AWS SES)
- **File Storage** (AWS S3 integration for document uploads)
- **Database Management** (PostgreSQL with JPA & Hibernate)
- **Caching** (Redis for performance optimization)
- **Security** (Spring Security, HTTPS, CSRF protection)
- **Deployment** (Dockerized, AWS ECS, RDS, Load Balancer, VPC)

## 🏗️ Project Structure
```
pfk-project-management/
│── src/main/java/com/pfk/projectmanagement/
│   ├── config/             # Configuration files (Security, CORS, DB, etc.)
│   ├── controller/         # REST API controllers
│   ├── dto/                # Data Transfer Objects (DTOs)
│   ├── entity/             # JPA Entities (Database Models)
│   ├── repository/         # Repository Interfaces (Spring Data JPA)
│   ├── service/            # Business logic (Service Layer)
│   ├── util/               # Utility classes (mappers, constants, helpers)
│   ├── exception/          # Global exception handling (`@ControllerAdvice`)
│── src/main/resources/
│── application.yml         # Configuration file
│── Dockerfile              # Docker setup
│── README.md               # Project documentation
```

## 🛠️ Tech Stack
- **Backend**: Spring Boot, Spring Security, JWT
- **Database**: PostgreSQL, Hibernate (JPA)
- **Caching**: Redis
- **Messaging**: WebSockets
- **Storage**: AWS S3
- **Email**: AWS SES
- **Deployment**: Docker, AWS ECS, RDS, Load Balancer

## 🔧 Installation & Setup
### Prerequisites
- Java 17+
- PostgreSQL
- Redis (for caching)
- Docker (if running inside a container)

### Step 1: Clone Repository
```bash
git clone https://github.com/your-repo/pfk-project-management.git
cd pfk-project-management
```

### Step 2: Configure Database
Update `application.yml` with your **PostgreSQL credentials**:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/project_management
    username: your_username
    password: your_password
```

### Step 3: Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

### Step 4: API Endpoints
- **User Authentication**
  - `POST /api/auth/register` - Register a new user
  - `POST /api/auth/login` - Authenticate user and receive JWT
- **Project Management**
  - `GET /api/projects` - Get all projects
  - `POST /api/projects` - Create a new project
  - `GET /api/projects/{id}` - Get project by ID
- **Task Management**
  - `POST /api/projects/{id}/tasks` - Add a task to a project
- **Notifications**
  - `GET /api/notifications` - Fetch user notifications
  
## 🚀 Deployment
### **Docker Setup**
Build and run the application using Docker:
```bash
docker build -t pfk-project-management .
docker run -p 8080:8080 pfk-project-management
```

### **AWS Deployment**
1. Deploy **PostgreSQL on AWS RDS**
2. Use **AWS S3** for file storage
3. Set up **AWS ECS** for containerized deployment
4. Configure **AWS SES** for email notifications
5. Use **Application Load Balancer & Auto Scaling**

## ✅ Security Best Practices
- **JWT-based Authentication**
- **Role-based Authorization (RBAC)**
- **Data Encryption & HTTPS Enforcement**
- **API Rate Limiting & CSRF Protection**

## 📜 License
This project is licensed under the MIT License.

## 👨‍💻 Contributors
- **[Nuh Ali]** - Developer

## 📧 Contact
For support or inquiries, email: **noah@pfkdigital.co.uk**

