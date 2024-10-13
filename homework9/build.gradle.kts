plugins {
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
	id("jacoco")
	id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	implementation(project(":starter"))
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-aop")

	testImplementation("org.mockito:mockito-core:5.13.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
	testImplementation("org.wiremock:wiremock:3.9.1")
	testImplementation("org.testcontainers:testcontainers:1.20.2")
	testImplementation("org.testcontainers:junit-jupiter:1.20.2")

	testImplementation("org.wiremock:wiremock-jetty12:3.9.1")
	testImplementation("org.wiremock:wiremock-standalone:3.9.1")
	testImplementation("org.wiremock.integrations.testcontainers:wiremock-testcontainers-module:1.0-alpha-14")
	testImplementation("org.eclipse.jetty:jetty-servlet:11.0.24")
	testImplementation("org.eclipse.jetty:jetty-servlets:11.0.24")
	testImplementation("org.eclipse.jetty:jetty-webapp:11.0.24")
	testImplementation("org.eclipse.jetty.http2:http2-server:11.0.24")

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
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
		}
	}))
}