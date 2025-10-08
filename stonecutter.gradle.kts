plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.11-SNAPSHOT" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("me.modmuss50.mod-publish-plugin") version "0.8.1" apply false
}

stonecutter active file("active.stonecutter")

stonecutter parameters {
    constants {
        match(node.metadata.project.substringAfterLast("-"), "fabric", "neoforge", "forge")
    }
}

stonecutter tasks {
//    order("publishMods")
}

/*
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
*/