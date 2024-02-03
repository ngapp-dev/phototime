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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ngapps.phototime.core.designsystem.component.PtTextFieldDefaults.textFieldContainerColor
import com.ngapps.phototime.core.designsystem.component.PtTextFieldDefaults.textFieldContentColor
import com.ngapps.phototime.core.designsystem.component.PtTextFieldDefaults.textFieldErrorColor

/**
 * Photo time field with error state. Wraps Material 3 [TextField].
 */
@Composable
fun PtTextFieldWithErrorState(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    text: String = "",
    label: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    textResult: (String) -> Unit,
    validate: (String) -> Unit,
    onNext: (KeyboardActionScope.() -> Unit)
) {
    Column {
        TextField(
            value = text,
            onValueChange = {
                textResult.invoke(it)
                validate(text)
            },
            singleLine = true,
            label = { Text(if (isError) "${label}*" else label) },
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            isError = isError,
            keyboardActions = KeyboardActions(
                onNext = onNext,
            ) { validate(text) },
            keyboardOptions = keyboardOptions,
            enabled = enabled,
            readOnly = readOnly,
            maxLines = maxLines,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = textFieldContainerColor(),
                unfocusedContainerColor = textFieldContainerColor(),
                disabledContainerColor = textFieldContainerColor(),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedLabelColor = textFieldContentColor(),
                focusedLabelColor = textFieldContentColor(),
            ),
            modifier = modifier.clip(MaterialTheme.shapes.medium),
        )
        if (isError) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
fun PtOutlinedTextFieldWithErrorState(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    trailingIcon: ImageVector? = null,
    trailingIconDescription: String,
    maxLines: Int = 1,
    shape: Shape = MaterialTheme.shapes.large,
    readOnly: Boolean = false,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    text: String = "",
    label: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    textResult: (String) -> Unit,
    validate: (String) -> Unit,
    onNext: (KeyboardActionScope.() -> Unit),
    onTrailingIconClick: () -> Unit
) {
    Column {
        OutlinedTextField(
            value = text,
            onValueChange = {
                textResult.invoke(it)
                validate(text)
            },
            trailingIcon = {
                if (trailingIcon != null) {
                    IconButton(onClick = { onTrailingIconClick() }) {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = trailingIconDescription,
                        )
                    }
                }
            },
            singleLine = true,
            label = { Text(if (isError) "${label}*" else label) },
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            isError = isError,
            keyboardActions = KeyboardActions(
                onNext = onNext,
            ) { validate(text) },
            keyboardOptions = keyboardOptions,
            enabled = enabled,
            readOnly = readOnly,
            maxLines = maxLines,
            shape = shape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = textFieldContainerColor(),
                unfocusedContainerColor = textFieldContainerColor(),
                disabledContainerColor = textFieldContainerColor(),
                unfocusedLabelColor = textFieldContentColor(),
                focusedLabelColor = textFieldContentColor(),
                errorBorderColor = textFieldErrorColor(),
                unfocusedBorderColor = textFieldContentColor(),
                focusedBorderColor = textFieldContentColor(),
            ),
            modifier = modifier.clip(shape),
        )
        if (isError) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
fun PtTextFieldWithErrorState(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    placeholder: String = "",
    leadingIcon: Int? = null,
    trailingIcon: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    text: String = "",
    label: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    textResult: (String) -> Unit,
    validate: (String) -> Unit = {},
    onNext: (KeyboardActionScope.() -> Unit),
    singleLine: Boolean = true,
) {
    Column {
        TextField(
            value = text,
            onValueChange = {
                textResult.invoke(it)
                validate(text)
            },
            leadingIcon = {
                if (leadingIcon != null) {
                    Icon(
                        painter = painterResource(id = leadingIcon),
                        contentDescription = label,
                    )
                }
            },
            trailingIcon = {
                if (trailingIcon != null) {
                    Icon(
                        painter = painterResource(id = trailingIcon),
                        contentDescription = label,
                    )
                }
            },
            singleLine = singleLine,
            label = { Text(if (isError) "${label}*" else label) },
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            isError = isError,
            keyboardActions = KeyboardActions(
                onNext = onNext,
            ) { validate(text) },
            keyboardOptions = keyboardOptions,
            enabled = enabled,
            readOnly = readOnly,
            maxLines = maxLines,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = textFieldContainerColor(),
                unfocusedContainerColor = textFieldContainerColor(),
                disabledContainerColor = textFieldContainerColor(),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedLabelColor = textFieldContentColor(),
                focusedLabelColor = textFieldContentColor(),
            ),
            modifier = modifier.clip(MaterialTheme.shapes.medium),
        )
        if (isError) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
fun PtTextFieldWithErrorState(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    text: String = "",
    label: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    textResult: (String) -> Unit,
    validate: (String) -> Unit,
    onNext: (KeyboardActionScope.() -> Unit)
) {
    Column {
        TextField(
            value = text,
            onValueChange = {
                textResult.invoke(it)
                validate(text)
            },
            leadingIcon = {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = label,
                    )
                }
            },
            trailingIcon = {
                if (trailingIcon != null) {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = label,
                    )
                }
            },
            singleLine = true,
            label = { Text(if (isError) "${label}*" else label) },
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            isError = isError,
            keyboardActions = KeyboardActions(
                onNext = onNext,
            ) { validate(text) },
            keyboardOptions = keyboardOptions,
            enabled = enabled,
            readOnly = readOnly,
            maxLines = maxLines,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = textFieldContainerColor(),
                unfocusedContainerColor = textFieldContainerColor(),
                disabledContainerColor = textFieldContainerColor(),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedLabelColor = textFieldContentColor(),
                focusedLabelColor = textFieldContentColor(),
            ),
            modifier = modifier.clip(MaterialTheme.shapes.medium),
        )
        if (isError) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
fun PtPasswordTextField(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    text: String = "",
    label: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    textResult: (String) -> Unit,
    validate: (String) -> Unit,
    onNext: (KeyboardActionScope.() -> Unit)
) {

    var showPassword by remember {
        mutableStateOf(false)
    }

    Column {
        TextField(
            value = text,
            onValueChange = {
                textResult.invoke(it)
                validate(text)
            },
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = if (showPassword) "Show Password" else "Hide Password",
                    )
                }
            },
            singleLine = true,
            label = { Text(if (isError) "${label}*" else label) },
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            isError = isError,
            keyboardActions = KeyboardActions(
                onNext = onNext,
            ) { validate(text) },
            keyboardOptions = keyboardOptions,
            enabled = enabled,
            readOnly = readOnly,
            maxLines = maxLines,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = textFieldContainerColor(),
                unfocusedContainerColor = textFieldContainerColor(),
                disabledContainerColor = textFieldContainerColor(),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedLabelColor = textFieldContentColor(),
                focusedLabelColor = textFieldContentColor(),
            ),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = modifier.clip(MaterialTheme.shapes.medium),
        )
        if (isError) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
fun PtTextFieldWithTrailingIcon(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    placeholder: String = "",
    trailingIcon: Int? = null,
    trailingIconV: ImageVector? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    text: String = "",
    label: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    textResult: (String) -> Unit,
    validate: (String) -> Unit = {},
    onNext: (KeyboardActionScope.() -> Unit),
    singleLine: Boolean = false,
    onIconClick: () -> Unit = {},
) {
    Column {
        TextField(
            value = text,
            onValueChange = {
                textResult.invoke(it)
                validate(text)
            },
            trailingIcon = {
                if (trailingIcon != null || trailingIconV != null) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onIconClick() },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (trailingIcon != null) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = trailingIcon),
                                contentDescription = label,
                            )
                        } else if (trailingIconV != null) {
                            Icon(
                                imageVector = trailingIconV,
                                contentDescription = label,
                            )
                        }
                    }
                }
            },
            singleLine = singleLine,
            label = { Text(if (isError) "${label}*" else label) },
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            isError = isError,
            keyboardActions = KeyboardActions(
                onNext = onNext,
            ) { validate(text) },
            keyboardOptions = keyboardOptions,
            enabled = enabled,
            readOnly = readOnly,
            maxLines = maxLines,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = textFieldContainerColor(),
                unfocusedContainerColor = textFieldContainerColor(),
                disabledContainerColor = textFieldContainerColor(),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedLabelColor = textFieldContentColor(),
                focusedLabelColor = textFieldContentColor(),
                unfocusedLeadingIconColor = textFieldContentColor(),
                unfocusedTrailingIconColor = textFieldContentColor(),
            ),
            modifier = modifier.clip(MaterialTheme.shapes.medium),
        )
        if (isError) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

/**
 * Photo time text field default values.
 */
object PtTextFieldDefaults {

    @Composable
    fun textFieldContainerColor() = MaterialTheme.colorScheme.secondaryContainer

    @Composable
    fun textFieldContentColor() = MaterialTheme.colorScheme.onSecondaryContainer

    @Composable
    fun textFieldErrorColor() = MaterialTheme.colorScheme.error
}

