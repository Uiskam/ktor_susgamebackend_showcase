
val ktor_version: String by project
val kotlin_version: String by project
val cbor_version = "1.5.1"

plugins {
    kotlin("jvm") version "1.9.23"
    id("io.ktor.plugin") version "2.3.11"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-websockets:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$cbor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:$cbor_version")
}

tasks.test {
    useJUnitPlatform()
}
tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
kotlin {
    jvmToolchain(21)
}