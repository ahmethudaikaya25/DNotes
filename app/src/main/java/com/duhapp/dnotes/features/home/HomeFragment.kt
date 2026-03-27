package com.duhapp.dnotes.features.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.duhapp.dnotes.foundation.theme.DNotesTheme
import dagger.hilt.android.AndroidEntryPoint
import com.duhapp.dnotes.foundation.navigation.Route

@AndroidEntryPoint
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DNotesTheme {
                    HomeScreenRoute(
                        onNavigate = { route ->
                            when (route) {
                                is Route.NoteEditor -> {
                                    val action = HomeFragmentDirections.actionNavigationHomeToNavigationNote(null) // legacy interop: passing null model for now
                                    findNavController().navigate(action)
                                }
                                is Route.AllNotes -> {
                                    val action = HomeFragmentDirections.actionNavigationHomeToAllNotesFragment(route.categoryId)
                                    findNavController().navigate(action)
                                }
                                else -> {}
                            }
                        }
                    )
                }
            }
        }
    }
}