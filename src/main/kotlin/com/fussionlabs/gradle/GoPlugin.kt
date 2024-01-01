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
            val checkTask = project.tasks.getByName("check")
            val buildTask = project.tasks.getByName("build")

            // Setup build tasks
            project.ext.os.forEach { osType ->
                project.ext.arch.forEach { archType ->
                    val task = project.tasks.register("goBuild${osType.capitalized()}${archType.capitalized()}", BuildTask::class.java) { goBuildTask ->
                        goBuildTask.group = GO_PLUGIN_GROUP
                        goBuildTask.description = "Build $osType $archType"

                        // Configure Inputs
                        goBuildTask.os = osType
                        goBuildTask.arch = archType
                        goBuildTask.ldFlagsConfig = project.ext.ldFlags

                        // Configure output
                        goBuildTask.outputBinary = "${project.ext.moduleName}-$osType-$archType"
                    }
                    buildTask.dependsOn(task)
                }
            }

            // Setup test task
            val testTask = project.tasks.register("test", TestTask::class.java) { testTask ->
                testTask.group = GO_PLUGIN_GROUP
                testTask.description = "Run tests"

            }

            checkTask.dependsOn(testTask)
        }
    }
}