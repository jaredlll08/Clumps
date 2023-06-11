package com.blamejared.clumps.gradle

import com.blamejared.gradle.mod.utils.GMUtils
import groovy.namespace.QName
import groovy.util.Node
import groovy.util.NodeList
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.tasks.GenerateModuleMetadata
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.*
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.plugins.ide.idea.model.IdeaModel
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class DefaultPlugin : Plugin<Project> {

    override fun apply(project: Project): Unit = project.run {
        setupDefaults(project)
        applyJavaPlugin(project)
        applyIdeaPlugin(project)
        applyDependencies(project)
        applyMavenPlugin(project)
    }

    private fun setupDefaults(project: Project) {
        project.plugins.apply(BasePlugin::class.java)
        val base = project.extensions.getByType(BasePluginExtension::class.java)

        base.archivesName.set("${Properties.NAME}-${project.name.toLowerCase()}-${Versions.MINECRAFT}")
        project.version = GMUtils.updatingVersion(Versions.MOD)
        project.group = Properties.GROUP

        project.tasks.withType<GenerateModuleMetadata>().all {
            enabled = false
        }

        project.repositories {
            this.add(this.maven("https://repo.spongepowered.org/repository/maven-public/") {
                name = "Sponge"
            })
            this.add(this.maven("https://maven.blamejared.com/") {
                name = "BlameJared"
            })
            this.add(this.maven("https://maven.parchmentmc.org/") {
                name = "ParchmentMC"
            })
        }

    }

    private fun applyJavaPlugin(project: Project) {
        project.plugins.apply(JavaLibraryPlugin::class.java)

        with(project.extensions.getByType(JavaPluginExtension::class.java)) {
            toolchain.languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_17.majorVersion))
            withSourcesJar()
            withJavadocJar()
            sourceSets {
                named("main") {
                    resources {
                        srcDirs.add(project.file("src/generated/resources"))
                    }
                }
            }
        }

        project.tasks {
            named<JavaCompile>("compileTestJava") {
                this.options.isFork = true
                this.options.compilerArgs.add("-XDenableSunApiLintControl")
            }

            withType<JavaCompile> {
                this.options.encoding = StandardCharsets.UTF_8.toString()
                this.options.release.set(Versions.JAVA.toInt())
            }

            withType<Javadoc> {
                this.options.encoding = StandardCharsets.UTF_8.toString()
                options {
                    // Javadoc defines this specifically as StandardJavadocDocletOptions
                    // but only has a getter for MinimalJavadocOptions, but let's just make sure to be safe
                    if (this is StandardJavadocDocletOptions) {
                        this.addStringOption("Xdoclint:none", "-quiet")
                    }
                }
            }

            withType<ProcessResources> {
                outputs.upToDateWhen { false }
                inputs.property("version", project.version)
                filesMatching("*.mixins.json") {
                    if (project.name == "fabric") {
                        expand("refmap_target" to "${project.extensions.getByType(BasePluginExtension::class.java).archivesName.get()}-")
                    } else {
                        expand("refmap_target" to "${Properties.MODID}.")
                    }
                }
                filesMatching("fabric.mod.json") {
                    expand("version" to project.version)
                }
            }

            withType<Jar>().configureEach {
                manifest {
                    attributes["Specification-Title"] = Properties.NAME
                    attributes["Specification-Vendor"] = Properties.AUTHOR
                    attributes["Specification-Version"] = archiveVersion
                    attributes["Implementation-Title"] = project.name
                    attributes["Implementation-Version"] = archiveVersion
                    attributes["Implementation-Vendor"] = Properties.AUTHOR
                    attributes["Implementation-Timestamp"] = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
                    attributes["Timestamp"] = System.currentTimeMillis()
                    attributes["Built-On-Java"] = "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})"
                    attributes["Built-On-Minecraft"] = Versions.MINECRAFT
                }
            }
        }

    }

    private fun applyDependencies(project: Project) {
        val implementation = project.configurations.getByName("implementation")
        val compileOnly = project.configurations.getByName("compileOnly")
        val annotationProcessor = project.configurations.getByName("annotationProcessor")

        implementation.dependencies.add(project.dependencies.create("org.jetbrains:annotations:24.0.1"))
        compileOnly.dependencies.add(project.dependencies.create("com.google.auto.service:auto-service-annotations:1.0.1"))
        annotationProcessor.dependencies.add(project.dependencies.create("com.google.auto.service:auto-service:1.0.1"))
    }

    private fun applyIdeaPlugin(project: Project) {
        project.plugins.apply(IdeaPlugin::class.java)

        val idea = project.extensions.getByType<IdeaModel>()
        idea.module.excludeDirs.addAll(setOf(project.file("run"), project.file("run_server"), project.file("run_client"), project.file("run_game_test")))
    }

    private fun applyMavenPlugin(project: Project) {
        project.plugins.apply(MavenPublishPlugin::class.java)

        val publishing = project.extensions.getByType<PublishingExtension>()
        project.afterEvaluate {
            val base = project.extensions.getByType<BasePluginExtension>()
            publishing.publications.register("mavenJava", MavenPublication::class.java) {
                artifactId = base.archivesName.get()
                from(project.components.getByName("java"))

                if (project.name.equals("forge")) {
                    pom.withXml {
                        val depNodeList = asNode()["dependencies"] as NodeList
                        depNodeList.map { it as Node }.forEach { depList ->
                            val deps = depList.getAt(QName("http://maven.apache.org/POM/4.0.0", "dependency"))
                            deps.map { it as Node }.forEach { dep ->
                                dep.parent().remove(dep)
                            }
                        }
                    }
                }
            }
        }
        publishing.repositories {
            maven("file:///${System.getenv("local_maven")}")
        }
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
