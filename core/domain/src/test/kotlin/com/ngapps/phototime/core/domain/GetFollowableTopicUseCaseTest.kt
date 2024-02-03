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

package com.ngapps.phototime.core.domain

import com.ngapps.phototime.core.model.data.FollowableTopic
import com.ngapps.phototime.core.model.data.Topic
import com.ngapps.phototime.core.testing.repository.TestTopicsRepository
import com.ngapps.phototime.core.testing.repository.TestUserDataRepository
import com.ngapps.phototime.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GetFollowableTopicUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val tasksRepository = TestTopicsRepository()
    private val userDataRepository = TestUserDataRepository()

    val useCase = GetFollowableTopicsUseCase(
        tasksRepository,
        userDataRepository,
    )

    @Test
    fun whenNoParams_followableTopicsAreReturnedWithNoSorting() = runTest {
        // NOTE: Obtain a stream of followable topics.
        val followableTopics = useCase()

        // NOTE: Send some test topics and their followed state.
        tasksRepository.sendTopics(testTasks)
        userDataRepository.setFollowedTopicIds(setOf(testTasks[0].id, testTasks[2].id))

        // NOTE: Check that the order hasn't changed and that the correct topics are marked as followed.
        assertEquals(
            listOf(
                FollowableTopic(testTasks[0], true),
                FollowableTopic(testTasks[1], false),
                FollowableTopic(testTasks[2], true),
            ),
            followableTopics.first(),
        )
    }

    @Test
    fun whenSortOrderIsByName_topicsSortedByNameAreReturned() = runTest {
        // NOTE: Obtain a stream of followable topics, sorted by name.
        val followableTopics = useCase(
            sortBy = TopicSortField.NAME,
        )

        // NOTE: Send some test topics and their followed state.
        tasksRepository.sendTopics(testTasks)
        userDataRepository.setFollowedTopicIds(setOf())

        // NOTE: Check that the followable topics are sorted by the topic name.
        assertEquals(
            followableTopics.first(),
            testTasks
                .sortedBy { it.name }
                .map {
                    FollowableTopic(it, false)
                },
        )
    }
}

private val testTasks = listOf(
    Topic("1", "Headlines", "", "", "", ""),
    Topic("2", "Android Studio", "", "", "", ""),
    Topic("3", "Compose", "", "", "", ""),
)
