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

package com.ngapps.phototime.core.datastore

import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListToMapMigrationTest {

    @Test
    fun ListToMapMigration_should_migrate_topic_ids() = runTest {
        // NOTE: Set up existing preferences with topic ids
        val preMigrationUserPreferences = userPreferences {
            deprecatedFollowedTopicIds.addAll(listOf("1", "2", "3"))
        }
        // NOTE: Assert that there are no topic ids in the map yet
        assertEquals(
            emptyMap<String, Boolean>(),
            preMigrationUserPreferences.followedTopicIdsMap,
        )

        // NOTE: Run the migration
        val postMigrationUserPreferences =
            ListToMapMigration.migrate(preMigrationUserPreferences)

        // NOTE: Assert the deprecated topic ids have been migrated to the topic ids map
        assertEquals(
            mapOf("1" to true, "2" to true, "3" to true),
            postMigrationUserPreferences.followedTopicIdsMap,
        )

        // NOTE: Assert that the migration has been marked complete
        assertTrue(postMigrationUserPreferences.hasDoneListToMapMigration)
    }

    @Test
    fun ListToMapMigration_should_migrate_author_ids() = runTest {
        // NOTE: Set up existing preferences with author ids
        val preMigrationUserPreferences = userPreferences {
            deprecatedFollowedAuthorIds.addAll(listOf("4", "5", "6"))
        }
        // NOTE: Assert that there are no author ids in the map yet
        assertEquals(
            emptyMap<String, Boolean>(),
            preMigrationUserPreferences.followedAuthorIdsMap,
        )

        // NOTE: Run the migration
        val postMigrationUserPreferences =
            ListToMapMigration.migrate(preMigrationUserPreferences)

        // NOTE: Assert the deprecated author ids have been migrated to the author ids map
        assertEquals(
            mapOf("4" to true, "5" to true, "6" to true),
            postMigrationUserPreferences.followedAuthorIdsMap,
        )

        // NOTE: Assert that the migration has been marked complete
        assertTrue(postMigrationUserPreferences.hasDoneListToMapMigration)
    }

    @Test
    fun ListToMapMigration_should_migrate_bookmarks() = runTest {
        // NOTE: Set up existing preferences with bookmarks
        val preMigrationUserPreferences = userPreferences {
            deprecatedBookmarkedNewsResourceIds.addAll(listOf("7", "8", "9"))
        }
        // NOTE: Assert that there are no bookmarks in the map yet
        assertEquals(
            emptyMap<String, Boolean>(),
            preMigrationUserPreferences.bookmarkedNewsResourceIdsMap,
        )

        // NOTE: Run the migration
        val postMigrationUserPreferences =
            ListToMapMigration.migrate(preMigrationUserPreferences)

        // NOTE: Assert the deprecated bookmarks have been migrated to the bookmarks map
        assertEquals(
            mapOf("7" to true, "8" to true, "9" to true),
            postMigrationUserPreferences.bookmarkedNewsResourceIdsMap,
        )

        // NOTE: Assert that the migration has been marked complete
        assertTrue(postMigrationUserPreferences.hasDoneListToMapMigration)
    }
}
