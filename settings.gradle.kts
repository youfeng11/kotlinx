@file:Suppress("UnstableApiUsage")
rootProject.name = "youfeng_kotlinx_serialization_json5"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}

dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io")
    maven { url = uri("https://repo.gradle.org/gradle/libs") }
  }
}

include(":kotlinx-serialization-json5")