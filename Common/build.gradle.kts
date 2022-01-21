import com.blamejared.modtemplate.Utils

val minecraftVersion: String by project
val modName: String by project
val modVersion: String by project

plugins {
    `java-library`
    `maven-publish`
    id("com.blamejared.modtemplate")
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
}

val baseArchiveName = "${modName}-common-${minecraftVersion}"
version = Utils.updatingSemVersion(modVersion)

base {
    archivesName.set(baseArchiveName)
}

minecraft {
    version(minecraftVersion)
    runs {
        client("Common Client") {
            workingDirectory(project.file("run"))
        }
        server("Common Server") {
            workingDirectory(project.file("run_server"))
        }
    }
}

dependencies {
    compileOnly("org.spongepowered:mixin:0.8.5")
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            artifactId = baseArchiveName
            from(components["java"])
        }
    }

    repositories {
        maven("file://${System.getenv("local_maven")}")
    }
}