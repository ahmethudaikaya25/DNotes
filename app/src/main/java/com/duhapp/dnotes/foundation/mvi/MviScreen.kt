package com.duhapp.dnotes.foundation.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Compose helper that connects an [MviViewModel] to a screen composable.
 *
 * - Collects the [MviViewModel.state] as Compose state (lifecycle-aware).
 * - Collects [MviViewModel.effect] in a [LaunchedEffect] and forwards each
 *   one-shot effect to [onEffect].
 * - Passes the current [S] state into [content] for rendering.
 *
 * Example:
 * ```
 * @Composable
 * fun HomeScreenRoute(viewModel: HomeViewModel = hiltViewModel()) {
 *     MviScreen(
 *         viewModel = viewModel,
 *         onEffect = { effect ->
 *             when (effect) {
 *                 is HomeEffect.NavigateToNote -> navController.navigate(...)
 *             }
 *         }
 *     ) { state ->
 *         HomeScreen(state = state, onIntent = viewModel::processIntent)
 *     }
 * }
 * ```
 */
@Composable
fun <I : UiIntent, S : UiState, E : UiEffect> MviScreen(
    viewModel: MviViewModel<I, S, E>,
    onEffect: (E) -> Unit,
    content: @Composable (state: S) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Collect one-shot effects. LaunchedEffect key = Unit so it runs once
    // per composition and survives recompositions.
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            onEffect(effect)
        }
    }

    content(state)
}
