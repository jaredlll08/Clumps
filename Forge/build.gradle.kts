import com.blamejared.modtemplate.Utils
import groovy.namespace.QName
import groovy.util.Node
import groovy.util.NodeList
import net.darkhax.curseforgegradle.TaskPublishCurseForge
import net.darkhax.curseforgegradle.Constants as CFG_Constants

plugins {
    `maven-publish`
    id("net.minecraftforge.gradle") version ("5.1.+")
    id("org.parchmentmc.librarian.forgegradle") version ("1.+")
    id("org.spongepowered.mixin") version ("0.7-SNAPSHOT")
    id("com.blamejared.modtemplate")
    id("net.darkhax.curseforgegradle") version ("1.0.8")
}

val modVersion: String by project
val minecraftVersion: String by project
val forgeVersion: String by project
val forgeAtsEnabled: String by project
val modName: String by project
val modAuthor: String by project
val modId: String by project
val modAvatar: String by project
val curseProjectId: String by project
val curseHomepageLink: String by project
val gitFirstCommit: String by project
val gitRepo: String by project
val modJavaVersion: String by project

val baseArchiveName = "${modName}-forge-${minecraftVersion}"

version = Utils.updatingSemVersion(modVersion)
base {
    archivesName.set(baseArchiveName)
}

mixin {
    add(sourceSets.main.get(), "${modId}.refmap.json")
    config("${modId}.mixins.json")
}

minecraft {
    mappings("official", "1.19.1")

    runs {
        create("client") {
            taskName("Client")
            workingDirectory(project.file("run"))
            ideaModule("${rootProject.name}.${project.name}.main")
            args("-mixin.config=${modId}.mixins.json")
            mods {
                create(modId) {
                    source(sourceSets.main.get())
                    source(project(":Common").sourceSets.main.get())
                }
            }
        }
        create("server") {
            taskName("Server")
            workingDirectory(project.file("run_server"))
            ideaModule("${rootProject.name}.${project.name}.main")
            args("-mixin.config=${modId}.mixins.json", "nogui")
            mods {
                create(modId) {
                    source(sourceSets.main.get())
                    source(project(":Common").sourceSets.main.get())
                }
            }
        }

        create("data") {
            taskName("Data")
            workingDirectory(project.file("run"))
            ideaModule("${rootProject.name}.${project.name}.main")
            args(
                    "--mod",
                    modId,
                    "--all",
                    "--output",
                    file("src/generated/resources/"),
                    "--existing",
                    file("src/main/resources/")
            )
            args("-mixin.config=${modId}.mixins.json")
            mods {
                create(modId) {
                    source(sourceSets.main.get())
                    source(project(":Common").sourceSets.main.get())
                }
            }
        }
    }
}

modTemplate {
    mcVersion(minecraftVersion)
    curseHomepage(curseHomepageLink)
    displayName(modName)
    modLoader("Forge")
    changelog {
        enabled(true)
        firstCommit(gitFirstCommit)
        repo(gitRepo)
    }
    versionTracker {
        enabled(true)
        author(modAuthor)
        projectName("${modName}-Forge")
        homepage(curseHomepageLink)
    }
}

sourceSets.main.get().resources.srcDir("src/generated/resources")

dependencies {
    "minecraft"("net.minecraftforge:forge:${minecraftVersion}-${forgeVersion}")
    compileOnly(project(":Common"))
    annotationProcessor("org.spongepowered:mixin:0.8.5-SNAPSHOT:processor")
}

tasks.withType<JavaCompile> {
    source(project(":Common").sourceSets.main.get().allSource)
}

tasks.processResources {
    outputs.upToDateWhen { false }
    from(project(":Common").sourceSets.main.get().resources)

    filesMatching("*.mixins.json") {
        expand("refmap_target" to "${modId}.")
    }
}

tasks {
    jar {
        finalizedBy("reobfJar")
    }
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            artifactId = baseArchiveName
            from(components["java"])

            pom.withXml {
                val depNodeList = asNode()["dependencies"] as NodeList
                depNodeList.map { it as Node }.forEach { depList ->
                    val deps = depList.getAt(QName("http://maven.apache.org/POM/4.0.0", "dependency"))
                    deps.map { it as Node }.forEach { dep ->
                        val versionList = dep.getAt(QName("http://maven.apache.org/POM/4.0.0", "version"))
                        versionList.map { it as Node }.map { it.value() as NodeList }.map { it.text() }
                                .forEach { version ->
                                    if (version.contains("_mapped_")) {
                                        dep.parent().remove(dep)
                                    }
                                }
                    }
                }
            }
        }
    }

    repositories {
        maven("file://${System.getenv("local_maven")}")
    }
}

tasks.create<TaskPublishCurseForge>("publishCurseForge") {
    apiToken = Utils.locateProperty(project, "curseforge_api_token") ?: 0

    val mainFile = upload(curseProjectId, file("${project.buildDir}/libs/$baseArchiveName-$version.jar"))
    mainFile.changelogType = "markdown"
    mainFile.changelog = Utils.getFullChangelog(project)
    mainFile.releaseType = CFG_Constants.RELEASE_TYPE_RELEASE
    mainFile.addJavaVersion("Java $modJavaVersion")

    doLast {
        project.ext.set("curse_file_url", "${curseHomepageLink}/files/${mainFile.curseFileId}")
    }
}