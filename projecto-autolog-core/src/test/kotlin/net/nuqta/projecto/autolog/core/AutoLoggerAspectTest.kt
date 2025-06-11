package net.nuqta.projecto.autolog.core

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.stereotype.Service
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [AutoLoggerAspectTest.TestConfig::class])
class AutoLoggerAspectTest {

    @Autowired
    private lateinit var testService: TestService

    @Test
    fun testAutoLog() {
        println("[DEBUG_LOG] Starting testAutoLog test")
        testService.doSomething("test")
        println("[DEBUG_LOG] Completed testAutoLog test")
    }

    @Test
    fun testStepLog() {
        println("[DEBUG_LOG] Starting testStepLog test")
        testService.processWithSteps("test")
        println("[DEBUG_LOG] Completed testStepLog test")
    }

    @Test
    fun testConditionalLogging() {
        println("[DEBUG_LOG] Starting testConditionalLogging test with debug=false")
        testService.conditionalMethod("test", false)
        println("[DEBUG_LOG] Starting testConditionalLogging test with debug=true")
        testService.conditionalMethod("test", true)
        println("[DEBUG_LOG] Completed testConditionalLogging test")
    }

    @TestConfiguration
    @EnableAspectJAutoProxy
    open class TestConfig {
        @Bean
        open fun autoLogProperties(): AutoLogProperties {
            return AutoLogProperties(
                enabled = true,
                logArgs = true,
                logReturnValue = true,
                logExceptionStacktrace = true,
                metricsEnabled = true
            )
        }

        @Bean
        open fun meterRegistry(): SimpleMeterRegistry {
            return SimpleMeterRegistry()
        }

        @Bean
        open fun autoLoggerAspect(properties: AutoLogProperties, meterRegistry: SimpleMeterRegistry): AutoLoggerAspect {
            return AutoLoggerAspect(properties, meterRegistry)
        }

        @Bean
        open fun testService(): TestService {
            return TestService()
        }
    }

    @Service
    @AutoLog
    @LoggedBy("Test Developer")
    open class TestService {
        
        @AutoLog(logReturnValue = true)
        open fun doSomething(input: String): String {
            println("[DEBUG_LOG] Inside doSomething method with input: $input")
            return "Processed: $input"
        }
        
        open fun processWithSteps(input: String): String {
            println("[DEBUG_LOG] Inside processWithSteps method with input: $input")
            
            // Step 1
            val validated = validateInput(input)
            
            // Step 2
            val processed = processInput(validated)
            
            return processed
        }
        
        @StepLog(description = "Validating input", logArgs = true)
        open fun validateInput(input: String): String {
            println("[DEBUG_LOG] Inside validateInput method with input: $input")
            return input
        }
        
        @StepLog(description = "Processing input", logArgs = true, logReturn = true)
        open fun processInput(input: String): String {
            println("[DEBUG_LOG] Inside processInput method with input: $input")
            return "Processed: $input"
        }
        
        @AutoLog(condition = "#debug == true")
        open fun conditionalMethod(input: String, debug: Boolean): String {
            println("[DEBUG_LOG] Inside conditionalMethod with input: $input, debug: $debug")
            return "Conditional: $input"
        }
    }
}