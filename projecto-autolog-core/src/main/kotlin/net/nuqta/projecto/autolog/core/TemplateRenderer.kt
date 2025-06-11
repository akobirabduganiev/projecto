package net.nuqta.projecto.autolog.core

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.reflect.MethodSignature
import java.lang.reflect.Method

/**
 * Utility class for rendering log message templates.
 * Supports placeholders like {{method}}, {{args}}, {{return}}, {{duration}}, etc.
 */
object TemplateRenderer {

    /**
     * Render a template for method entry.
     *
     * @param template The template to render
     * @param joinPoint The join point representing the method invocation
     * @param args The method arguments
     * @return The rendered template
     */
    fun renderEntryTemplate(
        template: String,
        joinPoint: JoinPoint,
        args: String
    ): String {
        val methodName = getMethodName(joinPoint)
        val requestId = MdcUtil.getRequestId()
        val developer = getDeveloperName(joinPoint)

        return template
            .replace("{{method}}", methodName)
            .replace("{{args}}", args)
            .replace("{{requestId}}", requestId)
            .replace("{{developer}}", developer ?: "unknown")
    }

    /**
     * Render a template for method exit.
     *
     * @param template The template to render
     * @param joinPoint The join point representing the method invocation
     * @param result The method result
     * @param duration The method execution duration in milliseconds
     * @return The rendered template
     */
    fun renderExitTemplate(
        template: String,
        joinPoint: JoinPoint,
        result: Any?,
        duration: Long
    ): String {
        val methodName = getMethodName(joinPoint)
        val requestId = MdcUtil.getRequestId()
        val developer = getDeveloperName(joinPoint)

        return template
            .replace("{{method}}", methodName)
            .replace("{{return}}", result?.toString() ?: "null")
            .replace("{{duration}}", duration.toString())
            .replace("{{requestId}}", requestId)
            .replace("{{developer}}", developer ?: "unknown")
    }

    /**
     * Render a template for method error.
     *
     * @param template The template to render
     * @param joinPoint The join point representing the method invocation
     * @param exception The exception that was thrown
     * @param duration The method execution duration in milliseconds
     * @return The rendered template
     */
    fun renderErrorTemplate(
        template: String,
        joinPoint: JoinPoint,
        exception: Throwable,
        duration: Long
    ): String {
        val methodName = getMethodName(joinPoint)
        val requestId = MdcUtil.getRequestId()
        val developer = getDeveloperName(joinPoint)

        return template
            .replace("{{method}}", methodName)
            .replace("{{exception}}", exception.message ?: "null")
            .replace("{{duration}}", duration.toString())
            .replace("{{requestId}}", requestId)
            .replace("{{developer}}", developer ?: "unknown")
    }

    /**
     * Render a template for step log.
     *
     * @param template The template to render
     * @param joinPoint The join point representing the method invocation
     * @param description The step description
     * @param result The step result
     * @param duration The step execution duration in milliseconds
     * @return The rendered template
     */
    fun renderStepTemplate(
        template: String,
        joinPoint: JoinPoint,
        description: String,
        result: Any?,
        duration: Long
    ): String {
        val methodName = getMethodName(joinPoint)
        val requestId = MdcUtil.getRequestId()
        val developer = getDeveloperName(joinPoint)

        return template
            .replace("{{method}}", methodName)
            .replace("{{description}}", description)
            .replace("{{return}}", result?.toString() ?: "null")
            .replace("{{duration}}", duration.toString())
            .replace("{{requestId}}", requestId)
            .replace("{{developer}}", developer ?: "unknown")
    }

    /**
     * Get the method name in the format "ClassName.methodName".
     *
     * @param joinPoint The join point representing the method invocation
     * @return The method name
     */
    private fun getMethodName(joinPoint: JoinPoint): String {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val declaringClass = method.declaringClass
        return "${declaringClass.simpleName}.${method.name}"
    }

    /**
     * Get the developer name from the @LoggedBy annotation.
     *
     * @param joinPoint The join point representing the method invocation
     * @return The developer name, or null if not specified
     */
    private fun getDeveloperName(joinPoint: JoinPoint): String? {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val methodAnnotation = method.getAnnotation(LoggedBy::class.java)
        if (methodAnnotation != null) {
            return methodAnnotation.value
        }

        val classAnnotation = method.declaringClass.getAnnotation(LoggedBy::class.java)
        return classAnnotation?.value
    }
}