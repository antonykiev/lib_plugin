plugins {
    kotlin("jvm") version "2.0.21"
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
}

group = "org.pet.project"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    // Use external processor module
    ksp(project(":processor"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}