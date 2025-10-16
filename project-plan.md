# Java Microservices Project Plan: EV Charging Network Batch Ingestion

Summary
- Business domain: Electric Vehicle (EV) Charging Network Management.
- Goal: Design a Java-based microservices system that ingests batch file inputs (station inventory, charging sessions, service contracts), validates and maps DTOs to internal models, persists to an in-memory store, exposes REST APIs for ingestion and retrieval, and proves behavior through tests.
- File inputs: 3 file types (CSV, JSON, TSV) to exercise multiple formats.

High-level architecture
- Technology choices:
  - Language: Java 17+ (LTS)
  - Framework: Spring Boot (2.7+ / 3.x) with Web, Validation, Data JPA, H2 (in-memory), Cache
  - Testing: JUnit 5, MockMvc/TestRestTemplate or WebTestClient, Mockito
  - Build: Gradle (Kotlin DSL recommended) with wrapper (gradle/wrapper)
  - Logging: SLF4J + Logback; structured logging (JSON) optional
  - CI: GitHub Actions (build, test, static checks, docker build)

- Microservices (each a separate Spring Boot app):
  1. StationService: ingest station inventory (CSV) and provide station queries.
  2. SessionService: ingest charging sessions (JSON) and provide session queries and aggregates.
  3. ContractService: ingest service contracts (TSV) for station maintenance and SLA, expose contract search.

- Supporting components (could be libraries):
  - Shared library: DTOs, validation annotations, error models, logging/correlation utilities, mapping helpers.
  - Centralized logging and observability adapter (export logs to console/ELK/Loki as configured).

Data flow (per service)
1. Receive HTTP request with batch payload or file upload.
2. Parse incoming payload into DTOs (explicit DTO objects per format).
3. Validate each DTO record using javax.validation + custom validators.
4. Map validated DTO -> internal domain model (internal fields intentionally differ from DTO).
5. Persist to in-memory DB (H2) or cache for quick retrieval. Optionally enqueue for further repository processing.
6. Return a processing summary: {processingId, receivedCount, successCount, failCount, errorsEndpoint}.
7. Log detailed processing events and per-record failures internally with correlationId.

API design and REST guidance
- Follow RESTful rules: use plural nouns, use HTTP status codes consistently, support idempotency for file uploads if possible.
- All ingestion endpoints are POST and return 202 Accepted for async processing or 200 OK with synchronous summary for small batches.
- Responses:
  - Success: 200 OK (synchronous) or 202 Accepted with Location to processing resource.
  - Validation errors (client-visible summary): 400 Bad Request with structured error model.
  - Partial failures: 207 Multi-Status or 200 OK with per-record failure summary and processingId.
  - Server errors: 5xx with generic client message and internal logs.

Client-facing error model (example)
{
  "code": "INVALID_BATCH",
  "message": "Batch contains 3 invalid records",
  "processingId": "uuid",
  "failedRecords": 3,
  "sampleErrors": [
    { "recordIndex": 5, "errorCode": "MISSING_FIELD", "message": "stationId is required" }
  ]
}

Internal logging
- Every request assigned a correlationId (X-Correlation-Id header supported; generated if missing).
- Internal logs include correlationId, stack traces, full validation error details (never sent to client in full).
- Processing metrics logged: recordsProcessed, recordsFailed, durationMillis, processingId.

DTO vs Internal Model guidance
- DTOs reflect inbound file columns/JSON shape.
- Internal models use domain types (IDs as UUID, enums, normalized timestamps) and different field names.
- Mapping layer performs transformation, augmentation (e.g., compute "stationHash"), and normalization (unit conversions).

Validation rules (per record)
- Required fields presence.
- Field formats (UUIDs, ISO-8601 timestamps, numeric ranges).
- Referential validations (e.g., session.stationRef must reference an existing stationId or be flagged for deferred processing).
- Business rules (e.g., session.duration > 0, contract.effectiveDate < contract.expiryDate).
- Each invalid record yields a record-level error; service processes remaining records (best-effort) unless total failure threshold reached.

Persistence & caching
- Primary persistence for the project: H2 in-memory database (for tests and local run). Use Spring Data JPA repositories.
- Cache: Spring Cache (in-memory, ConcurrentHashMap) for frequently requested lookups.
- Data lifecycle: ingest -> persist in H2 -> mark as staged for downstream processing (not implemented in plan).

Processing modes
- Synchronous processing (small batches): process on request thread and return summary.
- Asynchronous (large batches): accept file -> respond 202 Accepted with processingId -> process in background (executor) -> provide endpoint to fetch processing results.

Logging & Observability
- Structured logs (JSON) with keys: timestamp, level, service, correlationId, processingId, recordsProcessed, recordsFailed, message.
- Expose metrics endpoint (/actuator/metrics) and basic health checks (/actuator/health).

Testing strategy
- Unit tests:
  - Validators for each rule.
  - Mapper unit tests.
  - Service logic (processing) with mocked repositories.
- Controller tests:
  - MockMvc tests for endpoints with positive and negative scenarios.
- Integration tests:
  - SpringBootTest with H2 for end-to-end ingestion flows.
  - Test files (CSV/JSON/TSV) used as input resources.
- Acceptance criteria for each endpoint:
  - Passes happy path tests returning expected counts and saved entities.
  - Fails with expected client-visible error messages for malformed input.
  - Logs internal errors with correlationId when exceptions occur.

Test matrix (per endpoint)
- Happy path: valid batch, all records saved. Expect 200 with successCount == receivedCount.
- Partial failure: some invalid records. Expect 200 with successCount < receivedCount and sampleErrors.
- Fully invalid batch: all records invalid. Expect 400 with error summary.
- Large file async: accept 202 and later retrieve processing result (integration test with executor).
- Error path: simulate DB failure -> expect 500 with generic client message and internal log entry.

Security & backward compatibility
- For prototype, no auth required. Next steps: add OAuth2/JWT for partner apps.
- Version APIs as /api/v1/... to allow future changes.

CI/CD
- Build: ./gradlew clean build (Gradle Kotlin DSL)
- Tests run in CI; produce code coverage reports (JaCoCo).
- Dockerfile per service for containerization.
- Optional GitHub Actions workflow example included in repo templates.

Artifacts this plan will produce
- project-plan.md (this file)
- api-specs.md (API contracts and sample schemas)
- samples/: example CSV, JSON, TSV files
- README.md with run/test instructions
- skeleton Spring Boot services (optional next step)

Next steps
1. Finalize API OpenAPI YAML and DTO schemas. (todo)
2. Scaffold two or three Spring Boot microservices with shared module. (todo)
3. Implement ingestion controllers, validators, mappers, and repositories. (todo)
4. Implement tests and CI. (todo)

Acceptance
- Deliverables: plan + API specs + sample files. Further coding can follow this plan.
