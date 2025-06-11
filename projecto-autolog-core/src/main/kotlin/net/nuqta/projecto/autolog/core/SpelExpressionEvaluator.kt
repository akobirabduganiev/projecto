package net.nuqta.projecto.autolog.core

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

/**
 * Helper class for evaluating SpEL expressions in the context of a method invocation.
 */
object SpelExpressionEvaluator {
    private val parser: ExpressionParser = SpelExpressionParser()

    /**
     * Evaluate a SpEL expression in the context of a method invocation.
     *
     * @param expression The SpEL expression to evaluate
     * @param joinPoint The join point representing the method invocation
     * @return The result of the expression evaluation, or true if the expression is empty
     */
    fun evaluate(expression: String, joinPoint: JoinPoint): Boolean {
        // If the expression is empty, always return true
        if (expression.isBlank()) {
            return true
        }

        try {
            val signature = joinPoint.signature as MethodSignature
            val method = signature.method
            val parameterNames = signature.parameterNames
            val args = joinPoint.args

            // Create evaluation context with method parameters
            val context = StandardEvaluationContext()
            
            // Add method parameters to the context
            for (i in parameterNames.indices) {
                context.setVariable(parameterNames[i], args[i])
            }

            // Add the target object to the context as 'this'
            context.setVariable("this", joinPoint.target)
            
            // Add the method to the context
            context.setVariable("method", method)

            // Evaluate the expression
            return parser.parseExpression(expression).getValue(context, Boolean::class.java) ?: false
        } catch (e: Exception) {
            // Log the error and return false
            println("Error evaluating SpEL expression: $expression - ${e.message}")
            return false
        }
    }
}