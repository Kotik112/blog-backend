# 📝 Blog Backend

A Spring Boot backend for managing blog posts with support for image uploads, likes, and user-friendly APIs. Built with Kotlin, PostgreSQL, and follows modern best practices for scalable backend development.

---

## 🚀 Features

- User authentication and authorization (Session, roles)
- Blog post CRUD operations
- Commenting system
- Like functionality
- Image upload and management
- Contact Us form with status tracking
- Swagger API documentation
- Exception handling
- Database migrations with Flyway

---

## ⚙️ Tech Stack

- **Java 17 + Spring Boot**
- **PostgreSQL**
- **Maven**
- **Flyway**
- **Spring Security**
- **Elastic Beanstalk**
- **Multipart File Upload Support**

---

## 🛠️ Setup Instructions

1. **Clone the repository**
2. **Configure the database** in `application-<your profile>.yml`
3. **Run migrations** (Flyway auto-runs on startup)
4. **Start the application**
5. **Access Swagger UI** at `/swagger-ui.html`

### ✅ Prerequisites

- Java 17
- Maven
- PostgreSQL (Ensure it's running locally on port `5432`)
- IntelliJ IDEA (Recommended)

### 📦 Clone the Repo

```bash
git clone https://github.com/Kotik112/blog-backend.git
cd blog-backend