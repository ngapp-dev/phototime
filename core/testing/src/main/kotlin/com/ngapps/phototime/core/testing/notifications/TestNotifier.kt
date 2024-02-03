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

package com.ngapps.phototime.core.testing.notifications

import com.ngapps.phototime.core.model.data.location.LocationResource
import com.ngapps.phototime.core.model.data.contact.ContactResource
import com.ngapps.phototime.core.model.data.moodboard.MoodboardResource
import com.ngapps.phototime.core.model.data.shoot.ShootResource
import com.ngapps.phototime.core.model.data.task.TaskResource
import com.ngapps.phototime.core.model.data.user.UserResource
import com.ngapps.phototime.core.notifications.Notifier

/**
 * Aggregates news resources that have been notified for addition
 */
class TestNotifier : Notifier {

    private val mutableAddedNewTaskResources = mutableListOf<List<TaskResource>>()

    private val mutableAddedNewLocationResources = mutableListOf<List<LocationResource>>()

    private val mutableAddedNewContactResources = mutableListOf<List<ContactResource>>()

    private val mutableAddedNewShootResources = mutableListOf<List<ShootResource>>()

    private val mutableAddedNewMoodboardResources = mutableListOf<List<MoodboardResource>>()

    private val mutableAddedNewUserResources = mutableListOf<UserResource>()

    override fun postTaskNotifications(taskResources: List<TaskResource>) {
        mutableAddedNewTaskResources.add(taskResources)
    }

    override fun postLocationNotifications(locationResources: List<LocationResource>) {
        mutableAddedNewLocationResources.add(locationResources)
    }

    override fun postContactNotifications(contactResources: List<ContactResource>) {
        mutableAddedNewContactResources.add(contactResources)
    }

    override fun postShootNotifications(shootResources: List<ShootResource>) {
        mutableAddedNewShootResources.add(shootResources)
    }

    override fun postMoodboardNotifications(moodboardResources: List<MoodboardResource>) {
        mutableAddedNewMoodboardResources.add(moodboardResources)
    }

    override fun postUserNotifications(userResource: UserResource) {
        mutableAddedNewUserResources.add(userResource)
    }
}
