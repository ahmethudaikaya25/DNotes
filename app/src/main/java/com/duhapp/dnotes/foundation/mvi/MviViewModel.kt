package com.duhapp.dnotes.foundation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel for the MVI architecture pattern used in DNotes.
 *
 * Every screen ViewModel extends this class and provides:
 *  - [I] UiIntent  — all actions the user can perform on this screen
 *  - [S] UiState   — the single immutable snapshot of screen data
 *  - [E] UiEffect  — one-shot side effects (navigate, snackbar, dismiss, etc.)
 *
 * Usage:
 * ```
 * class HomeViewModel @Inject constructor(...) :
 *     MviViewModel<HomeIntent, HomeState, HomeEffect>(HomeState()) {
 *
 *     override fun processIntent(intent: HomeIntent) {
 *         when (intent) {
 *             is HomeIntent.LoadData -> loadData()
 *             is HomeIntent.ClickNote -> emitEffect(HomeEffect.NavigateToNote(intent.note.id))
 *         }
 *     }
 * }
 * ```
 */
abstract class MviViewModel<I : UiIntent, S : UiState, E : UiEffect>(
    initialState: S,
) : ViewModel() {

    // ── State ─────────────────────────────────────────────────────────────────
    private val _state = MutableStateFlow(initialState)

    /** Observed by the Compose screen to drive the UI. */
    val state: StateFlow<S> = _state.asStateFlow()

    // ── Effects ───────────────────────────────────────────────────────────────
    private val _effect = Channel<E>(Channel.BUFFERED)

    /**
     * One-shot side effects. Collected via [LaunchedEffect] in the Compose screen.
     * Using a [Channel] guarantees that every effect is delivered exactly once,
     * even if the collector hasn't started yet (BUFFERED capacity).
     */
    val effect: Flow<E> = _effect.receiveAsFlow()

    // ── Intent dispatcher ─────────────────────────────────────────────────────
    /**
     * Single entry point for all user actions.
     * Subclasses implement this to handle intents and call [updateState] / [emitEffect].
     */
    abstract fun processIntent(intent: I)

    // ── Helpers ───────────────────────────────────────────────────────────────
    /**
     * Atomically updates the UI state using a reducer lambda.
     * ```
     * updateState { copy(isLoading = true) }
     * ```
     */
    protected fun updateState(reducer: S.() -> S) {
        _state.update { it.reducer() }
    }

    /**
     * Emits a one-shot [UiEffect] to the screen.
     * Safe to call from any coroutine context.
     */
    protected fun emitEffect(effect: E) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    /**
     * Launches a coroutine on [viewModelScope].
     * Use this for all async domain/repository calls.
     */
    protected fun launch(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }
}
