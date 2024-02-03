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

package com.ngapps.phototime.feature.search

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.ngapps.phototime.core.ui.contacts.PreviewParameterContactData.contactResources
import com.ngapps.phototime.core.ui.locations.PreviewParameterLocationData.locationResources
import com.ngapps.phototime.core.ui.shoots.PreviewParameterShootData.shootResources
import com.ngapps.phototime.core.ui.tasks.PreviewParameterTaskData.taskResources

/* ktlint-disable max-line-length */
/**
 * This [PreviewParameterProvider](https://developer.android.com/reference/kotlin/androidx/compose/ui/tooling/preview/PreviewParameterProvider)
 * provides list of [SearchResultUiState] for Composable previews.
 */
class SearchUiStatePreviewParameterProvider : PreviewParameterProvider<SearchResultUiState> {
    override val values: Sequence<SearchResultUiState> = sequenceOf(
        SearchResultUiState.Success(
            locationResources = locationResources,
            contactResources = contactResources,
            taskResources = taskResources,
            shootResources = shootResources
        ),
    )
}
