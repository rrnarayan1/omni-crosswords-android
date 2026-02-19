package com.rohanNarayan.omnicrosswords.ui.crosswordscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohanNarayan.omnicrosswords.ui.utils.crosswordToolbarHeight
import com.rohanNarayan.omnicrosswords.ui.utils.horizontalPadding
import com.rohanNarayan.omnicrosswords.ui.utils.smallHorizontalPadding

@Composable
fun CrosswordClueToolbar(vm: CrosswordViewModel, activeClue: String?, clueFontSize: Int) {
    Row(modifier = Modifier.fillMaxWidth().height(crosswordToolbarHeight)
        .padding(horizontal = horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Row {
            CrosswordToolbarIconButton(image = Icons.AutoMirrored.Filled.RotateLeft,
                description = "Toggle Direction") {
                vm.toggleDirection()
            }

            CrosswordToolbarIconButton(image = Icons.Default.Support, description = "Solve Cell") {
                vm.solveCell()
            }
        }

        ScrollableText(text = activeClue ?: "", fontSize = clueFontSize)

        Row {
            CrosswordToolbarIconButton(image = Icons.Default.ChevronLeft, description = "Previous Clue") {
                vm.goToPreviousClue()
            }

            CrosswordToolbarIconButton(image = Icons.Default.ChevronRight, description = "Next Clue") {
                vm.goToNextClue()
            }
        }
    }
}

@Composable
fun ScrollableText(text: String, fontSize: Int) {
    val scrollState = rememberScrollState()

    val widthInDp: Float = with(LocalDensity.current) {
        LocalWindowInfo.current.containerSize.width.toDp().value
    }
    val width = (widthInDp - ((crosswordToolbarHeight.value+2)*4 + smallHorizontalPadding.value*2))

    LaunchedEffect(text) {
        scrollState.scrollTo(0)
    }

    Column(
        modifier = Modifier
            .width(width.dp)
            .height(crosswordToolbarHeight)
            .padding(horizontal = horizontalPadding)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            lineHeight = (fontSize + 1).sp,
            fontSize = fontSize.sp
        )
    }
}

@Composable
fun CrosswordToolbarIconButton(image: ImageVector, description: String, action: () -> Unit) {
    Box(modifier = Modifier.padding(horizontal = 2.dp).clickable { action() }) {
        Icon(imageVector = image,
            contentDescription = description,
            modifier = Modifier.height(crosswordToolbarHeight)
        )
    }
}