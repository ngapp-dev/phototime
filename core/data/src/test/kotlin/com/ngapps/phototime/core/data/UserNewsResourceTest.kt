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

import com.ngapps.phototime.core.model.data.DarkThemeConfig.FOLLOW_SYSTEM
import com.ngapps.phototime.core.model.data.FollowableTopic
import com.ngapps.phototime.core.model.data.NewsResource
import com.ngapps.phototime.core.model.data.NewsResourceType.Article
import com.ngapps.phototime.core.model.data.Topic
import com.ngapps.phototime.core.model.data.UserData
import com.ngapps.phototime.core.model.data.UserNewsResource
import kotlinx.datetime.Clock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UserNewsResourceTest {

    /**
     * Given: Some user data and news resources
     * When: They are combined using `UserNewsResource.from`
     * Then: The correct UserNewsResources are constructed
     */
    @Test
    fun userNewsResourcesAreConstructedFromNewsResourcesAndUserData() {
        val newsResource1 = NewsResource(
            id = "N1",
            title = "Test news title",
            content = "Test news content",
            url = "Test URL",
            headerImageUrl = "Test image URL",
            publishDate = Clock.System.now(),
            type = Article,
            topics = listOf(
                Topic(
                    id = "T1",
                    name = "Topic 1",
                    shortDescription = "Topic 1 short description",
                    longDescription = "Topic 1 long description",
                    url = "Topic 1 URL",
                    imageUrl = "Topic 1 image URL",
                ),
                Topic(
                    id = "T2",
                    name = "Topic 2",
                    shortDescription = "Topic 2 short description",
                    longDescription = "Topic 2 long description",
                    url = "Topic 2 URL",
                    imageUrl = "Topic 2 image URL",
                ),
            ),
        )

        val userData = UserData(
            bookmarkedNewsResources = setOf("N1"),
            viewedNewsResources = setOf("N1"),
            completedTaskResources = setOf("TA1"),
            followedTopics = setOf("T1"),
            darkThemeConfig = FOLLOW_SYSTEM,
            useDynamicColor = false,
            shouldHideOnboarding = true,
            userLocation = Pair("", "")
        )

        val userNewsResource = UserNewsResource(newsResource1, userData)

        // NOTE: Check that the simple field mappings have been done correctly.
        assertEquals(newsResource1.id, userNewsResource.id)
        assertEquals(newsResource1.title, userNewsResource.title)
        assertEquals(newsResource1.content, userNewsResource.content)
        assertEquals(newsResource1.url, userNewsResource.url)
        assertEquals(newsResource1.headerImageUrl, userNewsResource.headerImageUrl)
        assertEquals(newsResource1.publishDate, userNewsResource.publishDate)

        // NOTE: Check that each Topic has been converted to a FollowedTopic correctly.
        assertEquals(newsResource1.topics.size, userNewsResource.followableTopics.size)
        for (topic in newsResource1.topics) {
            // Construct the expected FollowableTopic.
            val followableTopic = FollowableTopic(
                topic = topic,
                isFollowed = userData.followedTopics.contains(topic.id),
            )
            assertTrue(userNewsResource.followableTopics.contains(followableTopic))
        }

        // NOTE: Check that the saved flag is set correctly.
        assertEquals(
            userData.bookmarkedNewsResources.contains(newsResource1.id),
            userNewsResource.isSaved,
        )
    }
}
