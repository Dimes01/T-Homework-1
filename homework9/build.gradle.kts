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

val wiremockVersion = project.properties["wiremockVersion"]
val junitVersion = project.properties["junitVersion"]
val jettyVersion = project.properties["jettyVersion"]
val testcontainersVersion = project.properties["testcontainersVersion"]
val mockitoVersion = project.properties["mockitoVersion"]
val reactorVersion = project.properties["reactorVersion"]
dependencies {
	implementation(project(":starter"))
	implementation(project(":homework8"))
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("io.projectreactor:reactor-core:$reactorVersion")
	implementation("io.projectreactor:reactor-test:$reactorVersion")

	testImplementation("org.mockito:mockito-core:$mockitoVersion")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
	testImplementation("org.wiremock:wiremock:$wiremockVersion")
	testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
	testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")

	testImplementation("org.wiremock:wiremock-jetty12:$wiremockVersion")
	testImplementation("org.wiremock:wiremock-standalone:$wiremockVersion")
	testImplementation("org.wiremock.integrations.testcontainers:wiremock-testcontainers-module:1.0-alpha-14")
	testImplementation("org.eclipse.jetty:jetty-servlet:$jettyVersion")
	testImplementation("org.eclipse.jetty:jetty-servlets:$jettyVersion")
	testImplementation("org.eclipse.jetty:jetty-webapp:$jettyVersion")
	testImplementation("org.eclipse.jetty.http2:http2-server:$jettyVersion")

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
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