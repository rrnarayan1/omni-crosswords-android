package com.rohanNarayan.omnicrosswords.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.rohanNarayan.omnicrosswords.data.Crossword
import com.rohanNarayan.omnicrosswords.ui.settings.ToggleSetting
import com.rohanNarayan.omnicrosswords.ui.utils.horizontalPadding
import com.rohanNarayan.omnicrosswords.ui.utils.verticalPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrosswordSettingsModal(crossword: Crossword, onDismiss: () -> Unit,
                            isErrorTrackingEnabled: Boolean, onErrorTrackingChange: () -> Unit,
                            isRebusModeEnabled: Boolean, onRebusModeChange: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = verticalPadding, horizontal = horizontalPadding)) {
            ToggleSetting(label = "Error Tracking",
                enabled = isErrorTrackingEnabled,
                onEnabledChange = { onErrorTrackingChange() })

            ToggleSetting(label = "Rebus Mode",
                enabled = isRebusModeEnabled,
                onEnabledChange = { onRebusModeChange() })

            CrosswordInfo(crossword = crossword)
        }
    }
}

@Composable
fun CrosswordInfo(crossword: Crossword) {
    var crosswordInfo = crossword.title + "\n" + crossword.author
    if (!crossword.notes.isEmpty()) {
        crosswordInfo += ("\n" + crossword.notes)
    }
    if (!crossword.copyright.isEmpty()) {
        crosswordInfo += ("\n" + crossword.copyright)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(crosswordInfo, modifier=Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }
}