# Projecto AutoLog

A robust, developer-friendly, highly customizable logging utility for Spring Boot applications that reduces boilerplate and integrates with observability tools.

## Features

- **@AutoLog Annotation**: Automatically log method entry, exit, and exceptions
- **@StepLog Annotation**: Step-by-step logging inside methods
- **YAML-based Configuration**: Customize logging behavior via application.yml
- **MDC Support**: Include requestId and custom values in logs
- **Conditional Logging**: Log conditionally based on method parameters (via SpEL)
- **Structured Logging**: Output logs in JSON format for Loki/ELK
- **Metrics Integration**: Expose method-level performance metrics with Micrometer
- **Template-based Log Output**: Customize log format using placeholders
- **Developer Tagging**: Tag methods with @LoggedBy annotation

## Installation

Add the dependency to your build.gradle.kts file:

```kotlin
implementation("net.nuqta:projecto-autolog-starter:1.1.0")
```

## Usage

### Basic Usage

```kotlin
@Service
@AutoLog  // Log all public methods in this class
class UserService {

    @AutoLog(logReturnValue = true)  // Override class-level settings
    fun findUser(id: String): User {
        // Method implementation
    }
}
```

### Step-by-Step Logging

```kotlin
@Service
@AutoLog
class OrderService {

    fun processOrder(order: Order): OrderResult {
        // Add user ID to MDC for this operation
        MdcUtil.put("userId", order.userId)
        
        try {
            // Step 1: Validate order
            validateOrder(order)
            
            // Step 2: Process payment
            processPayment(order)
            
            // Step 3: Create shipment
            return createShipment(order)
        } finally {
            // Clean up MDC
            MdcUtil.remove("userId")
        }
    }

    @StepLog(description = "Validating order", logArgs = true)
    private fun validateOrder(order: Order) {
        // Validation logic
    }

    @StepLog(description = "Processing payment", logArgs = true)
    private fun processPayment(order: Order) {
        // Payment processing logic
    }

    @StepLog(description = "Creating shipment", logReturn = true)
    private fun createShipment(order: Order): OrderResult {
        // Shipment creation logic
        return OrderResult(/* ... */)
    }
}
```

### Conditional Logging

```kotlin
@AutoLog(condition = "#debug == true")
fun calculateResult(a: Int, b: Int, debug: Boolean = false): Int {
    // This method will only be logged if debug is true
    return a + b
}
```

### Developer Tagging

```kotlin
@LoggedBy("John Doe")
fun importantMethod() {
    // Method implementation
}
```

### MDC Utilities

```kotlin
// Add values to MDC
MdcUtil.put("userId", "user-123")

// Execute code with MDC values
MdcUtil.withMdc(mapOf("operation" to "payment", "amount" to "100.00")) {
    // Code to execute with MDC values
}

// Get request ID
val requestId = MdcUtil.getRequestId()
```

## Configuration

Example application.yml configuration:

```yaml
projecto:
  autolog:
    enabled: true
    log-args: true
    log-return-value: false
    log-exception-stacktrace: true
    structured-logging: false
    metrics-enabled: true
    include-packages:
      - com.myapp.service
    exclude-packages:
      - com.myapp.cache
    level:
      entry: INFO
      exit: DEBUG
      error: ERROR
      step: DEBUG
    template:
      entry: "[ENTER] {{method}} args={{args}} requestId={{requestId}}"
      exit: "[EXIT] {{method}} returned={{return}} duration={{duration}}ms requestId={{requestId}}"
      error: "[ERROR] {{method}} exception={{exception}} duration={{duration}}ms requestId={{requestId}}"
      step: "[STEP] {{method}} - {{description}} duration={{duration}}ms requestId={{requestId}}"
```

## Metrics

When metrics are enabled, the following metrics are exposed:

- `projecto_autolog_method_duration_seconds`: Method execution duration
- `projecto_autolog_method_duration_seconds_step`: Step execution duration

These metrics include tags for method name, success status, and developer name.

## License

MIT License