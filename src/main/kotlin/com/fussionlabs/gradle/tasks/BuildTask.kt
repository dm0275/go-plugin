package com.fussionlabs.gradle.tasks

import com.fussionlabs.gradle.utils.PluginUtils.ext
import com.fussionlabs.gradle.utils.PluginUtils.toInt
import org.gradle.api.tasks.Input

open class BuildTask: GoTask() {
    @Input
    var os = ""

    @Input
    var arch = ""

    override fun exec() {
        // Setup task environment
        environment("GOOS", os)
        environment("GOARCH", arch)
        environment("CGO_ENABLED", project.ext.cgoEnabled.toInt())

        // Setup build dir
        val buildDir = project.layout.buildDirectory.get().asFile
        buildDir.mkdirs()

        // Configure build args
        goTaskArgs = "build "

        // Configura ldFlags
        if (project.ext.ldFlags.isNotEmpty()) {
            goTaskArgs += "-ldflags=\""
            project.ext.ldFlags.forEach { key, value ->
                goTaskArgs += " -X \'$key=$value\' "
            }
            goTaskArgs += "\" "
        }

        // Configure output
        goTaskArgs += "-o $buildDir/${project.ext.moduleName}-$os-$arch ${project.rootDir}"

        super.exec()
    }
}