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

package com.ngapps.phototime.core.ui.alert_dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.ngapps.phototime.core.designsystem.component.PtDivider
import com.ngapps.phototime.core.designsystem.component.PtIconButton
import com.ngapps.phototime.core.designsystem.component.PtOutlinedTextFieldWithErrorState
import com.ngapps.phototime.core.designsystem.icon.PtIcons
import com.ngapps.phototime.core.ui.R

@Composable
fun EditCategoriesAlertDialog(
    modifier: Modifier = Modifier,
    title: String,
    categories: List<String>,
    onUpdateCategories: (List<String>) -> Unit,
    onShowAlertDialog: (Boolean) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val focusManager = LocalFocusManager.current
    var categoriesList by rememberSaveable { mutableStateOf(categories) }
    var newCategoryString by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        textContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        iconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        onDismissRequest = { onShowAlertDialog(false) },
        title = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.fillMaxWidth(),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = stringResource(id = R.string.edit_categories_description),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                categoriesList.forEachIndexed { index, category ->
                    var categoryString by rememberSaveable { mutableStateOf(category) }
                    var onEditCategory by rememberSaveable { mutableStateOf(false) }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (onEditCategory) {
                            PtOutlinedTextFieldWithErrorState(
                                text = categoryString,
                                trailingIcon = PtIcons.Check,
                                trailingIconDescription = stringResource(id = R.string.save),
                                onTrailingIconClick = {
                                    categoriesList = categoriesList.toMutableList().apply {
                                        set(index, categoryString)
                                    }
                                    onEditCategory = false
                                },
                                label = stringResource(R.string.add_category),
                                isError = false,
                                validate = {
                                    categoryString.isNotEmpty() &&
                                            !categoriesList.contains(categoryString)
                                },
                                errorMessage = "Check data",
                                textResult = { categoryString = it },
                                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                                maxLines = 1,
                                modifier = Modifier,
                            )
                        } else {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                            PtIconButton(
                                onClick = { onEditCategory = true },
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(48.dp),
                            ) {
                                Icon(
                                    imageVector = PtIcons.Edit1,
                                    contentDescription = stringResource(id = R.string.edit),
                                )
                            }
                            PtIconButton(
                                onClick = { categoriesList -= categoryString },
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(48.dp),
                            ) {
                                Icon(
                                    imageVector = PtIcons.Delete1,
                                    contentDescription = stringResource(id = R.string.delete),
                                )
                            }
                        }
                    }
                }
                PtOutlinedTextFieldWithErrorState(
                    text = newCategoryString,
                    trailingIcon = PtIcons.Check,
                    trailingIconDescription = stringResource(id = R.string.save),
                    onTrailingIconClick = { categoriesList += newCategoryString },
                    label = stringResource(R.string.add_category),
                    isError = false,
                    validate = {
                        newCategoryString.isNotEmpty() &&
                                !categoriesList.contains(newCategoryString)
                    },
                    errorMessage = "Check data",
                    textResult = { newCategoryString = it },
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    maxLines = 1,
                    modifier = Modifier,
                )
                Spacer(modifier = Modifier.height(8.dp))
                PtDivider(
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                        alpha = 0.16f,
                    ),
                )
            }
        },
        dismissButton = {
            Text(
                text = stringResource(R.string.dismiss),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable {
                        onShowAlertDialog(false)
                    },
            )
        },
        confirmButton = {
            Text(
                text = stringResource(R.string.confirm),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable {
                        onUpdateCategories(categoriesList)
                        onShowAlertDialog(false)
                    },
            )
        },
    )
}