package net.nuqta.projecto.autolog.demo

import net.nuqta.projecto.autolog.core.AutoLog
import net.nuqta.projecto.autolog.core.LoggedBy
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Demo controller to demonstrate the usage of the @AutoLog annotation.
 * This class is annotated with @AutoLog, so all public methods will be logged.
 */
@RestController
@RequestMapping("/api/demo")
@AutoLog
@LoggedBy("Akobir")
open class DemoController(private val demoService: DemoService) {

    /**
     * Endpoint to process data.
     */
    @GetMapping("/process/{data}")
    open fun processData(@PathVariable data: String): Map<String, String> {
        val result = demoService.processData(data)
        return mapOf("result" to result)
    }

    /**
     * Endpoint to calculate a result.
     * Demonstrates conditional logging with the debug parameter.
     */
    @GetMapping("/calculate")
    open fun calculateResult(
        @RequestParam a: Int,
        @RequestParam b: Int,
        @RequestParam(defaultValue = "false") debug: Boolean
    ): Map<String, Int> {
        val result = demoService.calculateResult(a, b, debug)
        return mapOf("result" to result)
    }

    /**
     * Endpoint to demonstrate exception logging.
     */
    @GetMapping("/risky")
    open fun riskyOperation(@RequestParam(defaultValue = "false") shouldFail: Boolean): Map<String, String> {
        val result = demoService.riskyOperation(shouldFail)
        return mapOf("result" to result)
    }

    /**
     * Endpoint to demonstrate structured logging.
     * Toggle between JSON and regular logging.
     */
    @GetMapping("/toggle-structured-logging")
    open fun toggleStructuredLogging(
        @RequestParam(defaultValue = "false") enabled: Boolean
    ): Map<String, Boolean> {
        // In a real application, this would update the configuration
        // For demo purposes, we just return the value
        return mapOf("structuredLogging" to enabled)
    }
}
