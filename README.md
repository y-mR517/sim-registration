# SIM Registration Platform (NIDA eKYC)

A Spring Boot backend for registering SIM cards against a citizen's NIDA identity, built for the *SIM Registration Using NIDA eKYC* developer challenge.

**Status: work in progress.** The core registration flow works end to end, but NIDA, the mobile network operator (MNO), the regulator and SMS are all **simulated**. This README says plainly what is done, how each claim was checked, and what is still open.

## Project status

| Area | Status |
|---|---|
| Registration flow (verify NIN, start, approve, complete) | Works end to end against mocks |
| Agent authentication (JWT) | Done |
| Ownership checks on approve, complete and status | Done, with unit tests |
| Fraud checks (agent status, daily limit, duplicate number, OTP attempt limit) | Done, tested manually |
| Audit log | Done for approve, fraud checks and MNO assignment; not yet for identity failures |
| Real NIDA / MNO / regulator / SMS integrations | Not done (mocks only) |
| Unit tests | Ownership rules and error handling (see *Running the tests*) |
| Diagrams and design documents | Not written yet (see *Challenge deliverables*) |
| Unit tests | Ownership rules, error handling, `verify-nin`, and the token filter |

## Tech stack

Java 21, Spring Boot 4.1.1, Spring Security, JJWT 0.12.6, Spring Data JPA / Hibernate, PostgreSQL 16, Lombok, Maven, JUnit 5 and Mockito.

## How a registration works

1. **Login.** An agent logs in and receives a JWT. Every endpoint except login requires it.
2. **Verify NIN.** Checks that a NIN exists in NIDA. It answers valid or not valid.
3. **Start registration.** The agent is taken from the token, never from the request body. The service checks that the agent is active, has not hit the daily limit, and that the SIM number is not already in an active registration. It then re-verifies name and date of birth with NIDA, creates a `PENDING` registration, and sends an OTP.
4. **Approve.** The OTP is checked (hashed, constant-time comparison, 5 attempts, expiry, row lock). Only the agent who created the registration can approve it.
5. **Complete.** Only the owning agent, and only after approval. The SIM number must exist in the MNO pool and not already be `ASSIGNED`; it is marked `ASSIGNED`, submitted to the (mock) regulator, a (mock) confirmation SMS is sent, and the registration becomes `COMPLETED`.

### Two different phone numbers

| Number | Where it comes from | Used for |
|---|---|---|
| NIDA-registered number | NIDA's record for the NIN | Receiving the OTP, so a stolen NIN alone is not enough |
| SIM number being registered | `mobileNumber` in the request | The line being activated; the duplicate check, MNO pool lookup and registration all use this one |

## API

All endpoints except login need `Authorization: Bearer <token>`.

| Method and path | Purpose |
|---|---|
| `POST /api/auth/login` | Public. Returns a JWT |
| `POST /api/verify-nin` | Checks that a NIN is valid |
| `POST /api/start-registration` | Needs an `Idempotency-Key` header |
| `POST /api/approve-registration` | OTP approval |
| `POST /api/registrations/complete` | MNO assignment and regulator submission |
| `GET /api/registrations/{reference}/status` | Registration status |
| `GET /api/mno/available-numbers/{network}` | Lists numbers in the MNO pool (see limitations) |

Errors share one shape: `{"code": "...", "message": "...", "status": 409, "timestamp": "..."}`. Clients should switch on `code`.

| Code | Status | Meaning |
|---|---|---|
| `VALIDATION_FAILED`, `MALFORMED_REQUEST`, `MISSING_HEADER` | 400 | Bad input |
| `INVALID_OTP` | 400 | Wrong code |
| `INVALID_CREDENTIALS` | 401 | Wrong username or password, or agent not active |
| `REQUEST_BLOCKED` | 403 | A fraud rule stopped the request (the reason is only in the audit log) |
| `REGISTRATION_NOT_FOUND`, `REQUEST_ERROR` | 404 | Unknown reference or URL; the same answer is given for a reference that belongs to another agent |
| `INVALID_STATE`, `DUPLICATE_SIM`, `NUMBER_UNAVAILABLE` | 409 | State conflicts |
| `OTP_EXPIRED` | 410 | OTP window passed |
| `IDENTITY_VERIFICATION_FAILED` | 422 | NIN, name or date of birth did not match; deliberately identical for all three |
| `OTP_ATTEMPTS_EXCEEDED` | 429 | Too many wrong codes |
| `INTERNAL_ERROR` | 500 | Unexpected; details are only in the server log |

`NIDA_UNAVAILABLE` (503) and `NIDA_TIMEOUT` (504) exist but are never triggered, because the NIDA mock cannot fail.

## Security and fraud controls

"Unit test" means an automated test exists. "Manual" means it was exercised with curl against a running instance and the audit table was checked, but there is no automated test yet.

| Control | How it is checked |
|---|---|
| Every endpoint except login requires a valid JWT | Manual |
| Agent identity comes from the token, not the request body | Manual |
| Approve, complete and status reject agents who do not own the registration, and answer exactly as for a missing reference | Unit tests |
| Unknown URLs return 404 instead of 500 | Unit test |
| OTP stored hashed, compared in constant time, limited to 5 attempts, locked with a row lock, rejected after expiry | Manual |
| OTPs and full phone numbers are never logged (numbers are masked) | Manual |
| Blocked or suspended agents cannot log in or register | Manual |
| Daily registration limit per agent | Manual |
| Duplicate active SIM number is rejected | Manual |
| MNO number must exist in the pool and not be `ASSIGNED` | Manual |
| Identity failures give one generic response; fraud blocks give one generic response | Manual |
| Customer list and mock NIDA lookup endpoints removed (verified to return 404) | Manual |
| Mobile numbers encrypted with AES-GCM; NIN and phone hashed for lookup | Code review only (see limitations) |
| `verify-nin` answers only valid or not valid; it returns no name, date of birth or NIN | Unit tests |
| A token issued to an agent who is later blocked, suspended or deleted stops working on the next request | Unit tests |

## Running locally

**Prerequisites:** Java 21 or newer, PostgreSQL with a database called `sim_registration`.

**Environment variables.** Secrets are not stored in the repository.

| Variable | Purpose |
|---|---|
| `JWT_SECRET` | JWT signing secret, at least 32 bytes. Generate one with `openssl rand -base64 32` |
| `DB_PASSWORD` | Database password (leave empty for a local trust-auth database) |
| `app.encryption.key` | AES key for encrypting mobile numbers. Must be exactly 16, 24 or 32 characters (`openssl rand -hex 16` gives 32). |

The database username in `src/main/resources/application.properties` is a local value; change it to match your setup.

**Start the app:**

```
./mvnw spring-boot:run
```

`data.sql` runs on every start and seeds mock NIDA records and the MNO number pool.

**Create an agent.** There is no registration endpoint for agents. Insert one into the `agents` table with a BCrypt hash of a password you choose (`BCryptPasswordEncoder` from Spring Security can produce one). Then log in:

```
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"<agent username>","password":"<password>"}'
```

## Running the tests

```
./mvnw clean test -Dtest="JwtAuthFilterTest,VerifyNinServiceTest,GlobalExceptionHandlerTest,StartRegistrationServiceImplTest,ApproveRegistrationServiceImplTest"
```

A plain `./mvnw test` also runs `contextLoads`, which starts the whole application and therefore needs the database and the environment variables above.

## Known limitations and open work

These are listed in the order I plan to address them.

**Security**
- No rate limiting on login, `verify-nin` or OTP sending, so an agent could test NINs one by one.
- `verify-nin` answers only valid or not valid; it returns no name, date of birth or NIN | Unit tests |
- Agent status is read from the database on every request, one query each. At the 300 requests per second in the challenge, a short-lived cache would be needed.
- `HashService` uses plain SHA-256. NINs, phone numbers and OTPs are low-entropy, so an unkeyed hash can be brute-forced offline; it should be an HMAC with a secret key.
- No encryption key rotation or versioning.
- The device fingerprint is supplied by the client and can be changed per request, so it is a weak signal.

**Registration logic**
- The SIM number is only checked against the MNO pool at completion. It should be reserved at start (the `RESERVED` status exists but is unused), and abandoned or expired registrations do not release their number.
- Duplicate and daily-limit checks are not backed by database constraints, so concurrent requests can slip past them.
- Registration references are short (32 bits) and could collide at scale. The idempotency key is global, not per agent.
- `GET /api/mno/available-numbers/{network}` lists every number regardless of status.
- Network names are free text; phone numbers are validated by pattern but not normalised.
- The OTP expiry is hard-coded in the service instead of configurable.
- A missing approval record currently returns `INVALID_OTP` instead of `INVALID_STATE`.
- `SimRegistration.approved` ignores its default when built through the Lombok builder.

**Data and schema**
- Schema is managed by `ddl-auto=update`, not migrations. During development, stale `CHECK` constraints on the status columns caused failures and had to be fixed by hand. Flyway is the intended fix.
- Timestamps use `LocalDateTime` in the server's time zone; the daily limit boundary depends on it.
- Seed data in `data.sql` runs on every start in every environment.

**Architecture**
- `StartRegistrationServiceImpl` handles start, status and completion and should be split.
- NIDA, MNO and regulator are mocks with no interfaces for swapping in real clients. The regulator mock always succeeds, so failure and retry paths are untested.
- External calls happen inside database transactions, and there is no queue or retry for MNO or regulator failures.
- Some class names do not follow Java conventions (for example `aVerifyNinController`) and the `ServiceImpl` package name is not lowercase.

**Challenge requirements not met**
- `GET /customer-sims/{nin}` is not implemented.
- Endpoint paths differ from the challenge: `/api/registrations/complete` instead of `/register-sim`, and `/api/registrations/{reference}/status` instead of `/registration-status/{reference}`.

**Tooling**
- No Docker setup, CI pipeline or coverage report yet, and no integration tests.

## Challenge deliverables

| Deliverable | State |
|---|---|
| High-level architecture | Not written |
| ER diagram | Not written (entities exist in `src/main/java/.../entity`) |
| REST API specification | The API section above is a summary; no OpenAPI document yet |
| Sequence diagram | Not written |
| Security design | Partly described above; no full write-up |
| Fraud prevention strategy | Implemented checks listed above; no full write-up |
| Scalability approach | Not written |
| Technology stack recommendations | Stack listed above; no recommendations section |
| Assumptions and limitations | See below and *Known limitations* |

## Assumptions

- NIDA returns a person's name, date of birth and registered phone number for a NIN.
- A SIM line belongs to one active registration at a time.
- Agent accounts are created by an administrator, not through the API.
- The mock MNO pool stands in for numbers the operator has available to assign.
