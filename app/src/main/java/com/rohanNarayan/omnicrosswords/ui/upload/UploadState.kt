package com.rohanNarayan.omnicrosswords.ui.upload

import android.net.Uri

data class UploadState(
    val errorMessage: String? = null,
    val selectedFileUrl: Uri? = null,
    val selectedFileName: String? = null,
    val isUploading: Boolean = false,
    val isSuccessfulUpload: Boolean = false,
)