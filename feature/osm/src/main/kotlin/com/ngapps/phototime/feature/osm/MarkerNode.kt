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

import org.osmdroid.views.overlay.Marker

internal class MarkerNode(
    val mapView: OsmMapView,
    val markerState: MarkerState,
    val marker: Marker,
    var onMarkerClick: (Marker) -> Boolean
) : OsmAndNode {

    override fun onAttached() {
        markerState.marker = marker
    }

    override fun onRemoved() {
        markerState.marker = null
        marker.remove(mapView)
    }

    override fun onCleared() {
        markerState.marker = null
        marker.remove(mapView)
    }

    fun setupListeners() {
        marker.setOnMarkerClickListener { marker, _ ->
            val click = onMarkerClick.invoke(marker)
            if (marker.isInfoWindowShown) {
                marker.closeInfoWindow()
            } else {
                marker.showInfoWindow()
            }
            click
        }
    }
}