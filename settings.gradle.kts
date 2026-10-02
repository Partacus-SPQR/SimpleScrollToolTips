pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
        maven("https://maven.kikugie.dev/releases")
    }
    plugins {
        id("fabric-loom") version "1.16.3"
        id("dev.kikugie.stonecutter") version "0.5.1"
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.5.1"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    create(rootProject) {
        versions("26.1", "26.1.2", "26.2", "26.3")
        vcsVersion = "26.2"
    }
}

rootProject.name = "simplescrolltooltips"
