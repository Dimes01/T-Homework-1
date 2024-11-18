plugins {
    id("java")
    id("me.champeau.jmh") version "0.7.2"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val versionJmh = "1.37"
dependencies {
    jmh("org.openjdk.jmh:jmh-core:$versionJmh")
    jmh("org.openjdk.jmh:jmh-generator-annprocess:$versionJmh")
    jmh("org.openjdk.jmh:jmh-generator-bytecode:$versionJmh")

    implementation("com.rabbitmq:amqp-client:5.22.0")
    implementation("org.apache.kafka:kafka-clients:3.9.0")
}

tasks.test {
    useJUnitPlatform()
}