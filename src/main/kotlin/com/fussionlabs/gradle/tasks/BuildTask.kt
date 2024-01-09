package com.fussionlabs.gradle.tasks

import com.fussionlabs.gradle.utils.PluginUtils.ext
import com.fussionlabs.gradle.utils.PluginUtils.toInt
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile

open class BuildTask: GoTask() {
    @Input
    var os = ""

    @Input
    var arch = ""

    @Input
    var ldFlagsConfig = mapOf<String, String>()

    @InputFiles
    var inputFiles = project.fileTree(project.rootDir)
        .matching{ matchingFile ->
            matchingFile.include("**/**.go")
        }

    @OutputFile
    var outputBinary = ""

    override fun exec() {
        // Setup task environment
        goTaskEnv["GOOS"] = os
        goTaskEnv["GOARCH"] = arch
        goTaskEnv["CGO_ENABLED"] = project.ext.cgoEnabled.toInt()

        // Setup build dir
        val buildDir = project.layout.buildDirectory.get().asFile
        buildDir.mkdirs()

        // Configure build args
        val buildArgs = mutableListOf("build")

        // Configure ldFlags
        var ldFlags = ""
        if (ldFlagsConfig.isNotEmpty()) {
            ldFlags += "-ldflags="
            ldFlagsConfig.forEach { (key, value) ->
                ldFlags += " -X '$key=\"$value\"' "
            }
            buildArgs.add(ldFlags)
        }

        // Configure output
        buildArgs.addAll(listOf("-o", "$buildDir/$outputBinary"))

        // Add extraTestArgs (if defined)
        project.ext.extraBuildArgs.forEach { testArg ->
            buildArgs.add(testArg)
        }

        // Configure Project DIR
        buildArgs.add("${project.rootDir}")
        goTaskArgs = buildArgs

        super.exec()
    }
}