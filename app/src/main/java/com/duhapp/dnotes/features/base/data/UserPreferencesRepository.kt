package com.duhapp.dnotes.features.base.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.duhapp.dnotes.features.home.GroupBy
import com.duhapp.dnotes.features.home.SortBy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dnotes_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val SORT_BY = stringPreferencesKey("sort_by")
        val GROUP_BY = stringPreferencesKey("group_by")
    }

    val sortByFlow: Flow<SortBy> = dataStore.data.map { preferences ->
        val sortString = preferences[PreferencesKeys.SORT_BY] ?: SortBy.DATE_ADDED.name
        try { SortBy.valueOf(sortString) } catch (e: Exception) { SortBy.DATE_ADDED }
    }

    val groupByFlow: Flow<GroupBy> = dataStore.data.map { preferences ->
        val groupString = preferences[PreferencesKeys.GROUP_BY] ?: GroupBy.NONE.name
        try { GroupBy.valueOf(groupString) } catch (e: Exception) { GroupBy.NONE }
    }

    suspend fun updateSortBy(sortBy: SortBy) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_BY] = sortBy.name
        }
    }

    suspend fun updateGroupBy(groupBy: GroupBy) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GROUP_BY] = groupBy.name
        }
    }
}
