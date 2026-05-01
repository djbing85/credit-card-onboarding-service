# Credit Card Onboarding Service

A Spring Boot 4-based credit card onboarding system with automated verification rules and virtual thread support.

## Quick Start

### Run the Application

```bash
# Compile the project
mvn clean package

# Run the application
mvn spring-boot:run

# Or run the JAR directly
java -jar target/credit-card-onboarding-service-1.0-SNAPSHOT.jar
```

### Testing

```bash
# Run all unit tests
mvn test

# Run tests with coverage report (requires Jacoco plugin in pom.xml)
mvn test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

## Access URLs

- **Application**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/metrics

## Postman script
[credit-card-onboarding.postman_collection.json](credit-card-onboarding.postman_collection.json)


## Tech Stack

- **Spring Boot 4.0.2** - Web Framework
- **Java 25** - Programming Language
- **Tomcat 11.0+** - Servlet Container (Virtual Threads enabled)
- **H2 Database** - Embedded In-memory Database
- **MyBatis Plus** - ORM Framework
- **Jackson 3.x** - JSON Serialization/Deserialization
- **Maven 3.6+** - Build Tool
- **Lombok** - Code Simplification
- **MapStruct** - Object Mapping
- **Caffeine** - Local Caching
- **Log4j2** - High-performance Logging
- **Actuator** - Monitoring and Management

## Database Design

### Credit Card Onboarding Table (credit_card_onboarding)

| Field | Type | Description                                                 |
|-------|------|-------------------------------------------------------------|
| id | BIGINT | Auto-increment Primary Key                                  |
| emirates_id_number | VARCHAR(64) | Emirates ID Number (Unique Index)                           |
| name | VARCHAR(128) | Applicant Full Name                                         |
| mobile_number | VARCHAR(32) | Mobile Phone Number                                         |
| nationality | VARCHAR(64) | Nationality                                                 |
| address | VARCHAR(512) | Residential Address                                         |
| income | DECIMAL(16,2) | Annual Income                                               |
| employment_details | VARCHAR(512) | Employment Details                                          |
| requested_credit_limit | DECIMAL(16,2) | Requested Credit Limit                                      |
| bank_statement | VARCHAR(256) | Bank Statement File Path                                    |
| verified_result | VARCHAR(16) | Verification Result (PENDING/AUTO_ISSUE/MANUAL_REVIEW/etc.) |
| verified_score | VARCHAR(32) | Final Verification Score (0.0000 ~ 1.0000)                  |
| verified_detail | VARCHAR(512) | Detailed Verification Results (JSON string)                 |
| verified_time | BIGINT | Verification Timestamp                                      |
| created_time | BIGINT | Creation Timestamp                                          |
| updated_time | BIGINT | Update Timestamp                                            |
| version | BIGINT | Optimistic Locking Version                                  |
| status | TINYINT | Status (0: Disabled, 1: Enabled, 9: Deleted)                |
| operator | VARCHAR(32) | Last Operator                                               |

### Credit Card Onboarding Rules Table (credit_card_onboarding_rules)

| Field | Type | Description |
|-------|------|-------------|
| id | INT | Auto-increment Primary Key |
| criteria | VARCHAR(64) | Evaluation Criteria Name |
| mandatory_pass | BOOLEAN | Is Mandatory Pass Requirement |
| score_contribution | DECIMAL(5,4) | Weight in Final Score Calculation |
| score_type | SMALLINT | Score Type (0: Boolean, 1: Decimal) |
| score | DECIMAL(5,2) | Base Score Value |
| created_time | BIGINT | Creation Timestamp |
| updated_time | BIGINT | Update Timestamp |
| version | BIGINT | Optimistic Locking Version |
| status | TINYINT | Status (0: Disabled, 1: Enabled, 9: Deleted) |
| operator | VARCHAR(32) | Last Operator |

## API Endpoints

### Onboarding Applications (/api/onboarding)

- `POST /` - Create a new onboarding application
- `GET /{id}` - Get application by ID
- `GET /page` - Paginated list of applications (Ordered by ID DESC)
- `PUT /` - Update an existing application
- `DELETE /{id}` - Soft delete an application
- `POST /{id}/verify` - Trigger automated verification process

### Rule Management (/api/rules)

- `POST /` - Create a new verification rule
- `GET /{id}` - Get rule by ID
- `GET /page` - Paginated list of rules
- `PUT /` - Update an existing rule
- `DELETE /{id}` - Soft delete a rule

## Verification Engine

The system uses a multi-stage verification process:
1. **Mandatory Rules**: Executed concurrently. If any fail, the application is immediately rejected.
2. **Non-Mandatory Rules**: Executed concurrently if mandatory rules pass.
3. **Score Calculation**: 
   - Boolean Rules: Add base score if verified.
   - Decimal Rules: Add `riskScore * score_contribution`.
4. **Result Determination**:
   - `AUTO_ISSUE`: Score >= 0.9
   - `MANUAL_REVIEW_LIMIT`: Score >= 0.75
   - `MANUAL_REVIEW`: Score >= 0.5
   - `REJECTED`: Score < 0.5


## Key Features

✅ **Virtual Threads**: Tomcat and custom executors use Java 25 virtual threads for high concurrency.  
✅ **Automated Verification**: Concurrent execution of identity, employment, compliance, risk, and behavioral checks.  
✅ **Dynamic Rules**: Rules are loaded from the database and cached using Caffeine (default 60s expiry).  
✅ **Unified Response**: All APIs return a standardized `CommonResponse` with appropriate HTTP status codes.  
✅ **Request Tracing**: Automatic `trace_id` generation and MDC propagation across virtual threads.  
✅ **Auto-schema Init**: H2 database tables and initial data are created automatically on startup.  
✅ **High-Performance Logging**: Log4j2 with Disruptor for async logging.

## Notes

1. **Database**: Uses H2 in-memory mode (`jdbc:h2:mem:credit_card`). Data is lost on restart unless configured otherwise.
2. **Defaults**: String fields default to empty strings; numeric fields default to 0.
3. **Uniqueness**: `emirates_id_number` has a unique constraint.
4. **Timestamps**: Stored as millisecond-precision Unix timestamps.
5. **Third-Party Services**: Configured to connect to `http://localhost:8081` by default. Ensure external services are running for full verification functionality.
