package com.fussionlabs.gradle.tasks

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles

abstract class TestTask: GoTask() {
    @get:Input
    abstract val extraTestArgs: ListProperty<String>

    @get:InputFiles
    abstract val inputFiles: ConfigurableFileCollection

    init {
        inputFiles.from(project.fileTree(project.projectDir) { it.include("**/*_test.go") })
    }

    override fun exec() {
        val testArgs = mutableListOf("test")
        testArgs.addAll(extraTestArgs.get())
        testArgs.add("${projectRoot.get().asFile}/...")
        goTaskArgs.set(testArgs)

        super.exec()
    }
}
