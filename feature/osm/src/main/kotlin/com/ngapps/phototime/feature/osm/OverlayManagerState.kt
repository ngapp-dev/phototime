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

package com.ngapps.phototime.feature.osm

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.OverlayManager

@SuppressLint("MutableCollectionMutableState")
class OverlayManagerState(private var _overlayManager: OverlayManager?) {

    val overlayManager: OverlayManager
        get() = _overlayManager
            ?: throw IllegalStateException("Invalid Map attached!, please add other overlay in OpenStreetMap#onFirstLoadListener")

    private var _mapView: MapView? = null
    fun setMap(mapView: MapView) {
        _overlayManager = mapView.overlayManager
    }

    fun getMap(): MapView {
        return _mapView ?: throw IllegalStateException("Invalid Map attached!")
    }
}

@Composable
fun rememberOverlayManagerState() = remember {
    OverlayManagerState(null)
}