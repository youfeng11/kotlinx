plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.serialization) apply false
}

allprojects {
    group = "com.youfeng.kotlinx"
}

// 配置所有子项目
subprojects {
    apply(plugin = "maven-publish")

    // 配置发布任务
    tasks.withType<PublishToMavenRepository> {
        dependsOn(tasks.withType<Sign>())
    }
}