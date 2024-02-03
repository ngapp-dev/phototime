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

package com.ngapps.phototime.core.testing.data

import com.ngapps.phototime.core.model.data.DarkThemeConfig
import com.ngapps.phototime.core.model.data.task.ScheduledTimeResource
import com.ngapps.phototime.core.model.data.task.TaskResource
import com.ngapps.phototime.core.model.data.UserData
import com.ngapps.phototime.core.model.data.task.UserTaskResource

/* ktlint-disable max-line-length */
val userTaskResourcesTestData: List<UserTaskResource> = UserData(
    bookmarkedNewsResources = setOf("1", "4"),
    viewedNewsResources = setOf("1", "2", "4"),
    completedTaskResources = setOf("1", "2"),
    followedTopics = emptySet(),
    userLocation = Pair("53.90118608322789", "27.55552455952234"),
    darkThemeConfig = DarkThemeConfig.DARK,
    shouldHideOnboarding = true,
    useDynamicColor = false,
    locationCategories = setOf("Nature", "Urban", "Historical", "Modern"),
    contactCategories = setOf("Shooting", "Meeting", "Event"),
    taskCategories = setOf("Photography", "Editing", "Meeting", "Event"),
).let { userData ->
    listOf(
        UserTaskResource(
            taskResource = TaskResource(
                id = "1",
                category = "Photography",
                title = "Portrait Session with Amelia",
                description = "Capture stunning portraits with Amelia in a scenic forest setting.",
                photos = listOf(
                    "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEjrHro6d3BTw7ZZ4IXgfb6_8aESB7-SsWfelDSSInZVamiMSnYpBZzGBaZBBrWxWwYgLqOHuOtroGvGjxrwzdUkhjwuIvM1u6chIblGKS1gQ6JVkjXr-Vztyk2zoYb1ylvhNgLgC5q6M-7LaiXT1xnAT96DvkPx89APNb8JEaz-1mnMRcfaOYYBHzQL/w1200-h630-p-k-no-nu/Text%20to%20Speech%20-%20Social%20-%201024x512.png",
                    "https://miro.medium.com/max/1200/1*3FZeNmAPZDYUCmgL0cBXoA.png",
                ),
                scheduledTime = ScheduledTimeResource(
                    start = "1 June 16:00",
                    notification = "1 June 11:00",
                ),
                contacts = listOf("1", "2"),
                note = "Don't forget to bring additional lighting equipment.",
            ),
            userData = userData,
        ),
        UserTaskResource(
            taskResource = TaskResource(
                id = "2",
                category = "Editing",
                title = "Retouching Deadline",
                description = "Complete the retouching process for wedding photo shooting files.",
                photos = listOf(
                    "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEjrHro6d3BTw7ZZ4IXgfb6_8aESB7-SsWfelDSSInZVamiMSnYpBZzGBaZBBrWxWwYgLqOHuOtroGvGjxrwzdUkhjwuIvM1u6chIblGKS1gQ6JVkjXr-Vztyk2zoYb1ylvhNgLgC5q6M-7LaiXT1xnAT96DvkPx89APNb8JEaz-1mnMRcfaOYYBHzQL/w1200-h630-p-k-no-nu/Text%20to%20Speech%20-%20Social%20-%201024x512.png",
                    "https://miro.medium.com/max/1200/1*3FZeNmAPZDYUCmgL0cBXoA.png",
                ),
                scheduledTime = ScheduledTimeResource(
                    start = "2 June 16:00",
                    notification = "2 June 11:00",
                ),
                contacts = listOf("2", "3"),
                note = "Ensure high-quality edits for client satisfaction.",
            ),
            userData = userData,
        ),
        UserTaskResource(
            taskResource = TaskResource(
                id = "3",
                category = "Meeting",
                title = "Client Consultation",
                description = "Discuss upcoming projects and client requirements in a formal meeting.",
                photos = listOf(
                    "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEjrHro6d3BTw7ZZ4IXgfb6_8aESB7-SsWfelDSSInZVamiMSnYpBZzGBaZBBrWxWwYgLqOHuOtroGvGjxrwzdUkhjwuIvM1u6chIblGKS1gQ6JVkjXr-Vztyk2zoYb1ylvhNgLgC5q6M-7LaiXT1xnAT96DvkPx89APNb8JEaz-1mnMRcfaOYYBHzQL/w1200-h630-p-k-no-nu/Text%20to%20Speech%20-%20Social%20-%201024x512.png",
                    "https://miro.medium.com/max/1200/1*3FZeNmAPZDYUCmgL0cBXoA.png",
                ),
                scheduledTime = ScheduledTimeResource(
                    start = "3 June 14:30",
                    notification = "3 June 10:00",
                ),
                contacts = listOf("3"),
                note = "Prepare project proposals and sample works.",
            ),
            userData = userData,
        ),
        UserTaskResource(
            taskResource = TaskResource(
                id = "4",
                category = "Event",
                title = "Company Anniversary Celebration",
                description = "Coordinate and participate in the company's anniversary celebration event.",
                photos = listOf(
                    "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEjrHro6d3BTw7ZZ4IXgfb6_8aESB7-SsWfelDSSInZVamiMSnYpBZzGBaZBBrWxWwYgLqOHuOtroGvGjxrwzdUkhjwuIvM1u6chIblGKS1gQ6JVkjXr-Vztyk2zoYb1ylvhNgLgC5q6M-7LaiXT1xnAT96DvkPx89APNb8JEaz-1mnMRcfaOYYBHzQL/w1200-h630-p-k-no-nu/Text%20to%20Speech%20-%20Social%20-%201024x512.png",
                    "https://miro.medium.com/max/1200/1*3FZeNmAPZDYUCmgL0cBXoA.png",
                ),
                scheduledTime = ScheduledTimeResource(
                    start = "4 June 18:00",
                    notification = "4 June 14:00",
                ),
                contacts = listOf("1", "3", "4"),
                note = "Organize team activities for a memorable celebration.",
            ),
            userData = userData,
        ),
    )
}
