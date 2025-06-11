package net.nuqta.projecto.autolog.core

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Timer
import java.util.concurrent.TimeUnit

/**
 * Utility class for working with Micrometer metrics.
 * Provides methods for recording method execution times.
 */
class MetricsUtil(private val registry: MeterRegistry) {

    companion object {
        private const val TIMER_NAME = "projecto_autolog_method_duration_seconds"
    }

    /**
     * Record the execution time of a method.
     *
     * @param methodName The name of the method
     * @param durationNanos The duration of the method execution in nanoseconds
     * @param success Whether the method execution was successful
     * @param tags Additional tags to add to the metric
     */
    fun recordMethodExecution(
        methodName: String,
        durationNanos: Long,
        success: Boolean,
        tags: Map<String, String> = emptyMap()
    ) {
        val tagList = mutableListOf(
            Tag.of("method", methodName),
            Tag.of("success", success.toString())
        )
        
        // Add additional tags
        tags.forEach { (key, value) ->
            tagList.add(Tag.of(key, value))
        }
        
        Timer.builder(TIMER_NAME)
            .tags(tagList)
            .register(registry)
            .record(durationNanos, TimeUnit.NANOSECONDS)
    }

    /**
     * Record the execution time of a step within a method.
     *
     * @param methodName The name of the method
     * @param stepDescription The description of the step
     * @param durationNanos The duration of the step execution in nanoseconds
     * @param success Whether the step execution was successful
     * @param tags Additional tags to add to the metric
     */
    fun recordStepExecution(
        methodName: String,
        stepDescription: String,
        durationNanos: Long,
        success: Boolean,
        tags: Map<String, String> = emptyMap()
    ) {
        val tagList = mutableListOf(
            Tag.of("method", methodName),
            Tag.of("step", stepDescription),
            Tag.of("success", success.toString())
        )
        
        // Add additional tags
        tags.forEach { (key, value) ->
            tagList.add(Tag.of(key, value))
        }
        
        Timer.builder("${TIMER_NAME}_step")
            .tags(tagList)
            .register(registry)
            .record(durationNanos, TimeUnit.NANOSECONDS)
    }
}