package com.duhapp.dnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.duhapp.dnotes.ui.MainScreen
import com.duhapp.dnotes.ui.theme.DNotesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DNotesTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(
                        onNavigateToNote = {
                            // TODO: Navigate to note
                        },
                        onNavigateToAllNotes = { categoryId ->
                            // TODO: Navigate to all notes
                        },
                        onNavigateToCategoryBottomSheet = {
                            // TODO: Show category bottom sheet
                        }
                    )
                }
            }
        }
    }
}
