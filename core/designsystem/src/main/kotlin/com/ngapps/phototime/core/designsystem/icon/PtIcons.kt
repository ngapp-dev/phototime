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

package com.ngapps.phototime.core.designsystem.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.AssignmentInd
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SmartDisplay
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.Upcoming
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Grid3x3
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShortText
import androidx.compose.material.icons.rounded.Upcoming
import androidx.compose.material.icons.rounded.ViewDay
import androidx.compose.ui.graphics.vector.ImageVector
import com.ngapps.phototime.core.designsystem.R

/**
 * Shot in time icons. Material icons are [ImageVector]s, custom icons are drawable resource IDs.
 */
object PtIcons {
    val Add = Icons.Default.Add
    val ArrowBack = Icons.Rounded.ArrowBackIosNew
    val Bookmark = Icons.Rounded.Bookmark
    val BookmarkBorder = Icons.Rounded.BookmarkBorder
    val Bookmarks = Icons.Rounded.Bookmarks
    val BookmarksBorder = Icons.Outlined.Bookmarks
    val Check = Icons.Rounded.Check
    val Grid3x3 = Icons.Rounded.Grid3x3
    val Grid = Icons.Outlined.GridOn
    val SmartDisplay = Icons.Outlined.SmartDisplay
    val Awesome = Icons.Filled.AutoAwesome
    val Error = Icons.Default.ErrorOutline
    val Edit = R.drawable.edit
    val Delete = R.drawable.delete
    val Edit1 = Icons.Outlined.Edit
    val Delete1 = Icons.Outlined.Delete
    val More = Icons.Filled.MoreVert
    val Camera = Icons.Filled.CameraAlt
    val Gallery = Icons.Filled.Image
    val DateTime = Icons.Filled.DateRange
    val Keyboard = Icons.Outlined.Keyboard
    val Schedule = Icons.Outlined.Schedule

    val LocationBorder = Icons.Outlined.LocationOn
    val MoreVert = Icons.Default.MoreVert
    val Person = Icons.Rounded.Person
    val Email = Icons.Rounded.Email
    val Search = Icons.Rounded.Search
    val Settings = Icons.Rounded.Settings
    val ShortText = Icons.Rounded.ShortText

    val Upcoming = Icons.Rounded.Upcoming
    val UpcomingBorder = Icons.Outlined.Upcoming
    val ViewDay = Icons.Rounded.ViewDay
    val Logout = Icons.Filled.ExitToApp

    val KeyboardArrowLeft = Icons.Default.KeyboardArrowLeft
    val KeyboardArrowRight = Icons.Default.KeyboardArrowRight
    val KeyboardArrowUp = Icons.Default.KeyboardArrowUp
    val KeyboardArrowDown = Icons.Default.KeyboardArrowDown
    val PersonAdd = Icons.Default.PersonAdd
    val Assignment = Icons.Outlined.AssignmentInd

    val LeftBracket = R.drawable.leftbracket
    val RightBracket = R.drawable.rightbracket
    val Instagram = R.drawable.instagram
    val Phone = R.drawable.phone
    val Pin = R.drawable.pin
    val Shoot = R.drawable.shoot
    val ShootNew = R.drawable.shoot_new
    val Expand = Icons.Default.ExpandMore
    val Marker = R.drawable.marker
    val Google = R.drawable.google
    val Close = R.drawable.close
    val Close1 = Icons.Outlined.Close
    val Download = R.drawable.download
    val Download1 = Icons.Outlined.Download
    // Bottom Navigation bar

    val Location = Icons.Outlined.LocationOn
    val Contact = Icons.Outlined.Person
    val Home = Icons.Outlined.Home
    val Task = Icons.Outlined.TaskAlt
    val Note = Icons.Outlined.Lightbulb

//    val Location = R.drawable.locations
//    val Contact = R.drawable.contacts
//    val Home = R.drawable.home
//    val Task = R.drawable.tasks
//    val Note = R.drawable.notes
}
