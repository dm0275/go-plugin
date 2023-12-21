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
```
Go tasks
--------
goBuildDarwinAmd64 - Build darwin amd64 binary
goBuildDarwinArm64 - Build darwin arm64 binary
goBuildLinuxAmd64 - Build linux amd64 binary
goBuildLinuxArm64 - Build linux arm64 binary
```