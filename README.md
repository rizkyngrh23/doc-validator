# E-Government Document Validation System

A backend system for managing, validating, and verifying official documents in a digital government environment.

## Overview
This project provides a secure API for document upload, review, and public verification. It supports multiple user roles and a full validation workflow, with audit logging and JWT-based authentication.

## Tech Stack
- Java 17+
- Spring Boot 3.x
- Spring Security (JWT)
- PostgreSQL
- Spring Data JPA
- Maven
- Swagger/OpenAPI
- Apache Commons IO, Java NIO
- PDFBox, ZXing (optional)

## Main Features
- Upload and download documents
- Document approval and rejection
- Public verification by document ID or QR code
- Audit logs for all actions
- Role-based access (citizen, officer, verifier, public)

## Database Structure
- **User**: user_id, email, role, password_hash
- **Document**: doc_id, file_path, metadata, status, owner_id
- **ValidationLog**: log_id, doc_id, actor_id, action, timestamp

## Getting Started
1. Set up PostgreSQL and configure your database settings in `application.properties`.
2. Build the project:
   ```sh
   mvn clean install
   ```
3. Run the backend:
   ```sh
   mvn spring-boot:run
   ```
4. Access the API documentation at `/swagger-ui.html`.

## Java Desktop Client Example

A sample Java Swing desktop app is included in `src/main/java/com/egov/docvalidation/desktopui/`.

### How to Run the Desktop App
1. Open the project in your IDE (IntelliJ, Eclipse, etc.).
2. Build the project with Maven as above.
3. Run the main class:
   ```sh
   java -cp target/docvalidation-0.0.1-SNAPSHOT.jar com.egov.docvalidation.desktopui.MainApp
   ```
   Or run `MainApp.java` directly from your IDE.

### What the Desktop App Can Do
- Register and log in users
- List your uploaded documents
- Upload new documents
- Download and verify documents

The app communicates with the backend API using HTTP and handles JWT authentication automatically.
