# Mergington High School Activities (Spring Boot Microservices)

This is a Java Spring Boot microservices rewrite of the activities app.

## Services

- activities-service (port 8080): serves the web UI and exposes `/activities`.
- students-service (port 8081): in-memory student directory.
- signup-service (port 8082): signup logic (duplicate + capacity checks).

## Run

Start each service in a separate terminal:

```bash
cd springboot-microservices/activities-service
mvn spring-boot:run
```

```bash
cd springboot-microservices/students-service
mvn spring-boot:run
```

```bash
cd springboot-microservices/signup-service
mvn spring-boot:run
```

Open the UI:

- http://localhost:8080/

API endpoints:

- http://localhost:8080/activities
- http://localhost:8080/activities/{activityName}/signup?email=student@mergington.edu
- http://localhost:8081/students
