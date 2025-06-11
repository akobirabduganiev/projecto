package net.nuqta.projecto.autolog.demo

import net.nuqta.projecto.autolog.core.AutoLog
import net.nuqta.projecto.autolog.core.LoggedBy
import net.nuqta.projecto.autolog.core.MdcUtil
import net.nuqta.projecto.autolog.core.StepLog
import org.springframework.stereotype.Service
import java.util.UUID

/**
 * Demo service to demonstrate the usage of the @AutoLog annotation.
 */
@Service
@AutoLog
@LoggedBy("Akobir")
open class DemoService {

    /**
     * Method annotated with @AutoLog to demonstrate method-level logging.
     * Uses @StepLog to log individual steps within the method.
     */
    open fun processData(data: String): String {
        // Add user ID to MDC for this operation
        MdcUtil.put("userId", "user-123")

        try {
            // Step 1: Validate data
            validateData(data)

            // Step 2: Process data
            val processedData = doProcessData(data)

            // Step 3: Generate ID
            val id = generateId()

            return "Processed: $processedData (ID: $id)"
        } finally {
            // Clean up MDC
            MdcUtil.remove("userId")
        }
    }

    /**
     * Method annotated with @StepLog to demonstrate step logging.
     */
    @StepLog(description = "Validating input data", logArgs = true)
    open fun validateData(data: String) {
        // Simulate validation
        Thread.sleep(50)

        if (data.isBlank()) {
            throw IllegalArgumentException("Data cannot be blank")
        }
    }

    /**
     * Method annotated with @StepLog to demonstrate step logging.
     */
    @StepLog(description = "Processing data", logArgs = true, logReturn = true)
    open fun doProcessData(data: String): String {
        // Simulate processing
        Thread.sleep(100)
        return data.uppercase()
    }

    /**
     * Method annotated with @StepLog to demonstrate step logging.
     */
    @StepLog(description = "Generating unique ID", logReturn = true)
    open fun generateId(): String {
        // Simulate ID generation
        Thread.sleep(30)
        return UUID.randomUUID().toString()
    }

    /**
     * Method annotated with @AutoLog with custom settings and conditional logging.
     * Only logs when debug is true.
     */
    @AutoLog(condition = "#debug == true")
    open fun calculateResult(a: Int, b: Int, debug: Boolean = false): Int {
        // Use MDC with a block of code
        return MdcUtil.withMdc(mapOf("operation" to "calculate", "debug" to debug.toString())) {
            // Simulate calculation
            Thread.sleep(200)
            a + b
        }
    }

    /**
     * Method that throws an exception to demonstrate exception logging.
     */
    @LoggedBy("System")
    open fun riskyOperation(shouldFail: Boolean): String {
        // Simulate processing
        Thread.sleep(150)

        if (shouldFail) {
            throw RuntimeException("Operation failed as requested")
        }

        return "Operation completed successfully"
    }
}
