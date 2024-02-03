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

import com.ngapps.phototime.core.model.data.shoot.ShootResource
import com.ngapps.phototime.core.model.data.task.ScheduledTimeResource

/* ktlint-disable max-line-length */
val shootResourcesTestData: List<ShootResource> = listOf(
    ShootResource(
        id = "1",
        title = "Portrait Photo Session with Amelia",
        description = "Capture beautiful moments with Amelia in a picturesque forest setting.",
        photos = listOf(
            "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEjrHro6d3BTw7ZZ4IXgfb6_8aESB7-SsWfelDSSInZVamiMSnYpBZzGBaZBBrWxWwYgLqOHuOtroGvGjxrwzdUkhjwuIvM1u6chIblGKS1gQ6JVkjXr-Vztyk2zoYb1ylvhNgLgC5q6M-7LaiXT1xnAT96DvkPx89APNb8JEaz-1mnMRcfaOYYBHzQL/w1200-h630-p-k-no-nu/Text%20to%20Speech%20-%20Social%20-%201024x512.png",
            "https://miro.medium.com/max/1200/1*3FZeNmAPZDYUCmgL0cBXoA.png",
        ),
        scheduledTime = ScheduledTimeResource(
            start = "1 June 16:00",
            notification = "1 June 11:00",
        ),
        moodboards = listOf("1", "2"),
        contacts = listOf("1", "2"),
        locations = listOf("1", "2"),
        tasks = listOf("1", "2"),
    ),
    ShootResource(
        id = "2",
        title = "Wedding Photo Shoot Deadline",
        description = "Complete the wedding photo shoot files before the upcoming deadline.",
        photos = listOf(
            "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEjrHro6d3BTw7ZZ4IXgfb6_8aESB7-SsWfelDSSInZVamiMSnYpBZzGBaZBBrWxWwYgLqOHuOtroGvGjxrwzdUkhjwuIvM1u6chIblGKS1gQ6JVkjXr-Vztyk2zoYb1ylvhNgLgC5q6M-7LaiXT1xnAT96DvkPx89APNb8JEaz-1mnMRcfaOYYBHzQL/w1200-h630-p-k-no-nu/Text%20to%20Speech%20-%20Social%20-%201024x512.png",
            "https://miro.medium.com/max/1200/1*3FZeNmAPZDYUCmgL0cBXoA.png",
        ),
        scheduledTime = ScheduledTimeResource(
            start = "2 June 16:00",
            notification = "2 June 11:00",
        ),
        moodboards = listOf("2", "3"),
        contacts = listOf("2", "3"),
        locations = listOf("2", "3"),
        tasks = listOf("2", "3"),
    ),
    ShootResource(
        id = "3",
        title = "Product Photography Session",
        description = "Create captivating images for a new product line.",
        photos = listOf(
            "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEjrHro6d3BTw7ZZ4IXgfb6_8aESB7-SsWfelDSSInZVamiMSnYpBZzGBaZBBrWxWwYgLqOHuOtroGvGjxrwzdUkhjwuIvM1u6chIblGKS1gQ6JVkjXr-Vztyk2zoYb1ylvhNgLgC5q6M-7LaiXT1xnAT96DvkPx89APNb8JEaz-1mnMRcfaOYYBHzQL/w1200-h630-p-k-no-nu/Text%20to%20Speech%20-%20Social%20-%201024x512.png",
            "https://miro.medium.com/max/1200/1*3FZeNmAPZDYUCmgL0cBXoA.png",
        ),
        scheduledTime = ScheduledTimeResource(
            start = "3 June 14:30",
            notification = "3 June 10:00",
        ),
        moodboards = listOf("3", "4"),
        contacts = listOf("3"),
        locations = listOf("3", "4"),
        tasks = listOf("4"),
    ),
    ShootResource(
        id = "4",
        title = "Fashion Photography Showcase",
        description = "Prepare for a high-profile fashion photography showcase.",
        photos = listOf(
            "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEjrHro6d3BTw7ZZ4IXgfb6_8aESB7-SsWfelDSSInZVamiMSnYpBZzGBaZBBrWxWwYgLqOHuOtroGvGjxrwzdUkhjwuIvM1u6chIblGKS1gQ6JVkjXr-Vztyk2zoYb1ylvhNgLgC5q6M-7LaiXT1xnAT96DvkPx89APNb8JEaz-1mnMRcfaOYYBHzQL/w1200-h630-p-k-no-nu/Text%20to%20Speech%20-%20Social%20-%201024x512.png",
            "https://miro.medium.com/max/1200/1*3FZeNmAPZDYUCmgL0cBXoA.png",
        ),
        scheduledTime = ScheduledTimeResource(
            start = "4 June 18:00",
            notification = "4 June 14:00",
        ),
        moodboards = listOf("4", "1"),
        contacts = listOf("4", "1"),
        locations = listOf("1", "2", "4"),
        tasks = listOf("4", "1"),
    ),
)
