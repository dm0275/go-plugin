package com.fussionlabs.gradle.tasks

import com.fussionlabs.gradle.utils.PluginUtils.toInt
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile

abstract class BuildTask: GoTask() {
    @get:Input
    abstract val os: Property<String>

    @get:Input
    abstract val arch: Property<String>

    @get:Input
    abstract val ldFlagsConfig: MapProperty<String, String>

    @get:Input
    abstract val cgoEnabled: Property<Boolean>

    @get:Input
    abstract val extraBuildArgs: ListProperty<String>

    @get:InputFiles
    abstract val inputFiles: ConfigurableFileCollection

    @get:OutputFile
    abstract val outputBinary: RegularFileProperty

    init {
        inputFiles.from(project.fileTree(project.projectDir) { it.include("**/*.go") })
    }

    override fun exec() {
        goTaskEnv.put("GOOS", os.get())
        goTaskEnv.put("GOARCH", arch.get())
        goTaskEnv.put("CGO_ENABLED", cgoEnabled.get().toInt().toString())
        outputBinary.get().asFile.parentFile.mkdirs()
        val buildArgs = mutableListOf("build")

        val flags = ldFlagsConfig.get()
        if (flags.isNotEmpty()) {
            var ldFlags = "-ldflags="
            flags.forEach { (key, value) ->
                ldFlags += " -X '$key=\"$value\"' "
            }
            buildArgs.add(ldFlags)
        }

        buildArgs.addAll(listOf("-o", outputBinary.get().asFile.absolutePath))
        buildArgs.addAll(extraBuildArgs.get())
        buildArgs.add(projectRoot.get().asFile.absolutePath)
        goTaskArgs.set(buildArgs)

        super.exec()
    }
}
