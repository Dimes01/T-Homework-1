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
//val slf4jVersion = "2.0.16"
//val logbackVersion = "1.5.7"
dependencies {
    jmh("org.openjdk.jmh:jmh-core:$versionJmh")
    jmh("org.openjdk.jmh:jmh-generator-annprocess:$versionJmh")
    jmh("org.openjdk.jmh:jmh-generator-bytecode:$versionJmh")

    implementation("com.rabbitmq:amqp-client:5.22.0")
    implementation("org.apache.kafka:kafka-clients:3.9.0")

//    implementation("org.slf4j:slf4j-api:$slf4jVersion")
//    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}