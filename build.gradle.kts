import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import java.net.URI

val kotlinVersion = providers.gradleProperty("kotlin.version").get()

plugins {
    kotlin("multiplatform")
    `maven-publish`
}

kotlin {
    jvm()
    js {
        binaries.library()
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
                implementation(kotlinx.datetime)
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
                    url.set("https://github.com/aosamesan/task-executor")

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }

                    developers {
                        developer {
                            id.set("aosamesan")
                            name.set("Aosamesan")
                            email.set("aosamesan@gmail.com")
                        }
                    }

                    scm {
                        connection.set("scm:git:git://github.com/aosamesan/task-executor.git")
                        developerConnection.set("scm:git:ssh://github.com/aosamesan/task-executor.git")
                        url.set("https://github.com/aosamesan/task-executor")
                    }
                }
            }
        }
        repositories {
            maven { url = URI("https://jitpack.io") }
        }
    }
}