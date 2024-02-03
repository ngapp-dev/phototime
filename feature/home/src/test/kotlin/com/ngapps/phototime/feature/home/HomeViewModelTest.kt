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

package com.ngapps.phototime.feature.home

import androidx.lifecycle.SavedStateHandle
import com.ngapps.phototime.core.data.repository.CompositeUserNewsResourceRepository
import com.ngapps.phototime.core.domain.GetFollowableTopicsUseCase
import com.ngapps.phototime.core.model.data.FollowableTopic
import com.ngapps.phototime.core.model.data.NewsResource
import com.ngapps.phototime.core.model.data.NewsResourceType.Video
import com.ngapps.phototime.core.model.data.Topic
import com.ngapps.phototime.core.model.data.UserNewsResource
import com.ngapps.phototime.core.model.data.mapToUserNewsResources
import com.ngapps.phototime.core.testing.repository.TestNewsRepository
import com.ngapps.phototime.core.testing.repository.TestTopicsRepository
import com.ngapps.phototime.core.testing.repository.TestUserDataRepository
import com.ngapps.phototime.core.testing.repository.emptyUserData
import com.ngapps.phototime.core.testing.util.MainDispatcherRule
import com.ngapps.phototime.core.testing.util.TestSyncManager
import com.ngapps.phototime.core.ui.NewsFeedUiState
import com.ngapps.phototime.feature.home.navigation.LINKED_NEWS_RESOURCE_ID
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 */
class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val syncManager = TestSyncManager()
    private val userDataRepository = TestUserDataRepository()
    private val topicsRepository = TestTopicsRepository()
    private val newsRepository = TestNewsRepository()
    private val userNewsResourceRepository = CompositeUserNewsResourceRepository(
        newsRepository = newsRepository,
        userDataRepository = userDataRepository,
    )

    private val getFollowableTopicsUseCase = GetFollowableTopicsUseCase(
        topicsRepository = topicsRepository,
        userDataRepository = userDataRepository,
    )
    private val savedStateHandle = SavedStateHandle()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        viewModel = HomeViewModel(
            syncManager = syncManager,
            savedStateHandle = savedStateHandle,
            userDataRepository = userDataRepository,
            userNewsResourceRepository = userNewsResourceRepository,
            getFollowableTopics = getFollowableTopicsUseCase,
        )
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(
            OnboardingUiState.Loading,
            viewModel.onboardingUiState.value,
        )
        assertEquals(NewsFeedUiState.Loading, viewModel.newsFeedState.value)
    }

    @Test
    fun stateIsLoadingWhenFollowedTopicsAreLoading() = runTest {
        val collectJob1 =
            launch(UnconfinedTestDispatcher()) { viewModel.onboardingUiState.collect() }
        val collectJob2 = launch(UnconfinedTestDispatcher()) { viewModel.newsFeedState.collect() }

        topicsRepository.sendTopics(sampleTasks)

        assertEquals(
            OnboardingUiState.Loading,
            viewModel.onboardingUiState.value,
        )
        assertEquals(NewsFeedUiState.Loading, viewModel.newsFeedState.value)

        collectJob1.cancel()
        collectJob2.cancel()
    }

    @Test
    fun stateIsLoadingWhenAppIsSyncingWithNoInterests() = runTest {
        syncManager.setSyncing(true)

        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.isSyncing.collect() }

        assertEquals(
            true,
            viewModel.isSyncing.value,
        )

        collectJob.cancel()
    }

    @Test
    fun onboardingStateIsLoadingWhenTopicsAreLoading() = runTest {
        val collectJob1 =
            launch(UnconfinedTestDispatcher()) { viewModel.onboardingUiState.collect() }
        val collectJob2 = launch(UnconfinedTestDispatcher()) { viewModel.newsFeedState.collect() }

        userDataRepository.setFollowedTopicIds(emptySet())

        assertEquals(
            OnboardingUiState.Loading,
            viewModel.onboardingUiState.value,
        )
        assertEquals(NewsFeedUiState.Success(emptyList()), viewModel.newsFeedState.value)

        collectJob1.cancel()
        collectJob2.cancel()
    }

    @Test
    fun onboardingIsShownWhenNewsResourcesAreLoading() = runTest {
        val collectJob1 =
            launch(UnconfinedTestDispatcher()) { viewModel.onboardingUiState.collect() }
        val collectJob2 = launch(UnconfinedTestDispatcher()) { viewModel.newsFeedState.collect() }

        topicsRepository.sendTopics(sampleTasks)
        userDataRepository.setFollowedTopicIds(emptySet())

        assertEquals(
            OnboardingUiState.Shown(
                topics = listOf(
                    FollowableTopic(
                        topic = Topic(
                            id = "0",
                            name = "Headlines",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                    FollowableTopic(
                        topic = Topic(
                            id = "1",
                            name = "UI",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                    FollowableTopic(
                        topic = Topic(
                            id = "2",
                            name = "Tools",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                ),
            ),
            viewModel.onboardingUiState.value,
        )
        assertEquals(
            NewsFeedUiState.Success(
                feed = emptyList(),
            ),
            viewModel.newsFeedState.value,
        )

        collectJob1.cancel()
        collectJob2.cancel()
    }

    @Test
    fun onboardingIsShownAfterLoadingEmptyFollowedTopics() = runTest {
        val collectJob1 =
            launch(UnconfinedTestDispatcher()) { viewModel.onboardingUiState.collect() }
        val collectJob2 = launch(UnconfinedTestDispatcher()) { viewModel.newsFeedState.collect() }

        topicsRepository.sendTopics(sampleTasks)
        userDataRepository.setFollowedTopicIds(emptySet())
        newsRepository.sendNewsResources(sampleNewsResources)

        assertEquals(
            OnboardingUiState.Shown(
                topics = listOf(
                    FollowableTopic(
                        topic = Topic(
                            id = "0",
                            name = "Headlines",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                    FollowableTopic(
                        topic = Topic(
                            id = "1",
                            name = "UI",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                    FollowableTopic(
                        topic = Topic(
                            id = "2",
                            name = "Tools",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                ),
            ),
            viewModel.onboardingUiState.value,
        )
        assertEquals(
            NewsFeedUiState.Success(
                feed = emptyList(),

                ),
            viewModel.newsFeedState.value,
        )

        collectJob1.cancel()
        collectJob2.cancel()
    }

    @Test
    fun onboardingIsNotShownAfterUserDismissesOnboarding() = runTest {
        val collectJob1 =
            launch(UnconfinedTestDispatcher()) { viewModel.onboardingUiState.collect() }
        val collectJob2 = launch(UnconfinedTestDispatcher()) { viewModel.newsFeedState.collect() }

        topicsRepository.sendTopics(sampleTasks)

        val followedTopicIds = setOf("0", "1")
        val userData = emptyUserData.copy(followedTopics = followedTopicIds)
        userDataRepository.setUserData(userData)
        viewModel.dismissOnboarding()

        assertEquals(
            OnboardingUiState.NotShown,
            viewModel.onboardingUiState.value,
        )
        assertEquals(NewsFeedUiState.Loading, viewModel.newsFeedState.value)

        newsRepository.sendNewsResources(sampleNewsResources)

        assertEquals(
            OnboardingUiState.NotShown,
            viewModel.onboardingUiState.value,
        )
        assertEquals(
            NewsFeedUiState.Success(
                feed = sampleNewsResources.mapToUserNewsResources(userData),
            ),
            viewModel.newsFeedState.value,
        )

        collectJob1.cancel()
        collectJob2.cancel()
    }

    @Test
    fun topicSelectionUpdatesAfterSelectingTopic() = runTest {
        val collectJob1 =
            launch(UnconfinedTestDispatcher()) { viewModel.onboardingUiState.collect() }
        val collectJob2 = launch(UnconfinedTestDispatcher()) { viewModel.newsFeedState.collect() }

        topicsRepository.sendTopics(sampleTasks)
        userDataRepository.setFollowedTopicIds(emptySet())
        newsRepository.sendNewsResources(sampleNewsResources)

        assertEquals(
            OnboardingUiState.Shown(
                topics = sampleTasks.map {
                    FollowableTopic(it, false)
                },
            ),
            viewModel.onboardingUiState.value,
        )
        assertEquals(
            NewsFeedUiState.Success(
                feed = emptyList(),
            ),
            viewModel.newsFeedState.value,
        )

        val followedTopicId = sampleTasks[1].id
        viewModel.updateTopicSelection(followedTopicId, isChecked = true)

        assertEquals(
            OnboardingUiState.Shown(
                topics = sampleTasks.map {
                    FollowableTopic(it, it.id == followedTopicId)
                },
            ),
            viewModel.onboardingUiState.value,
        )

        val userData = emptyUserData.copy(followedTopics = setOf(followedTopicId))

        assertEquals(
            NewsFeedUiState.Success(
                feed = listOf(
                    UserNewsResource(sampleNewsResources[1], userData),
                    UserNewsResource(sampleNewsResources[2], userData),
                ),
            ),
            viewModel.newsFeedState.value,
        )

        collectJob1.cancel()
        collectJob2.cancel()
    }

    @Test
    fun topicSelectionUpdatesAfterUnselectingTopic() = runTest {
        val collectJob1 =
            launch(UnconfinedTestDispatcher()) { viewModel.onboardingUiState.collect() }
        val collectJob2 = launch(UnconfinedTestDispatcher()) { viewModel.newsFeedState.collect() }

        topicsRepository.sendTopics(sampleTasks)
        userDataRepository.setFollowedTopicIds(emptySet())
        newsRepository.sendNewsResources(sampleNewsResources)
        viewModel.updateTopicSelection("1", isChecked = true)
        viewModel.updateTopicSelection("1", isChecked = false)

        advanceUntilIdle()
        assertEquals(
            OnboardingUiState.Shown(
                topics = listOf(
                    FollowableTopic(
                        topic = Topic(
                            id = "0",
                            name = "Headlines",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                    FollowableTopic(
                        topic = Topic(
                            id = "1",
                            name = "UI",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                    FollowableTopic(
                        topic = Topic(
                            id = "2",
                            name = "Tools",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                ),
            ),
            viewModel.onboardingUiState.value,
        )
        assertEquals(
            NewsFeedUiState.Success(
                feed = emptyList(),
            ),
            viewModel.newsFeedState.value,
        )

        collectJob1.cancel()
        collectJob2.cancel()
    }

    @Test
    fun newsResourceSelectionUpdatesAfterLoadingFollowedTopics() = runTest {
        val collectJob1 =
            launch(UnconfinedTestDispatcher()) { viewModel.onboardingUiState.collect() }
        val collectJob2 = launch(UnconfinedTestDispatcher()) { viewModel.newsFeedState.collect() }

        val followedTopicIds = setOf("1")
        val userData = emptyUserData.copy(
            followedTopics = followedTopicIds,
            shouldHideOnboarding = true,
        )

        topicsRepository.sendTopics(sampleTasks)
        userDataRepository.setUserData(userData)
        newsRepository.sendNewsResources(sampleNewsResources)

        val bookmarkedNewsResourceId = "2"
        viewModel.updateNewsResourceSaved(
            newsResourceId = bookmarkedNewsResourceId,
            isChecked = true,
        )

        val userDataExpected = userData.copy(
            bookmarkedNewsResources = setOf(bookmarkedNewsResourceId),
        )

        assertEquals(
            OnboardingUiState.NotShown,
            viewModel.onboardingUiState.value,
        )
        assertEquals(
            NewsFeedUiState.Success(
                feed = listOf(
                    UserNewsResource(newsResource = sampleNewsResources[1], userDataExpected),
                    UserNewsResource(newsResource = sampleNewsResources[2], userDataExpected),
                ),
            ),
            viewModel.newsFeedState.value,
        )

        collectJob1.cancel()
        collectJob2.cancel()
    }

    @Test
    fun deepLinkedNewsResourceIsFetchedAndResetAfterViewing() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.deepLinkedNewsResource.collect() }

        newsRepository.sendNewsResources(sampleNewsResources)
        userDataRepository.setUserData(emptyUserData)
        savedStateHandle[LINKED_NEWS_RESOURCE_ID] = sampleNewsResources.first().id

        assertEquals(
            expected = UserNewsResource(
                newsResource = sampleNewsResources.first(),
                userData = emptyUserData,
            ),
            actual = viewModel.deepLinkedNewsResource.value,
        )

        viewModel.onNewsDeepLinkOpened(
            newsResourceId = sampleNewsResources.first().id,
        )

        assertNull(
            viewModel.deepLinkedNewsResource.value,
        )

        collectJob.cancel()
    }
}

private val sampleTasks = listOf(
    Topic(
        id = "0",
        name = "Headlines",
        shortDescription = "",
        longDescription = "long description",
        url = "URL",
        imageUrl = "image URL",
    ),
    Topic(
        id = "1",
        name = "UI",
        shortDescription = "",
        longDescription = "long description",
        url = "URL",
        imageUrl = "image URL",
    ),
    Topic(
        id = "2",
        name = "Tools",
        shortDescription = "",
        longDescription = "long description",
        url = "URL",
        imageUrl = "image URL",
    ),
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
        topics = listOf(
            Topic(
                id = "0",
                name = "Headlines",
                shortDescription = "",
                longDescription = "long description",
                url = "URL",
                imageUrl = "image URL",
            ),
        ),
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
        topics = listOf(
            Topic(
                id = "1",
                name = "UI",
                shortDescription = "",
                longDescription = "long description",
                url = "URL",
                imageUrl = "image URL",
            ),
        ),
    ),
    NewsResource(
        id = "3",
        title = "Community tip on Paging",
        content = "Tips for using the Paging library from the developer community",
        url = "https://youtu.be/r5JgIyS3t3s",
        headerImageUrl = "https://i.ytimg.com/vi/r5JgIyS3t3s/maxresdefault.jpg",
        publishDate = Instant.parse("2021-11-08T00:00:00.000Z"),
        type = Video,
        topics = listOf(
            Topic(
                id = "1",
                name = "UI",
                shortDescription = "",
                longDescription = "long description",
                url = "URL",
                imageUrl = "image URL",
            ),
        ),
    ),
)
