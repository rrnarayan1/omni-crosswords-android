package com.rohanNarayan.omnicrosswords.ui.upload

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rohanNarayan.omnicrosswords.data.CrosswordDataViewModel
import com.rohanNarayan.omnicrosswords.ui.settings.SettingsViewModel
import com.rohanNarayan.omnicrosswords.ui.utils.horizontalPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(dataViewModel: CrosswordDataViewModel, goBack: () -> Unit) {
    val vm = viewModel {
        UploadViewModel(dataVm = dataViewModel)
    }
    val state by vm.uploadState.collectAsState()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        vm.maybeSelectFile(uri, context)
    }

    LaunchedEffect(key1 = state.isSuccessfulUpload) {
        if (state.isSuccessfulUpload) {
            goBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                actions = {
                    IconButton(onClick = { uriHandler.openUri("https://omnicrosswords.app") }) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "Info")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { goBack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            state.errorMessage?.let {
                Text(state.errorMessage!!)
            }

            if (state.selectedFileUrl != null) {
                Text("Your selected file: " + state.selectedFileName!!)

                Button(
                    onClick = {
                        vm.uploadFile(context)
                    },
                    enabled = !state.isUploading
                ) {
                    if (state.isUploading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text(text = "Upload")
                    }
                }
            } else {
                Button(onClick = {
                    filePickerLauncher.launch(arrayOf("*/*"))
                }) {
                    Text("Select .puz file")
                }
            }
        }
    }
}