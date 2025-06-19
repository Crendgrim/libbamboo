pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.cassian.cc")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7-alpha.16"
}

var fabricVersions = linkedSetOf(   "1.20.1", "1.20.4", "1.20.6", "1.21.1", "1.21.3", "1.21.5", "1.21.6")
var forgeVersions = linkedSetOf(    "1.20.1", "1.20.4", "1.20.6", "1.21.1", "1.21.3", "1.21.5", "1.21.6")
var neoforgeVersions = linkedSetOf(           "1.20.4", "1.20.6", "1.21.1", "1.21.3", "1.21.5", "1.21.6")

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true
    create(rootProject) {
        // Root `src/` functions as the 'common' project
        versions(fabricVersions + forgeVersions + neoforgeVersions)
        branch("fabric") { versions(fabricVersions) }
        branch("forge") { versions(forgeVersions) }
        branch("neoforge") { versions(neoforgeVersions) }
    }
}

rootProject.name = "libbamboo"