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

package com.ngapps.phototime.ptcatalog.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ngapps.phototime.core.designsystem.component.PtCategorySelector
import com.ngapps.phototime.core.designsystem.component.PtButton
import com.ngapps.phototime.core.designsystem.component.PtFilterChip
import com.ngapps.phototime.core.designsystem.component.PtIconToggleButton
import com.ngapps.phototime.core.designsystem.component.PtNavigationBar
import com.ngapps.phototime.core.designsystem.component.PtNavigationBarItem
import com.ngapps.phototime.core.designsystem.component.PtNavigationRail
import com.ngapps.phototime.core.designsystem.component.PtNavigationRailItem
import com.ngapps.phototime.core.designsystem.component.PtOutlinedButton
import com.ngapps.phototime.core.designsystem.component.PtPasswordTextField
import com.ngapps.phototime.core.designsystem.component.PtSwitch
import com.ngapps.phototime.core.designsystem.component.PtTab
import com.ngapps.phototime.core.designsystem.component.PtTabRow
import com.ngapps.phototime.core.designsystem.component.PtTextButton
import com.ngapps.phototime.core.designsystem.component.PtTextFieldWithErrorState
import com.ngapps.phototime.core.designsystem.component.PtTopicTag
import com.ngapps.phototime.core.designsystem.component.PtViewToggleButton
import com.ngapps.phototime.core.designsystem.icon.PtIcons
import com.ngapps.phototime.core.designsystem.theme.PtTheme

/**
 * Photo time component catalog.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PtCatalog() {
    PtTheme {
        Surface {
            val contentPadding = WindowInsets
                .systemBars
                .add(WindowInsets(left = 16.dp, top = 16.dp, right = 16.dp, bottom = 16.dp))
                .asPaddingValues()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Text(
                        text = "Pt Catalog",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
                item { Text("Buttons", Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        PtButton(onClick = {}) {
                            Text(text = "Enabled")
                        }
                        PtOutlinedButton(onClick = {}) {
                            Text(text = "Enabled")
                        }
                        PtTextButton(onClick = {}) {
                            Text(text = "Enabled")
                        }
                    }
                }
                item { Text("Disabled buttons", Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        PtButton(
                            onClick = {},
                            enabled = false,
                        ) {
                            Text(text = "Disabled")
                        }
                        PtOutlinedButton(
                            onClick = {},
                            enabled = false,
                        ) {
                            Text(text = "Disabled")
                        }
                        PtTextButton(
                            onClick = {},
                            enabled = false,
                        ) {
                            Text(text = "Disabled")
                        }
                    }
                }
                item { Text("Buttons with leading icons", Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        PtButton(
                            onClick = {},
                            text = { Text(text = "Enabled") },
                            leadingIcon = {
                                Icon(imageVector = PtIcons.Add, contentDescription = null)
                            },
                        )
                        PtOutlinedButton(
                            onClick = {},
                            text = { Text(text = "Enabled") },
                            leadingIcon = {
                                Icon(imageVector = PtIcons.Add, contentDescription = null)
                            },
                        )
                        PtTextButton(
                            onClick = {},
                            text = { Text(text = "Enabled") },
                            leadingIcon = {
                                Icon(imageVector = PtIcons.Add, contentDescription = null)
                            },
                        )
                    }
                }
                item { Text("Disabled buttons with leading icons", Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        PtButton(
                            onClick = {},
                            enabled = false,
                            text = { Text(text = "Disabled") },
                            leadingIcon = {
                                Icon(imageVector = PtIcons.Add, contentDescription = null)
                            },
                        )
                        PtOutlinedButton(
                            onClick = {},
                            enabled = false,
                            text = { Text(text = "Disabled") },
                            leadingIcon = {
                                Icon(imageVector = PtIcons.Add, contentDescription = null)
                            },
                        )
                        PtTextButton(
                            onClick = {},
                            enabled = false,
                            text = { Text(text = "Disabled") },
                            leadingIcon = {
                                Icon(imageVector = PtIcons.Add, contentDescription = null)
                            },
                        )
                    }
                }
                item { Text("Dropdown menus", Modifier.padding(top = 16.dp)) }
                item { Text("Chips", Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        var firstChecked by remember { mutableStateOf(false) }
                        PtFilterChip(
                            selected = firstChecked,
                            onSelectedChange = { checked -> firstChecked = checked },
                            label = { Text(text = "Enabled") },
                        )
                        var secondChecked by remember { mutableStateOf(true) }
                        PtFilterChip(
                            selected = secondChecked,
                            onSelectedChange = { checked -> secondChecked = checked },
                            label = { Text(text = "Enabled") },
                        )
                        PtFilterChip(
                            selected = false,
                            onSelectedChange = {},
                            enabled = false,
                            label = { Text(text = "Disabled") },
                        )
                        PtFilterChip(
                            selected = true,
                            onSelectedChange = {},
                            enabled = false,
                            label = { Text(text = "Disabled") },
                        )
                    }
                }
                item { Text("Icon buttons", Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        var firstChecked by remember { mutableStateOf(false) }
                        PtIconToggleButton(
                            checked = firstChecked,
                            onCheckedChange = { checked -> firstChecked = checked },
                            icon = {
                                Icon(
                                    imageVector = PtIcons.BookmarkBorder,
                                    contentDescription = null,
                                )
                            },
                            checkedIcon = {
                                Icon(
                                    imageVector = PtIcons.Bookmark,
                                    contentDescription = null,
                                )
                            },
                        )
                        var secondChecked by remember { mutableStateOf(true) }
                        PtIconToggleButton(
                            checked = secondChecked,
                            onCheckedChange = { checked -> secondChecked = checked },
                            icon = {
                                Icon(
                                    imageVector = PtIcons.BookmarkBorder,
                                    contentDescription = null,
                                )
                            },
                            checkedIcon = {
                                Icon(
                                    imageVector = PtIcons.Bookmark,
                                    contentDescription = null,
                                )
                            },
                        )
                        PtIconToggleButton(
                            checked = false,
                            onCheckedChange = {},
                            icon = {
                                Icon(
                                    imageVector = PtIcons.BookmarkBorder,
                                    contentDescription = null,
                                )
                            },
                            checkedIcon = {
                                Icon(
                                    imageVector = PtIcons.Bookmark,
                                    contentDescription = null,
                                )
                            },
                            enabled = false,
                        )
                        PtIconToggleButton(
                            checked = true,
                            onCheckedChange = {},
                            icon = {
                                Icon(
                                    imageVector = PtIcons.BookmarkBorder,
                                    contentDescription = null,
                                )
                            },
                            checkedIcon = {
                                Icon(
                                    imageVector = PtIcons.Bookmark,
                                    contentDescription = null,
                                )
                            },
                            enabled = false,
                        )
                    }
                }
                item { Text("Switches", Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        var firstChecked by remember { mutableStateOf(false) }
                        PtSwitch(
                            checked = firstChecked,
                            onCheckedChange = { checked -> firstChecked = checked },
                            icon = PtIcons.Task,
                        )
                        var secondChecked by remember { mutableStateOf(true) }
                        PtSwitch(
                            checked = secondChecked,
                            onCheckedChange = { checked -> secondChecked = checked },
                            icon = PtIcons.Task,
                        )
                    }
                }
                item { Text("View toggle", Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        var firstExpanded by remember { mutableStateOf(false) }
                        PtViewToggleButton(
                            expanded = firstExpanded,
                            onExpandedChange = { expanded -> firstExpanded = expanded },
                            compactText = { Text(text = "Compact view") },
                            expandedText = { Text(text = "Expanded view") },
                        )
                        var secondExpanded by remember { mutableStateOf(true) }
                        PtViewToggleButton(
                            expanded = secondExpanded,
                            onExpandedChange = { expanded -> secondExpanded = expanded },
                            compactText = { Text(text = "Compact view") },
                            expandedText = { Text(text = "Expanded view") },
                        )
                        PtViewToggleButton(
                            expanded = false,
                            onExpandedChange = {},
                            compactText = { Text(text = "Disabled") },
                            expandedText = { Text(text = "Disabled") },
                            enabled = false,
                        )
                    }
                }
                item { Text("Tags", Modifier.padding(top = 16.dp)) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        PtTopicTag(
                            followed = true,
                            onClick = {},
                            text = { Text(text = "Topic 1".uppercase()) },
                        )
                        PtTopicTag(
                            followed = false,
                            onClick = {},
                            text = { Text(text = "Topic 2".uppercase()) },
                        )
                        PtTopicTag(
                            followed = false,
                            onClick = {},
                            text = { Text(text = "Disabled".uppercase()) },
                            enabled = false,
                        )
                    }
                }
                item { Text("Tabs", Modifier.padding(top = 16.dp)) }
                item {
                    var selectedTabIndex by remember { mutableStateOf(0) }
                    val titles = listOf("Topics", "People")
                    PtTabRow(selectedTabIndex = selectedTabIndex) {
                        titles.forEachIndexed { index, title ->
                            PtTab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(text = title) },
                            )
                        }
                    }
                }
                item { Text("Text Fields", Modifier.padding(top = 16.dp)) }
                item {
                    val errorMessage = "Text input too long"
                    var text by rememberSaveable { mutableStateOf("") }
                    var isError by rememberSaveable { mutableStateOf(false) }
                    val charLimit = 10
                    fun validate(text: String) {
                        isError = text.length > charLimit
                    }

                    val focusManager = LocalFocusManager.current

                    PtTextFieldWithErrorState(
                        text = text,
                        label = "Username",
                        isError = isError,
                        validate = { validate(it) },
                        errorMessage = errorMessage,
                        textResult = { text = it },
                        onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    )
                }
                item {
                    val errorMessage = "Email is not correct"
                    var text by rememberSaveable { mutableStateOf("") }
                    var isError by rememberSaveable { mutableStateOf(false) }
                    fun validate(text: String) {
                        isError = !android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()
                    }

                    val focusManager = LocalFocusManager.current
                    PtTextFieldWithErrorState(
                        text = text,
                        label = "Email",
                        isError = isError,
                        validate = { validate(it) },
                        errorMessage = errorMessage,
                        textResult = { text = it },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                        leadingIcon = PtIcons.Email,
                        trailingIcon = PtIcons.Person,
                        onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    )
                }
                item {
                    val errorMessage = "Text input too long"
                    var text by rememberSaveable { mutableStateOf("") }
                    var isError by rememberSaveable { mutableStateOf(false) }
                    val charLimit = 10
                    fun validate(text: String) {
                        isError = text.length > charLimit
                    }

                    val focusManager = LocalFocusManager.current
                    PtPasswordTextField(
                        text = text,
                        label = "Password",
                        isError = isError,
                        validate = { validate(it) },
                        errorMessage = errorMessage,
                        textResult = { text = it },
                        onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    )
                }
                item { Text("Navigation bar", Modifier.padding(top = 16.dp)) }
                item {
                    var selectedItem by remember { mutableStateOf(0) }
                    val items = listOf("Locations", "Contacts", "Home", "Tasks", "Notes")
                    val icons = listOf(
                        PtIcons.Location,
                        PtIcons.Contact,
                        PtIcons.Home,
                        PtIcons.Task,
                        PtIcons.Note,
                    )
                    val selectedIcons = listOf(
                        PtIcons.Location,
                        PtIcons.Contact,
                        PtIcons.Home,
                        PtIcons.Task,
                        PtIcons.Note,
                    )
                    PtNavigationBar {
                        items.forEachIndexed { index, item ->
                            PtNavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = icons[index],
                                        contentDescription = item,
                                    )
                                },
                                selectedIcon = {
                                    Icon(
                                        imageVector = selectedIcons[index],
                                        contentDescription = item,
                                    )
                                },
                                label = { Text(item) },
                                selected = selectedItem == index,
                                onClick = { selectedItem = index },
                            )
                        }
                    }
                    PtNavigationBar {
                        items.forEachIndexed { index, item ->
                            PtNavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = icons[index],
                                        contentDescription = item,
                                    )
                                },
                                selectedIcon = {
                                    Icon(
                                        imageVector = selectedIcons[index],
                                        contentDescription = item,
                                    )
                                },
                                selected = selectedItem == index,
                                onClick = { selectedItem = index },
                            )
                        }
                    }
                }
                item { Text("Navigation Rail", Modifier.padding(top = 16.dp)) }
                item {
                    var selectedItem by remember { mutableStateOf(0) }
                    val items = listOf("Locations", "Contacts", "Home", "Tasks", "Notes")
                    val icons = listOf(
                        PtIcons.Location,
                        PtIcons.Contact,
                        PtIcons.Home,
                        PtIcons.Task,
                        PtIcons.Note,
                    )
                    val selectedIcons = listOf(
                        PtIcons.Location,
                        PtIcons.Contact,
                        PtIcons.Home,
                        PtIcons.Task,
                        PtIcons.Note,
                    )
                    PtNavigationRail {
                        items.forEachIndexed { index, item ->
                            PtNavigationRailItem(
                                icon = {
                                    Icon(
                                        imageVector = icons[index],
                                        contentDescription = item,
                                    )
                                },
                                selectedIcon = {
                                    Icon(
                                        imageVector = selectedIcons[index],
                                        contentDescription = item,
                                    )
                                },
                                label = { Text(item) },
                                selected = selectedItem == index,
                                onClick = { selectedItem = index },
                            )
                        }
                    }
                    PtNavigationRail {
                        items.forEachIndexed { index, item ->
                            PtNavigationRailItem(
                                icon = {
                                    Icon(
                                        imageVector = icons[index],
                                        contentDescription = item,
                                    )
                                },
                                selectedIcon = {
                                    Icon(
                                        imageVector = selectedIcons[index],
                                        contentDescription = item,
                                    )
                                },
                                selected = selectedItem == index,
                                onClick = { selectedItem = index },
                            )
                        }
                    }
                }
                item {
                    PtCategorySelector(
                        categories = listOf(
                            "Nature",
                            "City",
                            "Studio",
                            "Forest",
                        ),
                        categoriesTitleRes = "Location categories",
                        onCategorySelected = {},
                        onEditCategories = {},
                    )
                }
            }
        }
    }
}
