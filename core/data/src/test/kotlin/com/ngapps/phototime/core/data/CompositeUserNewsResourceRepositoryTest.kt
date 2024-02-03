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

package com.ngapps.phototime.core.data

import com.ngapps.phototime.core.data.repository.CompositeUserNewsResourceRepository
import com.ngapps.phototime.core.data.repository.NewsResourceQuery
import com.ngapps.phototime.core.model.data.NewsResource
import com.ngapps.phototime.core.model.data.NewsResourceType.Video
import com.ngapps.phototime.core.model.data.Topic
import com.ngapps.phototime.core.model.data.mapToUserNewsResources
import com.ngapps.phototime.core.testing.repository.TestNewsRepository
import com.ngapps.phototime.core.testing.repository.TestUserDataRepository
import com.ngapps.phototime.core.testing.repository.emptyUserData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals

class CompositeUserNewsResourceRepositoryTest {

    private val newsRepository = TestNewsRepository()
    private val userDataRepository = TestUserDataRepository()

    private val userNewsResourceRepository = CompositeUserNewsResourceRepository(
        newsRepository = newsRepository,
        userDataRepository = userDataRepository,
    )

    @Test
    fun whenNoFilters_allNewsResourcesAreReturned() = runTest {
        // NOTE: Obtain the user news resources flow.
        val userNewsResources = userNewsResourceRepository.observeAll()

        // NOTE: Send some news resources and user data into the data repositories.
        newsRepository.sendNewsResources(sampleNewsResources)

        // NOTE: Construct the test user data with bookmarks and followed topics.
        val userData = emptyUserData.copy(
            bookmarkedNewsResources = setOf(sampleNewsResources[0].id, sampleNewsResources[2].id),
            followedTopics = setOf(sampleTask1.id),
        )

        userDataRepository.setUserData(userData)

        // NOTE: Check that the correct news resources are returned with their bookmarked state.
        assertEquals(
            sampleNewsResources.mapToUserNewsResources(userData),
            userNewsResources.first(),
        )
    }

    @Test
    fun whenFilteredByTopicId_matchingNewsResourcesAreReturned() = runTest {
        // NOTE: Obtain a stream of user news resources for the given topic id.
        val userNewsResources =
            userNewsResourceRepository.observeAll(
                NewsResourceQuery(
                    filterTopicIds = setOf(
                        sampleTask1.id,
                    ),
                ),
            )

        // NOTE: Send test data into the repositories.
        newsRepository.sendNewsResources(sampleNewsResources)
        userDataRepository.setUserData(emptyUserData)

        // NOTE: Check that only news resources with the given topic id are returned.
        assertEquals(
            sampleNewsResources
                .filter { it.topics.contains(sampleTask1) }
                .mapToUserNewsResources(emptyUserData),
            userNewsResources.first(),
        )
    }

    @Test
    fun whenFilteredByFollowedTopics_matchingNewsResourcesAreReturned() = runTest {
        // NOTE: Obtain a stream of user news resources for the given topic id.
        val userNewsResources =
            userNewsResourceRepository.observeAllForFollowedTopics()

        // NOTE: Send test data into the repositories.
        val userData = emptyUserData.copy(
            followedTopics = setOf(sampleTask1.id),
        )
        newsRepository.sendNewsResources(sampleNewsResources)
        userDataRepository.setUserData(userData)

        // NOTE: Check that only news resources with the given topic id are returned.
        assertEquals(
            sampleNewsResources
                .filter { it.topics.contains(sampleTask1) }
                .mapToUserNewsResources(userData),
            userNewsResources.first(),
        )
    }

    @Test
    fun whenFilteredByBookmarkedResources_matchingNewsResourcesAreReturned() = runTest {
        // NOTE: Obtain the bookmarked user news resources flow.
        val userNewsResources = userNewsResourceRepository.observeAllBookmarked()

        // NOTE: Send some news resources and user data into the data repositories.
        newsRepository.sendNewsResources(sampleNewsResources)

        // NOTE: Construct the test user data with bookmarks and followed topics.
        val userData = emptyUserData.copy(
            bookmarkedNewsResources = setOf(sampleNewsResources[0].id, sampleNewsResources[2].id),
            followedTopics = setOf(sampleTask1.id),
        )

        userDataRepository.setUserData(userData)

        // NOTE: Check that the correct news resources are returned with their bookmarked state.
        assertEquals(
            listOf(sampleNewsResources[0], sampleNewsResources[2]).mapToUserNewsResources(userData),
            userNewsResources.first(),
        )
    }
}

private val sampleTask1 = Topic(
    id = "Topic1",
    name = "Headlines",
    shortDescription = "",
    longDescription = "long description",
    url = "URL",
    imageUrl = "image URL",
)

private val sampleTask2 = Topic(
    id = "Topic2",
    name = "UI",
    shortDescription = "",
    longDescription = "long description",
    url = "URL",
    imageUrl = "image URL",
)

private val sampleNewsResources = listOf(
    NewsResource(
        id = "1",
        title = "Thanks for helping us reach 1M YouTube Subscribers",
        content = "Thank you everyone for following the Now in Android series and everything the " +
            "Android Developers YouTube channel has to offer. During the Android Developer " +
            "Summit, our YouTube channel reached 1 million subscribers! Here’s a small video to " +
            "thank you all.",
        url = "https://youtu.be/-fJ6poHQrjM",
        headerImageUrl = "https://i.ytimg.com/vi/-fJ6poHQrjM/maxresdefault.jpg",
        publishDate = Instant.parse("2021-11-09T00:00:00.000Z"),
        type = Video,
        topics = listOf(sampleTask1),
    ),
    NewsResource(
        id = "2",
        title = "Transformations and customisations in the Paging Library",
        content = "A demonstration of different operations that can be performed with Paging. " +
            "Transformations like inserting separators, when to create a new pager, and " +
            "customisation options for consuming PagingData.",
        url = "https://youtu.be/ZARz0pjm5YM",
        headerImageUrl = "https://i.ytimg.com/vi/ZARz0pjm5YM/maxresdefault.jpg",
        publishDate = Instant.parse("2021-11-01T00:00:00.000Z"),
        type = Video,
        topics = listOf(sampleTask1, sampleTask2),
    ),
    NewsResource(
        id = "3",
        title = "Community tip on Paging",
        content = "Tips for using the Paging library from the developer community",
        url = "https://youtu.be/r5JgIyS3t3s",
        headerImageUrl = "https://i.ytimg.com/vi/r5JgIyS3t3s/maxresdefault.jpg",
        publishDate = Instant.parse("2021-11-08T00:00:00.000Z"),
        type = Video,
        topics = listOf(sampleTask2),
    ),
)
