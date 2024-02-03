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

package com.ngapps.feature.profile

import android.annotation.SuppressLint
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapps.phototime.core.designsystem.component.PtOverlayLoadingWheel
import com.ngapps.phototime.core.designsystem.component.PtRoundImage
import com.ngapps.phototime.core.designsystem.component.PtTopAppBar
import com.ngapps.phototime.core.designsystem.icon.PtIcons
import com.ngapps.phototime.core.designsystem.theme.PtTheme
import com.ngapps.phototime.core.ui.DevicePreviews
import com.ngapps.phototime.feature.user.profile.R

@Composable
internal fun ProfileRoute(
    onBackClick: () -> Unit,
    onMoreActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val profileUiState by viewModel.profileUiState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    ProfileScreen(
        isSyncing = isSyncing,
        profileUiState = profileUiState,
        modifier = modifier,
        onBackClick = onBackClick,
        onMoreActionClick = onMoreActionClick,
    )
}

@Composable
internal fun ProfileScreen(
    isSyncing: Boolean,
    profileUiState: ProfileUiState,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onMoreActionClick: () -> Unit,
) {

    val isFeedLoading = profileUiState is ProfileUiState.Loading

    // NOTE: This code should be called when the UI is ready for use and relates to Time To Full Display.
    ReportDrawnWhen { !isSyncing && !isFeedLoading }

    when(profileUiState) {
        ProfileUiState.Loading -> {
            Column(modifier = modifier) {
                ProfileToolbar(
                    name = "",
                    onBackClick = onBackClick,
                    onMoreActionClick = onMoreActionClick,
                )
                this@Column.AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(
                        initialOffsetY = { fullHeight -> -fullHeight },
                    ) + fadeIn(),
                    exit = slideOutVertically(
                        targetOffsetY = { fullHeight -> -fullHeight },
                    ) + fadeOut(),
                ) {
                    val loadingContentDescription = stringResource(id = R.string.loading)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                    ) {
                        PtOverlayLoadingWheel(
                            modifier = Modifier.align(Alignment.Center),
                            contentDesc = loadingContentDescription,
                        )
                    }
                }
            }
        }
        ProfileUiState.Error -> TODO()
        is ProfileUiState.Success -> {
            var selectedTabIndex by remember { mutableStateOf(0) }
            Column(modifier = Modifier.fillMaxSize()) {
                ProfileToolbar(
                    name = profileUiState.user.username,
                    onBackClick = onBackClick,
                    onMoreActionClick = onMoreActionClick,
                )
                Spacer(modifier = Modifier.height(4.dp))
                ProfileSection()
                Spacer(modifier = Modifier.height(25.dp))
                ButtonSection(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(25.dp))
                HighlightSection(
                    highlights = listOf(
                        ImageWithText(
                            image = painterResource(id = R.drawable.youtube),
                            text = "YouTube",
                        ),
                        ImageWithText(
                            image = painterResource(id = R.drawable.qa),
                            text = "Q&A",
                        ),
                        ImageWithText(
                            image = painterResource(id = R.drawable.discord),
                            text = "Discord",
                        ),
                        ImageWithText(
                            image = painterResource(id = R.drawable.telegram),
                            text = "Telegram",
                        ),
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                )
                Spacer(modifier = Modifier.height(10.dp))
                PostTabView(
                    icons = listOf(
                        PtIcons.Grid,
                        PtIcons.SmartDisplay,
                        PtIcons.Awesome,
                        PtIcons.Assignment,
                    ),
                ) {
                    selectedTabIndex = it
                }
                when (selectedTabIndex) {
                    0 -> PostSection(
                        posts = listOf(
                            painterResource(id = R.drawable.coffee1),
                            painterResource(id = R.drawable.coffee2),
                            painterResource(id = R.drawable.coffee3),
                            painterResource(id = R.drawable.coffee4),
                            painterResource(id = R.drawable.coffee5),
                            painterResource(id = R.drawable.coffee6),
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileToolbar(
    name: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onMoreActionClick: () -> Unit
) {
    PtTopAppBar(
        modifier = modifier,
        title = name,
        navigationIcon = PtIcons.ArrowBack,
        navigationIconContentDescription = stringResource(R.string.back),
        actionIcon = PtIcons.Add,
        actionIconContentDescription = stringResource(R.string.add),
        moreActionIcon = PtIcons.MoreVert,
        moreActionIconContentDescription = stringResource(R.string.more),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        onNavigationClick = { onBackClick() },
        onActionClick = { },
        onMoreActionClick = { onMoreActionClick() },
    )
}

@Composable
fun ProfileSection(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
        ) {
            PtRoundImage(
                image = painterResource(id = R.drawable.profile),
                modifier = Modifier
                    .size(100.dp)
                    .weight(3f),
            )
            Spacer(modifier = Modifier.width(16.dp))
            StatSection(modifier = Modifier.weight(7f))
        }
        ProfileDescription(
            displayName = "Programming Mentor",
            description = "10 years of coding experience\n" +
                    "Want me to make your app? Send me an email!\n" +
                    "Subscribe to my YouTube channel!",
            url = "https://youtube.com/c/ThinkUpYourself",
            followedBy = listOf("thinkUp", "digitalworld"),
            otherCount = 17,
        )
    }
}

@Composable
fun StatSection(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier,
    ) {
        ProfileStat(numberText = "601", text = "Posts")
        ProfileStat(numberText = "100K", text = "Followers")
        ProfileStat(numberText = "72", text = "Following")
    }
}

@Composable
fun ProfileStat(
    numberText: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Text(
            text = numberText,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = text)
    }
}

@Composable
fun ProfileDescription(
    displayName: String,
    description: String,
    url: String,
    followedBy: List<String>,
    otherCount: Int
) {
    val letterSpacing = 0.5.sp
    val lineHeight = 20.sp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
    ) {
        Text(
            text = displayName,
            fontWeight = FontWeight.Bold,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight,
        )
        Text(
            text = description,
            letterSpacing = letterSpacing,
            lineHeight = lineHeight,
        )
        Text(
            text = url,
            color = Color(0xFF3D3D91),
            letterSpacing = letterSpacing,
            lineHeight = lineHeight,
        )
        if (followedBy.isNotEmpty()) {
            Text(
                text = buildAnnotatedString {
                    val boldStyle = SpanStyle(
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                    )
                    append("Followed by ")
                    followedBy.forEachIndexed { index, name ->
                        pushStyle(boldStyle)
                        append(name)
                        pop()
                        if (index < followedBy.size - 1) {
                            append(", ")
                        }
                    }
                    if (otherCount > 2) {
                        append(" and ")
                        pushStyle(boldStyle)
                        append("$otherCount others")
                    }
                },
                letterSpacing = letterSpacing,
                lineHeight = lineHeight,
            )
        }
    }
}

@Composable
fun ButtonSection(
    modifier: Modifier = Modifier
) {
    val minWidth = 95.dp
    val height = 30.dp
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.padding(horizontal = 12.dp),
    ) {
        ActionButton(
            text = "Edit profile",
            icon = PtIcons.KeyboardArrowDown,
            modifier = Modifier
                .defaultMinSize(minWidth = minWidth)
                .height(height)
                .weight(0.5f),
        )
        ActionButton(
            text = "Share profile",
            modifier = Modifier
                .defaultMinSize(minWidth = minWidth)
                .height(height)
                .weight(0.5f),
        )
        ActionButton(
            icon = PtIcons.PersonAdd,
            modifier = Modifier
                .size(height),
        )
    }
}

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: ImageVector? = null
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(5.dp),
            )
            .padding(6.dp),
    ) {
        if (text != null) {
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
        }
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Black,
            )
        }
    }
}

@Composable
fun HighlightSection(
    modifier: Modifier = Modifier,
    highlights: List<ImageWithText>
) {
    LazyRow(modifier = modifier) {
        items(highlights.size) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(end = 15.dp),
            ) {
                PtRoundImage(
                    image = highlights[it].image,
                    modifier = Modifier.size(70.dp),
                )
                Text(
                    text = highlights[it].text,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@SuppressLint("DesignSystem")
@Composable
fun PostTabView(
    modifier: Modifier = Modifier,
    icons: List<ImageVector>,
    onTabSelected: (selectedIndex: Int) -> Unit
) {
    var selectedTabIndex by remember {
        mutableStateOf(0)
    }
    val inactiveColor = Color(0xFF777777)
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        contentColor = Color.Black,
        modifier = modifier,
    ) {
        icons.forEachIndexed { index, item ->
            Tab(
                selected = selectedTabIndex == index,
                selectedContentColor = Color.Black,
                unselectedContentColor = inactiveColor,
                onClick = {
                    selectedTabIndex = index
                    onTabSelected(index)
                },
            ) {
                Icon(
                    imageVector = item,
                    contentDescription = "",
                    tint = if (selectedTabIndex == index) Color.Black else inactiveColor,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(20.dp),
                )
            }
        }
    }
}

@Composable
fun PostSection(
    posts: List<Painter>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier
            .scale(1.01f),
    ) {
        items(posts.size) {
            Image(
                painter = posts[it],
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .border(
                        width = 1.dp,
                        color = Color.White,
                    ),
            )
        }
    }
}

@DevicePreviews
@Composable
fun ProfileScreenPopulated() {
    PtTheme {
        ProfileScreen(
            isSyncing = false,
            profileUiState = ProfileUiState.Loading,
            onMoreActionClick = {},
        )
    }
}