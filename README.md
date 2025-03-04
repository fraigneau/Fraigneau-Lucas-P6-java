# PayMyBuddy

A peer-to-peer money transfer application built with Spring Boot that allows users to easily send money to friends in their network.

## Technology Stack

- **Java 21** - Programming language
- **Spring Boot 3.4.2** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence
- **Thymeleaf** - Server-side Java template engine
- **MySQL** - Database
- **Gradle** - Build and dependency management
- **JaCoCo** - Java code coverage
- **JUnit** - Testing framework
- **Lombok** - Reduces boilerplate code
- **MapStruct** - Bean mapping framework

## Features

- User registration and authentication
- Profile management
- Add and manage friends
- Send money to connected friends
- Deposit funds to account
- View transaction history

## Project Structure

The application follows a standard layered architecture:

- **Controller**: Handles HTTP requests and responses
- **Service**: Contains business logic
- **Repository**: Interfaces with the database
- **Model**: Domain entities
- **DTO**: Data Transfer Objects
- **Mapper**: MapStruct interfaces for object conversion

## Documentation

All project documentation is available in the `docs` directory of the GitHub repository and is deployed online:

- [**JaCoCo Reports**](https://fraigneau.github.io/Fraigneau-Lucas-P6-java/jacoco/): Code coverage analysis for the project
- [**Gradle Reports**](https://fraigneau.github.io/Fraigneau-Lucas-P6-java/test/): Build and test reports
- [**JavaDoc**](https://fraigneau.github.io/Fraigneau-Lucas-P6-java/javadoc/): API documentation for the codebase

You can access the documentation online through the GitHub Pages site for this repository.

## Getting Started

### Prerequisites

- Java 21
- MySQL
- Gradle

### Configuration

1. Clone the repository
2. Configure MySQL database connection in `application.properties`
3. Build the project using Gradle

```bash
./gradlew build
```

4. Run the application

```bash
./gradlew bootRun
```

The application should now be running at `http://localhost:8080`

## Testing

The project includes comprehensive unit tests covering the controllers, services, and security configuration.

Run tests with:

```bash
./gradlew test
```

To generate code coverage reports:

```bash
./gradlew jacocoTestReport
```

Reports can be found in `build/reports/jacoco/` directory.

## Database Schema

The application uses several interconnected entities:

- **User**: Represents application users
- **Transaction**: Tracks money transfers between users
- **UserConnection**: Manages friend relationships between users

## Security

The application implements Spring Security for authentication and authorization. Passwords are encoded using BCrypt.

## License

[MIT License](LICENSE)