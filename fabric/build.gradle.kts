import com.blamejared.clumps.gradle.Properties
import com.blamejared.clumps.gradle.Versions
import com.blamejared.modtemplate.Utils
import net.darkhax.curseforgegradle.TaskPublishCurseForge
import net.darkhax.curseforgegradle.Constants as CFG_Constants

plugins {
    id("fabric-loom") version "1.1-SNAPSHOT"
    id("com.blamejared.clumps.default")
    id("com.blamejared.clumps.loader")
}

dependencies {
    minecraft("com.mojang:minecraft:${Versions.MINECRAFT}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.18.1:2021.12.19@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${Versions.FABRIC_LOADER}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${Versions.FABRIC}")
    implementation(project(":common"))
}

loom {
    runs {
        named("client") {
            client()
            setConfigName("Fabric Client")
            ideConfigGenerated(true)
            runDir("run")
        }
    }
}

tasks.create<TaskPublishCurseForge>("publishCurseForge") {
    apiToken = Utils.locateProperty(project, "curseforgeApiToken")

    val mainFile = upload(Properties.CURSE_PROJECT_ID, file("${project.buildDir}/libs/${base.archivesName.get()}-$version.jar"))
    mainFile.changelogType = "markdown"
    mainFile.changelog = Utils.getFullChangelog(project)
    mainFile.releaseType = CFG_Constants.RELEASE_TYPE_RELEASE
    mainFile.addJavaVersion("Java ${Versions.JAVA}")
    mainFile.addGameVersion(Versions.MINECRAFT)
    mainFile.addRequirement("fabric-api")

    doLast {
        project.ext.set("curse_file_url", "${Properties.CURSE_HOMEPAGE}/files/${mainFile.curseFileId}")
    }
}
