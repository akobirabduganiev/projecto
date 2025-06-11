package net.nuqta.projecto.autolog.core

/**
 * Annotation to tag a method or class with the developer who wrote or owns it.
 * This information will be included in the logs.
 *
 * @property value The name of the developer who wrote or owns the method or class
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LoggedBy(
    val value: String
)