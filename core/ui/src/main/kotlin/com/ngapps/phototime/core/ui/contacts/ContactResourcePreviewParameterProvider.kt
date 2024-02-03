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

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.ngapps.phototime.core.model.data.contact.ContactResource
import com.ngapps.phototime.core.ui.contacts.PreviewParameterContactData.contactResources

/* ktlint-disable max-line-length */
/**
 * This [PreviewParameterProvider](https://developer.android.com/reference/kotlin/androidx/compose/ui/tooling/preview/PreviewParameterProvider)
 * provides list of [ContactResource] for Composable previews.
 */
class ContactResourcePreviewParameterProvider :
    PreviewParameterProvider<List<ContactResource>> {

    override val values: Sequence<List<ContactResource>> = sequenceOf(contactResources)
}

object PreviewParameterContactData {

    val contactResources = listOf(
        ContactResource(
            id = "1",
            category = "Shooting",
            name = "Amelia Johnson",
            description = "Photographer specializing in outdoor sessions",
            photos = listOf(
                "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEh1VWQmqQu6wDswls9f_5NpEQnq4eR57g2NwzWvhKItcKtV6rb_Cyo75XSyL6vvmCIo4tzQn-8taNagEp7QG0KU1L4yMqwbYozNMzBMEFxEN2XintAhy5nLI4RQDaOXr8dgiIFdGOBMdl577Ndelzc0tDBzjI6mz7e4MF8_Tn09KWguZi6I-bS5NbJn/w1200-h630-p-k-no-nu/unnamed%20%2816%29.png",
                "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEhH63icac2kmydOI8Fs2I09KiuRA3GUo2pfZ1Wpf0M5JIEoVQ8dj9LYSl8jpxSQlmlsUVXoeXbwN4UbHMCf5p0M7FHh_EXzMeFRAJ-6feI9-7eIyhBmtGZSD5o-MItwFLH_ESi15Cxd01AlznWaGy9WDqhK0NWtMQwiWELg3xE1I7hba-_7eVqs747V/w1200-h630-p-k-no-nu/WhasNewinPixelDevices_Social.png",
            ),
            phone = "+1 123-456-7890",
            messenger = "ameliaphoto",
        ),
        ContactResource(
            id = "2",
            category = "Shooting",
            name = "John Smith",
            description = "Portrait photographer with a focus on studio sessions",
            photos = listOf(
                "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEh1VWQmqQu6wDswls9f_5NpEQnq4eR57g2NwzWvhKItcKtV6rb_Cyo75XSyL6vvmCIo4tzQn-8taNagEp7QG0KU1L4yMqwbYozNMzBMEFxEN2XintAhy5nLI4RQDaOXr8dgiIFdGOBMdl577Ndelzc0tDBzjI6mz7e4MF8_Tn09KWguZi6I-bS5NbJn/w1200-h630-p-k-no-nu/unnamed%20%2816%29.png",
                "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEhH63icac2kmydOI8Fs2I09KiuRA3GUo2pfZ1Wpf0M5JIEoVQ8dj9LYSl8jpxSQlmlsUVXoeXbwN4UbHMCf5p0M7FHh_EXzMeFRAJ-6feI9-7eIyhBmtGZSD5o-MItwFLH_ESi15Cxd01AlznWaGy9WDqhK0NWtMQwiWELg3xE1I7hba-_7eVqs747V/w1200-h630-p-k-no-nu/WhasNewinPixelDevices_Social.png",
            ),
            phone = "+1 987-654-3210",
            messenger = "johnsmithphoto",
        ),
        ContactResource(
            id = "3",
            category = "Meeting",
            name = "Alice Johnson",
            description = "Business consultant specializing in marketing strategies",
            photos = listOf(
                "https://example.com/alice-image1.png",
                "https://example.com/alice-image2.png",
            ),
            phone = "+1 555-123-4567",
            messenger = "alicebusiness",
        ),
        ContactResource(
            id = "4",
            category = "Event",
            name = "Bob Williams",
            description = "Event coordinator with expertise in organizing conferences",
            photos = listOf(
                "https://example.com/bob-image1.png",
                "https://example.com/bob-image2.png",
            ),
            phone = "+1 555-987-6543",
            messenger = "bobevents",
        ),
    )
}
