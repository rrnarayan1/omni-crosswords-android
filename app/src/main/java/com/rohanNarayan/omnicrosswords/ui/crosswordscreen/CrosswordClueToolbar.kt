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
    val buttonData = vm.getButtons()

    Row(modifier = Modifier
        .width(width.dp)
        .height(crosswordToolbarHeight),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Row {
            CrosswordToolbarIconButton(image = buttonData[0].image,
                leftButton = true,
                description = buttonData[0].description) {
                buttonData[0].action()
            }

            CrosswordToolbarIconButton(image = buttonData[1].image,
                description = buttonData[1].description) {
                buttonData[1].action()
            }
        }

        val textWidth = width - (crosswordToolbarHeight.value-4)*4
        ClueText(text = activeClue ?: "", textWidth = textWidth, fontSize = clueFontSize,
            isClickEnabled = vm.isClueTappable()) {
            vm.onClueTap()
        }

        Row {
            CrosswordToolbarIconButton(image = buttonData[2].image,
                description = buttonData[2].description) {
                buttonData[2].action()
            }

            CrosswordToolbarIconButton(image = buttonData[3].image,
                description = buttonData[3].description) {
                buttonData[3].action()
            }
        }
    }
}

@Composable
fun ClueText(text: String, textWidth: Float, fontSize: Int, isClickEnabled: Boolean, onClick: () -> Unit) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .width(textWidth.dp)
            .verticalScroll(scrollState)
            .padding(horizontal = horizontalPadding)
            .clickable(enabled = isClickEnabled) { onClick() },
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

enum class CrosswordClueToolbarButton {
    TOGGLE,
    SOLVE,
    PREVIOUS,
    NEXT
}

class CrosswordClueToolbarButtonData(
    var image: ImageVector,
    var description: String,
    var action: () -> Unit,
)

@Composable
fun CrosswordToolbarIconButton(image: ImageVector, leftButton: Boolean = false, description: String,
                               action: () -> Unit) {
    Box(modifier = Modifier
        .padding(start = if(leftButton) 0.dp else 2.dp, end = if(leftButton) 2.dp else 0.dp)
        .clickable { action() }) {
        Icon(imageVector = image,
            contentDescription = description,
            modifier = Modifier.height(crosswordToolbarHeight)
        )
    }
}