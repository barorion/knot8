plugins {
    kotlin("jvm") version "1.6.0"
}

group = "com.zwendo"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.5.31")
}