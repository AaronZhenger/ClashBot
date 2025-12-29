plugins {
    id("java")
    id("com.diffplug.spotless") version "8.1.0"
}

group = "com.discord"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.2.0") { // replace $version with the latest version
        // Optionally disable audio natives to reduce jar size by excluding `opus-java`
        // Gradle DSL:
        // exclude module: 'opus-java'
        // Kotlin DSL:
        // exclude(module="opus-java")

    }
    implementation("org.json:json:20230227")
}



subprojects {
    apply(plugin = "com.diffplug.spotless")
}