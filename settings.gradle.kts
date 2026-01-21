pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "8.9.1"
        id("com.android.library") version "8.9.1"
        id("org.jetbrains.kotlin.android") version "1.9.24"
        id("com.google.gms.google-services") version "4.4.2"
        id("com.google.devtools.ksp") version "1.9.24-1.0.20"
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "QuestMaster"
include(":app")
 