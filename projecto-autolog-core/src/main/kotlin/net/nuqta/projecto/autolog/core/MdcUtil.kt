package net.nuqta.projecto.autolog.core

import org.slf4j.MDC
import java.util.UUID
import java.util.concurrent.Callable

/**
 * Utility class for working with MDC (Mapped Diagnostic Context).
 * Provides methods for adding, removing, and retrieving MDC values.
 */
object MdcUtil {

    /**
     * Get the current request ID from MDC.
     * If not present, generates a new UUID and stores it in MDC.
     *
     * @return The current request ID
     */
    fun getRequestId(): String {
        return MDC.get(RequestIdFilter.REQUEST_ID_MDC_KEY) ?: run {
            val newRequestId = UUID.randomUUID().toString()
            MDC.put(RequestIdFilter.REQUEST_ID_MDC_KEY, newRequestId)
            newRequestId
        }
    }

    /**
     * Add a value to MDC.
     *
     * @param key The MDC key
     * @param value The value to add
     */
    fun put(key: String, value: String) {
        MDC.put(key, value)
    }

    /**
     * Remove a value from MDC.
     *
     * @param key The MDC key to remove
     */
    fun remove(key: String) {
        MDC.remove(key)
    }

    /**
     * Get a value from MDC.
     *
     * @param key The MDC key
     * @return The value, or null if not present
     */
    fun get(key: String): String? {
        return MDC.get(key)
    }

    /**
     * Execute a block of code with additional MDC values.
     * The values are added before execution and removed after execution.
     *
     * @param values Map of MDC keys and values to add
     * @param block The code block to execute
     * @return The result of the code block
     */
    fun <T> withMdc(values: Map<String, String>, block: () -> T): T {
        val oldValues = mutableMapOf<String, String?>()
        
        try {
            // Save old values and set new ones
            values.forEach { (key, value) ->
                oldValues[key] = MDC.get(key)
                MDC.put(key, value)
            }
            
            // Execute the block
            return block()
        } finally {
            // Restore old values
            values.keys.forEach { key ->
                val oldValue = oldValues[key]
                if (oldValue != null) {
                    MDC.put(key, oldValue)
                } else {
                    MDC.remove(key)
                }
            }
        }
    }

    /**
     * Execute a callable with additional MDC values.
     * The values are added before execution and removed after execution.
     *
     * @param values Map of MDC keys and values to add
     * @param callable The callable to execute
     * @return The result of the callable
     */
    fun <T> withMdc(values: Map<String, String>, callable: Callable<T>): T {
        return withMdc(values) { callable.call() }
    }
}