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

package com.ngapps.phototime.feature.calendar.calendarEndless.paging

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.paging.Pager
import androidx.paging.PagingConfig

/**
 * Controller for managing the paging functionality of Calendar items.
 */
@Stable
class CalendarPagingController {

    private val repository = CalendarRepository()

    /**
     * Flow of Calendar items loaded from the repository using the Paging library.
     */
    val calendarItems = Pager(PagingConfig(pageSize = 12)) {
        CalendarPagingSource(repository)
    }.flow
}

/**
 * Remembers an instance of [CalendarPagingController].
 *
 * @return The remembered [CalendarPagingController] instance.
 */
@Composable
fun rememberCalendarPagingController(): CalendarPagingController {
    return remember {
        CalendarPagingController()
    }
}
