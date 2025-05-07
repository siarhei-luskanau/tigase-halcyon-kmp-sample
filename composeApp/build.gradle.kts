import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.buildConfig)
}

kotlin {
    jvmToolchain(libs.versions.build.jvmTarget.get().toInt())
    androidTarget {
        // https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    jvm()

    js {
        browser()
        binaries.executable()
    }

//    wasmJs {
//        browser()
//        binaries.executable()
//    }

    listOf(
        // iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.runtime)
            implementation(libs.tigase.halcyon.core)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(libs.androidx.activityCompose)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }

        jsMain.dependencies {
            implementation(compose.html.core)
        }
    }
}

android {
    namespace = "tigase.halcyon.kmp.sample"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        targetSdk = 36

        applicationId = "tigase.halcyon.kmp.sample.androidApp"
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.testLogging {
                    exceptionFormat = TestExceptionFormat.FULL
                    events = TestLogEvent.values().toSet()
                }
            }
        }
        animationsDisabled = true
        @Suppress("UnstableApiUsage")
        managedDevices.localDevices.create("managedVirtualDevice") {
            device = "Pixel 2"
            apiLevel = 33
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Halcyon Multiplatform Sample App"
            packageVersion = "1.0.0"

            linux {
                iconFile.set(project.file("desktopAppIcons/LinuxIcon.png"))
            }
            windows {
                iconFile.set(project.file("desktopAppIcons/WindowsIcon.ico"))
            }
            macOS {
                iconFile.set(project.file("desktopAppIcons/MacosIcon.icns"))
                bundleID = "tigase.halcyon.kmp.sample.desktopApp"
            }
        }
    }
}

buildConfig {
    packageName(android.namespace.orEmpty())
    useKotlinOutput {
        topLevelConstants = true
        internalVisibility = false
    }
    val xmppServerAddress = System.getProperty("XMPP_SERVER_ADDRESS") ?: "localhost"
    buildConfigField("String", "XMPP_SERVER_ADDRESS", "\"$xmppServerAddress\"")
    val xmppServerPort = 5222
    buildConfigField("Int", "XMPP_SERVER_PORT", "$xmppServerPort")
    val userJID = "user1@localhost"
    buildConfigField("String", "USER_JID", "\"$userJID\"")
    val userPassword = "user1password"
    buildConfigField("String", "USER_PASSWORD", "\"$userPassword\"")
}
