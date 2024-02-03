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

package com.ngapps.phototime.core.database.model

import com.ngapps.phototime.core.model.data.NewsResource
import com.ngapps.phototime.core.model.data.NewsResourceType.Video
import com.ngapps.phototime.core.model.data.Topic
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals

class PopulatedNewsResourceKtTest {
    @Test
    fun populated_news_resource_can_be_mapped_to_news_resource() {
        val populatedNewsResource = PopulatedNewsResource(
            entity = NewsResourceEntity(
                id = "1",
                title = "news",
                content = "Hilt",
                url = "url",
                headerImageUrl = "headerImageUrl",
                type = Video,
                publishDate = Instant.fromEpochMilliseconds(1),
            ),
            topics = listOf(
                TopicEntity(
                    id = "3",
                    name = "name",
                    shortDescription = "short description",
                    longDescription = "long description",
                    url = "URL",
                    imageUrl = "image URL",
                ),
            ),
        )
        val newsResource = populatedNewsResource.asExternalModel()

        assertEquals(
            NewsResource(
                id = "1",
                title = "news",
                content = "Hilt",
                url = "url",
                headerImageUrl = "headerImageUrl",
                type = Video,
                publishDate = Instant.fromEpochMilliseconds(1),
                topics = listOf(
                    Topic(
                        id = "3",
                        name = "name",
                        shortDescription = "short description",
                        longDescription = "long description",
                        url = "URL",
                        imageUrl = "image URL",
                    ),
                ),
            ),
            newsResource,
        )
    }
}