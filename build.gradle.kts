import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.70"
    id("java")
    id("application")
}

group = "com.javalinist"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compile("io.javalin:javalin:3.7.0")
    compile("org.slf4j:slf4j-simple:1.6.1")
    compile("com.fasterxml.jackson.core:jackson-databind:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
}

application {
    mainClassName = "com.javalinist.JavalinistApplicationKt"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xopt-in=kotlin.RequiresOptIn")
        jvmTarget = "1.8"
    }
}
