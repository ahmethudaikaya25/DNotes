package com.duhapp.dnotes.di

import android.content.Context
import androidx.room.Room
import com.duhapp.dnotes.app.database.AppDatabase
import com.duhapp.dnotes.app.database.CategoryDao
import com.duhapp.dnotes.app.database.NoteDao
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.duhapp.dnotes.features.add_or_update_category.data.CategoryRepository
import com.duhapp.dnotes.features.add_or_update_category.data.CategoryRepositoryImpl
import com.duhapp.dnotes.features.note.data.NoteRepository
import com.duhapp.dnotes.features.note.data.NoteRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dnotes_preferences")

@InstallIn(SingletonComponent::class)
@Module
object DomainModule {
    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "app_database"
        ).addMigrations(
            AppDatabase.MIGRATION_1_2,
            AppDatabase.MIGRATION_2_3,
            AppDatabase.MIGRATION_3_4
        ).build()
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(
        database: AppDatabase,
        categoryDao: CategoryDao,
        noteDao: NoteDao
    ): CategoryRepository {
        return CategoryRepositoryImpl(database, categoryDao, noteDao, Dispatchers.IO)
    }

    @Provides
    @Singleton
    fun provideNoteRepository(noteDao: NoteDao, categoryDao: CategoryDao): NoteRepository {
        return NoteRepositoryImpl(noteDao, categoryDao, Dispatchers.IO)
    }
}
