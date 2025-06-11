package net.nuqta.projecto.autolog.demo

import net.nuqta.projecto.autolog.core.AutoLog
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
     */
    @GetMapping("/calculate")
    open fun calculateResult(
        @RequestParam a: Int,
        @RequestParam b: Int
    ): Map<String, Int> {
        val result = demoService.calculateResult(a, b)
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
}
