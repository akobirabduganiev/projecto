package net.nuqta.projecto.autolog.core

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.boot.logging.LogLevel

/**
 * Configuration properties for the AutoLog aspect.
 *
 * @property enabled Whether the AutoLog aspect is enabled (default: true)
 * @property logArgs Whether to log method arguments by default (default: true)
 * @property logReturnValue Whether to log method return values by default (default: false)
 * @property logExceptionStacktrace Whether to log exception stacktraces (default: true)
 * @property includePackages List of packages to include in logging (empty means all)
 * @property excludePackages List of packages to exclude from logging
 * @property level Log level configuration
 * @property template Template configuration for log messages
 * @property structuredLogging Whether to use structured logging (JSON format) (default: false)
 * @property metricsEnabled Whether to collect metrics (default: false)
 */
@ConfigurationProperties(prefix = "projecto.autolog")
data class AutoLogProperties(
    val enabled: Boolean = true,
    val logArgs: Boolean = true,
    val logReturnValue: Boolean = false,
    val logExceptionStacktrace: Boolean = true,
    val includePackages: List<String> = emptyList(),
    val excludePackages: List<String> = emptyList(),
    @NestedConfigurationProperty
    val level: LogLevelProperties = LogLevelProperties(),
    @NestedConfigurationProperty
    val template: TemplateProperties = TemplateProperties(),
    val structuredLogging: Boolean = false,
    val metricsEnabled: Boolean = false
)

/**
 * Log level configuration properties.
 *
 * @property entry Log level for method entry (default: INFO)
 * @property exit Log level for method exit (default: DEBUG)
 * @property error Log level for method errors (default: ERROR)
 * @property step Log level for step logs (default: DEBUG)
 */
data class LogLevelProperties(
    val entry: LogLevel = LogLevel.INFO,
    val exit: LogLevel = LogLevel.DEBUG,
    val error: LogLevel = LogLevel.ERROR,
    val step: LogLevel = LogLevel.DEBUG
)

/**
 * Template configuration properties for log messages.
 *
 * @property entry Template for method entry logs
 * @property exit Template for method exit logs
 * @property error Template for method error logs
 * @property step Template for step logs
 */
data class TemplateProperties(
    val entry: String = "[ENTER] {{method}} args={{args}}",
    val exit: String = "[EXIT] {{method}} returned={{return}} duration={{duration}}ms",
    val error: String = "[ERROR] {{method}} exception={{exception}}",
    val step: String = "[STEP] {{method}} - {{description}} duration={{duration}}ms"
)
