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

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.ngapps.phototime.core.data.model.RecentSearchQuery
import com.ngapps.phototime.core.testing.data.contactResourcesTestData
import com.ngapps.phototime.core.testing.data.locationResourcesTestData
import com.ngapps.phototime.core.testing.data.shootResourcesTestData
import com.ngapps.phototime.core.testing.data.taskResourcesTestData
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * UI test for checking the correct behaviour of the Search screen.
 */
class SearchScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var clearSearchContentDesc: String
    private lateinit var clearRecentSearchesContentDesc: String
    private lateinit var topicsString: String
    private lateinit var updatesString: String
    private lateinit var locationsString: String
    private lateinit var contactsString: String
    private lateinit var tasksString: String
    private lateinit var shootsString: String
    private lateinit var tryAnotherSearchString: String
    private lateinit var searchNotReadyString: String

    @Before
    fun setup() {
        composeTestRule.activity.apply {
            clearSearchContentDesc = getString(R.string.clear_search_text_content_desc)
            clearRecentSearchesContentDesc = getString(R.string.clear_recent_searches_content_desc)
            topicsString = getString(R.string.topics)
            updatesString = getString(R.string.updates)
            locationsString = getString(R.string.locations)
            contactsString = getString(R.string.contacts)
            tasksString = getString(R.string.tasks)
            shootsString = getString(R.string.shoots)
            tryAnotherSearchString = getString(R.string.try_another_search) +
                    " " + getString(R.string.interests) + " " + getString(R.string.to_browse_topics)
            searchNotReadyString = getString(R.string.search_not_ready)
        }
    }

    @Test
    fun searchTextField_isFocused() {
        composeTestRule.setContent {
            SearchScreen()
        }

        composeTestRule
            .onNodeWithTag("searchTextField")
            .assertIsFocused()
    }

    @Test
    fun emptySearchResult_emptyScreenIsDisplayed() {
        composeTestRule.setContent {
            SearchScreen(
                searchResultUiState = SearchResultUiState.Success(),
            )
        }

        composeTestRule
            .onNodeWithText(tryAnotherSearchString)
            .assertIsDisplayed()
    }

    @Test
    fun emptySearchResult_nonEmptyRecentSearches_emptySearchScreenAndRecentSearchesAreDisplayed() {
        val recentSearches = listOf("photo")
        composeTestRule.setContent {
            SearchScreen(
                searchResultUiState = SearchResultUiState.Success(),
                recentSearchesUiState = RecentSearchQueriesUiState.Success(
                    recentQueries = recentSearches.map(::RecentSearchQuery),
                ),
            )
        }

        composeTestRule
            .onNodeWithText(tryAnotherSearchString)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription(clearRecentSearchesContentDesc)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("photo")
            .assertIsDisplayed()
    }

    @Test
    fun searchResultWithLocationResources_firstLocationResourcesIsVisible() {
        composeTestRule.setContent {
            SearchScreen(
                searchResultUiState = SearchResultUiState.Success(
                    locationResources = locationResourcesTestData,
                ),
            )
        }

        composeTestRule
            .onNodeWithText(locationsString)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(locationResourcesTestData[0].title)
            .assertIsDisplayed()
    }

    @Test
    fun searchResultWithContactResources_firstContactResourcesIsVisible() {
        composeTestRule.setContent {
            SearchScreen(
                searchResultUiState = SearchResultUiState.Success(
                    contactResources = contactResourcesTestData,
                ),
            )
        }

        composeTestRule
            .onNodeWithText(contactsString)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(contactResourcesTestData[0].name)
            .assertIsDisplayed()
    }

    @Test
    fun searchResultWithTaskResources_firstTaskResourcesIsVisible() {
        composeTestRule.setContent {
            SearchScreen(
                searchResultUiState = SearchResultUiState.Success(
                    taskResources = taskResourcesTestData,
                ),
            )
        }

        composeTestRule
            .onNodeWithText(tasksString)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(taskResourcesTestData[0].category)
            .assertIsDisplayed()
    }

    @Test
    fun searchResultWithShootResources_firstShootResourcesIsVisible() {
        composeTestRule.setContent {
            SearchScreen(
                searchResultUiState = SearchResultUiState.Success(
                    shootResources = shootResourcesTestData,
                ),
            )
        }

        composeTestRule
            .onNodeWithText(shootsString)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(shootResourcesTestData[0].title)
            .assertIsDisplayed()
    }

    @Test
    fun emptyQuery_notEmptyRecentSearches_verifyClearSearchesButton_displayed() {
        val recentSearches = listOf("photo", "shooting")
        composeTestRule.setContent {
            SearchScreen(
                searchResultUiState = SearchResultUiState.EmptyQuery,
                recentSearchesUiState = RecentSearchQueriesUiState.Success(
                    recentQueries = recentSearches.map(::RecentSearchQuery),
                ),
            )
        }

        composeTestRule
            .onNodeWithContentDescription(clearRecentSearchesContentDesc)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("photo")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("shooting")
            .assertIsDisplayed()
    }

    @Test
    fun searchNotReady_verifySearchNotReadyMessageIsVisible() {
        composeTestRule.setContent {
            SearchScreen(
                searchResultUiState = SearchResultUiState.SearchNotReady,
            )
        }

        composeTestRule
            .onNodeWithText(searchNotReadyString)
            .assertIsDisplayed()
    }
}
