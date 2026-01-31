GymHub
GymHub is a backend system based on a microservices architecture, developed using Spring Boot (Java 17) and aligned with Domain-Driven Design (DDD) principles.

Each microservice represents a bounded context and owns its own PostgreSQL database. Communication between services is performed using synchronous REST APIs and asynchronous domain events via RabbitMQ.

The system is designed to run entirely inside Kubernetes (Minikube), using Docker images as the packaging mechanism.

Architecture Overview
Main infrastructure components:

Eureka Server – service discovery
API Gateway – single entry point for all client requests
RabbitMQ – asynchronous communication via domain events
PostgreSQL – database per microservice
All components run inside the same Kubernetes cluster.

Microservices
The system is composed of the following microservices:

Members Service
Manages gym members and their profiles.

Trainers Service
Manages trainers, specialities, and availability.

Schedule Service
Core service responsible for session scheduling and lifecycle management.

Workout Service
Manages personalized workout plans for members.

Report Service
Aggregates domain events and provides analytical reports for gym management.

Notifications Service
Consumes domain events and sends notifications to members and trainers.

Databases
Each microservice owns its own PostgreSQL database:

members_db
trainers_db
schedule_db
workout_db
report_db
notifications_db
There is no database sharing between services.

Communication Model
Synchronous Communication
REST APIs exposed by each service
Routed through the API Gateway
Used for validation and query operations
Asynchronous Communication
RabbitMQ is used for domain event propagation
Event producers:
Members Service
Trainers Service
Schedule Service
Workout Service
Event consumers:
Report Service
Notifications Service
Report and Notifications services do not publish events.

Kubernetes Deployment
All services and infrastructure components are deployed in Kubernetes (Minikube). Docker is used only to build container images.

Quick Start
First time setup or cleanup:

# Clean docker-compose artifacts (recommended before K8s deployment)
.\cleanup-docker.ps1

# Deploy to Kubernetes
.\setup-k8s.ps1
Important: Do NOT use docker-compose up for production. Use Kubernetes only.

Kubernetes manifests are located in the k8s/ directory and include:

Deployments
Services
ConfigMaps
PostgreSQL database definitions
Detailed deployment instructions are available in:

K8S-SETUP.md
port-forward.md
Project Structure
gymhub/ ├── api-gateway/ ├── eureka-server/ ├── members/ ├── trainers/ ├── schedule/ ├── workout/ ├── report/ ├── notifications/ ├── k8s/ ├── README.md ├── K8S-SETUP.md └── port-forward.md
