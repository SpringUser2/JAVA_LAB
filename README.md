EV Charging Network - Java Microservices Project Plan

Overview
This workspace contains a project plan and artifacts for a Java-based microservices system to ingest batch file inputs for an EV Charging Network (stations, charging sessions, and service contracts). The goal is to validate and map inbound DTOs, persist to an in-memory store, expose REST APIs, and prove behavior through tests.

Files
- project-plan.md - Detailed plan for architecture, data flow, validation, testing, and CI/CD.
- api-specs.md - API endpoints, DTO summaries, and response models.
- samples/StationInventory.csv - Example CSV station inventory.
- samples/ChargingSessions.json - Example JSON charging sessions batch.
- samples/ServiceContracts.tsv - Example TSV contracts batch.

How to use this plan
1. Review `project-plan.md` and `api-specs.md` to finalize API shapes.
2. Scaffold Spring Boot microservices: StationService, SessionService, and ContractService using Gradle.
3. Implement shared library for DTOs, validation, mapping, and logging utilities.
4. Write controller unit and integration tests per the testing strategy.

Testing guidance
- Use JUnit 5 and Spring Boot Test for integration tests.
- Use MockMvc or WebTestClient for controller tests to validate endpoints.
- Provide tests for happy path, partial failures, fully invalid batches, and error paths.

Next steps
- Convert `api-specs.md` into OpenAPI YAML files for each service.
- Scaffold Gradle multi-module project with shared module and service modules (Kotlin DSL recommended).
- Implement one service (StationService) end-to-end including file upload parsing, validation, mapping, persistence, and tests.

Notes
- This is a planning workspace; no service code is implemented yet. If you'd like, I can scaffold the project skeleton (Gradle Kotlin DSL, wrapper, sample Spring Boot app, controllers, DTOs, and tests).

 If you want, I can now:
 - Scaffold the StationService skeleton (pom.xml, sample Spring Boot app, controllers, DTOs, and tests).
 Tell me which service to scaffold first (StationService recommended) and whether to use synchronous or asynchronous ingestion by default.

 Gradle build & test (PowerShell)
 If you have the Gradle wrapper (recommended) or Gradle installed, run these from the workspace root:

 ```powershell
 ./gradlew clean build
 ```

 If the wrapper files are not present, generate them (requires Gradle installed):

 ```powershell
 gradle wrapper
 ./gradlew clean build
 ```

 The `station-service` module contains a working sample with H2 in-memory DB and integration tests.
 
 Contact
 Tell me which service to scaffold first (StationService recommended) and whether to use synchronous or asynchronous ingestion by default.