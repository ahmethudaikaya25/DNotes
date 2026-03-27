package com.duhapp.dnotes.app.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [CategoryEntity::class, NoteEntity::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun noteDao(): NoteDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Enforce foreign keys via recreation (SQLite limitation)
                db.execSQL("""
                    CREATE TABLE NoteEntity_new (
                        id INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        details TEXT NOT NULL,
                        category_id INTEGER NOT NULL,
                        FOREIGN KEY(category_id) REFERENCES CategoryEntity(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                // In SQLite auto-increment is written as PRIMARY KEY AUTOINCREMENT not AUTO_INCREMENT
                // Adjusting syntax...
                db.execSQL("""
                    CREATE TABLE NoteEntity_temp (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        details TEXT NOT NULL,
                        category_id INTEGER NOT NULL,
                        FOREIGN KEY(category_id) REFERENCES CategoryEntity(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("INSERT INTO NoteEntity_temp (id, title, details, category_id) SELECT id, title, details, category_id FROM NoteEntity")
                db.execSQL("DROP TABLE NoteEntity")
                db.execSQL("ALTER TABLE NoteEntity_temp RENAME TO NoteEntity")
                db.execSQL("CREATE INDEX index_NoteEntity_category_id ON NoteEntity(category_id)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE NoteEntity ADD COLUMN is_pinned INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE NoteEntity ADD COLUMN created_at INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE NoteEntity ADD COLUMN updated_at INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE CategoryEntity ADD COLUMN sort_order INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
