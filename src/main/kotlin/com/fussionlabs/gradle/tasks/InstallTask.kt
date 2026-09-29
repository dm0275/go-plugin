package com.fussionlabs.gradle.tasks

import com.fussionlabs.gradle.GO_BINARY
import com.fussionlabs.gradle.GO_SETUP_DIR
import com.fussionlabs.gradle.GRADLE_FILES_DIR
import com.fussionlabs.gradle.utils.PluginUtils
import com.fussionlabs.gradle.utils.PluginUtils.getArch
import com.fussionlabs.gradle.utils.PluginUtils.getOs
import com.fussionlabs.gradle.utils.PluginUtils.goInstalled
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class InstallTask: DefaultTask() {
    @get:Input
    abstract val configuredGoVersion: Property<String>

    @get:Input
    abstract val defaultGoVersion: Property<String>

    @get:Internal
    abstract val projectRoot: DirectoryProperty

    init {
        onlyIf {
            !goInstalled() || configuredGoVersion.get().isNotEmpty()
        }
    }

    @TaskAction
    fun install() {
        val buildDir = project.layout.buildDirectory.get().asFile
        val goVersion = configuredGoVersion.get().ifEmpty {
            defaultGoVersion.get()
        }

        val url = "https://go.dev/dl/go${goVersion}.${getOs()}-${getArch()}.tar.gz"
        val outputLocation = "$buildDir/go${goVersion}.${getOs()}-${getArch()}.tar.gz"

        val goBinary = if (goInstalled() && configuredGoVersion.get().isEmpty()) {
            GO_BINARY
        } else {
            "${projectRoot.get().asFile}/$GRADLE_FILES_DIR/$GO_SETUP_DIR-$goVersion/go/bin/$GO_BINARY"
        }

        if (!File(goBinary).exists()) {
            buildDir.mkdirs()

            val outputFile = File(outputLocation)
            outputFile.createNewFile()

            val destinationDir = File("${projectRoot.get().asFile}/$GRADLE_FILES_DIR/$GO_SETUP_DIR-$goVersion")
            destinationDir.mkdirs()

            logger.lifecycle("Downloading Go version $goVersion")
            logger.info("Source URL: $url")
            logger.info("Destination Path: $destinationDir")
            PluginUtils.downloadFile(url, outputFile)

            logger.lifecycle("Extracting tar.gz archive")
            PluginUtils.extractTarGz(project, outputFile, destinationDir)

            logger.lifecycle("Done")
        }
    }
}
