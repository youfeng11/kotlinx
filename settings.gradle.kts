rootProject.name = "youfeng_kotlinx_serialization_json5"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    maven { setUrl("https://jitpack.io") }
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io")
    maven { url = uri("https://nodejs.org/dist") } 
    maven { url = uri("https://repo.gradle.org/gradle/libs") }
  }
}

include(":kotlinx-serialization-json5")