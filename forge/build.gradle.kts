import com.blamejared.clumps.gradle.Properties
import com.blamejared.clumps.gradle.Versions
import com.blamejared.modtemplate.Utils
import net.darkhax.curseforgegradle.TaskPublishCurseForge
import net.darkhax.curseforgegradle.Constants as CFG_Constants

plugins {
    id("com.blamejared.clumps.default")
    id("com.blamejared.clumps.loader")
    id("net.minecraftforge.gradle") version ("5.1.+")
    id("org.spongepowered.mixin") version ("0.7-SNAPSHOT")
}

mixin {
    add(sourceSets.main.get(), "${Properties.MODID}.refmap.json")
    config("${Properties.MODID}.mixins.json")
}

minecraft {
    mappings("official", Versions.MINECRAFT)
    runs {
        create("client") {
            taskName("Client")
            workingDirectory(project.file("run"))
            ideaModule("${rootProject.name}.${project.name}.main")
            args("-mixin.config=${Properties.MODID}.mixins.json")
            mods {
                create(Properties.MODID) {
                    source(sourceSets.main.get())
                    source(project(":common").sourceSets.main.get())
                }
            }
        }
    }
}

dependencies {
    "minecraft"("net.minecraftforge:forge:${Versions.MINECRAFT}-${Versions.FORGE}")
    compileOnly(project(":common"))
    annotationProcessor("org.spongepowered:mixin:0.8.5-SNAPSHOT:processor")
}

tasks.create<TaskPublishCurseForge>("publishCurseForge") {
    apiToken = Utils.locateProperty(project, "curseforgeApiToken") ?: 0

    val mainFile = upload(Properties.CURSE_PROJECT_ID, file("${project.buildDir}/libs/${base.archivesName.get()}-$version.jar"))
    mainFile.changelogType = "markdown"
    mainFile.changelog = Utils.getFullChangelog(project)
    mainFile.releaseType = CFG_Constants.RELEASE_TYPE_RELEASE
    mainFile.addJavaVersion("Java ${Versions.JAVA}")

    doLast {
        project.ext.set("curse_file_url", "${Properties.CURSE_HOMEPAGE}/files/${mainFile.curseFileId}")
    }
}