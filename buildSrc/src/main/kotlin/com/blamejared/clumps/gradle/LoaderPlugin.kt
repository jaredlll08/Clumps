package com.blamejared.clumps.gradle

import com.blamejared.modtemplate.ModTemplatePlugin
import com.blamejared.modtemplate.Utils
import com.blamejared.modtemplate.extensions.ModTemplateExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
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
        applyModTemplate(project)
    }

    private fun applyJavaPlugin(project: Project) {
        val commonJava = commonJava(project)

        project.tasks {
            withType<ProcessResources> {
                from(commonJava.sourceSets.getByName("main").resources)
            }

            withType<JavaCompile> {
                source(commonJava(project).sourceSets.getByName("main").allSource)
            }

            withType<Javadoc> {
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

    private fun applyModTemplate(project: Project) {

        project.plugins.apply(ModTemplatePlugin::class.java)

        with(project.extensions.getByType(ModTemplateExtension::class.java)) {
            mcVersion(Versions.MINECRAFT)
            curseHomepage(Properties.CURSE_HOMEPAGE)
            displayName(Properties.NAME)
            modLoader(project.name)
            changelog {
                // Don't register the task since we will never use it, but the properties are used
                enabled(false)
                firstCommit(Properties.FIRST_COMMIT)
                repo(Properties.GIT_REPO)
            }

            versionTracker {
                enabled(true)
                endpoint(Utils.locateProperty(project, "versionTrackerAPI"))
                author(Properties.AUTHOR)
                projectName("${Properties.NAME}-${project.name}")
                homepage(Properties.CURSE_HOMEPAGE)
                uid(Utils.locateProperty(project, "versionTrackerKey"))
            }

        }
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