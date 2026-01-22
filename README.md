# EduApp (Backend)

A Java Spring Boot REST API for managing educational domain entities (e.g., Teachers, Users, Personal Info) with JWT-based authentication and MySQL persistence. The project exposes endpoints for authentication and teacher operations, includes OpenAPI/Swagger UI for API exploration, and uses Gradle for builds and testing.

## Tech Stack
- Language: Java 17 (tested with Amazon Corretto 17)
- Framework: Spring Boot 3.5.4
  - Spring Web, Spring Data JPA, Spring Security, Bean Validation
- Persistence: MySQL (runtime), H2 (tests)
- Auth: JWT (jjwt)
- API Docs: springdoc-openapi (Swagger UI)
- Build Tool: Gradle 8.14 (Wrapper included)
- Code Coverage: JaCoCo
- Testing: JUnit 5, Spring Boot Test, DataFaker
- Mapping/Boilerplate: Lombok (annotation processor)

## Requirements
- JDK 17+
- MySQL 8.x running locally or accessible via network (for dev/prod runs)
- Internet access to resolve Gradle dependencies
- Shell that can execute the Gradle Wrapper scripts:
  - Windows: `gradlew.bat`
  - macOS/Linux: `./gradlew`

## Project Entry Point
- Main class: `gr.aueb.cf.eduapp.EduappApplication`
- Default profile: `dev` (set in `src/main/resources/application.properties`)

## Configuration & Environment Variables
The `dev` profile reads database connection settings from environment variables with sensible defaults (see `src/main/resources/application-dev.properties`).

- MYSQL_HOST (default: `localhost`)
- MYSQL_PORT (default: `3306`)
- MYSQL_DB (default: `edudb8`)
- MYSQL_USER (default: `user8`)
- MYSQL_PASSWORD (default: `12345`)

Other notable properties (currently stored in `application-dev.properties`):
- `app.security.secret-key`
- `app.security.jwt-expiration`

Note: You can override any Spring property via environment variables by using uppercase and underscores, e.g., `APP_SECURITY_SECRET_KEY` to override `app.security.secret-key`.

### Example: setting env vars
- PowerShell (Windows):
  - `$Env:MYSQL_HOST = "localhost"`
  - `$Env:MYSQL_DB = "edudb8"`
- Bash (macOS/Linux):
  - `export MYSQL_HOST=localhost`
  - `export MYSQL_DB=edudb8`

## Setup
1. Ensure MySQL is running and a database exists that matches `MYSQL_DB`.
2. Optionally set the environment variables above to match your local setup.
3. Build dependencies to verify the environment:
   - Windows: `gradlew.bat clean build`
   - macOS/Linux: `./gradlew clean build`

## Running the Application
You can run the application either via Gradle or using the packaged JAR.

- Using Gradle (convenient for development):
  - Windows: `gradlew.bat bootRun`
  - macOS/Linux: `./gradlew bootRun`

- Using the JAR (packaged app):
  1. Build the executable JAR: `gradlew.bat bootJar` (Windows) or `./gradlew bootJar` (macOS/Linux)
  2. Run:
     - `java -jar build/libs/eduapp.jar`

The app starts on `http://localhost:8080` by default.

### Profiles
- Default active profile is `dev`. To override at runtime:
  - `java -jar build/libs/eduapp.jar --spring.profiles.active=dev`
  - or set env var `SPRING_PROFILES_ACTIVE=dev`

## API Documentation (OpenAPI/Swagger)
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Authentication
- Endpoint: `POST /api/auth/authenticate`
- Request body:
  ```json
  { "username": "user", "password": "pass" }
  ```
- Response: JWT token object. Use the returned token in the `Authorization` header for secured endpoints:
  - `Authorization: Bearer <token>`

## Database & Migrations
- JPA/Hibernate is configured with `spring.jpa.hibernate.ddl-auto=update` for the `dev` profile (convenient for development).
- A Flyway migration file exists at `src/main/resources/db/migration/V2__add_index_on_lastname.sql` but the Flyway dependency is currently commented out in `build.gradle`.
- TODO: Decide on migration strategy for non-dev environments (enable Flyway and manage baselines vs. manual SQL).

## Common Gradle Tasks (Scripts)
- Clean and build: `gradlew[.bat] clean build`
- Run tests: `gradlew[.bat] test`
- Code coverage report: `gradlew[.bat] jacocoTestReport`
  - HTML report: `build/reports/jacoco/test/html/index.html`
- Run app (dev): `gradlew[.bat] bootRun`
- Package JAR: `gradlew[.bat] bootJar` (outputs `build/libs/eduapp.jar`)

## Testing
- Unit and integration tests are under `src/test/java/...`
- H2 in-memory database is used in repository tests.
- Run: `gradlew[.bat] test`
- Coverage: `gradlew[.bat] jacocoTestReport` and open `build/reports/jacoco/test/html/index.html`

## Project Structure
```
.
├─ build.gradle
├─ settings.gradle
├─ gradlew / gradlew.bat
├─ src
│  ├─ main
│  │  ├─ java/gr/aueb/cf/eduapp
│  │  │  ├─ EduappApplication.java             # Entry point
│  │  │  ├─ api/                               # REST controllers (Auth, Teacher)
│  │  │  ├─ authentication/                    # Auth services & JWT
│  │  │  ├─ core/                              # Config, filters, errors, OpenAPI
│  │  │  ├─ dto/                               # Data transfer objects
│  │  │  ├─ mapper/                            # Mapping utilities
│  │  │  ├─ model/                             # JPA entities
│  │  │  ├─ repository/                        # Spring Data repositories
│  │  │  ├─ security/                          # Security config & filters
│  │  │  └─ service/                           # Services business logic
│  │  └─ resources
│  │     ├─ application.properties             # Sets active profile to dev
│  │     ├─ application-dev.properties         # Dev profile settings (MySQL, JWT)
│  │     ├─ db/migration/V2__add_index_on_lastname.sql
│  │     └─ logback-spring.xml                 # Logging configuration
│  └─ test
│     └─ java/gr/aueb/cf/eduapp                # Tests (service, repository, controller)
└─ README.md
```

## Logging
- Configured via `src/main/resources/logback-spring.xml`.

## Known Ports
- HTTP server: 8080
- MySQL default: 3306 (configurable via env)

## License
No license file found in the repository.
- TODO: Add a LICENSE file (e.g., MIT, Apache-2.0) and update this section accordingly.

## Notes & TODOs
- TODO: Decide on database migration strategy (enable Flyway and add baseline if using existing DB).
- TODO: Provide Dockerfile/docker-compose for easy local MySQL and app startup (if desired).
- TODO: Add API examples for Teacher endpoints and any other secured resources.

---

## Original Quick Reference (kept and updated)
- Java 17 (tested with Amazon Corretto 17.0.15_6)
- Gradle 8.14
- Build: `gradlew clean build`
- Run JAR: `java -jar ./build/libs/eduapp.jar`

