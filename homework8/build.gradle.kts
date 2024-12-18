plugins {
	java
	jacoco
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

var resilience4jVersion = "2.2.0"
var jacksonVersion = "2.18.0"
dependencies {
	implementation(project(":domain"))

	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webflux:3.3.4")

	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

	implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
	implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
	implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$jacksonVersion")

	implementation("io.github.resilience4j:resilience4j-spring-boot2:$resilience4jVersion")
	implementation("io.github.resilience4j:resilience4j-reactor:$resilience4jVersion")
	implementation("io.github.resilience4j:resilience4j-circuitbreaker:$resilience4jVersion")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)

}
tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(false)
		csv.required.set(false)
	}
	classDirectories.setFrom(files(classDirectories.files.map {
		fileTree(it) {
			exclude("**/models/**")
			exclude("**/dto/**")
			exclude("**/utilities/**")
		}
	}))
}