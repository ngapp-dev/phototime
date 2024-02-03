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
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.currentComposer
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

@Composable
internal fun MapViewUpdater(
    mapProperties: MapProperties,
    mapListeners: MapListeners,
    cameraState: CameraState,
    overlayManagerState: OverlayManagerState
) {
    val mapViewComposed = (currentComposer.applier as MapApplier).mapView

    ComposeNode<MapPropertiesNode, MapApplier>(factory = {
        MapPropertiesNode(mapViewComposed, mapListeners, cameraState, overlayManagerState)
    }, update = {

        set(mapProperties.mapOrientation) { mapViewComposed.mapOrientation = it }
        set(mapProperties.isMultiTouchControls) { mapViewComposed.setMultiTouchControls(it) }
        set(mapProperties.minZoomLevel) { mapViewComposed.minZoomLevel = it }
        set(mapProperties.maxZoomLevel) { mapViewComposed.maxZoomLevel = it }
        set(mapProperties.isFlingEnable) { mapViewComposed.isFlingEnabled = it }
        set(mapProperties.isUseDataConnection) { mapViewComposed.setUseDataConnection(it) }
        set(mapProperties.isTilesScaledToDpi) { mapViewComposed.isTilesScaledToDpi = it }
        set(mapProperties.tileSources) { if (it != null) mapViewComposed.setTileSource(it) }
        set(mapProperties.overlayManager) { if (it != null) mapViewComposed.overlayManager = it }
        set(mapProperties.isDarkMode) { if (it != null) mapViewComposed.overlayManager.tilesOverlay.setColorFilter(it) }

        set(mapProperties.isEnableRotationGesture) {
            val rotationGesture = RotationGestureOverlay(mapViewComposed)
            rotationGesture.isEnabled = it
            mapViewComposed.overlayManager.add(rotationGesture)
        }

        set(mapProperties.zoomButtonVisibility) {
            val visibility = when (it) {
                ZoomButtonVisibility.ALWAYS -> CustomZoomButtonsController.Visibility.ALWAYS
                ZoomButtonVisibility.SHOW_AND_FADEOUT -> CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT
                ZoomButtonVisibility.NEVER -> CustomZoomButtonsController.Visibility.NEVER
            }

            mapViewComposed.zoomController.setVisibility(visibility)
        }
    })
}