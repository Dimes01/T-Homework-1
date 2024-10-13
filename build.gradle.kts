plugins {
    id("java")
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("jacoco")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "jacoco")

    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.34")
        annotationProcessor ("org.projectlombok:lombok:1.18.34")

        implementation("org.springframework.boot:spring-boot-starter-validation")

        implementation("org.slf4j:slf4j-api:2.0.16")
        implementation("ch.qos.logback:logback-classic:1.5.7")
        implementation("com.fasterxml.jackson.core:jackson-databind")
        implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")

        testImplementation("org.mockito:mockito-core:5.13.0")

        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }
}

tasks.test {
    useJUnitPlatform()
}