plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.10.9999" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}
stonecutter active file("active.stonecutter")

// Runs active versions for each loader
for (node in stonecutter.tree.nodes) {
    if (!node.metadata.isActive || node.branch.id.isEmpty()) continue
    for (type in listOf("Client", "Server")) tasks.register("runActive$type${node.branch.id.upperCaseFirst()}") {
        group = "project"
        dependsOn("${node.hierarchy}run$type")
    }
}

allprojects {
    repositories {
        maven("https://thedarkcolour.github.io/KotlinForForge/")
        maven("https://maven.isxander.dev/releases")
    }
}
stonecutter parameters {
    constants {
        match(branch.id, "fabric", "neoforge", "forge")
    }
    listOf(
        "forgified_fabric_api_forge",
        "forgified_fabric_api_neoforge"
    ).map {
        // For e.g. :fabric:1.20.1, use the property of :1.20.1
        it to (node.sibling("") ?: node).project.property("deps.$it").toString()
    }.forEach { (mod, version) ->
        val modIsPresent = !version.startsWith("[");
        constants[mod] = modIsPresent
        dependencies[mod] = parse(if (modIsPresent) version.split("+")[0] else "0")
    }
}
