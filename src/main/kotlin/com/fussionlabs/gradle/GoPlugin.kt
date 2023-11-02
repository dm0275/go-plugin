package com.fussionlabs.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class GoPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        // Create plugin extension
        project.extensions.create("go", PluginExtension::class.java)
    }

}