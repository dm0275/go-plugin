package com.fussionlabs.gradle

import com.fussionlabs.gradle.tasks.BuildTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class GoPluginTest {

    @TempDir
    lateinit var tempDir: File

    @Test
    private fun project(): Project {
        val project = ProjectBuilder.builder().withProjectDir(tempDir).build()
        project.pluginManager.apply(GoPlugin::class.java)
        return project
    }

    @Test
    fun defaultTasksExistImmediately() {
        val project = project()
        val extension = project.extensions.getByType(PluginExtension::class.java)

        val expectedTasks = listOf("installGo", "test", buildTaskName(extension.os.single(), extension.arch.single()))
        expectedTasks.forEach { expectedTask ->
            assertNotNull(project.tasks.findByName(expectedTask))
        }
    }

    @Test
    fun buildTasksUseExtensionProperties() {
        val project = project()
        val extension = project.extensions.getByType(PluginExtension::class.java)
        val os = extension.os.single()
        val arch = extension.arch.single()
        val task = project.tasks.named(buildTaskName(os, arch), BuildTask::class.java).get()

        assertEquals(os, task.os.get())
        assertEquals(arch, task.arch.get())
        assertFalse(task.cgoEnabled.get())
        assertTrue(task.outputBinary.get().asFile.path.endsWith("${project.name}-$os-$arch"))
    }

    @Test
    fun replacingTargetsDisablesObsoleteTasks() {
        val project = project()
        val extension = project.extensions.getByType(PluginExtension::class.java)
        val defaultTask = buildTaskName(extension.os.single(), extension.arch.single())

        extension.os = listOf("freebsd")
        extension.arch = listOf("386")

        assertTrue(project.tasks.named("goBuildFreebsd386").get().enabled)
        assertFalse(project.tasks.named(defaultTask).get().enabled)

    }

    @Test
    fun addingTargetRegistersMatchingTasks() {
        val project = project()
        val extension = project.extensions.getByType(PluginExtension::class.java)

        extension.addOs("freebsd")

        assertNotNull(project.tasks.findByName(buildTaskName("freebsd", extension.arch.single())))
    }

    private fun buildTaskName(os: String, arch: String): String =
        "goBuild${os.replaceFirstChar { it.uppercase() }}${arch.replaceFirstChar { it.uppercase() }}"
}
