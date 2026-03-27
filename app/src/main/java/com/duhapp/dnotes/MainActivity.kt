package com.duhapp.dnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.duhapp.dnotes.foundation.navigation.AppNavGraph
import com.duhapp.dnotes.foundation.theme.DNotesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        viewModel.initializeDefaultDataModels()

        setContent {
            DNotesTheme {
                AppNavGraph()
            }
        }
    }
}
