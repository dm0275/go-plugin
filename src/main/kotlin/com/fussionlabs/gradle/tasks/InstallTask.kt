package com.fussionlabs.gradle.tasks

import com.fussionlabs.gradle.GO_SETUP_DIR
import com.fussionlabs.gradle.GRADLE_FILES_DIR
import com.fussionlabs.gradle.utils.PluginUtils
import com.fussionlabs.gradle.utils.PluginUtils.ext
import com.fussionlabs.gradle.utils.PluginUtils.getArch
import com.fussionlabs.gradle.utils.PluginUtils.getOs
import com.fussionlabs.gradle.utils.PluginUtils.goBinary
import com.fussionlabs.gradle.utils.PluginUtils.goInstalled
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class InstallTask: DefaultTask() {
    init {
        onlyIf {
            installGo()
        }
    }

    fun installGo(): Boolean {
        return (!goInstalled() || project.ext.goVersion.isNotEmpty())
    }

    @TaskAction
    fun install() {
        val buildDir = project.layout.buildDirectory.get().asFile
        val goVersion = project.ext.goVersion.ifEmpty {
            project.ext.defaultGoVersion
        }

        val url = "https://go.dev/dl/go${goVersion}.${getOs()}-${getArch()}.tar.gz"
        val outputLocation = "$buildDir/go${goVersion}.${getOs()}-${getArch()}.tar.gz"

        if (!File(goBinary(project)).exists()) {
            val outputFile = File(outputLocation)
            outputFile.createNewFile()

            val destinationDir = File("${project.rootDir}/$GRADLE_FILES_DIR/$GO_SETUP_DIR-$goVersion")
            destinationDir.mkdirs()

            // Download the file
            logger.lifecycle("Downloading Go version $goVersion")
            PluginUtils.downloadFile(url, outputFile)

            // Extract the file
            logger.lifecycle("Extracting archive")
            PluginUtils.extractTarGz(project, outputFile, destinationDir)

            logger.lifecycle("Done")
        }
    }
}