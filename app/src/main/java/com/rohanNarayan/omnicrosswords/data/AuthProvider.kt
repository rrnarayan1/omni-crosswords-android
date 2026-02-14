package com.rohanNarayan.omnicrosswords.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseUser

object AuthProvider {
    var user by mutableStateOf<FirebaseUser?>(null)
    var isAuthenticated by mutableStateOf(false)

    fun updateAuthState(user: FirebaseUser?) {
        this.user = user
        this.isAuthenticated = user != null
    }
}