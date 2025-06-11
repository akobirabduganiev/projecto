plugins {
    alias(libs.plugins.kotlinPluginSerialization)
    kotlin("jvm")
    `java-library`
    `maven-publish`
}

dependencies {
    implementation(libs.bundles.springBoot)
    implementation(libs.bundles.springBootWeb)
    implementation(libs.bundles.aspectj)
    implementation(libs.bundles.logging)
    implementation(libs.bundles.metrics)

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

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            groupId = "net.nuqta.projecto"
            artifactId = "projecto-autolog-core"
            version = "0.1.0"

            pom {
                name.set("Projecto AutoLog Core")
                description.set("Core module for automatic logging in Spring Boot applications")
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
