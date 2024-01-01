package com.fussionlabs.gradle

import com.fussionlabs.gradle.tasks.BuildTask
import com.fussionlabs.gradle.tasks.TestTask
import com.fussionlabs.gradle.utils.PluginUtils.ext
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized

class GoPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        // Create plugin extension
        project.extensions.create(GO_PLUGIN_EXTENSION, PluginExtension::class.java)

        // Apply the base plugin
        project.plugins.apply("base")

        // Configure the moduleName
        if (project.ext.moduleName.isEmpty()) {
            project.ext.moduleName = project.name
        }

        project.afterEvaluate {
            val buildTask = project.tasks.getByName("build")

            // Setup build tasks
            project.ext.os.forEach { osType ->
                project.ext.arch.forEach { archType ->
                    val task = project.tasks.register("goBuild${osType.capitalized()}${archType.capitalized()}", BuildTask::class.java) {
                        it.group = GO_PLUGIN_GROUP
                        it.description = "Build $osType $archType"

                        // Configure Inputs
                        it.os = osType
                        it.arch = archType
                        it.ldFlagsConfig = project.ext.ldFlags

                        // Configure output
                        it.outputBinary = "${project.ext.moduleName}-$osType-$archType"
                    }
                    buildTask.dependsOn(task)
                }
            }

            // Setup test task
            project.tasks.register("test", TestTask::class.java) {
                it.group = GO_PLUGIN_GROUP
                it.description = "Run tests"
            }
        }
    }
}