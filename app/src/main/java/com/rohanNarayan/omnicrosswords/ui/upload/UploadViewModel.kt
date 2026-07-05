package com.rohanNarayan.omnicrosswords.ui.upload

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rohanNarayan.omnicrosswords.data.CrosswordDataViewModel
import com.rohanNarayan.omnicrosswords.ui.settings.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class UploadViewModel(dataVm: CrosswordDataViewModel): ViewModel() {
    private val _uploadState = MutableStateFlow(UploadState())
    private val _dataVm: CrosswordDataViewModel = dataVm
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()
    val uploadClient: UploadApi = UploadClient.instance.create(UploadApi::class.java)

    suspend fun getToken(): String? {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser ?: return null

        return try {
            val tokenResult = currentUser.getIdToken(false).await()
            tokenResult.token
        } catch (e: Exception) {
            null
        }
    }

    fun maybeSelectFile(uri: Uri?, context: Context) {
        uri?.let {
            val document = DocumentFile.fromSingleUri(context, uri)
            val fileName = document?.name ?: ""
            if (fileName.endsWith(".puz")) {
                _uploadState.update {
                    UploadState(
                        errorMessage = null,
                        selectedFileUrl = uri,
                        selectedFileName = fileName
                    )
                }
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } else {
                showErrorMessage("Choose a .puz file")
            }
        }
    }

    fun uploadFile(context: Context): Boolean {
        _uploadState.update {
            it.copy(
                isUploading = true
            )
        }
        val fileUri: Uri = _uploadState.value.selectedFileUrl ?: return false

        viewModelScope.launch(Dispatchers.IO) {
            val token: String? = getToken()
            if (token == null) {
                showErrorMessage("Not Authorized to Upload")
                return@launch
            }
            val bearerValue: String = String.format("Bearer %s", token)

            val fileBytes: ByteArray? = context.contentResolver.openInputStream(fileUri)?.use {
                it.readBytes()
            }

            if (fileBytes == null) {
                showErrorMessage("Could not read file")
                return@launch
            }

            val requestFile = fileBytes.toRequestBody()
            val response = uploadClient.uploadFile(bearerValue, requestFile)

            if (response.errorBody() != null || response.body() == null) {
                showErrorMessage("Could not parse file")
                return@launch
            }
            _dataVm.formatAndInsert(response.body()!!)

            // reset state to signal to go back to home screen
            _uploadState.update {
                UploadState()
            }
        }

        return _uploadState.value.errorMessage == null
    }

    fun showErrorMessage(errorMessage: String) {
        UploadState(
            errorMessage = errorMessage
        )
    }
}

