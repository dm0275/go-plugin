package com.fussionlabs.gradle.tasks

import com.fussionlabs.gradle.GO_BINARY
import com.fussionlabs.gradle.GO_INSTALL_TASK
import com.fussionlabs.gradle.GO_SETUP_DIR
import com.fussionlabs.gradle.GRADLE_FILES_DIR
import com.fussionlabs.gradle.utils.PluginUtils.binaryExists
import com.fussionlabs.gradle.utils.PluginUtils.ext
import com.fussionlabs.gradle.utils.PluginUtils.goBinary
import org.gradle.api.GradleException
import org.gradle.api.tasks.AbstractExecTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

open class GoTask: AbstractExecTask<GoTask>(GoTask::class.java) {
    @Input
    var goTaskArgs: MutableList<String> = mutableListOf()

    @Internal
    var goTaskEnv: MutableMap<String, Any> = mutableMapOf()

    init {
        dependsOn(GO_INSTALL_TASK)
    }

    override fun exec() {
        val goVersion = project.ext.goVersion
        val goBinary = goBinary(project)

        // Configure GOROOT (if needed)
        if (goBinary != GO_BINARY) {
            goTaskEnv["GOROOT"] = "${project.rootDir}/$GRADLE_FILES_DIR/$GO_SETUP_DIR-$goVersion/go"
        }

        executable = goBinary
        args = goTaskArgs
        goTaskEnv.forEach { (key, value) ->
            environment(key, value)
        }

        logger.info("goTaskEnv: $goTaskEnv")
        logger.info("goTaskArgs: $goTaskArgs")

        super.exec()
    }
}