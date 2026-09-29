package com.fussionlabs.gradle.tasks

import com.fussionlabs.gradle.GO_BINARY
import com.fussionlabs.gradle.GO_INSTALL_TASK
import com.fussionlabs.gradle.GO_SETUP_DIR
import com.fussionlabs.gradle.GRADLE_FILES_DIR
import com.fussionlabs.gradle.utils.PluginUtils.goInstalled
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.AbstractExecTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

abstract class GoTask: AbstractExecTask<GoTask>(GoTask::class.java) {
    /**
     * Command arguments are assembled by concrete tasks immediately before the
     * process runs. BuildTask and TestTask expose the values that compose these
     * arguments as individual inputs.
     */
    @get:Internal
    abstract val goTaskArgs: ListProperty<String>

    @get:Internal
    abstract val goTaskEnv: MapProperty<String, String>

    @get:Input
    abstract val configuredGoVersion: Property<String>

    @get:Input
    abstract val defaultGoVersion: Property<String>

    @get:Internal
    abstract val projectRoot: DirectoryProperty

    init {
        dependsOn(GO_INSTALL_TASK)
        goTaskArgs.convention(emptyList())
        goTaskEnv.convention(emptyMap())
    }

    override fun exec() {
        val goVersion = configuredGoVersion.get().ifEmpty {
            defaultGoVersion.get()
        }
        val goBinary = if (goInstalled() && configuredGoVersion.get().isEmpty()) {
            GO_BINARY
        } else {
            "${projectRoot.get().asFile}/$GRADLE_FILES_DIR/$GO_SETUP_DIR-$goVersion/go/bin/$GO_BINARY"
        }

        if (goBinary != GO_BINARY) {
            goTaskEnv.put("GOROOT", "${projectRoot.get().asFile}/$GRADLE_FILES_DIR/$GO_SETUP_DIR-$goVersion/go")
        }

        executable = goBinary
        args = goTaskArgs.get()
        goTaskEnv.get().forEach { (key, value) ->
            environment(key, value)
        }

        logger.info("goTaskEnv: ${goTaskEnv.get()}")
        logger.info("goTaskArgs: ${goTaskArgs.get()}")

        super.exec()
    }
}
