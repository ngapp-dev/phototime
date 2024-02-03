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

import android.graphics.ColorFilter
import org.osmdroid.tileprovider.tilesource.ITileSource
import org.osmdroid.views.overlay.OverlayManager

data class MapProperties(
    val mapOrientation: Float = 0f,
    val isMultiTouchControls: Boolean = true,
    val isAnimating: Boolean = true,
    val minZoomLevel: Double = 6.0,
    val maxZoomLevel: Double = 29.0,
    val isFlingEnable: Boolean = true,
    val isEnableRotationGesture: Boolean = false,
    val isUseDataConnection: Boolean = true,
    val isTilesScaledToDpi: Boolean = false,
    val tileSources: ITileSource? = null,
    val overlayManager: OverlayManager? = null,
    val zoomButtonVisibility: ZoomButtonVisibility = ZoomButtonVisibility.ALWAYS,
    val isDarkMode: ColorFilter? = null
)

val DefaultMapProperties = MapProperties()