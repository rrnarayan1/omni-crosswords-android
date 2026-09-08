package com.rohanNarayan.omnicrosswords.ui.crosswordscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Support
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohanNarayan.omnicrosswords.ui.utils.crosswordToolbarHeight
import com.rohanNarayan.omnicrosswords.ui.utils.horizontalPadding
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun CrosswordClueToolbar(vm: CrosswordViewModel, width: Float, activeClue: String?, clueFontSize: Int) {
    Row(modifier = Modifier
        .width(width.dp)
        .height(crosswordToolbarHeight),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Row {
            CrosswordToolbarIconButton(image = Icons.AutoMirrored.Filled.RotateLeft,
                firstButton = true,
                description = "Toggle Direction") {
                vm.toggleDirection()
            }

            CrosswordToolbarIconButton(image = Icons.Default.Support, firstButton = false,
                description = "Solve Cell") {
                vm.solveCell()
            }
        }

        val textWidth = width - (crosswordToolbarHeight.value-4)*4
        ClueText(text = activeClue ?: "", textWidth = textWidth, fontSize = clueFontSize)

        Row {
            CrosswordToolbarIconButton(image = Icons.Default.ChevronLeft, firstButton = true,
                description = "Previous Clue") {
                vm.goToPreviousClue()
            }

            CrosswordToolbarIconButton(image = Icons.Default.ChevronRight, firstButton = false,
                description = "Next Clue") {
                vm.goToNextClue()
            }
        }
    }
}

@Composable
fun ClueText(text: String, textWidth: Float, fontSize: Int) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .width(textWidth.dp)
            .verticalScroll(scrollState)
            .padding(horizontal = horizontalPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MarkdownText(
            markdown = text,
            style = TextStyle(
                lineHeight = (fontSize + 1).sp,
                fontSize = fontSize.sp
        ))
    }
}

@Composable
fun CrosswordToolbarIconButton(image: ImageVector, firstButton: Boolean, description: String,
                               action: () -> Unit) {
    Box(modifier = Modifier
        .padding(start = if(firstButton) 0.dp else 2.dp, end = if(firstButton) 2.dp else 0.dp)
        .clickable { action() }) {
        Icon(imageVector = image,
            contentDescription = description,
            modifier = Modifier.height(crosswordToolbarHeight)
        )
    }
}