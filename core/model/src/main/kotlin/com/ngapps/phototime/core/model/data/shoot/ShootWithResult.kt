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

package com.ngapps.phototime.core.model.data.shoot

import com.ngapps.phototime.core.model.data.contact.ContactResource
import com.ngapps.phototime.core.model.data.location.LocationResource
import com.ngapps.phototime.core.model.data.moodboard.MoodboardResource
import com.ngapps.phototime.core.model.data.task.TaskResource

/**
 * External data layer representation of a Pt Shoot with Moodboards, Contacts, Locations and Tasks
 */
data class ShootResourceWithData(
    val shoot: List<ShootResource>,
    val moodboards: List<MoodboardResource>,
    val contacts: List<ContactResource>,
    val locations: List<LocationResource>,
    val tasks: List<TaskResource>
)