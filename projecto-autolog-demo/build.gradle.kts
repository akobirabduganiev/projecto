plugins {
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.springBoot)
    alias(libs.plugins.springDependencyManagement)
    kotlin("jvm")
}

dependencies {
    implementation(project(":projecto-autolog-starter"))
    implementation(libs.bundles.springBootWeb)

    testImplementation(libs.springBootStarterTest)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
