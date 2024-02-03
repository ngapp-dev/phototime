/*
 * Copyright 2024 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.ngapps.phototime.PtBuildType

plugins {
    alias(libs.plugins.phototime.android.application)
    alias(libs.plugins.phototime.android.application.compose)
    alias(libs.plugins.phototime.android.application.flavors)
    alias(libs.plugins.phototime.android.application.jacoco)
    alias(libs.plugins.phototime.android.hilt)
    id("jacoco")
    alias(libs.plugins.phototime.android.application.firebase)
    id("com.google.android.gms.oss-licenses-plugin")
    alias(libs.plugins.baselineprofile)
}

android {
    defaultConfig {

        val versionMajor = 0
        val versionMinor = 5
        val versionPatch = 0

        applicationId = "com.ngapps.phototime"
        versionCode = versionMajor * 10000 + versionMinor * 100 + versionPatch
        versionName =
            "$versionMajor.$versionMinor.${versionPatch}" // X.Y.Z; X = Major, Y = minor, Z = Patch level

        // NOTE: Custom test runner to set up Hilt dependency graph
        testInstrumentationRunner = "com.ngapps.phototime.core.testing.SitTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = PtBuildType.DEBUG.applicationIdSuffix
            signingConfig = signingConfigs.getByName("debug")
        }
        val release = getByName("release") {
            isMinifyEnabled = true
            applicationIdSuffix = PtBuildType.RELEASE.applicationIdSuffix
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            // NOTE: To publish on the Play store a private signing key is required, but to allow anyone
            //  who clones the code to sign and run the release variant, use the debug signing key.
            // TODO: Abstract the signing configuration to a separate file to avoid hardcoding this.
            signingConfig = signingConfigs.getByName("debug")
            // Ensure Baseline Profile is fresh for release builds.
            baselineProfile.automaticGenerationDuringBuild = true
        }
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    namespace = "com.ngapps.phototime"
}

dependencies {
    implementation(projects.feature.locations)
    implementation(projects.feature.contacts)
    implementation(projects.feature.home)
    implementation(projects.feature.tasks)
    implementation(projects.feature.notes)
    implementation(projects.feature.auth)
    implementation(projects.feature.user.profile)
    implementation(projects.feature.user.settings)
    implementation(projects.feature.search)

    implementation(projects.core.common)
    implementation(projects.core.ui)
    implementation(projects.core.designsystem)
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.core.analytics)

    implementation(projects.sync.work)

    androidTestImplementation(projects.core.testing)
    androidTestImplementation(projects.core.datastoreTest)
    androidTestImplementation(projects.core.dataTest)
    androidTestImplementation(projects.core.network)
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.accompanist.testharness)
    androidTestImplementation(kotlin("test"))
    debugImplementation(libs.androidx.compose.ui.testManifest)
    debugImplementation(projects.uiTestHiltManifest)

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.compose.material3.adaptive) {
        this.isTransitive = false
    }
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite) {
        this.isTransitive = false
    }
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.window.manager)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.coil.kt)

    baselineProfile(project(":benchmarks"))

    // Core functions
    testImplementation(projects.core.testing)
    testImplementation(projects.core.datastoreTest)
    testImplementation(projects.core.dataTest)
    testImplementation(projects.core.network)
    testImplementation(libs.androidx.navigation.testing)
    testImplementation(libs.accompanist.testharness)
    testImplementation(libs.work.testing)
    testImplementation(kotlin("test"))
    kspTest(libs.hilt.compiler)
}
baselineProfile {
    // Don't build on every iteration of a full assemble.
    // Instead enable generation directly for the release build variant.
    automaticGenerationDuringBuild = false
}
