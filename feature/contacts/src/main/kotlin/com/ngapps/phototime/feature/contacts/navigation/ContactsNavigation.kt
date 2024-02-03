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

package com.ngapps.phototime.feature.contacts.navigation

import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.ngapps.phototime.core.decoder.StringDecoder
import com.ngapps.phototime.feature.contacts.EditContactRoute
import com.ngapps.phototime.feature.contacts.ContactsRoute
import com.ngapps.phototime.feature.contacts.SingleContactRoute

private const val contactsGraphRoutePattern = "contacts_graph"
const val contactsRoute = "contacts_route"
const val editContactRoute = "edit_contacts_route"

@VisibleForTesting
internal const val contactIdArg = "contactId"

internal class ContactArgs(val contactId: String) {
    constructor(savedStateHandle: SavedStateHandle, stringDecoder: StringDecoder) :
            this(stringDecoder.decodeString(checkNotNull(savedStateHandle[contactIdArg])))
}

fun NavController.navigateToContactsGraph(navOptions: NavOptions? = null) {
    this.navigate(contactsGraphRoutePattern, navOptions)
}

fun NavGraphBuilder.contactsGraph(
    onContactClick: (String) -> Unit,
    onEditActionClick: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
        route = contactsGraphRoutePattern,
        startDestination = contactsRoute,
    ) {
        composable(route = contactsRoute) {
            ContactsRoute(
                onContactClick = onContactClick,
                onEditActionClick = onEditActionClick,
                onShowSnackbar = onShowSnackbar,
            )
        }
        nestedGraphs()
    }
}

fun NavController.navigateToEditContact(contactId: String) {
    val encodedId = Uri.encode(contactId)
    this.navigate("$editContactRoute/$encodedId") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.editContactScreen(
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(
        route = "$editContactRoute/{$contactIdArg}",
        arguments = listOf(
            navArgument(contactIdArg) { type = NavType.StringType },
        ),
    ) {
        EditContactRoute(
            onBackClick = onBackClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}

fun NavController.navigateToSingleContact(contactId: String) {
    val encodedId = Uri.encode(contactId)
    this.navigate("$contactsRoute/$encodedId") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.singleContactScreen(
    onBackClick: () -> Unit,
    onEditActionClick: (String) -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
) {
    composable(
        route = "$contactsRoute/{$contactIdArg}",
        arguments = listOf(
            navArgument(contactIdArg) { type = NavType.StringType },
        ),
    ) {
        SingleContactRoute(
            onBackClick = onBackClick,
            onEditActionClick = onEditActionClick,
            onShowSnackbar = onShowSnackbar,
        )
    }
}
