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

import com.ngapps.phototime.FlavorDimension
import com.ngapps.phototime.SitFlavor

plugins {
    alias(libs.plugins.phototime.android.application)
    alias(libs.plugins.phototime.android.application.compose)
}

android {
    defaultConfig {

        val versionMajor = 0
        val versionMinor = 0
        val versionPatch = 1

        applicationId = "com.ngapps.phototime.ptcatalog"
        versionCode = versionMajor * 10000 + versionMinor * 100 + versionPatch
        versionName = "$versionMajor.$versionMinor.${versionPatch}" // X.Y.Z; X = Major, Y = minor, Z = Patch level

        // NOTE: The UI catalog does not depend on content from the app, however, it depends on modules
        //  which do, so we must specify a default value for the contentType dimension.
        missingDimensionStrategy(FlavorDimension.contentType.name, SitFlavor.demo.name)
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    namespace = "com.ngapps.phototime.ptcatalog"

    buildTypes {
        release {
            // NOTE: To publish on the Play store a private signing key is required, but to allow anyone
            //  who clones the code to sign and run the release variant, use the debug signing key.
            // TODO: Abstract the signing configuration to a separate file to avoid hardcoding this.
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.ui)
    implementation(libs.androidx.activity.compose)
}
