# Projecto

Projecto is an open-source library for Spring Boot applications that aims to reduce boilerplate code.

## Modules

### projecto-autolog

The `projecto-autolog` module provides automatic logging for Spring Boot applications.

#### Features

- **@AutoLog annotation**: Can be used on classes or methods to enable automatic logging
- **Automatic logging of method entry and exit**: Logs when a method is entered and exited
- **Execution duration logging**: Logs the execution time of methods in milliseconds
- **Exception logging**: Catches and logs exceptions in a standardized format
- **Request ID tracking**: Includes request ID in logs for easier tracing
- **Configurable via application.yml**: Control logging behavior through configuration
- **@StepLog annotation**: (Future feature) Enables detailed step-level logging within methods

## Getting Started

### Add Dependency

Add the following dependency to your `build.gradle.kts` file:

```kotlin
implementation("net.nuqta.projecto:projecto-autolog-starter:0.1.0")
```

Or if you're using Maven:

```xml
<dependency>
    <groupId>net.nuqta.projecto</groupId>
    <artifactId>projecto-autolog-starter</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Configuration

Configure the library in your `application.yml` file:

```yaml
projecto:
  autolog:
    enabled: true           # Enable/disable the aspect (default: true)
    log-args: true          # Log method arguments (default: true)
    log-return-value: false # Log method return values (default: false)
```

### Usage

1. Add the `@AutoLog` annotation to your classes or methods:

```kotlin
import net.nuqta.projecto.autolog.core.AutoLog

@Service
class MyService {

    @AutoLog
    fun processData(data: String): String {
        // Method implementation
        return "Processed: $data"
    }

    @AutoLog(logArgs = true, logReturnValue = true)
    fun calculateResult(a: Int, b: Int): Int {
        return a + b
    }
}
```

2. The library will automatically log:
   - Method entry: `--> Entering: MyService.processData(requestId=abc123, ...)`
   - Method exit: `<-- Exiting: MyService.processData [duration=12ms]`
   - Exceptions: `!! Exception in MyService.processData: Error message`

3. (Future Feature) Use the `@StepLog` annotation for detailed step-level logging:

```kotlin
@AutoLog
fun complexProcess(data: String): Result {
    // Step 1: Validate input
    val validatedData = @StepLog("Validate Input") validateData(data)

    // Step 2: Process data
    val processedData = @StepLog("Process Data", logReturnValue = true) processData(validatedData)

    // Step 3: Generate result
    return @StepLog("Generate Result") createResult(processedData)
}
```

## Building the Project

This project uses [Gradle](https://gradle.org/).

* Run `./gradlew build` to build the project
* Run `./gradlew :projecto-autolog-demo:bootRun` to run the demo application

## Project Structure

- **projecto-autolog-core**: Core annotations, aspects, and filters
- **projecto-autolog-starter**: Spring Boot auto-configuration module
- **projecto-autolog-demo**: Demo Spring Boot application

## License

This project is licensed under the MIT License - see the LICENSE file for details.
