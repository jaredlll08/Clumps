package com.blamejared.clumps.gradle

import com.blamejared.gradle.mod.utils.GradleModUtilsPlugin
import com.blamejared.gradle.mod.utils.extensions.VersionTrackerExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources

class LoaderPlugin : Plugin<Project> {

    override fun apply(project: Project): Unit = project.run {

        applyJavaPlugin(project)
        applyDependencies(project)
        applyGradleModUtils(project)
    }

    private fun applyJavaPlugin(project: Project) {
        val commonJava = commonJava(project)

        project.tasks {
            withType<ProcessResources>().matching { notNeoTask(it) }.configureEach {
                from(commonJava.sourceSets.getByName("main").resources)
            }

            withType<JavaCompile>().matching { notNeoTask(it) }.configureEach {
                source(commonJava(project).sourceSets.getByName("main").allSource)
            }

            withType<Javadoc>().matching { notNeoTask(it) }.configureEach {
                source(commonJava(project).sourceSets.getByName("main").allJava)
            }

            named<Jar>("sourcesJar") {
                from(commonJava(project).sourceSets.getByName("main").allSource)
            }

            if (project.name.equals("forge")) {
                withType(Jar::class.java) {
                    finalizedBy("reobfJar")
                }
            }

        }
    }

    private fun applyDependencies(project: Project) {
    }

    private fun applyGradleModUtils(project: Project) {

        project.plugins.apply(GradleModUtilsPlugin::class.java)

        with(project.extensions.getByType(VersionTrackerExtension::class.java)) {
            mcVersion.set(Versions.MINECRAFT)
            homepage.set(Properties.CURSE_HOMEPAGE)
            author.set(Properties.AUTHOR)
            projectName.set(Properties.NAME)
        }
    }

    private fun notNeoTask(task: Task): Boolean {
        return !task.name.startsWith("neo")
    }

    private fun notCommon(project: Project): Boolean {
        return !project.name.equals("common")
    }

    private fun common(project: Project): Project {
        return project.project(":common")
    }

    private fun commonJava(project: Project): JavaPluginExtension {
        return common(project).extensions.getByType(JavaPluginExtension::class.java)
    }

    private fun depJava(project: Project, other: String): JavaPluginExtension {
        return project.project(other).extensions.getByType(JavaPluginExtension::class.java)
    }
}