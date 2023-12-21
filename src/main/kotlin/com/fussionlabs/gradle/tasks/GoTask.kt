package com.fussionlabs.gradle.tasks

import com.fussionlabs.gradle.GO_BINARY
import com.fussionlabs.gradle.utils.PluginUtils.binaryExists
import org.gradle.api.GradleException
import org.gradle.api.tasks.AbstractExecTask
import org.gradle.api.tasks.Input

open class GoTask: AbstractExecTask<GoTask>(GoTask::class.java) {
    @Input
    var goTaskArgs: MutableList<String> = mutableListOf()

    init {
        if (!binaryExists(GO_BINARY)) {
            throw GradleException("$GO_BINARY is either not installed or included in the PATH")
        }
    }

    override fun exec() {
        executable = GO_BINARY
        args = goTaskArgs

        super.exec()
    }
}