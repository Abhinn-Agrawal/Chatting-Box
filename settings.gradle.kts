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
    plugins { id("com.google.devtools.ksp") version "1.9.10-1.0.13"}
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven (  "https://storage.zego.im/maven" )   // <- Add this line.
        maven (  "https://www.jitpack.io" )   // <- Add this line.
    }
}

rootProject.name = "Chatting App"
include(":app")
 