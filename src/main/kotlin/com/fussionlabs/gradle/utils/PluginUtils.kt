package com.fussionlabs.gradle.utils

import org.gradle.platform.base.Binary

object PluginUtils {

    fun binaryExists(binary: String): Boolean {
        val process = Runtime.getRuntime().exec("which $binary")
        process.waitFor()
        return process.exitValue() == 0
    }
}