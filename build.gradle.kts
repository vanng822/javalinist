import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.3.70"
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "com.javalinist"
version = "1.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.javalin:javalin:3.7.0")
    implementation("org.slf4j:slf4j-simple:1.6.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
    // template
    implementation("org.thymeleaf:thymeleaf:3.0.9.RELEASE")
    // inmemory db
    implementation("org.jetbrains.exposed", "exposed-core", "0.22.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.22.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.22.1")
    implementation("com.h2database:h2:1.4.200")
    // markdown
    implementation("com.vladsch.flexmark:flexmark-all:0.60.2")
    // openapi
    implementation("io.swagger.core.v3:swagger-core:2.0.9")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.1")
    implementation("cc.vileda:kotlin-openapi3-dsl:0.20.2")
    implementation("org.webjars:swagger-ui:3.24.3")
    // testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.1")
    testImplementation("com.konghq:unirest-java:3.4.00")
    testImplementation("org.assertj:assertj-core:3.11.1")
}

application {
    mainClassName = "com.javalinist.JavalinistApplicationKt"
}

tasks {
    named<ShadowJar>("shadowJar") {
        mergeServiceFiles()
        manifest {
            attributes["Main-Class"] = application.mainClassName
        }
    }
}

tasks {
    "build" {
        dependsOn(shadowJar)
    }
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
