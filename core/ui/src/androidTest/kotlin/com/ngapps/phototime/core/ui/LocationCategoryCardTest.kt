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

package com.ngapps.phototime.core.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.ngapps.phototime.core.testing.data.followableTopicTestData
import com.ngapps.phototime.core.testing.data.locationResourcesTestData
import com.ngapps.phototime.core.testing.data.userNewsResourcesTestData
import com.ngapps.phototime.core.ui.datetime.dateFormatted
import com.ngapps.phototime.core.ui.locations.LocationCategoryCard
import com.ngapps.phototime.core.ui.locations.LocationResourceCard
import com.ngapps.phototime.core.ui.swipe_dismiss.PtSwipeToDismiss
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LocationCategoryCardTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var clickActionLabel: String

    @Before
    fun setup() {
        composeTestRule.activity.apply {
            clickActionLabel = getString(R.string.card_tap_action)
        }
    }

    @Test
    fun testLocationCategoryDisplay_withLocationResource() {
        val locationResourcesTestData = locationResourcesTestData
        val locationResourcesCategoryTestData =
            locationResourcesTestData.groupBy { it.category }.map { (category, locations) ->
                category to locations
            }

        composeTestRule.setContent {
            LocationCategoryCard(
                locationResourceCategory = locationResourcesCategoryTestData[0],
                onExpandClick = { },
            )
        }

        composeTestRule
            .onNodeWithText(locationResourcesCategoryTestData[0].first)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(locationResourcesCategoryTestData[0].second.size.toString())
            .assertIsDisplayed()
    }

    @Test
    fun testLocationCategoryDisplay_withManyLocationResource() {

    }

    @Test
    fun testLocationCategoryClick_shouldShowItems() {
        val locationResourcesTestData = locationResourcesTestData
        val locationResourcesCategoryTestData =
            locationResourcesTestData.groupBy { it.category }.map { (category, locations) ->
                category to locations
            }

        val locationResourceCategory = locationResourcesCategoryTestData[0].first
        val locationResources = locationResourcesCategoryTestData[0].second

        var expanded = false

        composeTestRule.setContent {
            val state = rememberLazyGridState()

            LazyVerticalGrid(
                columns = GridCells.Adaptive(300.dp),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("locationCategory:feed"),
                state = state,
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    LocationCategoryCard(
                        locationResourceCategory = Pair(
                            locationResourceCategory,
                            locationResources,
                        ),
                        onExpandClick = { expanded = !expanded },
                    )
                }
                items(locationResources) { location ->
                    if (expanded) {
                        PtSwipeToDismiss(
                            onEditActionClick = { },
                            onDeleteActionClick = { },
                            modifier = Modifier.padding(vertical = 6.dp),
                        ) {
                            LocationResourceCard(
                                locationResource = location,
                                onLocationClick = {},
                                modifier = Modifier.padding(vertical = 6.dp),
                            )
                        }
                    }
                }
            }
        }

        composeTestRule
            .onNodeWithText(locationResourcesCategoryTestData[0].first)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(locationResourcesCategoryTestData[0].second.size.toString())
            .assertIsDisplayed()


        composeTestRule
            .onNodeWithTag(clickActionLabel)
            .performClick()

        composeTestRule
            .onNodeWithText(locationResources[0].title)
            .assertIsDisplayed()
    }

    @Test
    fun testMetaDataDisplay_withCodelabResource() {
        val newsWithKnownResourceType = userNewsResourcesTestData[0]
        lateinit var dateFormatted: String

        composeTestRule.setContent {
            NewsResourceCardExpanded(
                userNewsResource = newsWithKnownResourceType,
                isBookmarked = false,
                hasBeenViewed = false,
                onToggleBookmark = {},
                onClick = {},
                onTopicClick = {},
            )

            dateFormatted = dateFormatted(publishDate = newsWithKnownResourceType.publishDate)
        }

        composeTestRule
            .onNodeWithText(
                composeTestRule.activity.getString(
                    R.string.card_meta_data_text,
                    dateFormatted,
                    newsWithKnownResourceType.type,
                ),
            )
            .assertExists()
    }

    @Test
    fun testMetaDataDisplay_withEmptyResourceType() {
        val newsWithEmptyResourceType = userNewsResourcesTestData[3]
        lateinit var dateFormatted: String

        composeTestRule.setContent {
            NewsResourceCardExpanded(
                userNewsResource = newsWithEmptyResourceType,
                isBookmarked = false,
                hasBeenViewed = false,
                onToggleBookmark = {},
                onClick = {},
                onTopicClick = {},
            )

            dateFormatted = dateFormatted(publishDate = newsWithEmptyResourceType.publishDate)
        }

        composeTestRule
            .onNodeWithText(dateFormatted)
            .assertIsDisplayed()
    }

    @Test
    fun testTopicsChipColorBackground_matchesFollowedState() {
        composeTestRule.setContent {
            NewsResourceTopics(
                topics = followableTopicTestData,
                onTopicClick = {},
            )
        }

        for (followableTopic in followableTopicTestData) {
            val topicName = followableTopic.topic.name
            val expectedContentDescription = if (followableTopic.isFollowed) {
                "$topicName is followed"
            } else {
                "$topicName is not followed"
            }
            composeTestRule
                .onNodeWithText(topicName.uppercase())
                .assertContentDescriptionEquals(expectedContentDescription)
        }
    }

    @Test
    fun testUnreadDot_displayedWhenUnread() {
        val unreadNews = userNewsResourcesTestData[2]

        composeTestRule.setContent {
            NewsResourceCardExpanded(
                userNewsResource = unreadNews,
                isBookmarked = false,
                hasBeenViewed = false,
                onToggleBookmark = {},
                onClick = {},
                onTopicClick = {},
            )
        }

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.getString(
                    R.string.unread_resource_dot_content_description,
                ),
            )
            .assertIsDisplayed()
    }

    @Test
    fun testUnreadDot_notDisplayedWhenRead() {
        val readNews = userNewsResourcesTestData[0]

        composeTestRule.setContent {
            NewsResourceCardExpanded(
                userNewsResource = readNews,
                isBookmarked = false,
                hasBeenViewed = true,
                onToggleBookmark = {},
                onClick = {},
                onTopicClick = {},
            )
        }

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.getString(
                    R.string.unread_resource_dot_content_description,
                ),
            )
            .assertDoesNotExist()
    }
}
