import com.blamejared.modtemplate.Utils
import net.darkhax.curseforgegradle.TaskPublishCurseForge
import net.darkhax.curseforgegradle.Constants as CFG_Contants

plugins {
    `maven-publish`
    id("fabric-loom") version "0.12-SNAPSHOT"
    id("com.blamejared.modtemplate")
    id("net.darkhax.curseforgegradle") version ("1.0.8")
}

val modVersion: String by project
val minecraftVersion: String by project
val fabricLoaderVersion: String by project
val modName: String by project
val modAuthor: String by project
val modId: String by project
val modAvatar: String by project
val curseProjectId: String by project
val curseHomepageLink: String by project
val gitFirstCommit: String by project
val gitRepo: String by project
val modJavaVersion: String by project

val baseArchiveName = "${modName}-fabric-${minecraftVersion}"

version = Utils.updatingSemVersion(modVersion)
base {
    archivesName.set(baseArchiveName)
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-1.18.2:2022.06.05@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${fabricLoaderVersion}")
    implementation(project(":Common"))
}

loom {
    runs {
        named("client") {
            client()
            setConfigName("Fabric Client")
            ideConfigGenerated(true)
            runDir("run")
        }
        named("server") {
            server()
            setConfigName("Fabric Server")
            ideConfigGenerated(true)
            runDir("run_server")
        }
    }
}

modTemplate {
    mcVersion(minecraftVersion)
    curseHomepage(curseHomepageLink)
    displayName(modName)
    modLoader("Fabric")
    changelog {
        enabled(true)
        firstCommit(gitFirstCommit)
        repo(gitRepo)
    }
    versionTracker {
        enabled(true)
        author(modAuthor)
        projectName("${modName}-Fabric")
        homepage(curseHomepageLink)
    }
}

tasks.processResources {
    outputs.upToDateWhen { false }
    from(project(":Common").sourceSets.main.get().resources)

    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }

    filesMatching("*.mixins.json") {
        expand("refmap_target" to "$baseArchiveName-")
    }
}

tasks.withType<JavaCompile> {
    source(project(":Common").sourceSets.main.get().allSource)
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

tasks.create<TaskPublishCurseForge>("publishCurseForge") {
    apiToken = Utils.locateProperty(project, "curseforge_api_token")

    val mainFile = upload(curseProjectId, file("${project.buildDir}/libs/$baseArchiveName-$version.jar"))
    mainFile.changelogType = "markdown"
    mainFile.changelog = Utils.getFullChangelog(project)
    mainFile.releaseType = CFG_Contants.RELEASE_TYPE_RELEASE
    mainFile.addJavaVersion("Java $modJavaVersion")
    mainFile.addGameVersion(minecraftVersion)

    doLast {
        project.ext.set("curse_file_url", "${curseHomepageLink}/files/${mainFile.curseFileId}")
    }
}
