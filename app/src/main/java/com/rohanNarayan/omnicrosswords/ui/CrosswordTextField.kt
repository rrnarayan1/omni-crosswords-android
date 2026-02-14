package com.rohanNarayan.omnicrosswords.ui;

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun CrosswordTextField(vm: CrosswordViewModel, focusRequester: FocusRequester) {
    val emptyTextToolbar = object : TextToolbar {
        override val status: TextToolbarStatus = TextToolbarStatus.Hidden
        override fun hide() {}
        override fun showMenu(
            rect: Rect,
            onCopyRequested: (() -> Unit)?,
            onPasteRequested: (() -> Unit)?,
            onCutRequested: (() -> Unit)?,
            onSelectAllRequested: (() -> Unit)?
        ) {
            // Do nothing - prevents the menu from showing
        }
    }
    val bufferChar = "\u2800"
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = bufferChar,
                selection = TextRange(1) // Place cursor at the end
            )
        )
    }
    CompositionLocalProvider(LocalTextToolbar provides emptyTextToolbar) {
        // Your HiddenInput goes here
        BasicTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                val newText = newValue.text
                if (newText == bufferChar) {
                    return@BasicTextField
                } else if (newText.isEmpty()) {
                    // User deleted the buffer
                    vm.onBackspace()
                    textFieldValue = TextFieldValue(
                        text = bufferChar,
                        selection = TextRange(1)
                    )
                    return@BasicTextField
                }
                val realValue = newText.replace(bufferChar, "")
                if (realValue.length == 1) {
                    // User typed a letter
                    vm.onInputReceived(realValue)
                    textFieldValue = TextFieldValue(
                        text = bufferChar,
                        selection = TextRange(1)
                    )
                }
            },
            modifier = Modifier.size(1.dp)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
                autoCorrectEnabled = false,
                capitalization = KeyboardCapitalization.Characters
            ),
        )
    }
}
