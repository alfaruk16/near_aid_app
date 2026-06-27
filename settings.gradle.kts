pluginManagement {
    includeBuild("build-logic")
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
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "NearAid"

include(":app")

// Core modules
include(":core:common")
include(":core:model")
include(":core:designsystem")
include(":core:navigation")
include(":core:datastore")
include(":core:network")
include(":core:database")
include(":core:domain")
include(":core:data")

// Feature modules
include(":feature:auth")
include(":feature:discovery")
include(":feature:post")
include(":feature:activity")
include(":feature:messages")
include(":feature:profile")
