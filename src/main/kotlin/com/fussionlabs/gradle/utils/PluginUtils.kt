package com.fussionlabs.gradle.utils

import com.fussionlabs.gradle.PluginExtension
import org.gradle.api.Project

object PluginUtils {
    val Project.ext: PluginExtension
        get() = this.extensions.getByType(PluginExtension::class.java)

    fun Boolean.toInt(): Int {
        return if (this) 1 else 0
    }

    fun binaryExists(binary: String): Boolean {
        val process = Runtime.getRuntime().exec("which $binary")
        process.waitFor()
        return process.exitValue() == 0
    }
}