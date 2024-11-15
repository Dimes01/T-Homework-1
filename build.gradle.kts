plugins {
    id("java")
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
    apply(plugin = "jacoco")

    dependencies {
        compileOnly("org.projectlombok:lombok:$lombokVersion")
        annotationProcessor ("org.projectlombok:lombok:$lombokVersion")

        implementation("org.slf4j:slf4j-api:$slf4jVersion")
        implementation("ch.qos.logback:logback-classic:$logbackVersion")

        testImplementation("org.mockito:mockito-core:$mockitoVersion")

        testImplementation(platform("org.junit:junit-bom:$junitVersion"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }
}

tasks.test {
    useJUnitPlatform()
}