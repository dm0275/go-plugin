package com.fussionlabs.gradle

import com.fussionlabs.gradle.tasks.BuildTask
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

        // Setup build tasks
        project.afterEvaluate {
            val buildTask = project.tasks.getByName("build")
            project.ext.os.forEach { osType ->
                project.ext.arch.forEach { archType ->
                    val task = project.tasks.register("goBuild${osType.capitalized()}${archType.capitalized()}", BuildTask::class.java) {
                        it.group = GO_PLUGIN_GROUP
                        it.description = "Build $osType $archType"

                        it.os = osType
                        it.arch = archType
                    }
                    buildTask.dependsOn(task)
                }
            }
        }
    }
}