pluginManagement {
    repositories {
        maven("https://maven.blamejared.com")
        maven("https://maven.neoforged.net/releases")
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        maven("https://repo.spongepowered.org/repository/maven-public/") {
            name = "Sponge Snapshots"
        }
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "Clumps"
include("common")
include("fabric")
include("forge")
include("neoforge")