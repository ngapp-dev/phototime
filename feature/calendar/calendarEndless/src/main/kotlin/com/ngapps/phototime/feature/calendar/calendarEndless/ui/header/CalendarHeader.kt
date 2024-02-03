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

package com.ngapps.phototime.feature.calendar.calendarEndless.ui.header

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ngapps.phototime.core.designsystem.icon.PtIcons
import com.ngapps.phototime.feature.calendar.calendarEndless.ui.CalendarIconButton
import com.ngapps.phototime.feature.calendar.calendarEndless.util.MultiplePreviews
import kotlinx.datetime.Month
import java.time.format.TextStyle
import java.util.Locale

/**
 * Composable function for displaying the header of the Calendar, which includes the month and year.
 *
 * @param month The current month to display.
 * @param year The current year to display.
 * @param calendarTextConfig The configuration for styling the header text.
 * @param modifier The modifier for styling the header.
 * @param onPreviousClick The callback for when the previous arrow button is clicked.
 * @param onNextClick The callback for when the next arrow button is clicked.
 * @param arrowShown Determines whether the arrow buttons should be shown.
 */
@Composable
fun CalendarHeader(
    month: Month,
    year: Int,
    calendarTextConfig: CalendarTextConfig,
    modifier: Modifier = Modifier,
    onPreviousClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    arrowShown: Boolean = true
) {
    var isNext by remember { mutableStateOf(true) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(all = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val titleText = remember(month, year) { getTitleText(month, year) }

        AnimatedContent(
            targetState = titleText,
            transitionSpec = {
                addAnimation(isNext = isNext).using(
                    SizeTransform(clip = false),
                )
            },
            label = "calendarHeader",
        ) { targetText ->
            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth()
                    .align(Alignment.CenterVertically),
                color = calendarTextConfig.calendarTextColor,
                fontSize = calendarTextConfig.calendarTextSize,
                text = targetText,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
            )
        }
        if (arrowShown) {
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.CenterVertically),
                horizontalArrangement = Arrangement.End,
            ) {
                CalendarIconButton(
                    modifier = Modifier.wrapContentSize(),
                    imageVector = PtIcons.KeyboardArrowLeft,
                    contentDescription = "Previous Month",
                    onClick = {
                        isNext = false
                        onPreviousClick()
                    },
                )
                CalendarIconButton(
                    modifier = Modifier.wrapContentSize(),
                    imageVector = PtIcons.KeyboardArrowRight,
                    contentDescription = "Next Month",
                    onClick = {
                        isNext = true
                        onNextClick()
                    },
                )
            }
        }
    }
}

/**
 * Adds the animation to the content based on the given duration and direction.
 *
 * @param duration The duration of the animation in milliseconds.
 * @param isNext Determines the direction of the animation.
 * @return The content transformation with the specified animation.
 */
@OptIn(ExperimentalAnimationApi::class)
private fun addAnimation(duration: Int = 200, isNext: Boolean): ContentTransform {
    return slideInVertically(
        animationSpec =
        tween(durationMillis = duration),
    ) { height -> if (isNext) height else -height } + fadeIn(
        animationSpec = tween(durationMillis = duration),
    ) with slideOutVertically(
        animationSpec =
        tween(durationMillis = duration),
    ) { height -> if (isNext) -height else height } + fadeOut(
        animationSpec = tween(durationMillis = duration),
    )
}

/**
 * Returns the formatted title text for the Calendar header.
 *
 * @param month The current month.
 * @param year The current year.
 * @return The formatted title text.
 */
internal fun getTitleText(month: Month, year: Int): String {
    val monthDisplayName = month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        .lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    val shortYear = year.toString().takeLast(2)
    return "$monthDisplayName '$shortYear"
}

@MultiplePreviews
@Composable
fun CalendarHeaderPreview() {
    CalendarHeader(
        month = java.time.Month.APRIL,
        year = 2023,
        calendarTextConfig = CalendarTextConfig.previewDefault(),
    )
}
