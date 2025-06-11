plugins {
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.springBoot) apply false
    alias(libs.plugins.springDependencyManagement)
    kotlin("jvm")
    `java-library`
    `maven-publish`
}

dependencies {
    api(project(":projecto-autolog-core"))

    implementation(libs.springBootAutoconfigure)
    implementation(libs.springBootConfigurationProcessor)

    testImplementation(libs.springBootStarterTest)
}

java {
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

// Since Spring Boot plugin is applied with 'apply false', we need to configure it differently
tasks.named("jar") {
    enabled = true
}

// Ensure bootJar is not created for this library project
tasks.register("bootJar") {
    enabled = false
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            groupId = "net.nuqta.projecto"
            artifactId = "projecto-autolog-starter"
            version = "0.1.0"

            pom {
                name.set("Projecto AutoLog Starter")
                description.set("Spring Boot starter for automatic logging in Spring Boot applications")
                url.set("https://github.com/nuqta/projecto")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("nuqta")
                        name.set("Nuqta")
                        email.set("info@nuqta.net")
                    }
                }
            }
        }
    }
}
