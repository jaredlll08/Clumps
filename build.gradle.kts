import com.blamejared.Properties
import com.blamejared.Versions
import com.blamejared.gradle.mod.utils.GMUtils
import com.diluv.schoomp.Webhook
import com.diluv.schoomp.message.Message
import com.diluv.schoomp.message.embed.Embed
import java.io.IOException
import java.util.*

plugins {
    `java-library`
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
}

version = GMUtils.updatingVersion(Versions.MOD)

tasks.create("postDiscord") {
    val taskName = "publishCurseForge"
    dependsOn(":fabric:${taskName}", ":forge:${taskName}", ":neoforge:${taskName}")
    doLast {
        try {

            // Create a new webhook instance for Discord
            val webhook = Webhook(GMUtils.locateProperty(project, "discordCFWebhook"), "${Properties.NAME} CurseForge Gradle Upload")

            // Craft a message to send to Discord using the webhook.
            val message = Message()
            message.username = Properties.NAME
            message.avatarUrl = Properties.AVATAR
            message.content = "${Properties.NAME} $version for Minecraft ${Versions.MINECRAFT} has been published!"

            val embed = Embed()
            val downloadSources = StringJoiner("\n")

            mapOf(Pair("fabric", "<:fabric:932163720568782878>"), Pair("forge", "<:forge:932163698003443804>"), Pair("neoforge", "<:neoforged:1184738260371644446>"))
                    .filter {
                        project(":${it.key}").ext.has("curse_file_url")
                    }.map { "${it.value} [${it.key.capitalize(Locale.ENGLISH)}](${project(":${it.key}").ext.get("curse_file_url")})" }
                    .forEach { downloadSources.add(it) }

            listOf("common", "fabric", "forge", "neoforge")
                    .map { project(":${it}") }
                    .map { "<:maven:932165250738970634> `\"${it.group}:${it.base.archivesName.get()}:${it.version}\"`" }
                    .forEach { downloadSources.add(it) }

            // Add Curseforge DL link if available.
            val downloadString = downloadSources.toString()

            if (downloadString.isNotEmpty()) {

                embed.addField("Download", downloadString, false)
            }

            embed.addField("Changelog", GMUtils.smallChangelog(project, Properties.GIT_REPO).take(1000), false)

            embed.color = 0xF16436
            message.addEmbed(embed)

            webhook.sendMessage(message)
        } catch (e: IOException) {

            project.logger.error("Failed to push CF Discord webhook.")
            project.file("post_discord_error.log").writeText(e.stackTraceToString())
        }
    }

}