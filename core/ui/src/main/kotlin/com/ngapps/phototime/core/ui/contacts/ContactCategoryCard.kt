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

package com.ngapps.phototime.core.ui.contacts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.ngapps.phototime.core.designsystem.component.PtDivider
import com.ngapps.phototime.core.designsystem.icon.PtIcons
import com.ngapps.phototime.core.designsystem.theme.PtTheme
import com.ngapps.phototime.core.model.data.contact.ContactResource
import com.ngapps.phototime.core.ui.R

/**
 * [ContactCategoryCard] card used to show Contact Resource sorted by category
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactCategoryCard(
    contactResourceCategory: Pair<String, List<ContactResource>>,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clickActionLabel = stringResource(R.string.card_tap_action)
    Column {
        Card(
            onClick = onExpandClick,
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
            // NOTE: Use custom label for accessibility services to communicate button's action to user.
            //  Pass null for action to only override the label and not the actual action.
            modifier = modifier
                .fillMaxWidth()
                .semantics {
                    onClick(label = clickActionLabel, action = null)
                },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ContactCategoryTitle(
                            contactCategoryTitle = contactResourceCategory.first,
                            modifier = Modifier.weight(1f),
                        )
                        ContactResourceAmount(
                            contactResourceAmount = contactResourceCategory.second.size.toString(),
                            modifier = Modifier,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    PtDivider()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactResourceCard(
    contactResource: ContactResource,
    modifier: Modifier = Modifier,
    onContactClick: (String) -> Unit,
    onAddCommMethod: () -> Unit = {},
) {
    val clickActionLabel = stringResource(R.string.card_tap_action)

    Card(
        onClick = { onContactClick.invoke(contactResource.id) },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        // NOTE: Use custom label for accessibility services to communicate button's action to user.
        //  Pass null for action to only override the label and not the actual action.
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                onClick(label = clickActionLabel, action = null)
            },
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ContactResourceName(
                contactResourceNane = contactResource.name,
                modifier = Modifier.fillMaxWidth(),
            )
            ContactPhoneMessengerRow(
                icon = PtIcons.Phone,
                text = contactResource.phone,
            )
            ContactPhoneMessengerRow(
                icon = PtIcons.Instagram,
                text = contactResource.messenger,
            )
            if (contactResource.phone.isEmpty() && contactResource.messenger.isEmpty()) {
                Box(
                    modifier = modifier
                        .clickable { onAddCommMethod() }
                        .fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(com.ngapps.phototime.core.designsystem.R.string.add_communication_method),
                        color = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.38f,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
fun ContactPhoneMessengerRow(
    icon: Int,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (text.isNotEmpty()) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                modifier = Modifier
                    .height(20.dp)
                    .defaultMinSize(1.dp),
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}


@Composable
fun ContactResourceName(
    contactResourceNane: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = contactResourceNane,
        style = MaterialTheme.typography.titleSmall,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = modifier,
    )
}

@Composable
fun ContactCategoryTitle(
    contactCategoryTitle: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = contactCategoryTitle,
        style = MaterialTheme.typography.titleMedium,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = modifier,
    )
}

@Composable
fun ContactResourceAmount(
    contactResourceAmount: String,
    modifier: Modifier = Modifier,
) {
    Text(
        contactResourceAmount,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier,
    )
}


@Preview("ContactResourceCardCollapsed")
@Composable
private fun LocationResourceCollapsedPreview(
    @PreviewParameter(ContactResourcePreviewParameterProvider::class)
    contactResources: List<ContactResource>,
) {
    CompositionLocalProvider(
        LocalInspectionMode provides true,
    ) {
        PtTheme {
            Surface {
                ContactCategoryCard(
                    contactResourceCategory = Pair("Client", contactResources),
                    onExpandClick = {},
                )
            }
        }
    }
}

@Preview("ContactResourceCardPreview")
@Composable
private fun ContactResourceCardPreview(
    @PreviewParameter(ContactResourcePreviewParameterProvider::class)
    contactResources: List<ContactResource>,
) {
    CompositionLocalProvider(
        LocalInspectionMode provides true,
    ) {
        PtTheme {
            Surface {
                ContactResourceCard(
                    contactResource = contactResources[0],
                    onContactClick = {},
                    onAddCommMethod = {},
                )
            }
        }
    }
}
