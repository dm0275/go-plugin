package com.fussionlabs.gradle

import com.fussionlabs.gradle.tasks.BuildTask
import com.fussionlabs.gradle.tasks.GoTask
import com.fussionlabs.gradle.tasks.InstallTask
import com.fussionlabs.gradle.tasks.TestTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

class GoPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        // Create plugin extension + apply the base plugin
        val extension = project.extensions.create(GO_PLUGIN_EXTENSION, PluginExtension::class.java)
        extension.moduleName.convention(project.name)
        project.plugins.apply("base")

        val assembleTask = project.tasks.named("assemble")
        val checkTask = project.tasks.named("check")

        // Setup install task
        project.tasks.register(GO_INSTALL_TASK, InstallTask::class.java) {
            it.group = GO_PLUGIN_GROUP
            it.description = "Install Golang"
            it.configuredGoVersion.set(extension.goVersion)
            it.defaultGoVersion.set(extension.defaultGoVersion)
            it.projectRoot.set(project.layout.projectDirectory)
        }

        project.tasks.withType(GoTask::class.java).configureEach {
            it.configuredGoVersion.set(extension.goVersion)
            it.defaultGoVersion.set(extension.defaultGoVersion)
            it.projectRoot.set(project.layout.projectDirectory)
        }
        project.tasks.withType(BuildTask::class.java).configureEach {
            it.ldFlagsConfig.set(extension.ldFlags)
            it.cgoEnabled.set(extension.cgoEnabled)
            it.extraBuildArgs.set(extension.extraBuildArgs)
        }
        project.tasks.withType(TestTask::class.java).configureEach {
            it.extraTestArgs.set(extension.extraTestArgs)
        }

        val testTask = project.tasks.register("test", TestTask::class.java) {
            it.group = GO_PLUGIN_GROUP
            it.description = "Run tests"
        }
        checkTask.configure { it.dependsOn(testTask) }

        val buildTasks = mutableMapOf<Pair<String, String>, TaskProvider<BuildTask>>()

        fun registerBuildTask(osType: String, archType: String) {
            val key = osType to archType
            val existing = buildTasks[key]
            if (existing != null) {
                existing.configure { it.enabled = true }
                assembleTask.configure { it.dependsOn(existing) }
                return
            }

            val task = project.tasks.register(buildTaskName(osType, archType), BuildTask::class.java) {
                it.group = GO_PLUGIN_GROUP
                it.description = "Build $osType $archType"
                it.os.set(osType)
                it.arch.set(archType)
                it.outputBinary.set(project.layout.buildDirectory.file(
                    extension.moduleName.map { name -> "$name-$osType-$archType" }
                ))
            }
            buildTasks[key] = task
            assembleTask.configure { it.dependsOn(task) }
        }

        fun deactivateBuildTask(osType: String, archType: String) {
            val task = buildTasks[osType to archType] ?: return
            task.configure { it.enabled = false }
        }

        extension.targetOs.whenObjectAdded { osType -> extension.targetArch.forEach { registerBuildTask(osType, it) } }
        extension.targetOs.whenObjectRemoved { osType -> extension.targetArch.forEach { deactivateBuildTask(osType, it) } }
        extension.targetArch.whenObjectAdded { archType -> extension.targetOs.forEach { registerBuildTask(it, archType) } }
        extension.targetArch.whenObjectRemoved { archType -> extension.targetOs.forEach { deactivateBuildTask(it, archType) } }

        extension.targetOs.add(defaultTarget(project, "GOOS") { hostOs(project) })
        extension.targetArch.add(defaultTarget(project, "GOARCH") { hostArch(project) })
    }

    private fun buildTaskName(osType: String, archType: String) =
        "goBuild${osType.taskSuffix()}${archType.taskSuffix()}"

    private fun String.taskSuffix() =
        replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

    private fun defaultTarget(project: Project, environmentVariable: String, fallback: () -> String): String {
        return project.providers.environmentVariable(environmentVariable).orNull
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: fallback()
    }

    private fun hostOs(project: Project): String {
        val host = project.providers.systemProperty("os.name").orNull.orEmpty().lowercase()
        return when {
            host.contains("mac") || host.contains("darwin") -> "darwin"
            host.startsWith("windows") -> "windows"
            else -> "linux"
        }
    }

    private fun hostArch(project: Project): String {
        return when (val arch = project.providers.systemProperty("os.arch").orNull.orEmpty().lowercase()) {
            "x86_64", "amd64" -> "amd64"
            "aarch64", "arm64" -> "arm64"
            else -> arch
        }
    }
}
