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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class MarkerState(geoPoint: GeoPoint = GeoPoint(0.0, 0.0), rotation: Float = 0f) {
    var geoPoint: GeoPoint by mutableStateOf(geoPoint)
    var rotation: Float by mutableFloatStateOf(0f)

    private val markerState: MutableState<Marker?> = mutableStateOf(null)

    var marker: Marker?
        get() = markerState.value
        set(value) {
            if (markerState.value == null && value == null) return
            if (markerState.value != null && value != null) {
                error("MarkerState may only be associated with one Marker at a time.")
            }
            markerState.value = value
        }

    companion object {
        val Saver: Saver<MarkerState, Pair<GeoPoint, Float>> = Saver(
            save = {
                   Pair(it.geoPoint, it.rotation)
            },
            restore = { MarkerState(it.first, it.second) }
        )
    }
}

@Composable
fun rememberMarkerState(
    key: String? = null,
    geoPoint: GeoPoint = GeoPoint(0.0, 0.0),
    rotation: Float = 0f
): MarkerState = rememberSaveable(key = key, saver = MarkerState.Saver) {
    MarkerState(geoPoint, rotation)
}