plugins {
    kotlin("jvm") version "1.6.0"
    id("java-gradle-plugin")
    `maven-publish`

}

group = "com.zwendo"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:1.6.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.6.0")
}

gradlePlugin {
    plugins {
        create("knot8CompilerPlugin") {
            id = "com.zwendo.knot8"
            displayName = "Knot8 Plugin"
            description = "Gradle plugin for Knot8, Kotlin compiler plugin"
            implementationClass = "com.zwendo.knot8.gradle.Knot8GradlePlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
            groupId = project.group.toString()
            artifactId = project.name.toLowerCase()
            version = project.version.toString()

            pom {
                name.set(project.name)

                developers {
                    developer {
                        id.set("ZwenDo")
                    }
                }
            }
        }
    }
}