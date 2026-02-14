package com.rohanNarayan.omnicrosswords

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.rohanNarayan.omnicrosswords.data.AppDatabase
import com.rohanNarayan.omnicrosswords.data.AuthProvider
import com.rohanNarayan.omnicrosswords.data.CrosswordDataViewModel
import com.rohanNarayan.omnicrosswords.ui.NavigationStack
import com.rohanNarayan.omnicrosswords.ui.theme.OmniCrosswordsTheme

class MainActivity : ComponentActivity() {
    private val crosswordDb by lazy {
        Room.databaseBuilder(
            context = applicationContext,
            klass = AppDatabase::class.java,
            name = "crossword"
        ).build()
    }

    private val firestoreDb = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    private val dataViewModel by viewModels<CrosswordDataViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CrosswordDataViewModel(crosswordDb.crosswordDao(), firestoreDb) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        enableEdgeToEdge()
        setContent {
            OmniCrosswordsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthProvider.updateAuthState(auth.currentUser)

                    if (AuthProvider.isAuthenticated) {
                        NavigationStack(dataViewModel = dataViewModel)
                    } else {
                        LoginView()
                    }
                }
            }
        }
    }

    @Composable
    fun LoginView() {
        Text("Loading")
        LaunchedEffect(Unit) {
            auth.signInAnonymously().addOnSuccessListener {
                AuthProvider.updateAuthState(auth.currentUser)
            }
        }
    }
}