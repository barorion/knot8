plugins {
    kotlin("jvm") version "1.6.0"
    id("com.zwendo.knot8") version "0.1.0"
    application
}

group = "com.zwendo"
version = "0.1.0"

repositories {
    mavenCentral()
    mavenLocal()
}

application {
    mainClass.set("com.zwendo.knot8.annotation.AnnotationsKt")
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.6.0")
}