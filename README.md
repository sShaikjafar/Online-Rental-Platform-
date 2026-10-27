Online Rental Platform

📌 Project Overview

The Online Rental Platform is a Java-based web application backend that allows users to list products for rent, discover available products, and manage rental orders through a centralized platform. It provides a structured system for product owners, customers, and administrators to manage rental activities securely and efficiently.

The application is built using **Java, Spring Boot, Spring Security, Spring Data JPA, and MySQL**. It supports JWT-based authentication, email and phone OTP verification, product approval workflows, location-based product discovery, order management, and real-time notifications.

✨ Key Features

- User Authentication: User registration, login, JWT authentication, and secure password management.
- Email & Phone Verification: OTP-based verification and OTP resend functionality.
- Product Management: Create, update, view, and delete product listings.
- Product Discovery: Browse products using categories, keywords, price ranges, sorting, and pagination.
- Location-Based Search: Discover nearby products using geographic coordinates and distance calculations.
- Rental Order Management: Create orders, view order history, update order status, and cancel orders.
- Admin Dashboard APIs: Manage users, approve or reject product listings, and manage product categories.
- Real-Time Notifications: WebSocket-based messaging for order updates and other platform events.
- Email & SMS Notifications: Send verification codes and order-related notifications.
- Security: Spring Security, JWT authentication, password hashing, and role-based access control.
- Exception Handling: Centralized exception handling and standardized API responses.

🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| Java 17 | Core programming language |
| Spring Boot 3.1.0 | Backend application framework |
| Spring Web | REST API development |
| Spring Data JPA | Database operations and ORM |
| MySQL | Relational database |
| Spring Security | Authentication and authorization |
| JWT | Token-based authentication |
| Spring Validation | Request data validation |
| Spring Mail | Email notifications |
| Twilio | SMS and OTP integration |
| WebSocket | Real-time messaging |
| Maven | Dependency management and build automation |

🏗️ Project Architecture

The application follows a layered backend architecture:

- Controller Layer: Handles incoming HTTP requests and REST API endpoints.
- Service Layer: Implements business logic for users, products, authentication, and orders.
- Repository Layer: Performs database operations using Spring Data JPA.
- Model Layer: Defines entities such as User, Product, Order, Category, and VerificationToken.
- DTO Layer: Structures request and response data.
- Security & Configuration Layer: Configures JWT authentication, Spring Security, database access, and WebSocket communication.
- Utility Layer: Provides JWT handling, OTP generation, image URL validation, and location-related utilities.

📂 Project Structure

```text
OnlineRentalPlatform/
├── src/
│   ├── main/
│   │   ├── java/com/rental/platform/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── util/
│   │   │   ├── websocket/
│   │   │   └── RentalPlatformApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── .mvn/
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

⚙️ Installation and Setup

 Prerequisites

Make sure you have installed:

- Java Development Kit (JDK) 17
- MySQL Server
- Maven, or use the included Maven Wrapper
- An IDE such as IntelliJ IDEA, Eclipse, or VS Code

Step 1: Clone the Repository

```bash
git clone <your-github-repository-url>
cd OnlineRentalPlatform
```

Step 2: Create the Database

Open MySQL and create a database:

```sql
CREATE DATABASE rental_platform;
```

Step 3: Configure Application Properties

Open `src/main/resources/application.properties` and configure the database connection, JWT settings, email credentials, and Twilio credentials as required.

Example database configuration:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/rental_platform
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbcDriver
```

Configure the remaining properties according to your environment. Never commit real passwords, API tokens, or secret keys to GitHub.

Step 4: Build the Project

On Windows:

```bash
mvnw clean install
```

On Linux or macOS:

```bash
./mvnw clean install
```

Alternatively, use an installed Maven version:

```bash
mvn clean install
```

Step 5: Run the Application

On Windows:

```bash
mvnw.cmd spring-boot: run
```

On Linux or macOS:

```bash
./mvnw spring-boot: run
```

The application is configured to use port `8080` by default. After successful startup, the backend will be available at:

`http://localhost:8080`

🔗 Main API Endpoints

| Module | Endpoint | Description |
|---|---|---|
| Authentication | `/api/auth/register` | Register a user |
| Authentication | `/api/auth/login` | Authenticate a user |
| Authentication | `/api/auth/verify-email-otp` | Verify email OTP |
| Authentication | `/api/auth/verify-phone` | Verify phone OTP |
| Products | `/api/products` | Create or browse products |
| Products | `/api/products/{id}` | Retrieve or update a product |
| Products | `/api/products/my-products` | View the current user's listings |
| Products | `/api/products/categories` | Retrieve product categories |
| Orders | `/api/orders` | Create a rental order |
| Orders | `/api/orders/my-orders` | View customer orders |
| Orders | `/api/orders/my-product-orders` | View orders for owned products |
| Users | `/api/users/profile` | View or update the user profile |
| Users | `/api/users/change-password` | Change the account password |
| Admin | `/api/admin/pending-products` | View products awaiting approval |
| Admin | `/api/admin/users` | Manage users |
| Admin | `/api/admin/categories` | Manage product categories |

Note: Some endpoints require JWT authentication or administrator privileges. The table lists representative endpoints; consult the controller classes for request parameters, HTTP methods, and authorization requirements.

🎯 Project Objectives

- Simplify the process of listing and discovering rental products.
- Provide secure authentication and access control.
- Enable efficient product and rental order management.
- Support location-based discovery of nearby products.
- Improve communication through automated notifications.
- Provide administrative controls for managing listings and users.

🚀 Future Enhancements

- Develop a responsive frontend for customers, product owners, and administrators.
- Integrate an online payment gateway and rental payment tracking.
- Add booking calendars, rental duration selection, and availability management.
- Introduce customer reviews, ratings, and owner feedback.
- Deploy the application to a cloud platform.
- Add automated API testing and CI/CD pipelines.

👨‍💻 Learning Outcomes

This project demonstrates practical experience with Java backend development, REST API design, Spring Boot architecture, relational database integration, JWT security, role-based authorization, notification services, and real-time communication.

Project Name: Online Rental Platform  
Backend: Java + Spring Boot  
Database: MySQL  
Build Tool: Maven
