# API Specifications (Overview)

Services: StationService, SessionService, ContractService

Common headers
- X-Correlation-Id: optional; server generates if missing
- Content-Type: application/json for JSON endpoints; multipart/form-data for file uploads

Error model (schema)
- code: string
- message: string
- processingId: string (UUID)
- failedRecords: integer
- sampleErrors: array of {recordIndex, errorCode, message}

StationService
- POST /api/v1/stations/upload
  - Description: Accepts CSV file (multipart/form-data) containing station inventory records.
  - Request: multipart file param "file" or raw text/plain body for delimited
  - Responses:
    - 200 OK: {processingId, receivedCount, successCount, failedCount, sampleErrors}
    - 400 Bad Request: malformed CSV or invalid headers
    - 202 Accepted: if server queues for async processing
- POST /api/v1/stations/batch
  - Description: Accepts application/json array of station DTOs for small batches.
  - Request body: [StationDTO, ...]
- GET /api/v1/stations/{stationId}
  - Returns station internal model

StationDTO (CSV columns)
- stationId (string, required)
- name (string, required)
- address (string)
- latitude (decimal)
- longitude (decimal)
- capacity (int, required)
- status (string: ACTIVE|MAINTENANCE|OFFLINE)

Station Internal Model
- id (UUID)
- code (normalized stationId)
- displayName
- location (lat, lon)
- capacity
- status (Enum)
- createdAt (timestamp)

SessionService
- POST /api/v1/sessions/upload
  - Accepts JSON payload with array of SessionDTO or single JSON-lines file.
- POST /api/v1/sessions/batch
  - Accepts application/json for small batches
- GET /api/v1/sessions/{sessionId}
- GET /api/v1/sessions?stationId=...&from=...&to=...

SessionDTO (JSON schema)
- sessionId: string
- stationRef: string
- vehicleId: string
- startTime: ISO-8601
- endTime: ISO-8601
- energyKWh: decimal
- costCents: integer

Session Internal Model
- id (UUID)
- stationId (UUID ref)
- vehicleHash
- startTs (Instant)
- endTs (Instant)
- durationSeconds
- energyKWh
- costCents

ContractService
- POST /api/v1/contracts/upload
  - Accepts TSV file of service contracts
- POST /api/v1/contracts/batch
  - Accepts application/json for small batches
- GET /api/v1/contracts/{contractId}

ContractDTO (TSV columns)
- contractId
- stationId
- providerName
- effectiveDate (yyyy-MM-dd)
- expiryDate (yyyy-MM-dd)
- slaHours

Contracts Internal Model
- id (UUID)
- stationId (UUID)
- providerId (UUID or normalized name)
- effectiveAt (LocalDate)
- expiresAt (LocalDate)
- slaHours

Validation strategy
- Use javax.validation annotations for basic checks
- Custom validators for cross-field rules
- Collect record-level errors into a list with codes for client

Processing response examples
- Success:
  {"processingId":"uuid","receivedCount":10,"successCount":10,"failedCount":0}
- Partial failure:
  {"processingId":"uuid","receivedCount":10,"successCount":7,"failedCount":3,"sampleErrors":[{"recordIndex":2,"errorCode":"INVALID_DATE","message":"startTime not ISO-8601"}]}

Testing contracts
- Each endpoint will have tests proving:
  - successful ingest and persisted entities
  - partial failures produce correct response structure
  - malformed files produce client-facing error

OpenAPI
- Next step: translate these endpoints and DTO schemas into OpenAPI 3.0 YAML (openapi.yaml) for each service.
