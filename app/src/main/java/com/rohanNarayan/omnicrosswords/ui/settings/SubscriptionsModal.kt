package com.rohanNarayan.omnicrosswords.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsModal(
    onDismiss: () -> Unit,
    options: List<String>,
    selectedOptions: Set<String>,
    onToggle: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(options) { option ->
                val isChecked = selectedOptions.contains(option)

                ListItem(
                    modifier = Modifier.clickable { onToggle(option) },
                    headlineContent = { Text(option) },
                    trailingContent = {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = null // Click handled by ListItem
                        )
                    },
                )
            }
        }
    }
}