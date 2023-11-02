package com.fussionlabs.gradle.tasks

import com.fussionlabs.gradle.utils.PluginUtils.binaryExists
import org.gradle.api.GradleException
import org.gradle.api.tasks.AbstractExecTask
import org.gradle.api.tasks.Input

open class GoTask: AbstractExecTask<GoTask>(GoTask::class.java) {
    @Input
    var goTaskArgs: String = ""

    init {
        if (!binaryExists("go")) {
            throw GradleException("Go is either not installed or included in the PATH")
        }
    }

    override fun exec() {
        executable = "sh"
        args = mutableListOf("-c", "go $goTaskArgs")

        super.exec()
    }
}