package com.duhapp.dnotes.features.add_or_update_category.data

import androidx.room.withTransaction
import com.duhapp.dnotes.R
import com.duhapp.dnotes.app.database.AppDatabase
import com.duhapp.dnotes.app.database.CategoryDao
import com.duhapp.dnotes.app.database.NoteDao
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.add_or_update_category.ui.toUIModel
import com.duhapp.dnotes.features.base.data.BaseRepository
import com.duhapp.dnotes.features.base.domain.CustomException
import com.duhapp.dnotes.features.base.domain.CustomExceptionCode
import com.duhapp.dnotes.features.base.domain.CustomExceptionData
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val categoryDao: CategoryDao,
    private val noteDao: NoteDao,
    dispatcher: CoroutineDispatcher
) : CategoryRepository, BaseRepository(dispatcher) {
    private var lastDeletedCategory: CategoryUIModel? = null
    private var lastMovedNoteIds: List<Int> = emptyList()

    override suspend fun deleteCategory(
        categoryUIModel: CategoryUIModel,
        targetCategory: CategoryUIModel
    ) {
        runOnIO {
            try {
                database.withTransaction {
                    // NoteEntity.category_id cascades on delete, so the notes have to be
                    // re-parented before the category row disappears.
                    val movedNoteIds = noteDao.getNoteIdsByCategoryId(categoryUIModel.id)
                    noteDao.moveNotesToCategory(categoryUIModel.id, targetCategory.id)
                    categoryDao.deleteCategoryWithId(categoryUIModel.id)
                    if (categoryUIModel.isDefault) {
                        categoryDao.clearDefaultExcept(targetCategory.id)
                        categoryDao.markAsDefault(targetCategory.id)
                    }
                    // repository keeps the deleted category and the notes it moved so that
                    // undo can restore both, including the default flag
                    lastDeletedCategory = categoryUIModel
                    lastMovedNoteIds = movedNoteIds
                }
            } catch (e: Exception) {
                Timber.e(e)
                throw CustomException.DatabaseException(
                    CustomExceptionData(
                        title = R.string.Error_Database,
                        message = R.string.Error_While_Deleting_Category,
                        code = CustomExceptionCode.DATABASE_EXCEPTION.code
                    )
                )
            }
        }
    }

    override suspend fun insert(categoryUIModel: CategoryUIModel): Int {
        val category = categoryUIModel.toEntity()
        var id: Long = -1
        runOnIO {
            try {
                id = categoryDao.insert(category)
            } catch (e: Exception) {
                Timber.e(e)
                throw CustomException.DatabaseException(
                    CustomExceptionData(
                        title = R.string.Error_Database,
                        message = R.string.Error_While_Inserting_Category,
                        code = CustomExceptionCode.DATABASE_EXCEPTION.code
                    )
                )
            }
        }
        return id.toInt()
    }

    override suspend fun getById(id: Int): CategoryUIModel? = runOnIO {
        try {
            categoryDao.getById(id)?.toUIModel()
        } catch (e: Exception) {
            Timber.e(e)
            throw CustomException.DatabaseException(
                CustomExceptionData(
                    title = R.string.Error_Database,
                    message = R.string.Error_While_Fetching_Category,
                    code = CustomExceptionCode.DATABASE_EXCEPTION.code
                )
            )
        }
    }

    override suspend fun getDefaultCategory(): CategoryUIModel? = runOnIO {
        try {
            categoryDao.getDefaultCategory()?.toUIModel()
        } catch (e: Exception) {
            Timber.e(e)
            throw CustomException.DatabaseException(
                CustomExceptionData(
                    title = R.string.Error_Database,
                    message = R.string.Default_Category_Could_Not_Be_Fetch,
                    code = CustomExceptionCode.DATABASE_EXCEPTION.code
                )
            )
        }
    }

    override suspend fun setDefaultCategory(id: Int) {
        runOnIO {
            try {
                database.withTransaction {
                    categoryDao.clearDefaultExcept(id)
                    categoryDao.markAsDefault(id)
                }
            } catch (e: Exception) {
                Timber.e(e)
                throw CustomException.DatabaseException(
                    CustomExceptionData(
                        title = R.string.Error_Database,
                        message = R.string.Error_While_Updating_Category,
                        code = CustomExceptionCode.DATABASE_EXCEPTION.code
                    )
                )
            }
        }
    }

    override suspend fun undo(): Boolean {
        val deletedCategory = lastDeletedCategory
            ?: throw CustomException.UndoUnavailableException(
                CustomExceptionData(R.string.undo_unavailable, R.string.undo_unavailable, -1)
            )
        val movedNoteIds = lastMovedNoteIds
        try {
            runOnIO {
                database.withTransaction {
                    categoryDao.insert(deletedCategory.toEntity())
                    if (movedNoteIds.isNotEmpty()) {
                        noteDao.moveNotesToCategoryByIds(movedNoteIds, deletedCategory.id)
                    }
                    if (deletedCategory.isDefault) {
                        categoryDao.clearDefaultExcept(deletedCategory.id)
                        categoryDao.markAsDefault(deletedCategory.id)
                    }
                }
            }
            clearLastDeletedCategory()
        } catch (e: Exception) {
            Timber.e("Undo process didn't succeeded")
            clearLastDeletedCategory()
            throw CustomException.DatabaseException(
                CustomExceptionData(
                    title = R.string.Error_Database,
                    message = R.string.Error_While_Inserting_Category,
                    code = CustomExceptionCode.DATABASE_EXCEPTION.code
                )
            )
        }
        return true
    }

    override suspend fun updateCategory(categoryUIModel: CategoryUIModel) {
        val category = categoryUIModel.toEntity()
        category.id = categoryUIModel.id
        runOnIO {
            try {
                categoryDao.update(category)
            } catch (e: Exception) {
                Timber.e(e)
                throw CustomException.DatabaseException(
                    CustomExceptionData(
                        title = R.string.Error_Database,
                        message = R.string.Error_While_Updating_Category,
                        code = CustomExceptionCode.DATABASE_EXCEPTION.code
                    )
                )
            }
        }
    }

    override suspend fun getCategories(): List<CategoryUIModel> = runOnIO {
        try {
            categoryDao.getCategories().map { categoryEntity ->
                categoryEntity.toUIModel()
            }
        } catch (e: Exception) {
            Timber.e(e)
            throw CustomException.DatabaseException(
                CustomExceptionData(
                    title = R.string.Error_Database,
                    message = R.string.Error_While_Fetching_Category,
                    code = CustomExceptionCode.DATABASE_EXCEPTION.code
                )
            )
        }
    }

    private fun clearLastDeletedCategory() {
        lastDeletedCategory = null
        lastMovedNoteIds = emptyList()
    }
}
