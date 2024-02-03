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

import org.osmdroid.events.DelayedMapListener
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay

internal class MapPropertiesNode(
    val mapViewComposed: OsmMapView,
    val mapListeners: MapListeners,
    private val cameraState: CameraState,
    overlayManagerState: OverlayManagerState
) : OsmAndNode {

    private var delayedMapListener: DelayedMapListener? = null
    private var eventOverlay: MapEventsOverlay? = null

    init {
        overlayManagerState.setMap(mapViewComposed)
        cameraState.setMap(mapViewComposed)
    }

    override fun onAttached() {
        mapViewComposed.controller.setCenter(cameraState.geoPoint)
        mapViewComposed.controller.setZoom(cameraState.zoom)

        delayedMapListener = DelayedMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                val currentGeoPoint =
                    mapViewComposed.let { GeoPoint(it.mapCenter.latitude, it.mapCenter.longitude) }
                cameraState.geoPoint = currentGeoPoint
                return false
            }

            override fun onZoom(event: ZoomEvent?): Boolean {
                val currentZoom = mapViewComposed.zoomLevelDouble
                cameraState.zoom = currentZoom
                return false
            }
        }, 1000L)

        mapViewComposed.addMapListener(delayedMapListener)

        val eventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let { mapListeners.onMapClick.invoke(it) }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                p?.let { mapListeners.onMapLongClick.invoke(it) }
                return true
            }
        }

        eventOverlay = MapEventsOverlay(eventsReceiver)

        mapViewComposed.overlayManager.add(eventOverlay)

        if (mapViewComposed.isLayoutOccurred) {
            mapListeners.onFirstLoadListener.invoke("")
        }
    }

    override fun onCleared() {
        super.onCleared()
        delayedMapListener?.let { mapViewComposed.removeMapListener(it) }
        eventOverlay?.let { mapViewComposed.overlayManager.remove(eventOverlay) }
    }

    override fun onRemoved() {
        super.onRemoved()
        delayedMapListener?.let { mapViewComposed.removeMapListener(it) }
        eventOverlay?.let { mapViewComposed.overlayManager.remove(eventOverlay) }
    }
}