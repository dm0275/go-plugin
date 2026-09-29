package com.fussionlabs.gradle

import org.gradle.api.DomainObjectSet
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

open class PluginExtension @Inject constructor(objects: ObjectFactory) {
    val moduleName: Property<String> = objects.property(String::class.java)
    val cgoEnabled: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(false)
    /**
     * Target operating systems. Kept as a mutable Kotlin property for source
     * compatibility with `os = listOf(...)` build scripts.
     */
    var os: List<String>
        get() = targetOs.toList()
        set(values) = replace(targetOs, values)

    /**
     * Target architectures. Kept as a mutable Kotlin property for source
     * compatibility with `arch = listOf(...)` build scripts.
     */
    var arch: List<String>
        get() = targetArch.toList()
        set(values) = replace(targetArch, values)

    internal val targetOs: DomainObjectSet<String> = objects.domainObjectSet(String::class.java)
    internal val targetArch: DomainObjectSet<String> = objects.domainObjectSet(String::class.java)
    val goVersion: Property<String> = objects.property(String::class.java).convention("")
    val defaultGoVersion: Property<String> = objects.property(String::class.java).convention(DEFAULT_GO_VERSION)
    val ldFlags: MapProperty<String, String> = objects.mapProperty(String::class.java, String::class.java).convention(emptyMap())
    val extraBuildArgs: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())
    val extraTestArgs: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

    /** Adds one operating system without replacing existing targets. */
    fun addOs(value: String) = targetOs.add(value)

    /** Adds one architecture without replacing existing targets. */
    fun addArch(value: String) = targetArch.add(value)

    private fun replace(set: DomainObjectSet<String>, values: Iterable<String>) {
        val replacement = values.toSet()
        set.toList().filter { it !in replacement }.forEach(set::remove)
        replacement.filter { it !in set }.forEach(set::add)
    }
}
