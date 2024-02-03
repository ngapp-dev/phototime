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

package com.ngapps.phototime.core.data.repository

import com.ngapps.phototime.core.analytics.AnalyticsEvent
import com.ngapps.phototime.core.analytics.AnalyticsEvent.Param
import com.ngapps.phototime.core.analytics.AnalyticsHelper

fun AnalyticsHelper.logNewsResourceBookmarkToggled(newsResourceId: String, isBookmarked: Boolean) {
    val eventType = if (isBookmarked) "news_resource_saved" else "news_resource_unsaved"
    val paramKey = if (isBookmarked) "saved_news_resource_id" else "unsaved_news_resource_id"
    logEvent(
        AnalyticsEvent(
            type = eventType,
            extras = listOf(
                Param(key = paramKey, value = newsResourceId),
            ),
        ),
    )
}

fun AnalyticsHelper.logTaskResourceCompleteToggled(taskResourceId: String, isCompleted: Boolean) {
    val eventType = if (isCompleted) "task_resource_completed" else "task_resource_incompleted"
    val paramKey = if (isCompleted) "completed_task_resource_id" else "incompleted_task_resource_id"
    logEvent(
        AnalyticsEvent(
            type = eventType,
            extras = listOf(
                Param(key = paramKey, value = taskResourceId),
            ),
        ),
    )
}

fun AnalyticsHelper.logTopicFollowToggled(followedTopicId: String, isFollowed: Boolean) {
    val eventType = if (isFollowed) "topic_followed" else "topic_unfollowed"
    val paramKey = if (isFollowed) "followed_topic_id" else "unfollowed_topic_id"
    logEvent(
        AnalyticsEvent(
            type = eventType,
            extras = listOf(
                Param(key = paramKey, value = followedTopicId),
            ),
        ),
    )
}

fun AnalyticsHelper.logDarkThemeConfigChanged(darkThemeConfigName: String) =
    logEvent(
        AnalyticsEvent(
            type = "dark_theme_config_changed",
            extras = listOf(
                Param(key = "dark_theme_config", value = darkThemeConfigName),
            ),
        ),
    )

fun AnalyticsHelper.logDynamicColorPreferenceChanged(useDynamicColor: Boolean) =
    logEvent(
        AnalyticsEvent(
            type = "dynamic_color_preference_changed",
            extras = listOf(
                Param(key = "dynamic_color_preference", value = useDynamicColor.toString()),
            ),
        ),
    )

fun AnalyticsHelper.logOnboardingStateChanged(shouldHideOnboarding: Boolean) {
    val eventType = if (shouldHideOnboarding) "onboarding_complete" else "onboarding_reset"
    logEvent(
        AnalyticsEvent(type = eventType),
    )
}
