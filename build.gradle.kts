plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.serialization) apply false
}

group = "com.github.youfeng11"

// 配置所有子项目
subprojects {
    apply(plugin = "maven-publish")
    group = "com.github.youfeng11.kotlinx"
}