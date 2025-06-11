package net.nuqta.projecto.autolog.starter

import net.nuqta.projecto.autolog.core.AutoLogProperties
import net.nuqta.projecto.autolog.core.AutoLoggerAspect
import net.nuqta.projecto.autolog.core.RequestIdFilter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * Auto-configuration for the AutoLog aspect.
 * This configuration is automatically loaded by Spring Boot when the starter is included in the classpath.
 */
@Configuration
@EnableConfigurationProperties(AutoLogProperties::class)
@ConditionalOnProperty(
    prefix = "projecto.autolog",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true
)
open class AutoLogAutoConfiguration {

    /**
     * Creates the AutoLoggerAspect bean if it doesn't exist.
     */
    @Bean
    @ConditionalOnMissingBean
    open fun autoLoggerAspect(properties: AutoLogProperties): AutoLoggerAspect {
        return AutoLoggerAspect(properties)
    }

    /**
     * Creates the RequestIdFilter bean if it doesn't exist and if the application is a web application.
     */
    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnMissingBean
    open fun requestIdFilter(): RequestIdFilter {
        return RequestIdFilter()
    }
}
