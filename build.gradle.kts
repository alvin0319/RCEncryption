import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.minjae.rcencryption"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "opencollab-repo-snapshot"
        url = uri("https://repo.opencollab.dev/maven-snapshots/")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly("cn.nukkit:nukkit:1.0-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks {
    shadowJar {
        archiveClassifier.set("")
    }
}
