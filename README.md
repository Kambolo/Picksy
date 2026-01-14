# Picksy – Real-Time Group Voting Application

Picksy is a web-based application designed to facilitate real-time group voting.  
The system allows users to create voting rooms, invite participants, and conduct interactive voting sessions with live result updates.

The application was developed as part of an engineering thesis and focuses on:
- real-time communication,
- scalability,
- security,
- and a clear separation of responsibilities using a microservices architecture.

---

## Features

- Creation and management of voting rooms
- Two voting modes:
  - **SWIPE** – voting via gestures (left/right) or buttons
  - **PICK** – selecting one or multiple options
- Real-time vote updates using WebSockets (STOMP)
- Support for both authenticated and guest users
- Voting history for logged-in users
- Responsive web interface
- Asynchronous inter-service communication using Apache Kafka

---

## System Architecture

The system is based on a **microservices architecture** and consists of the following components:

### Backend Services
- **API Gateway** – request routing, CORS configuration, WebSocket proxying
- **Auth Service** – authentication, JWT generation, OAuth2 login
- **User Service** – user profiles management
- **Room Service** – room lifecycle and participants management
- **Decision Service** – voting logic and poll handling
- **Category Service** – categories and voting options

### Other Components
- **Apache Kafka** – asynchronous communication between services
- **Eureka Server** – service discovery
- **Databases** – separate database per service
- **WebSocket (STOMP)** – real-time communication for rooms and polls

---

## Technologies Used

### Backend
- Java
- Spring Boot
- Spring Cloud (Gateway, Eureka)
- Spring WebSocket (STOMP)
- Spring Data JPA (Hibernate)
- Apache Kafka
- JWT Authentication

### Frontend
- React
- TypeScript
- Vite
- SockJS + STOMP

### Infrastructure
- Docker
- Docker Compose
- Nginx (optional reverse proxy)

---

## Running the Project (Docker)

### Environment Variables (.env)

To run the project locally, an additional ```.env``` file is required.
For security reasons, this file is not included in the repository.

The ```.env``` file contains environment-specific configuration such as:
 - database credentials,
 - service ports,
 - JWT secret,
 - external service API keys (Cloudinary, Google OAuth, TMDB),
 - email configuration.

Example ```.env``` file structure

```bash
POSTGRES_PASSWORD_AUTHSERVICE=authservice
POSTGRES_USER_AUTHSERVICE=authservice
POSTGRES_DB_AUTHSERVICE=auth_service
POSTGRES_PORT_AUTHSERVICE=5435
SERVER_PORT_AUTHSERVICE=8081

POSTGRES_PASSWORD_CATEGORYSERVICE=categoryservice
POSTGRES_USER_CATEGORYSERVICE=categoryservice
POSTGRES_DB_CATEGORYSERVICE=category_service
POSTGRES_PORT_CATEGORYSERVICE=5434
SERVER_PORT_CATEGORYSERVICE=8083

POSTGRES_PASSWORD_USERSERVICE=userservice
POSTGRES_USER_USERSERVICE=userservice
POSTGRES_DB_USERSERVICE=user_service
POSTGRES_PORT_USERSERVICE=5436
SERVER_PORT_USERSERVICE=8082

POSTGRES_PASSWORD_ROOMSERVICE=roomservice
POSTGRES_USER_ROOMSERVICE=roomservice
POSTGRES_DB_ROOMSERVICE=room_service
POSTGRES_PORT_ROOMSERVICE=5437
SERVER_PORT_ROOMSERVICE=8084

POSTGRES_PASSWORD_DECISIONSERVICE=decisionservice
POSTGRES_USER_DECISIONSERVICE=decisionservice
POSTGRES_DB_DECISIONSERVICE=decision_service
POSTGRES_PORT_DECISIONSERVICE=5438
SERVER_PORT_DECISIONSERVICE=8085

SERVER_PORT_APIGATEWAY=8080
SERVER_PORT_EUREKASERVER=8761

JWT_SECRET=your_jwt_secret

CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret
CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
CLOUDINARY_URL=cloudinary://...

TMDB_READ_API_TOKEN=your_tmdb_token

GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_SECRET_KEY=your_google_client_secret

EMAIL=your_email_address
EMAIL_PASSWORD=your_email_password

FRONTEND_PORT=5173
```

#### Important notes
 - The ```.env``` file must be placed in the root directory of the project.
 - The file is automatically loaded by Docker Compose during startup.
 - All sensitive values should be replaced with your own credentials.

### Prerequisites
- Docker
- Docker Compose

### Steps

1. Clone the repository:
```bash
git clone https://github.com/Kambolo/picksy.git
cd picksy
```
2. Start all services
```bash
docker-compose up --build
```
3.Access the application:
 - Frontend: ```http://localhost:5173```
 - API Gateway: ```http://localhost:8080```
 - Eureka Dashboard: ```http://localhost:8761```


---
### Author
Engineering thesis project

Author: Kamil Bołoz

Year: 2025

