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

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.io.IOException

/**
 * Paging source for loading CalendarModelEntity items from the CalendarRepository.
 *
 * @param calendarRepository The repository for retrieving CalendarModelEntity items.
 */
class CalendarPagingSource(
    private val calendarRepository: CalendarRepository
) : PagingSource<Int, CalendarModelEntity>() {

    private val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    /**
     * Returns the refresh key for the paging state.
     */
    override fun getRefreshKey(state: PagingState<Int, CalendarModelEntity>) = null

    /**
     * Loads the [CalendarModelEntity] items based on the provided load parameters.
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CalendarModelEntity> {
        return try {
            val page = params.key ?: today.year

            // NOTE: Filter out the months before the current month and year
            val calendarItems = calendarRepository.generateDates(page)
                .filter { date ->
                    date.year >= today.year && date.month.value >= today.monthNumber
                }

            val nextPage = page.plus(1)

            LoadResult.Page(
                data = calendarItems,
                prevKey = null,
                nextKey = nextPage,
            )
        } catch (e: IOException) {
            LoadResult.Error(e.fillInStackTrace())
        }
    }
}
