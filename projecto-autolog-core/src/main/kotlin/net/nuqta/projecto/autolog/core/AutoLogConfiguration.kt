package net.nuqta.projecto.autolog.core

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * Configuration class for AutoLog.
 * Provides beans for AutoLogProperties, MeterRegistry, and imports RequestIdFilter.
 */
@Configuration
@EnableConfigurationProperties(AutoLogProperties::class)
@Import(RequestIdFilter::class)
open class AutoLogConfiguration {

    /**
     * Provides a MeterRegistry bean if one is not already defined.
     * This is used for collecting metrics.
     *
     * @return A SimpleMeterRegistry instance
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
        prefix = "projecto.autolog",
        name = ["metrics-enabled"],
        havingValue = "true",
        matchIfMissing = false
    )
    open fun meterRegistry(): MeterRegistry {
        return SimpleMeterRegistry()
    }
}
