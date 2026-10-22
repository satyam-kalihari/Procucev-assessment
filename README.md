# LinguaScribe

> A Spring Boot application for real-time multilingual speech transcription and transcript management.

LinguaScribe is a full-stack speech-to-text application built for the Procucev AI Tech Hiring Assignment. It combines a browser-based live transcription experience with a Java/Spring backend that validates, persists, and exposes transcription history through a REST API.

The project demonstrates practical backend engineering with Spring Boot, Spring Data JPA, relational persistence, RESTful CRUD operations, validation, and a responsive web interface.

## Features

- Real-time speech recognition in the browser using the Web Speech API
- Interim transcription results while the user is speaking
- Multilingual recognition locale selection:
  - English (India) - `en-IN`
  - Hindi - `hi-IN`
  - English (US) - `en-US`
  - Marathi - `mr-IN`
- Save, view, edit, and delete transcription sessions
- Newest-first transcription history
- REST API with request validation
- H2 database for zero-configuration local development
- PostgreSQL profile for a production-style relational database
- Docker Compose configuration for local PostgreSQL
- Static frontend served directly by Spring Boot
- Provider-neutral backend that can later integrate with a self-hosted Whisper or Vosk service

## Technology Stack

### Backend

- Java 17
- Spring Boot 3.4
- Spring Web
- Spring Data JPA
- Jakarta Bean Validation
- Hibernate

### Data

- H2 for local development
- PostgreSQL for deployment
- Docker Compose for local database orchestration

### Frontend

- HTML5
- CSS3
- Vanilla JavaScript
- Web Speech API

## Architecture

The application follows a layered structure:

```text
Browser
  │
  ├── Web Speech API
  │
  └── REST/JSON
        │
        ▼
TranscriptionController
        │
        ▼
TranscriptionService
        │
        ▼
TranscriptionRepository
        │
        ▼
Relational Database
```

### Backend responsibilities

- `TranscriptionController` exposes the public REST API.
- `TranscriptionService` contains application and persistence logic.
- `TranscriptionRepository` provides database access through Spring Data JPA.
- `Transcription` represents the persisted transcription entity.
- Request and response records keep the API contract separate from the persistence model.

### Frontend responsibilities

- Starts and stops browser speech recognition.
- Displays interim and final speech results.
- Sends saved transcripts to the REST API.
- Renders transcript history and provides edit/delete actions.

## Prerequisites

Install the following before running the project:

- Java 17 or later
- Maven 3.9 or later
- A modern browser with speech recognition support
- Docker Desktop, only if using the PostgreSQL setup

Chrome and Edge provide the most reliable Web Speech API support. Microphone access requires `localhost` during development or HTTPS in a deployed environment.

## Quick Start

### 1. Clone the repository

```bash
git clone <repository-url>
cd Procucev
```

### 2. Start the application

The default configuration uses a file-backed H2 database and requires no database setup.

```bash
mvn spring-boot:run
```

### 3. Open the dashboard

Visit:

```text
http://localhost:8080
```

Allow microphone access, choose a recognition language, and select **Start listening**.

## PostgreSQL Setup

The repository includes a PostgreSQL service definition for local development.

### 1. Start PostgreSQL

```bash
docker compose up -d postgres
```

The default database configuration is:

| Setting | Value |
|---|---|
| Database | `speech_to_text` |
| Username | `postgres` |
| Password | `postgres` |
| Port | `5432` |

### 2. Start Spring Boot with the PostgreSQL profile

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

### 3. Stop PostgreSQL

```bash
docker compose down
```

The PostgreSQL data is stored in the `postgres-data` Docker volume. To remove the volume as well:

```bash
docker compose down -v
```

## Configuration

The application supports environment-variable overrides:

| Variable | Default | Description |
|---|---|---|
| `DATABASE_URL` | H2 local file URL | JDBC connection URL |
| `DATABASE_USERNAME` | `sa` | Database username |
| `DATABASE_PASSWORD` | empty | Database password |
| `DATABASE_PLATFORM` | H2 dialect | Hibernate database platform |

For PostgreSQL, the `postgres` Spring profile sets the PostgreSQL JDBC URL and dialect. Override credentials when deploying:

```powershell
$env:DATABASE_URL = "jdbc:postgresql://db-host:5432/speech_to_text"
$env:DATABASE_USERNAME = "application_user"
$env:DATABASE_PASSWORD = "use-a-secret-manager"
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

Do not commit production credentials to source control.

## REST API

Base URL:

```text
/api/transcriptions
```

| Method | Endpoint | Description | Success |
|---|---|---|---|
| `GET` | `/api/transcriptions` | List all transcripts, newest first | `200 OK` |
| `GET` | `/api/transcriptions/{id}` | Fetch one transcript | `200 OK` |
| `POST` | `/api/transcriptions` | Create a transcript | `201 Created` |
| `PUT` | `/api/transcriptions/{id}` | Replace a transcript | `200 OK` |
| `DELETE` | `/api/transcriptions/{id}` | Delete a transcript | `204 No Content` |

### Create a transcript

```bash
curl -X POST http://localhost:8080/api/transcriptions ^
  -H "Content-Type: application/json" ^
  -d "{\"title\":\"Daily standup\",\"content\":\"Namaste, today's progress is complete.\",\"language\":\"hi-IN\"}"
```

PowerShell:

```powershell
$body = @{
    title = "Daily standup"
    content = "Namaste, today's progress is complete."
    language = "hi-IN"
} | ConvertTo-Json

Invoke-RestMethod `
    -Uri "http://localhost:8080/api/transcriptions" `
    -Method Post `
    -ContentType "application/json" `
    -Body $body
```

Example response:

```json
{
  "id": "a6f7d2b7-0e1b-4c61-81b5-8af4d25c1d0e",
  "title": "Daily standup",
  "content": "Namaste, today's progress is complete.",
  "language": "hi-IN",
  "createdAt": "2026-10-22T12:00:00Z",
  "updatedAt": "2026-10-22T12:00:00Z"
}
```

### Update a transcript

```bash
curl -X PUT http://localhost:8080/api/transcriptions/{id} ^
  -H "Content-Type: application/json" ^
  -d "{\"title\":\"Updated standup\",\"content\":\"Updated transcript text\",\"language\":\"en-IN\"}"
```

### Delete a transcript

```bash
curl -X DELETE http://localhost:8080/api/transcriptions/{id}
```

## Validation Rules

- `title` is required and limited to 100 characters.
- `content` is required.
- `language` is optional and limited to 20 characters.
- If no language is supplied, the backend uses `en-IN` for new transcripts.
- Unknown transcript IDs return `404 Not Found`.

## Project Structure

```text
.
├── docker-compose.yml
├── pom.xml
├── README.md
└── src
    └── main
        ├── java/com/procucev/speechtotext
        │   ├── SpeechToTextApplication.java
        │   └── transcription
        │       ├── Transcription.java
        │       ├── TranscriptionController.java
        │       ├── TranscriptionRepository.java
        │       ├── TranscriptionRequest.java
        │       ├── TranscriptionResponse.java
        │       └── TranscriptionService.java
        └── resources
            ├── application.properties
            ├── application-postgres.properties
            └── static
                ├── app.js
                ├── index.html
                └── styles.css
```

## Testing and Verification

Run the Maven test lifecycle:

```bash
mvn test
```

Build the executable artifact:

```bash
mvn clean package
```

Run the packaged application:

```bash
java -jar target/speech-to-text-0.0.1-SNAPSHOT.jar
```

## Production Considerations

Before deploying publicly, consider the following:

- Serve the application over HTTPS so browsers permit microphone access.
- Replace `spring.jpa.hibernate.ddl-auto=update` with a versioned migration strategy such as Flyway or Liquibase.
- Store database credentials in a secret manager or deployment platform secrets.
- Restrict `@CrossOrigin` to the deployed frontend origin instead of allowing all origins.
- Add authentication and authorization before exposing transcript data to multiple users.
- Add rate limiting and request-size limits for public APIs.
- Replace the browser recognition adapter with a self-hosted speech model gateway when server-side transcription, auditability, or consistent model behavior is required.

## Current Scope

The current implementation intentionally uses the browser's speech recognition engine to provide a working real-time demo without requiring an external API key or a separate AI model server. The Spring Boot API and persistence layer are independent of that implementation choice, allowing a future Whisper, Vosk, or Gemini Live adapter to be introduced without changing the transcript history contract.

## License

This project is provided for assessment and demonstration purposes.
# Procucev-assessment
