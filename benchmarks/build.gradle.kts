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
import com.ngapps.phototime.configureFlavors

plugins {
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.phototime.android.test)
}

android {
    namespace = "com.ngapps.phototime.benchmarks"

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "APP_BUILD_TYPE_SUFFIX", "\"\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        // This benchmark buildType is used for benchmarking, and should function like your
        // release build (for example, with minification on). It's signed with a debug key
        // for easy local/CI testing.
        create("benchmark") {
            // Keep the build type debuggable so we can attach a debugger if needed.
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks.add("release")
            buildConfigField(
                "String",
                "APP_BUILD_TYPE_SUFFIX",
                "\"${PtBuildType.BENCHMARK.applicationIdSuffix ?: ""}\""
            )
        }
    }

    // Use the same flavor dimensions as the application to allow generating Baseline Profiles on prod,
    // which is more close to what will be shipped to users (no fake data), but has ability to run the
    // benchmarks on demo, so we benchmark on stable data. 
    configureFlavors(this) { flavor ->
        buildConfigField(
            "String",
            "APP_FLAVOR_SUFFIX",
            "\"${flavor.applicationIdSuffix ?: ""}\""
        )
    }

    testOptions.managedDevices.devices {
        create<com.android.build.api.dsl.ManagedVirtualDevice>("pixel6Api33") {
            device = "Pixel 6"
            apiLevel = 33
            systemImageSource = "aosp"
        }
    }

    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

baselineProfile {
    // This specifies the managed devices to use that you run the tests on.
    managedDevices += "pixel6Api33"

    // Don't use a connected device but rely on a GMD for consistency between local and CI builds.
    useConnectedDevices = false

}

dependencies {
    implementation(libs.androidx.benchmark.macro)
    implementation(libs.androidx.test.core)
    implementation(libs.androidx.test.espresso.core)
    implementation(libs.androidx.test.ext)
    implementation(libs.androidx.test.rules)
    implementation(libs.androidx.test.runner)
    implementation(libs.androidx.test.uiautomator)
}

androidComponents {
    beforeVariants {
        it.enable = it.buildType == "benchmark"
    }
}
