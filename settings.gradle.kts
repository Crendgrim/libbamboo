pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.kikugie.dev/snapshots")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7.9"
}

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true
    create(rootProject) {
        fun mc(version: String, vararg loaders: String) {
            for (it in loaders) {
                version("$version-$it", version)
            }
        }
        mc("1.20.1", "fabric", "forge")
        mc("1.21.1", "fabric", "forge", "neoforge")
        mc("1.21.3", "fabric", "forge", "neoforge")
        mc("1.21.5", "fabric", "forge", "neoforge")
        mc("1.21.6", "fabric", "forge", "neoforge")
        mc("1.21.9", "fabric", "forge", "neoforge")
    }
}

rootProject.name = "libbamboo"