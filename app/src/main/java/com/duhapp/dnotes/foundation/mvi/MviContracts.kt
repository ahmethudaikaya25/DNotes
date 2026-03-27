package com.duhapp.dnotes.foundation.mvi

/**
 * Marker interface for all screen UI states.
 * Every screen's state must be an immutable data class implementing this interface.
 */
interface UiState

/**
 * Marker interface for all user intents / actions.
 * Every action the user can perform on a screen maps to an Intent.
 */
interface UiIntent

/**
 * Marker interface for one-shot side effects that are NOT part of the state.
 * Examples: navigate to another screen, show a snackbar, dismiss a sheet.
 */
interface UiEffect
