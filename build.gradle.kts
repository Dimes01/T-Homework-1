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

val lombokVersion = project.properties["lombokVersion"]
val mockitoVersion = project.properties["mockitoVersion"]
val junitVersion = project.properties["junitVersion"]
val logbackVersion = project.properties["logbackVersion"]
val slf4jVersion = project.properties["slf4jVersion"]
subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "jacoco")

    dependencies {
        compileOnly("org.projectlombok:lombok:$lombokVersion")
        annotationProcessor ("org.projectlombok:lombok:$lombokVersion")

        implementation("org.springframework.boot:spring-boot-starter-validation")

        implementation("org.slf4j:slf4j-api:$slf4jVersion")
        implementation("ch.qos.logback:logback-classic:$logbackVersion")

        implementation("com.fasterxml.jackson.core:jackson-databind")
        implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")

        testImplementation("org.mockito:mockito-core:$mockitoVersion")

        testImplementation(platform("org.junit:junit-bom:$junitVersion"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }
}

tasks.test {
    useJUnitPlatform()
}