plugins {
    kotlin("jvm") version "1.6.0"
    `maven-publish`
    java
}

group = "com.zwendo"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.6.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.6.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
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