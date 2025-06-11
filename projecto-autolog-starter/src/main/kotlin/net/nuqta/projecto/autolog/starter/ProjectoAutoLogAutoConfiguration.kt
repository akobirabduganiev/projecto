package net.nuqta.projecto.autolog.starter

import net.nuqta.projecto.autolog.core.AutoLogConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * Auto-configuration for the Projecto AutoLog library.
 * Imports the core configuration to enable automatic logging.
 */
@Configuration
@Import(AutoLogConfiguration::class)
open class ProjectoAutoLogAutoConfiguration