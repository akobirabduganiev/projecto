package net.nuqta.projecto.autolog.core

/**
 * Annotation to enable automatic logging for methods or classes.
 * When applied to a class, all public methods will be logged.
 * When applied to a method, only that method will be logged.
 *
 * The logs will include:
 * - Method entry with arguments (if enabled)
 * - Method exit with return value (if enabled) and execution duration
 * - Exception details if the method throws an exception
 *
 * @property logArgs Whether to log method arguments (default: true)
 * @property logReturnValue Whether to log method return value (default: false)
 * @property condition SpEL expression to determine if logging should be performed (default: "")
 *                    If empty, logging is always performed.
 *                    If not empty, logging is only performed if the expression evaluates to true.
 *                    Example: "#debug == true" will only log if a parameter named "debug" is true.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class AutoLog(
    val logArgs: Boolean = true,
    val logReturnValue: Boolean = false,
    val condition: String = ""
)
