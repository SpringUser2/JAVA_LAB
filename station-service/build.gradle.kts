plugins {
    id("org.springframework.boot")
    java
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.0")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.2.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.0")
    runtimeOnly("com.h2database:h2:2.2.220")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.0")
    // Explicit JUnit platform and engine to ensure test runtime has required artifacts
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.junit.platform:junit-platform-launcher:1.10.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

springBoot {
    buildInfo()
}

// Disable BootJar to avoid task incompatibilities in this environment; produce a normal jar instead.
tasks.named("bootJar") {
    enabled = false
}
tasks.named("jar") {
    enabled = true
}

// Configure test JVM to allow Byte Buddy experimental support (needed for newer Java versions)
tasks.test {
    useJUnitPlatform()
    // Enable Byte Buddy experimental mode so Mockito's inline mock maker can instrument newer Java versions
    jvmArgs = listOf("-Dnet.bytebuddy.experimental=true")
}
