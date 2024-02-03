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

package com.ngapps.phototime.core.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.ngapps.phototime.core.designsystem.component.DynamicAsyncImage
import com.ngapps.phototime.core.designsystem.component.PtTopAppBar
import com.ngapps.phototime.core.designsystem.icon.PtIcons

@Composable
fun ResourceCardNumber(
    index: Int,
    modifier: Modifier = Modifier
) {
    Column {
        if (index > 1) Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "№$index",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = modifier,
        )
    }
}

@Composable
fun TitleCategoryDescriptionPhotosCard(
    title: String,
    description: String,
    photos: List<String>,
    modifier: Modifier = Modifier,
    category: String? = "",
    onDownloadClick: (String) -> Unit = {},
) {
    val titleCard = if (category != "") "${category}: $title" else title
    val (isDialogOpen, setIsDialogOpen) = remember { mutableStateOf(false) }
    val (selectedImageIndex, setSelectedImageIndex) = remember { mutableIntStateOf(0) }

    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        modifier = modifier
            .padding(top = 16.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = titleCard,
                style = MaterialTheme.typography.titleSmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = modifier,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = modifier,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                photos.forEachIndexed { index, photo ->
                    DynamicAsyncImage(
                        imageUrl = photo,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(114.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable {
                                setIsDialogOpen(true)
                                setSelectedImageIndex(index)
                            },
                    )
                }
            }
        }
    }

    if (isDialogOpen) {
        ImageDialog(
            images = photos,
            selectedImageIndex = selectedImageIndex,
            modifier = Modifier,
            onDownloadClick = onDownloadClick,
            onDismissClick = { setIsDialogOpen(false) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ImageDialog(
    images: List<String>,
    selectedImageIndex: Int,
    modifier: Modifier = Modifier,
    onDownloadClick: (String) -> Unit,
    onDismissClick: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = selectedImageIndex,
        initialPageOffsetFraction = 0f,
    ) { images.size }

    var currentImageIndex by remember { mutableIntStateOf(selectedImageIndex) }

    Dialog(
        onDismissRequest = { onDismissClick() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = true,
        ),
    ) {
        (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(1f)
        Column {
            PtTopAppBar(
                modifier = modifier,
                title = stringResource(R.string.image_title, currentImageIndex + 1, images.size),
                navigationIcon = PtIcons.Download1,
                navigationIconContentDescription = stringResource(
                    id = R.string.download,
                ),

                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                actionIcon = PtIcons.Close1,
                actionIconContentDescription = stringResource(
                    id = R.string.dismiss,
                ),
                onNavigationClick = { onDownloadClick(images[currentImageIndex]) },
                onActionClick = { onDismissClick() },
            )
            HorizontalPager(
                state = pagerState,
            ) { pageIndex ->
                currentImageIndex = pagerState.currentPage
                DynamicAsyncImage(
                    imageUrl = images[pageIndex],
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
fun LocationAddress(
    trailingIcon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (text.isNotEmpty()) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = text,
                    modifier = Modifier
                        .height(20.dp)
                        .defaultMinSize(1.dp),
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}

@Composable
fun ContactPhone(
    contactIcon: Int,
    phoneType: String,
    phone: String,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (phone.isNotEmpty()) {
                Icon(
                    painter = painterResource(id = contactIcon),
                    contentDescription = phone,
                    modifier = Modifier
                        .height(20.dp)
                        .defaultMinSize(1.dp),
                )
                Text(
                    text = "$phoneType | $phone",
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}

@Composable
fun ContactMessenger(
    messengerIcon: Int,
    messengerType: String,
    messenger: String,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (messenger.isNotEmpty()) {
                Icon(
                    painter = painterResource(id = messengerIcon),
                    contentDescription = messenger,
                    modifier = Modifier
                        .height(20.dp)
                        .defaultMinSize(1.dp),
                )
                Text(
                    text = "$messengerType | $messenger",
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}

@Composable
fun Note(
    noteIcon: ImageVector,
    note: String,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (note.isNotEmpty()) {
                Icon(
                    imageVector = noteIcon,
                    contentDescription = note,
                    modifier = Modifier
                        .height(20.dp)
                        .defaultMinSize(1.dp),
                )
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}