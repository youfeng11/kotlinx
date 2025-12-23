plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.serialization)
}

version = "0.2.0"

kotlin {
    explicitApi()

    androidLibrary {
        namespace = "com.youfeng.kotlinx.serialization.json5"
        compileSdk = 36
        minSdk = 24

        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    // JVM 平台
    jvm {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }

        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    // JavaScript 平台
    js(IR) {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        nodejs()
    }

    // WebAssembly
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)  // 新注解
    wasmJs {
        browser()
        nodejs()
    }

    // Native 平台 - iOS
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    // Native 平台 - macOS
    macosX64()
    macosArm64()

    // Native 平台 - watchOS
    watchosArm32()
    watchosArm64()
    watchosX64()
    watchosSimulatorArm64()
    watchosDeviceArm64()

    // Native 平台 - tvOS
    tvosArm64()
    tvosX64()
    tvosSimulatorArm64()

    // Native 平台 - Linux
    linuxX64()
    linuxArm64()

    // Native 平台 - Windows
    mingwX64()

    // Native 平台 - Android Native
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()

    sourceSets {
        // ===== Common =====
        commonMain {
            dependencies {
                api(libs.kotlinx.serialization.json)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        // ===== JVM =====
        jvmTest {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }

        // ===== JS =====
        jsTest {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

// 配置发布
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "kotlinx-serialization-json5"
            version = project.version.toString()

            pom {
                name.set("Kotlinx Serialization JSON5")
                description.set("JSON5 support for kotlinx.serialization")
                url.set("https://github.com/youfeng11/kotlinx-serialization-json5")
                
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                
                developers {
                    developer {
                        id.set("youfeng11")
                        name.set("由风")
                        email.set("youfeng11@outlook.com")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/youfeng/kotlinx-serialization-json5.git")
                    developerConnection.set("scm:git:ssh://github.com/youfeng11/kotlinx-serialization-json5.git")
                    url.set("https://github.com/youfeng11/kotlinx-serialization-json5")
                }
            }
        }
    }
}