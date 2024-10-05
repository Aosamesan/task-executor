val kotlinVersion =

plugins {
    kotlin("multiplatform")
    `maven-publish`
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    js {
        nodejs()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlinx.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlinx.coroutines.test)
            }
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.aosamesan"
                artifactId = "task-executor"
                version = "1.0.0"

                pom {
                    name.set("Task Executor")
                    description.set("Task Executor for Kotlin Multiplatform")
                }
            }
        }
    }
}