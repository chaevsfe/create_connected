plugins {
    id("net.fabricmc.fabric-loom") version "1.16-SNAPSHOT"
    `maven-publish`
}

group = property("maven_group") as String
version = "${property("mod_version")}+fabric-mc${property("minecraft_version")}"

base {
    archivesName.set(property("archives_base_name") as String)
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://api.modrinth.com/maven") {
        content { includeGroup("maven.modrinth") }
    }
}

loom {
    splitEnvironmentSourceSets()
    mods {
        create("create_connected") {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets["client"])
        }
    }
}

sourceSets {
    main {
        resources.srcDir("src/generated/resources")
        java.exclude(
            "com/hlysine/create_connected/ConnectedLang.java",
            "com/hlysine/create_connected/CreateConnected.java",
            "com/hlysine/create_connected/CreateConnectedClient.java",
            "com/hlysine/create_connected/compat/**",
            "com/hlysine/create_connected/config/**",
            "com/hlysine/create_connected/content/**",
            "com/hlysine/create_connected/datagen/**",
            "com/hlysine/create_connected/foundation/**",
            "com/hlysine/create_connected/mixin/**",
            "com/hlysine/create_connected/ponder/**",
            "com/hlysine/create_connected/registries/**",
        )
    }
    named("client") {
        java.exclude(
            "com/hlysine/create_connected/**",
        )
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
    implementation("maven.modrinth:create-fly:${property("create_fabric_version")}")

    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(25)
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    inputs.property("version", version)
    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version.toString(),
            "minecraft_dependency_version" to project.property("minecraft_dependency_version") as String,
            "fabric_loader_version" to project.property("fabric_loader_version") as String,
            "create_fabric_version_range" to project.property("create_fabric_version_range") as String,
        )
    }
}

tasks.jar {
    from("LICENSE")
}
