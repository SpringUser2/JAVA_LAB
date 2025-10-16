plugins {
    java
    id("org.springframework.boot") version "3.2.0" apply false
    id("io.spring.dependency-management") version "1.1.0" apply false
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

subprojects {
    apply(plugin = "java")
    // do not apply Spring dependency-management globally (incompatible with Gradle 9); subprojects
    // that need it should apply it explicitly.

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    }

    tasks.test {
        useJUnitPlatform()
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
}
