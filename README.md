# Enrichment Service

This project implements a service layer that simulates OpenAI summarization for different tenants, using tenant-specific configurations and robust logic.

## Features

- Tenant-specific configurations for AI model usage
- Dynamic prompt building based on tenant settings
- Simulated AI response generation with randomness
- Error simulation with retry logic (20% of calls fail)
- Structured logging with MDC (Mapped Diagnostic Context) for request tracing
- Caching of the last 5 summaries per tenant

## Architecture

The service follows a clean architecture approach:

- **Controller Layer**: REST endpoints for client interaction
- **Service Layer**: Business logic implementation
- **Repository Layer**: Data access and storage
- **Model Layer**: Domain objects

## Components

### EnrichmentService

The main service that handles the summarization process:

```java
SummaryResponse summarize(String tenantId, String inputText)
```

### TenantMdcFilter

A servlet filter that automatically extracts tenant ID from requests and sets up MDC context for logging. 
The filter enhances logs with the following diagnostic information:

- `requestId`: A unique ID generated for each request
- `tenantId`: The tenant identifier extracted from the request path
- `model`: The AI model being used for the current request
- `attemptNumber`: The current retry attempt number for API calls

### TenantSettings

Contains tenant-specific configurations:
- model: The AI model to use (e.g., "gpt-3.5", "gpt-4")
- tone: The desired tone for summaries (e.g., "formal", "friendly", "technical")
- maxTokens: Maximum token limit for summaries
- retryAttempts: Number of retry attempts for failed API calls

### PromptBuilder

Constructs prompts for the AI model based on tenant settings.

### Custom Caching

Implements a tenant-aware cache that stores the last 5 summaries per tenant.

### Exception Handling

The service includes several custom exception classes for proper error handling:

- **EmptyInputException**: Thrown when the input text is empty or null
- **TenantNotFoundException**: Thrown when the specified tenant ID cannot be found
- **TokenLimitExceededException**: Thrown when the input text exceeds the tenant's token limit
- **EnrichmentException**: General exception for enrichment process failures

All exceptions are properly handled by a global exception handler that returns appropriate HTTP status codes and error messages.

## Running the Application

### Prerequisites
- Java 17+
- Maven

### Build and Run
```bash
# Build the application
mvn clean package

# Run the application
java -jar target/enrichment-service-0.0.1-SNAPSHOT.jar
```

## API Usage

### Summarize Text
```
POST /api/v1/enrichment/summarize
Content-Type: application/json
X-TENANT-ID: <tenant-id>

{
  "input_text": "<text to summarize>"
}
```

Example:
```bash
curl -X POST http://localhost:8080/api/v1/enrichment/summarize \
  -H "Content-Type: application/json" \
  -H "X-TENANT-ID: tenant1" \
  -d '{"input_text": "Text to be summarized goes here..."}'
```

## Testing

### Running Tests
```bash
# Run tests only
mvn test

# Run tests with coverage report
mvn clean test

# Or use the convenience script
./run-tests.sh
```

### Code Coverage
The project uses JaCoCo for code coverage. After running tests with the coverage profile, you can view the coverage report:

```bash
# Generate coverage report
./run-coverage.sh

# View coverage report
open target/site/jacoco/index.html
```
