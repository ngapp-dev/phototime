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

package com.ngapps.phototime.core.data.testdoubles

import com.ngapps.phototime.core.database.dao.NewsResourceDao
import com.ngapps.phototime.core.database.model.NewsResourceEntity
import com.ngapps.phototime.core.database.model.NewsResourceTopicCrossRef
import com.ngapps.phototime.core.database.model.PopulatedNewsResource
import com.ngapps.phototime.core.database.model.TopicEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

val filteredInterestsIds = setOf("1")
val nonPresentInterestsIds = setOf("2")

/**
 * Test double for [NewsResourceDao]
 */
class TestNewsResourceDao : NewsResourceDao {

    private var entitiesStateFlow = MutableStateFlow(
        emptyList<NewsResourceEntity>(),
    )

    internal var topicCrossReferences: List<NewsResourceTopicCrossRef> = listOf()

    override fun getNewsResources(
        useFilterTopicIds: Boolean,
        filterTopicIds: Set<String>,
        useFilterNewsIds: Boolean,
        filterNewsIds: Set<String>,
    ): Flow<List<PopulatedNewsResource>> =
        entitiesStateFlow
            .map { newsResourceEntities ->
                newsResourceEntities.map { entity ->
                    entity.asPopulatedNewsResource(topicCrossReferences)
                }
            }
            .map { resources ->
                var result = resources
                if (useFilterTopicIds) {
                    result = result.filter { resource ->
                        resource.topics.any { it.id in filterTopicIds }
                    }
                }
                if (useFilterNewsIds) {
                    result = result.filter { resource ->
                        resource.entity.id in filterNewsIds
                    }
                }
                result
            }

    override suspend fun insertOrIgnoreNewsResources(
        entities: List<NewsResourceEntity>,
    ): List<Long> {
        entitiesStateFlow.update { oldValues ->
            // NOTE: Old values come first so new values don't overwrite them
            (oldValues + entities)
                .distinctBy(NewsResourceEntity::id)
                .sortedWith(
                    compareBy(NewsResourceEntity::publishDate).reversed(),
                )
        }
        // NOTE: Assume no conflicts on insert
        return entities.map { it.id.toLong() }
    }

    override suspend fun upsertNewsResources(newsResourceEntities: List<NewsResourceEntity>) {
        entitiesStateFlow.update { oldValues ->
            // NOTE: New values come first so they overwrite old values
            (newsResourceEntities + oldValues)
                .distinctBy(NewsResourceEntity::id)
                .sortedWith(
                    compareBy(NewsResourceEntity::publishDate).reversed(),
                )
        }
    }

    override suspend fun insertOrIgnoreTopicCrossRefEntities(
        newsResourceTopicCrossReferences: List<NewsResourceTopicCrossRef>,
    ) {
        // NOTE: Keep old values over new ones
        topicCrossReferences = (topicCrossReferences + newsResourceTopicCrossReferences)
            .distinctBy { it.newsResourceId to it.topicId }
    }

    override suspend fun deleteNewsResources(ids: List<String>) {
        val idSet = ids.toSet()
        entitiesStateFlow.update { entities ->
            entities.filterNot { idSet.contains(it.id) }
        }
    }
}

private fun NewsResourceEntity.asPopulatedNewsResource(
    topicCrossReferences: List<NewsResourceTopicCrossRef>,
) = PopulatedNewsResource(
    entity = this,
    topics = topicCrossReferences
        .filter { it.newsResourceId == id }
        .map { newsResourceTopicCrossRef ->
            TopicEntity(
                id = newsResourceTopicCrossRef.topicId,
                name = "name",
                shortDescription = "short description",
                longDescription = "long description",
                url = "URL",
                imageUrl = "image URL",
            )
        },
)
