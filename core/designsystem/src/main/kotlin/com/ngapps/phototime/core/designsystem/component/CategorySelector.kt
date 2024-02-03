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

package com.ngapps.phototime.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.ngapps.phototime.core.designsystem.icon.PtIcons
import com.ngapps.phototime.core.designsystem.theme.PtTheme

/**
 * Location category selector with selection change state. Wraps Material 3
 * [Button] and [Icon].
 *
 * @param modifier Modifier to be applied to this item.
 * @param categories The list of String of category items.
 * @param onCategorySelected Whether the category is selected.
 * @param onEditCategories Whether the category is need to be created.
 */
@Composable
fun PtCategorySelector(
    modifier: Modifier = Modifier,
    categories: List<String>,
    categoriesTitleRes: String,
    onCategorySelected: (String) -> Unit,
    onEditCategories: () -> Unit,
) {
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(if (categories.isNotEmpty()) categories[0] else "") }
    val scrollState = rememberScrollState()
    onCategorySelected(selectedCategory.orEmpty())

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = categoriesTitleRes,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(scrollState)
                .padding(top = 10.dp),
        ) {
            categories.forEach { category ->
                PtTextButton(
                    onClick = {
                        selectedCategory = category
                        onCategorySelected(category)
                    },
                    modifier = Modifier
                        .defaultMinSize(minWidth = 1.dp, minHeight = 28.dp)
                        .clickable {
                            selectedCategory = category
                            onCategorySelected(category)
                        },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    shape = MaterialTheme.shapes.extraSmall,
                    contentPadding = PaddingValues(all = 0.dp),
                    enabled = selectedCategory != category,
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = PtIcons.LeftBracket),
                            contentDescription = categoriesTitleRes,
                            tint = MaterialTheme.colorScheme.inversePrimary,
                            modifier = Modifier,
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = PtIcons.RightBracket),
                            contentDescription = categoriesTitleRes,
                            tint = MaterialTheme.colorScheme.inversePrimary,
                            modifier = Modifier,
                        )
                    },
                    text = {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            PtCreateCategoryTextButton(onClick = onEditCategories)
        }
    }
}


@ThemePreviews
@Composable
fun LocationCategorySelectorPreview(
    @PreviewParameter(CategoryProvider::class) categories: List<String> = listOf(
        "Nature",
        "City",
        "Studio",
    ),
) {
    PtTheme {
        Surface {
            PtCategorySelector(
                categoriesTitleRes = "Location category",
                categories = categories,
                onCategorySelected = {},
                onEditCategories = {},
            )
        }
    }
}

class CategoryProvider : PreviewParameterProvider<List<String>> {
    override val values: Sequence<List<String>>
        get() = sequenceOf(
            listOf("City", "Nature", "Studio"),
            listOf("Sport", "Art", "Restaurants"),
            // Add more category lists for preview variations
        )
}