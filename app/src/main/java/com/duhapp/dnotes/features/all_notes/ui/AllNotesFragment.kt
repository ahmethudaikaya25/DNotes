package com.duhapp.dnotes.features.all_notes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.duhapp.dnotes.foundation.navigation.Route
import com.duhapp.dnotes.foundation.theme.DNotesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllNotesFragment : Fragment() {

    private val args: AllNotesFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DNotesTheme {
                    AllNotesScreenRoute(
                        categoryId = args.categoryId,
                        onNavigateBack = {
                            findNavController().popBackStack()
                        },
                        onNavigateToNote = { noteId ->
                            // Navigate to note editor via legacy action for interop
                            val action = AllNotesFragmentDirections.actionAllNotesFragmentToNavigationNote(null)
                            findNavController().navigate(action)
                        }
                    )
                }
            }
        }
    }
}