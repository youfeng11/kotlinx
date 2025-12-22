plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
    explicitApi()

    // JVM 平台
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
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
    @OptIn(org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl::class)
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

        // ===== Native 共享代码 =====
        val nativeMain by creating {
            dependsOn(commonMain.get())
        }

        val nativeTest by creating {
            dependsOn(commonTest.get())
        }

        // Apple 平台
        val appleMain by creating {
            dependsOn(nativeMain)
        }

        val appleTest by creating {
            dependsOn(nativeTest)
        }

        // iOS
        val iosMain by creating {
            dependsOn(appleMain)
        }
        iosX64().compilations["main"].defaultSourceSet.dependsOn(iosMain)
        iosArm64().compilations["main"].defaultSourceSet.dependsOn(iosMain)
        iosSimulatorArm64().compilations["main"].defaultSourceSet.dependsOn(iosMain)

        val iosTest by creating {
            dependsOn(appleTest)
        }
        iosX64().compilations["test"].defaultSourceSet.dependsOn(iosTest)
        iosArm64().compilations["test"].defaultSourceSet.dependsOn(iosTest)
        iosSimulatorArm64().compilations["test"].defaultSourceSet.dependsOn(iosTest)

        // macOS
        val macosMain by creating {
            dependsOn(appleMain)
        }
        macosX64().compilations["main"].defaultSourceSet.dependsOn(macosMain)
        macosArm64().compilations["main"].defaultSourceSet.dependsOn(macosMain)

        val macosTest by creating {
            dependsOn(appleTest)
        }
        macosX64().compilations["test"].defaultSourceSet.dependsOn(macosTest)
        macosArm64().compilations["test"].defaultSourceSet.dependsOn(macosTest)

        // watchOS
        val watchosMain by creating {
            dependsOn(appleMain)
        }
        watchosArm32().compilations["main"].defaultSourceSet.dependsOn(watchosMain)
        watchosArm64().compilations["main"].defaultSourceSet.dependsOn(watchosMain)
        watchosX64().compilations["main"].defaultSourceSet.dependsOn(watchosMain)
        watchosSimulatorArm64().compilations["main"].defaultSourceSet.dependsOn(watchosMain)
        watchosDeviceArm64().compilations["main"].defaultSourceSet.dependsOn(watchosMain)

        val watchosTest by creating {
            dependsOn(appleTest)
        }
        watchosArm32().compilations["test"].defaultSourceSet.dependsOn(watchosTest)
        watchosArm64().compilations["test"].defaultSourceSet.dependsOn(watchosTest)
        watchosX64().compilations["test"].defaultSourceSet.dependsOn(watchosTest)
        watchosSimulatorArm64().compilations["test"].defaultSourceSet.dependsOn(watchosTest)
        watchosDeviceArm64().compilations["test"].defaultSourceSet.dependsOn(watchosTest)

        // tvOS
        val tvosMain by creating {
            dependsOn(appleMain)
        }
        tvosArm64().compilations["main"].defaultSourceSet.dependsOn(tvosMain)
        tvosX64().compilations["main"].defaultSourceSet.dependsOn(tvosMain)
        tvosSimulatorArm64().compilations["main"].defaultSourceSet.dependsOn(tvosMain)

        val tvosTest by creating {
            dependsOn(appleTest)
        }
        tvosArm64().compilations["test"].defaultSourceSet.dependsOn(tvosTest)
        tvosX64().compilations["test"].defaultSourceSet.dependsOn(tvosTest)
        tvosSimulatorArm64().compilations["test"].defaultSourceSet.dependsOn(tvosTest)

        // Linux
        val linuxMain by creating {
            dependsOn(nativeMain)
        }
        linuxX64().compilations["main"].defaultSourceSet.dependsOn(linuxMain)
        linuxArm64().compilations["main"].defaultSourceSet.dependsOn(linuxMain)

        val linuxTest by creating {
            dependsOn(nativeTest)
        }
        linuxX64().compilations["test"].defaultSourceSet.dependsOn(linuxTest)
        linuxArm64().compilations["test"].defaultSourceSet.dependsOn(linuxTest)

        // Windows
        val mingwMain by creating {
            dependsOn(nativeMain)
        }
        mingwX64().compilations["main"].defaultSourceSet.dependsOn(mingwMain)

        val mingwTest by creating {
            dependsOn(nativeTest)
        }
        mingwX64().compilations["test"].defaultSourceSet.dependsOn(mingwTest)

        // Android Native
        val androidNativeMain by creating {
            dependsOn(nativeMain)
        }
        androidNativeArm32().compilations["main"].defaultSourceSet.dependsOn(androidNativeMain)
        androidNativeArm64().compilations["main"].defaultSourceSet.dependsOn(androidNativeMain)
        androidNativeX86().compilations["main"].defaultSourceSet.dependsOn(androidNativeMain)
        androidNativeX64().compilations["main"].defaultSourceSet.dependsOn(androidNativeMain)

        val androidNativeTest by creating {
            dependsOn(nativeTest)
        }
        androidNativeArm32().compilations["test"].defaultSourceSet.dependsOn(androidNativeTest)
        androidNativeArm64().compilations["test"].defaultSourceSet.dependsOn(androidNativeTest)
        androidNativeX86().compilations["test"].defaultSourceSet.dependsOn(androidNativeTest)
        androidNativeX64().compilations["test"].defaultSourceSet.dependsOn(androidNativeTest)
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