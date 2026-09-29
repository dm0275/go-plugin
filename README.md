# Go Plugin
## Overview
The `Go-Plugin` is a Gradle plugin for Go projects. This plugin does not intend to replace Go's native dependency management system, 
instead this plugin focuses on replacing traditional task orchestrators like Make, offering a more versatile and reusable approach for task automation.

## Usage
Add the following to apply the plugin to your project:

**Groovy DSL**:
```groovy
plugins {
    id "com.fussionlabs.gradle.go-plugin" version "$version"
}
```

**Kotling DSL**:
```kotlin
plugins {
    id("com.fussionlabs.gradle.go-plugin") version("$version")
}
```

## Tasks
The go-plugin offers the following built-in tasks:
```
Go tasks
--------
goBuild<Os><Arch> - Build the configured OS/architecture binary
installGo - Install Golang (will only run if Go is not installed locally or if goVersion is defined)
test - Run tests
```

## Configuration
The plugin can be easily configured using an extension with the following customizable fields:

| Field Name       | Type                       | Description                                               | Default Value               |
|------------------|----------------------------|-----------------------------------------------------------|-----------------------------|
| `moduleName`     | `Property<String>`         | Name used for output binaries.                            | project name                |
| `cgoEnabled`     | `Property<Boolean>`        | Enable or disable the `CGO_ENABLED` option for builds.    | `false`                     |
| `os`             | `List<String>`             | Target operating systems.                                 | `GOOS`, then host OS        |
| `arch`           | `List<String>`             | Target architectures.                                     | `GOARCH`, then host CPU     |
| `ldFlags`        | `MapProperty<String, String>` | Set custom ldflags for use during builds.              | empty map                   |
| `goVersion`      | `Property<String>`         | Go version to install.                                    | `1.21.6`                    |
| `extraBuildArgs` | `ListProperty<String>`     | Extra build arguments to pass to `goBuild$Os$Arch` tasks. | empty list                  |
| `extraTestArgs`  | `ListProperty<String>`     | Extra test arguments to `test` task.                      | empty list                  |

`installGo`, `test`, and the default `goBuild*` task are registered as soon as the plugin is applied; no `afterEvaluate` hook is used. Per dimension, an explicit `os` / `arch` assignment takes precedence over `GOOS` / `GOARCH`; those environment variables take precedence over the detected host platform. Use `addOs` / `addArch` to add targets. Gradle does not support unregistering created tasks, so replaced targets are disabled and skipped by `assemble`.

### Example Configuration
```kotlin
go {
    cgoEnabled.set(true)
    os = listOf("linux")
    arch = listOf("amd64")
    goVersion.set("1.20.13")
    ldFlags.set(mapOf("key1" to "value1", "key2" to "value2"))
    extraBuildArgs.set(listOf("arg1", "arg2"))
    extraTestArgs.set(listOf("arg3", "arg4"))
}
```

To add a build target while keeping the defaults:
```kotlin
go {
    addOs("windows")
}
```

## Custom Tasks
In addition to the default tasks, you can create custom Go tasks for basically any Go command:
```kotlin
tasks.register("goVersion", com.fussionlabs.gradle.tasks.GoTask::class.java) {
    goTaskArgs.add("version")
}
```
