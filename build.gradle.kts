import com.blamejared.modtemplate.Utils
import com.diluv.schoomp.Webhook
import com.diluv.schoomp.message.Message
import com.diluv.schoomp.message.embed.Embed
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.diluv.schoomp:Schoomp:1.2.5")
    }
}
plugins {
    `java-library`
    idea
    id("com.blamejared.modtemplate") version ("3.0.0.37")
}

val minecraftVersion: String by project
val modName: String by project
val modAuthor: String by project
val modJavaVersion: String by project
val modAvatar: String by project
val modVersion: String by project
val gitRepo: String by project

version = Utils.updatingSemVersion(modVersion)

subprojects {

    apply(plugin = "java")

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(modJavaVersion))
        withJavadocJar()
        withSourcesJar()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(modJavaVersion.toInt())
    }

    tasks {

        jar {
            manifest {
                attributes(
                        "Specification-Title" to modName,
                        "Specification-Vendor" to modAuthor,
                        "Specification-Version" to archiveVersion,
                        "Implementation-Title" to project.name,
                        "Implementation-Version" to archiveVersion,
                        "Implementation-Vendor" to modAuthor,
                        "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
                        "Timestamp" to System.currentTimeMillis(),
                        "Built-On-Java" to "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})",
                        "Build-On-Minecraft" to minecraftVersion
                )
            }
        }
    }

    repositories {

        mavenCentral()

        maven("https://repo.spongepowered.org/repository/maven-public/") {
            name = "Sponge / Mixin"
        }

        maven("https://maven.blamejared.com") {
            name = "BlameJared Maven (CrT / Bookshelf)"
        }
    }

    dependencies {
        implementation("org.jetbrains:annotations:21.0.1")
    }

    tasks.withType<GenerateModuleMetadata>() {

        enabled = false
    }
}

tasks.create("postDiscord") {

    doLast {
        try {

            // Create a new webhook instance for Discord
            val webhook = Webhook(Utils.locateProperty(project, "discordCFWebhook"), "$modName CurseForge Gradle Upload")

            // Craft a message to send to Discord using the webhook.
            val message = Message()
            message.username = modName
            message.avatarUrl = modAvatar
            message.content = "$modName $version for Minecraft $minecraftVersion has been published!"

            val embed = Embed()
            val downloadSources = StringJoiner("\n")

            if (project(":Fabric").ext.has("curse_file_url")) {

                downloadSources.add("<:fabric:932163720568782878> [Fabric](${project(":Fabric").ext.get("curse_file_url")})")
            }

            if (project(":Forge").ext.has("curse_file_url")) {

                downloadSources.add("<:forge:932163698003443804> [Forge](${project(":Forge").ext.get("curse_file_url")})")
            }

            downloadSources.add(
                    "<:maven:932165250738970634> `\"com.blamejared.clumps:${project(":Common").base.archivesName.get()}:${
                        project(":Common").version
                    }\"`"
            )
            downloadSources.add(
                    "<:maven:932165250738970634> `\"com.blamejared.clumps:${project(":Fabric").base.archivesName.get()}:${
                        project(":Fabric").version
                    }\"`"
            )
            downloadSources.add(
                    "<:maven:932165250738970634> `\"com.blamejared.clumps:${project(":Forge").base.archivesName.get()}:${
                        project(":Forge").version
                    }\"`"
            )

            // Add Curseforge DL link if available.
            val downloadString = downloadSources.toString()

            if (downloadString.isNotEmpty()) {

                embed.addField("Download", downloadString, false)
            }

            // Just use the Forge changelog for now, the files are the same anyway.
            embed.addField("Changelog", Utils.getCIChangelog(project, gitRepo).take(1000), false)

            embed.color = 0xF16436
            message.addEmbed(embed)

            webhook.sendMessage(message)
        } catch (e: IOException) {

            project.logger.error("Failed to push CF Discord webhook.")
            project.file("post_discord_error.log").writeText(e.stackTraceToString())
        }
    }

}