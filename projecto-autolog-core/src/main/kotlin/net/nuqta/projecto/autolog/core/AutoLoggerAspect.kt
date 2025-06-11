package net.nuqta.projecto.autolog.core

import io.micrometer.core.instrument.MeterRegistry
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.logging.LogLevel
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * Aspect that intercepts methods annotated with @AutoLog and logs method entry, exit, and exceptions.
 * Also handles @StepLog annotations for step-by-step logging within methods.
 */
@Aspect
@Component
@ConditionalOnProperty(
    prefix = "projecto.autolog",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true
)
class AutoLoggerAspect(
    private val properties: AutoLogProperties,
    @Autowired(required = false) private val meterRegistry: MeterRegistry?
) {
    private val logger = LoggerFactory.getLogger(AutoLoggerAspect::class.java)
    private val metricsUtil by lazy { meterRegistry?.let { MetricsUtil(it) } }

    @Pointcut("@annotation(net.nuqta.projecto.autolog.core.AutoLog)")
    fun annotatedMethod() {
    }

    @Pointcut("@within(net.nuqta.projecto.autolog.core.AutoLog)")
    fun annotatedClass() {
    }

    @Pointcut("@annotation(net.nuqta.projecto.autolog.core.StepLog)")
    fun stepLogMethod() {
    }

    @Around("annotatedMethod() || annotatedClass()")
    fun around(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val declaringClass = method.declaringClass
        val methodName = "${declaringClass.simpleName}.${method.name}"

        // Get annotation from method or class
        val methodAnnotation = method.getAnnotation(AutoLog::class.java)
        val classAnnotation = declaringClass.getAnnotation(AutoLog::class.java)
        val annotation = methodAnnotation ?: classAnnotation

        // Check if we should skip logging based on condition
        if (annotation != null && annotation.condition.isNotBlank()) {
            if (!SpelExpressionEvaluator.evaluate(annotation.condition, joinPoint)) {
                // Condition not met, skip logging
                return joinPoint.proceed()
            }
        }

        // Check if the package should be included/excluded
        if (!shouldLogPackage(declaringClass.name)) {
            return joinPoint.proceed()
        }

        // Determine if we should log arguments and return value
        val logArgs = annotation?.logArgs ?: properties.logArgs
        val logReturnValue = annotation?.logReturnValue ?: properties.logReturnValue

        // Get request ID from MDC or generate a new one
        val requestId = MdcUtil.getRequestId()

        // Get developer name if available
        val developer = getDeveloperName(method, declaringClass)

        // Log method entry
        val args = if (logArgs) {
            joinPoint.args.joinToString(", ") { arg -> arg?.toString() ?: "null" }
        } else {
            ""
        }

        // Use template for entry log
        val entryMessage = TemplateRenderer.renderEntryTemplate(
            properties.template.entry,
            joinPoint,
            args
        )

        // Log with appropriate level
        log(properties.level.entry, entryMessage)

        val startTime = System.nanoTime()
        var success = false

        return try {
            // Execute the method
            val result = joinPoint.proceed()
            success = true

            // Calculate duration
            val durationNanos = System.nanoTime() - startTime
            val durationMillis = TimeUnit.NANOSECONDS.toMillis(durationNanos)

            // Record metrics if enabled
            if (properties.metricsEnabled && metricsUtil != null) {
                metricsUtil?.recordMethodExecution(
                    methodName,
                    durationNanos,
                    true,
                    mapOf("developer" to (developer ?: "unknown"))
                )
            }

            // Use template for exit log
            val exitMessage = TemplateRenderer.renderExitTemplate(
                properties.template.exit,
                joinPoint,
                if (logReturnValue) result else null,
                durationMillis
            )

            // Log with appropriate level
            log(properties.level.exit, exitMessage)

            result
        } catch (e: Exception) {
            // Calculate duration
            val durationNanos = System.nanoTime() - startTime
            val durationMillis = TimeUnit.NANOSECONDS.toMillis(durationNanos)

            // Record metrics if enabled
            if (properties.metricsEnabled && metricsUtil != null) {
                metricsUtil?.recordMethodExecution(
                    methodName,
                    durationNanos,
                    false,
                    mapOf("developer" to (developer ?: "unknown"))
                )
            }

            // Use template for error log
            val errorMessage = TemplateRenderer.renderErrorTemplate(
                properties.template.error,
                joinPoint,
                e,
                durationMillis
            )

            // Log with appropriate level
            if (properties.logExceptionStacktrace) {
                log(properties.level.error, errorMessage, e)
            } else {
                log(properties.level.error, errorMessage)
            }

            throw e
        }
    }

    @Around("stepLogMethod()")
    fun aroundStep(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val declaringClass = method.declaringClass
        val methodName = "${declaringClass.simpleName}.${method.name}"

        // Get the StepLog annotation
        val stepLogAnnotation = method.getAnnotation(StepLog::class.java)
            ?: return joinPoint.proceed() // No annotation, just proceed

        // Get the step description
        val description = stepLogAnnotation.description

        // Determine if we should log arguments and return value
        val logArgs = stepLogAnnotation.logArgs
        val logReturn = stepLogAnnotation.logReturn

        // Get request ID from MDC
        val requestId = MdcUtil.getRequestId()

        // Get developer name if available
        val developer = getDeveloperName(method, declaringClass)

        // Log step entry
        if (logArgs) {
            val args = joinPoint.args.joinToString(", ") { arg -> arg?.toString() ?: "null" }
            log(properties.level.step, "[STEP] $methodName - $description (args=[$args])")
        } else {
            log(properties.level.step, "[STEP] $methodName - $description")
        }

        val startTime = System.nanoTime()
        var success = false

        return try {
            // Execute the step
            val result = joinPoint.proceed()
            success = true

            // Calculate duration
            val durationNanos = System.nanoTime() - startTime
            val durationMillis = TimeUnit.NANOSECONDS.toMillis(durationNanos)

            // Record metrics if enabled
            if (properties.metricsEnabled && metricsUtil != null) {
                metricsUtil?.recordStepExecution(
                    methodName,
                    description,
                    durationNanos,
                    true,
                    mapOf("developer" to (developer ?: "unknown"))
                )
            }

            // Use template for step log
            val stepMessage = TemplateRenderer.renderStepTemplate(
                properties.template.step,
                joinPoint,
                description,
                if (logReturn) result else null,
                durationMillis
            )

            // Log with appropriate level
            log(properties.level.step, stepMessage)

            result
        } catch (e: Exception) {
            // Calculate duration
            val durationNanos = System.nanoTime() - startTime
            val durationMillis = TimeUnit.NANOSECONDS.toMillis(durationNanos)

            // Record metrics if enabled
            if (properties.metricsEnabled && metricsUtil != null) {
                metricsUtil?.recordStepExecution(
                    methodName,
                    description,
                    durationNanos,
                    false,
                    mapOf("developer" to (developer ?: "unknown"))
                )
            }

            // Log error
            log(properties.level.error, "[STEP ERROR] $methodName - $description: ${e.message}")

            throw e
        }
    }

    /**
     * Log a message with the specified log level.
     */
    private fun log(level: LogLevel, message: String, throwable: Throwable? = null) {
        when (level) {
            LogLevel.TRACE -> if (throwable != null) logger.trace(message, throwable) else logger.trace(message)
            LogLevel.DEBUG -> if (throwable != null) logger.debug(message, throwable) else logger.debug(message)
            LogLevel.INFO -> if (throwable != null) logger.info(message, throwable) else logger.info(message)
            LogLevel.WARN -> if (throwable != null) logger.warn(message, throwable) else logger.warn(message)
            LogLevel.ERROR -> if (throwable != null) logger.error(message, throwable) else logger.error(message)
            LogLevel.FATAL -> if (throwable != null) logger.error(message, throwable) else logger.error(message)
            LogLevel.OFF -> { /* Do nothing */ }
        }
    }

    /**
     * Check if a package should be logged based on include/exclude lists.
     */
    private fun shouldLogPackage(packageName: String): Boolean {
        // If include list is empty, include all packages
        if (properties.includePackages.isEmpty()) {
            // If exclude list is empty, include all packages
            if (properties.excludePackages.isEmpty()) {
                return true
            }

            // Check if the package is in the exclude list
            return properties.excludePackages.none { packageName.startsWith(it) }
        }

        // Check if the package is in the include list
        return properties.includePackages.any { packageName.startsWith(it) }
    }

    /**
     * Get the developer name from the @LoggedBy annotation.
     */
    private fun getDeveloperName(method: java.lang.reflect.Method, declaringClass: Class<*>): String? {
        val methodAnnotation = method.getAnnotation(LoggedBy::class.java)
        if (methodAnnotation != null) {
            return methodAnnotation.value
        }

        val classAnnotation = declaringClass.getAnnotation(LoggedBy::class.java)
        return classAnnotation?.value
    }
}
