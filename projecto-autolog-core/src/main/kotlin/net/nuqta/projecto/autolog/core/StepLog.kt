package net.nuqta.projecto.autolog.core

/**
 * Annotation to enable step-by-step logging inside methods annotated with @AutoLog.
 * This annotation allows for more granular logging of specific steps within a method.
 *
 * The logs will include:
 * - Step description
 * - Step execution duration
 * - Return value of the step (if enabled)
 *
 * @property description A description of the step being logged
 * @property logReturn Whether to log the return value of the step (default: false)
 * @property logArgs Whether to log the arguments of the step (default: false)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class StepLog(
    val description: String,
    val logReturn: Boolean = false,
    val logArgs: Boolean = false
)
