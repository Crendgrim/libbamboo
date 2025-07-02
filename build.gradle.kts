@file:Suppress("UnstableApiUsage")

plugins {
    kotlin("jvm") version "2.1.21"
    id("com.google.devtools.ksp") version "2.1.21-2.0.2"
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.7"
    id("dev.kikugie.fletching-table.neoforge") version "0.1.0-alpha.7"
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom")
    id("com.github.johnrengelman.shadow")
    id("maven-publish")
}

val minecraft = stonecutter.current.version
val loader = Loader.of(stonecutter.current.project)
class ModDependencies {
    operator fun get(name: String) = property("deps.$name").toString()
}
val deps = ModDependencies()
enum class DependencyLevel {
    Include,
    Implementation,
    CompileOnly
}
fun modDependency(modId: String, url: String, level: DependencyLevel) {
    val isPresent = !deps[modId].startsWith("[")
    stonecutter {
        constants {
            put(modId, isPresent)
        }
    }

    if (isPresent) {
        val resolvedUrl = url.replace("{}", deps[modId])
        dependencies {
            when (level) {
                DependencyLevel.Include -> {
                    modImplementation(resolvedUrl)
                    include(resolvedUrl)
                }

                DependencyLevel.Implementation -> modImplementation(resolvedUrl)
                DependencyLevel.CompileOnly -> modCompileOnly(resolvedUrl)
            }
        }
    }
}

version = "${mod.version}+$minecraft"
group = "${mod.group}.$loader"
base {
    archivesName.set(mod.id)
}

val shadowBundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

repositories {
    mavenLocal()
    maven("https://repo.spongepowered.org/maven")

    //strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
    //strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")

    when (loader) {
        Loader.Forge -> {
            maven("https://maven.minecraftforge.net")
            maven("https://thedarkcolour.github.io/KotlinForForge/")
            maven("https://maven.su5ed.dev/releases") // Forgified Fabric API
        }

        Loader.NeoForge -> {
            maven("https://maven.neoforged.net/releases/")
            maven("https://thedarkcolour.github.io/KotlinForForge/")
            maven("https://maven.su5ed.dev/releases") // Forgified Fabric API
        }

        Loader.Fabric -> {
            maven("https://maven.terraformersmc.com/")
        }
    }

    // load this after KotlinForForge
    maven("https://maven.isxander.dev/releases")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings(loom.layered {
        mappings("net.fabricmc:yarn:$minecraft+build.${mod.dep("yarn_build")}:v2")
        mod.dep("neoforge_patch").takeUnless { it.startsWith('[') }?.let {
            mappings("dev.architectury:yarn-mappings-patch-neoforge:$it")
        }
    })

    when (loader) {
        Loader.Fabric -> {
            modImplementation("net.fabricmc:fabric-loader:${mod.dep("fabric_loader")}")
        }
        Loader.Forge -> {
            "forge"("net.minecraftforge:forge:$minecraft-${mod.dep("forge_loader")}")
            "io.github.llamalad7:mixinextras-common:${mod.dep("mixin_extras")}".let {
                annotationProcessor(it)
                compileOnly(it)
            }
            "io.github.llamalad7:mixinextras-forge:${mod.dep("mixin_extras")}".let {
                implementation(it)
                include(it)
            }
            if (stonecutter.eval(minecraft, ">=1.21.6")) {
                annotationProcessor("net.minecraftforge:eventbus-validator:7.0-beta.7")
            }
        }
        Loader.NeoForge -> {
            "neoForge"("net.neoforged:neoforge:${mod.dep("neoforge_loader")}")
        }
    }

    modDependency("fabric_api", "net.fabricmc.fabric-api:fabric-api:{}", DependencyLevel.Implementation)
    modDependency("modmenu", "com.terraformersmc:modmenu:{}", DependencyLevel.Implementation)
    modDependency("yacl", "dev.isxander:yet-another-config-lib:{}", DependencyLevel.Implementation)
    modDependency("forgified_fabric_api",
        if (loader.isForge()) "dev.su5ed.sinytra.fabric-api:fabric-api:{}"
        else "org.sinytra.forgified-fabric-api:forgified-fabric-api:{}",
        DependencyLevel.Implementation)
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/${mod.id}.accesswidener")

    if (loader.isForge()) {
        forge.convertAccessWideners = true
        forge.mixinConfigs(
            "${mod.id}.mixins.json"
        )
    }

    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }
}

java {
    withSourcesJar()
    val java = if (stonecutter.eval(minecraft, ">=1.20.5"))
        JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.remapJar {
    injectAccessWidener = true
    inputFile = tasks.shadowJar.get().archiveFile
    archiveClassifier = loader.toString()
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    configurations = listOf(shadowBundle)
    archiveClassifier = "dev-shadow"
    when (loader) {
        Loader.Fabric -> exclude("META-INF", "pack.mcmeta", "architectury.common.json")
        Loader.Forge -> exclude("fabric.mod.json", "META-INF/neoforge.mods.toml", "architectury.common.json")
        Loader.NeoForge -> exclude( "fabric.mod.json", "META-INF/mods.toml", "architectury.common.json")
    }
}

tasks.processResources {
    properties(
        listOf("fabric.mod.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml", "pack.mcmeta"),
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "minecraft" to mod.prop("mc_dep")
    )
}

fletchingTable {
    mixins.create("main") {
        default = "${mod.id}.mixins.json"
    }
    fabric {
        applyMixinConfig = false
    }
    neoforge {
        applyMixinConfig = false
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = mod.prop("group")
            artifactId = mod.prop("id")
            version = "${mod.version}+${minecraft}-${loader}"

            artifact(tasks.remapJar.get().archiveFile)
            artifact(tasks.remapSourcesJar.get().archiveFile) {
                classifier = "sources"
            }
        }
    }
}
