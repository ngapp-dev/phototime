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

package com.ngapps.phototime.core.data.model

import com.ngapps.phototime.core.model.data.NewsResourceType.Article
import com.ngapps.phototime.core.network.model.NetworkNewsResource
import com.ngapps.phototime.core.network.model.NetworkNewsResourceExpanded
import com.ngapps.phototime.core.network.model.NetworkTopic
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals

class NetworkEntityKtTest {

    @Test
    fun network_topic_can_be_mapped_to_topic_entity() {
        val networkModel = NetworkTopic(
            id = "0",
            name = "Test",
            shortDescription = "short description",
            longDescription = "long description",
            url = "URL",
            imageUrl = "image URL",
        )
        val entity = networkModel.asEntity()

        assertEquals("0", entity.id)
        assertEquals("Test", entity.name)
        assertEquals("short description", entity.shortDescription)
        assertEquals("long description", entity.longDescription)
        assertEquals("URL", entity.url)
        assertEquals("image URL", entity.imageUrl)
    }

    @Test
    fun network_news_resource_can_be_mapped_to_news_resource_entity() {
        val networkModel =
            NetworkNewsResource(
                id = "0",
                title = "title",
                content = "content",
                url = "url",
                headerImageUrl = "headerImageUrl",
                publishDate = Instant.fromEpochMilliseconds(1),
                type = Article,
            )
        val entity = networkModel.asEntity()

        assertEquals("0", entity.id)
        assertEquals("title", entity.title)
        assertEquals("content", entity.content)
        assertEquals("url", entity.url)
        assertEquals("headerImageUrl", entity.headerImageUrl)
        assertEquals(Instant.fromEpochMilliseconds(1), entity.publishDate)
        assertEquals(Article, entity.type)

        val expandedNetworkModel =
            NetworkNewsResourceExpanded(
                id = "0",
                title = "title",
                content = "content",
                url = "url",
                headerImageUrl = "headerImageUrl",
                publishDate = Instant.fromEpochMilliseconds(1),
                type = Article,
            )

        val entityFromExpanded = expandedNetworkModel.asEntity()

        assertEquals("0", entityFromExpanded.id)
        assertEquals("title", entityFromExpanded.title)
        assertEquals("content", entityFromExpanded.content)
        assertEquals("url", entityFromExpanded.url)
        assertEquals("headerImageUrl", entityFromExpanded.headerImageUrl)
        assertEquals(Instant.fromEpochMilliseconds(1), entityFromExpanded.publishDate)
        assertEquals(Article, entityFromExpanded.type)
    }
}
