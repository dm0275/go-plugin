package com.fussionlabs.gradle.utils

import com.fussionlabs.gradle.GO_BINARY
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.GradleException
import org.gradle.api.Project
import java.io.File
import java.io.IOException
import java.net.URL

object PluginUtils {
    fun Boolean.toInt(): Int {
        return if (this) 1 else 0
    }

    fun binaryExists(binary: String): Boolean {
        val process = Runtime.getRuntime().exec("which $binary")
        process.waitFor()
        return process.exitValue() == 0
    }

    fun goInstalled(): Boolean {
        return binaryExists(GO_BINARY)
    }

    fun getOs(): String {
        return when {
            Os.isFamily(Os.FAMILY_MAC) -> "darwin"
            Os.isFamily(Os.FAMILY_WINDOWS) -> "windows"
            else -> "linux"
        }
    }

    fun getArch(): String {
        val arch = System.getProperty("os.arch")
        return if (arch == "x86_64") {
            "amd64"
        } else if (arch == "aarch64") {
            "arm64"
        } else {
            arch
        }
    }

    fun downloadFile(url: String, outfile: File) {
        try {
            val binaryInputStream = URL(url).openStream()
            binaryInputStream.use { inputStream ->
                outfile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: IOException) {
            throw GradleException("There was an issue downloading file. ERROR: $e")
        }
    }

    fun extractTarGz(project: Project, tarFile: File, destinationDir: File) {
        if (tarFile.exists() && destinationDir.exists()) {
            project.copy {
                it.from(project.tarTree(tarFile))
                it.into(destinationDir)
            }
            tarFile.delete()
        }
    }

}
