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

package com.ngapps.phototime.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.ngapps.phototime.core.database.PtDatabase
import com.ngapps.phototime.core.database.model.NewsResourceEntity
import com.ngapps.phototime.core.database.model.NewsResourceTopicCrossRef
import com.ngapps.phototime.core.database.model.TopicEntity
import com.ngapps.phototime.core.database.model.asExternalModel
import com.ngapps.phototime.core.model.data.NewsResourceType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class NewsResourceDaoTest {

    private lateinit var newsResourceDao: NewsResourceDao
    private lateinit var topicDao: TopicDao
    private lateinit var db: PtDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            PtDatabase::class.java,
        ).build()
        newsResourceDao = db.newsResourceDao()
        topicDao = db.topicDao()
    }

    @Test
    fun newsResourceDao_fetches_items_by_descending_publish_date() = runTest {
        val newsResourceEntities = listOf(
            testNewsResource(
                id = "0",
                millisSinceEpoch = 0,
            ),
            testNewsResource(
                id = "1",
                millisSinceEpoch = 3,
            ),
            testNewsResource(
                id = "2",
                millisSinceEpoch = 1,
            ),
            testNewsResource(
                id = "3",
                millisSinceEpoch = 2,
            ),
        )
        newsResourceDao.upsertNewsResources(
            newsResourceEntities,
        )

        val savedNewsResourceEntities = newsResourceDao.getNewsResources()
            .first()

        assertEquals(
            listOf(3L, 2L, 1L, 0L),
            savedNewsResourceEntities.map {
                it.asExternalModel().publishDate.toEpochMilliseconds()
            },
        )
    }

    @Test
    fun newsResourceDao_filters_items_by_news_ids_by_descending_publish_date() = runTest {
        val newsResourceEntities = listOf(
            testNewsResource(
                id = "0",
                millisSinceEpoch = 0,
            ),
            testNewsResource(
                id = "1",
                millisSinceEpoch = 3,
            ),
            testNewsResource(
                id = "2",
                millisSinceEpoch = 1,
            ),
            testNewsResource(
                id = "3",
                millisSinceEpoch = 2,
            ),
        )
        newsResourceDao.upsertNewsResources(
            newsResourceEntities,
        )

        val savedNewsResourceEntities = newsResourceDao.getNewsResources(
            useFilterNewsIds = true,
            filterNewsIds = setOf("3", "0"),
        )
            .first()

        assertEquals(
            listOf("3", "0"),
            savedNewsResourceEntities.map {
                it.entity.id
            },
        )
    }

    @Test
    fun newsResourceDao_filters_items_by_topic_ids_by_descending_publish_date() = runTest {
        val topicEntities = listOf(
            testTopicEntity(
                id = "1",
                name = "1",
            ),
            testTopicEntity(
                id = "2",
                name = "2",
            ),
        )
        val newsResourceEntities = listOf(
            testNewsResource(
                id = "0",
                millisSinceEpoch = 0,
            ),
            testNewsResource(
                id = "1",
                millisSinceEpoch = 3,
            ),
            testNewsResource(
                id = "2",
                millisSinceEpoch = 1,
            ),
            testNewsResource(
                id = "3",
                millisSinceEpoch = 2,
            ),
        )
        val newsResourceTopicCrossRefEntities = topicEntities.mapIndexed { index, topicEntity ->
            NewsResourceTopicCrossRef(
                newsResourceId = index.toString(),
                topicId = topicEntity.id,
            )
        }

        topicDao.insertOrIgnoreTopics(
            topicEntities = topicEntities,
        )
        newsResourceDao.upsertNewsResources(
            newsResourceEntities,
        )
        newsResourceDao.insertOrIgnoreTopicCrossRefEntities(
            newsResourceTopicCrossRefEntities,
        )

        val filteredNewsResources = newsResourceDao.getNewsResources(
            useFilterTopicIds = true,
            filterTopicIds = topicEntities
                .map(TopicEntity::id)
                .toSet(),
        ).first()

        assertEquals(
            listOf("1", "0"),
            filteredNewsResources.map { it.entity.id },
        )
    }

    @Test
    fun newsResourceDao_filters_items_by_news_and_topic_ids_by_descending_publish_date() = runTest {
        val topicEntities = listOf(
            testTopicEntity(
                id = "1",
                name = "1",
            ),
            testTopicEntity(
                id = "2",
                name = "2",
            ),
        )
        val newsResourceEntities = listOf(
            testNewsResource(
                id = "0",
                millisSinceEpoch = 0,
            ),
            testNewsResource(
                id = "1",
                millisSinceEpoch = 3,
            ),
            testNewsResource(
                id = "2",
                millisSinceEpoch = 1,
            ),
            testNewsResource(
                id = "3",
                millisSinceEpoch = 2,
            ),
        )
        val newsResourceTopicCrossRefEntities = topicEntities.mapIndexed { index, topicEntity ->
            NewsResourceTopicCrossRef(
                newsResourceId = index.toString(),
                topicId = topicEntity.id,
            )
        }

        topicDao.insertOrIgnoreTopics(
            topicEntities = topicEntities,
        )
        newsResourceDao.upsertNewsResources(
            newsResourceEntities,
        )
        newsResourceDao.insertOrIgnoreTopicCrossRefEntities(
            newsResourceTopicCrossRefEntities,
        )

        val filteredNewsResources = newsResourceDao.getNewsResources(
            useFilterTopicIds = true,
            filterTopicIds = topicEntities
                .map(TopicEntity::id)
                .toSet(),
            useFilterNewsIds = true,
            filterNewsIds = setOf("1"),
        ).first()

        assertEquals(
            listOf("1"),
            filteredNewsResources.map { it.entity.id },
        )
    }

    @Test
    fun newsResourceDao_deletes_items_by_ids() =
        runTest {
            val newsResourceEntities = listOf(
                testNewsResource(
                    id = "0",
                    millisSinceEpoch = 0,
                ),
                testNewsResource(
                    id = "1",
                    millisSinceEpoch = 3,
                ),
                testNewsResource(
                    id = "2",
                    millisSinceEpoch = 1,
                ),
                testNewsResource(
                    id = "3",
                    millisSinceEpoch = 2,
                ),
            )
            newsResourceDao.upsertNewsResources(newsResourceEntities)

            val (toDelete, toKeep) = newsResourceEntities.partition { it.id.toInt() % 2 == 0 }

            newsResourceDao.deleteNewsResources(
                toDelete.map(NewsResourceEntity::id),
            )

            assertEquals(
                toKeep.map(NewsResourceEntity::id)
                    .toSet(),
                newsResourceDao.getNewsResources().first()
                    .map { it.entity.id }
                    .toSet(),
            )
        }
}

private fun testTopicEntity(
    id: String = "0",
    name: String,
) = TopicEntity(
    id = id,
    name = name,
    shortDescription = "",
    longDescription = "",
    url = "",
    imageUrl = "",
)

private fun testNewsResource(
    id: String = "0",
    millisSinceEpoch: Long = 0,
) = NewsResourceEntity(
    id = id,
    title = "",
    content = "",
    url = "",
    headerImageUrl = "",
    publishDate = Instant.fromEpochMilliseconds(millisSinceEpoch),
    type = NewsResourceType.DAC,
)
