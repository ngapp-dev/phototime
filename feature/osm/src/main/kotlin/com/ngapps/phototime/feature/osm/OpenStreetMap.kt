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

import android.content.Context
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.awaitCancellation
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

internal typealias OsmMapView = MapView

@Composable
fun rememberMapViewWithLifecycle(vararg mapListener: MapListener): OsmMapView {
    val context = LocalContext.current
    val mapView = remember {
        OsmMapView(context)
    }

    val lifecycleObserver = rememberMapLifecycleObserver(context, mapView, *mapListener)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return mapView
}

@Composable
fun rememberMapLifecycleObserver(
    context: Context,
    mapView: OsmMapView,
    vararg mapListener: MapListener
): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    Configuration.getInstance().userAgentValue = context.packageName
                    Configuration.getInstance()
                        .load(context, context.getSharedPreferences("osm", Context.MODE_PRIVATE))
                }

                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_DESTROY -> {
                    mapListener.onEach { mapView.removeMapListener(it) }
                }

                else -> {}
            }
        }
    }

@LayoutScopeMarker
interface OsmAndroidScope

enum class ZoomButtonVisibility {
    ALWAYS, NEVER, SHOW_AND_FADEOUT
}

@Composable
fun OpenStreetMap(
    modifier: Modifier = Modifier,
    cameraState: CameraState = rememberCameraState(),
    overlayManagerState: OverlayManagerState = rememberOverlayManagerState(),
    properties: MapProperties = DefaultMapProperties,
    onMapClick: (GeoPoint) -> Unit = {},
    onMapLongClick: (GeoPoint) -> Unit = {},
    onFirstLoadListener: () -> Unit = {},
    content: (@Composable @OsmAndroidComposable OsmAndroidScope.() -> Unit)? = null
) {

    val mapView = rememberMapViewWithLifecycle()

    val mapListeners = remember {
        MapListeners()
    }.also {
        it.onMapClick = onMapClick
        it.onMapLongClick = onMapLongClick
        it.onFirstLoadListener = {
            onFirstLoadListener.invoke()
        }
    }

    val mapProperties by rememberUpdatedState(properties)

    val parentComposition = rememberCompositionContext()
    val currentContent by rememberUpdatedState(content)

    LaunchedEffect(Unit) {
        disposingComposition {
            mapView.newComposition(parentComposition) {
                MapViewUpdater(mapProperties, mapListeners, cameraState, overlayManagerState)
                currentContent?.invoke(object : OsmAndroidScope {})
            }
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            mapView
        },
        update = {
            it.controller.animateTo(
                cameraState.geoPoint,
                cameraState.zoom,
                cameraState.speed,
            )
        },
    )
}

internal suspend inline fun disposingComposition(factory: () -> Composition) {
    val composition = factory()
    try {
        awaitCancellation()
    } finally {
        composition.dispose()
    }
}

private fun OsmMapView.newComposition(
    parent: CompositionContext,
    content: @Composable () -> Unit
): Composition {
    return Composition(
        MapApplier(this), parent,
    ).apply {
        setContent(content)
    }
}