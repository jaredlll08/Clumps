import com.blamejared.Properties
import com.blamejared.Versions
import com.blamejared.gradle.mod.utils.GMUtils
import net.darkhax.curseforgegradle.TaskPublishCurseForge
import net.darkhax.curseforgegradle.Constants as CFG_Constants

plugins {
    id("blamejared-modloader-conventions")
    id("net.minecraftforge.gradle") version ("[6.0.24,6.2)")
    id("org.spongepowered.mixin") version ("0.7-SNAPSHOT")
    id("com.modrinth.minotaur")
}

mixin {
    config("${Properties.MODID}.mixins.json")
}

minecraft {
    mappings("official", Versions.MINECRAFT)
    reobf = false
    runs {
        create("client") {
            taskName("Client")
            workingDirectory(project.file("run"))
            ideaModule("${rootProject.name}.${project.name}.main")
            args("-mixin.config=${Properties.MODID}.mixins.json")
            mods {
                create(Properties.MODID) {
                    source(sourceSets.main.get())
                }
            }
        }
    }
}

dependencies {
    "minecraft"("net.minecraftforge:forge:${Versions.MINECRAFT}-${Versions.FORGE}")
    annotationProcessor("org.spongepowered:mixin:0.8.5-SNAPSHOT:processor")
    implementation("net.sf.jopt-simple:jopt-simple:5.0.4") { version { strictly("5.0.4") } }
}

sourceSets.configureEach {
    val dir = layout.buildDirectory.dir("sourcesSets/$this.name")
    this.output.setResourcesDir(dir)
    this.java.destinationDirectory.set(dir)
}

tasks.create<TaskPublishCurseForge>("publishCurseForge") {
    dependsOn(tasks.jar)
    apiToken = GMUtils.locateProperty(project, "curseforgeApiToken") ?: 0

    val mainFile = upload(Properties.CURSE_PROJECT_ID, tasks.jar.get().archiveFile)
    mainFile.changelogType = "markdown"
    mainFile.changelog = GMUtils.smallChangelog(project, Properties.GIT_REPO)
    mainFile.releaseType = CFG_Constants.RELEASE_TYPE_RELEASE
    mainFile.addJavaVersion("Java ${Versions.JAVA}")
    mainFile.addGameVersion(Versions.MINECRAFT)
    mainFile.addModLoader("Forge")

    doLast {
        project.ext.set("curse_file_url", "${Properties.CURSE_HOMEPAGE}/files/${mainFile.curseFileId}")
    }
}

modrinth {
    token.set(GMUtils.locateProperty(project, "modrinth_token"))
    projectId.set(Properties.MODRINTH_PROJECT_ID)
    changelog.set(GMUtils.smallChangelog(project, Properties.GIT_REPO))
    versionName.set("Forge-${Versions.MINECRAFT}-$version")
    versionType.set("release")
    gameVersions.set(listOf(Versions.MINECRAFT))
    uploadFile.set(tasks.jar.get())
    loaders.add("forge")
}
tasks.modrinth.get().dependsOn(tasks.jar)