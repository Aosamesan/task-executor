rootProject.name = "task-executor"

pluginManagement {
    val kotlinVersion: String = providers.gradleProperty("kotlin.version").get()

    repositories {
        mavenCentral()
    }

    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
        kotlin("multiplatform") version kotlinVersion
    }
}

dependencyResolutionManagement {
    val kotlinCoroutinesVersion: String = providers.gradleProperty("kotlinx.coroutines.version").get()

    versionCatalogs {
        create("kotlinx") {
            library("coroutines-core", "org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
            library("coroutines-test", "org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutinesVersion")
        }
    }
}