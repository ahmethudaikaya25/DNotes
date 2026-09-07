package com.duhapp.dnotes.features.add_or_update_category.domain

import com.duhapp.dnotes.R
import com.duhapp.dnotes.features.add_or_update_category.data.CategoryRepository
import com.duhapp.dnotes.features.add_or_update_category.ui.CategoryUIModel
import com.duhapp.dnotes.features.base.domain.CustomException
import com.duhapp.dnotes.features.base.domain.CustomExceptionCode
import com.duhapp.dnotes.features.base.domain.CustomExceptionData
import com.duhapp.dnotes.features.base.domain.asCustomException

class DeleteCategory(
    private val categoryRepository: CategoryRepository,
    private val defaultCategoryModel: CategoryUIModel,
) {
    /**
     * Deletes [categoryUIModel] and moves its notes to another category.
     *
     * The default category can be deleted too, as long as another category is left to take
     * over the default role — [newDefaultCategoryId] names it and is required in that case.
     */
    suspend operator fun invoke(
        categoryUIModel: CategoryUIModel,
        newDefaultCategoryId: Int? = null,
    ) {
        if (categoryUIModel.id <= 0) {
            throw CustomException.NotValidParametersException(
                CustomExceptionData(
                    title = R.string.Category_Deleting_Error,
                    message = R.string.Category_Id_Cannot_Be_Minus_Val,
                    code = CustomExceptionCode.NOT_VALID_PARAMETERS_EXCEPTION.code
                )
            )
        }

        // the stored row is the source of truth for the default flag, not the UI copy
        val storedCategory = categoryRepository.getById(categoryUIModel.id)
            ?: throw CustomException.NotValidParametersException(
                CustomExceptionData(
                    title = R.string.Category_Deleting_Error,
                    message = R.string.Category_Could_Not_Be_Found,
                    code = CustomExceptionCode.NOT_VALID_PARAMETERS_EXCEPTION.code
                )
            )

        val remainingCategories = categoryRepository.getCategories()
            .filter { it.id != storedCategory.id }

        if (remainingCategories.isEmpty()) {
            throw CustomException.NotValidParametersException(
                CustomExceptionData(
                    title = R.string.Category_Deleting_Error,
                    message = R.string.Last_Category_Could_Not_Be_Deleted,
                    code = CustomExceptionCode.NOT_VALID_PARAMETERS_EXCEPTION.code
                )
            )
        }

        val targetCategory = if (storedCategory.isDefault) {
            remainingCategories.firstOrNull { it.id == newDefaultCategoryId }
                ?: throw CustomException.NotValidParametersException(
                    CustomExceptionData(
                        title = R.string.Category_Deleting_Error,
                        message = R.string.New_Default_Category_Must_Be_Selected,
                        code = CustomExceptionCode.NOT_VALID_PARAMETERS_EXCEPTION.code
                    )
                )
        } else {
            categoryRepository.getDefaultCategory()
                ?: remainingCategories.firstOrNull { it.id == defaultCategoryModel.id }
                ?: remainingCategories.first()
        }

        try {
            categoryRepository.deleteCategory(storedCategory, targetCategory)
        } catch (e: Exception) {
            throw e.asCustomException(
                title = R.string.Unknown_Error,
                message = R.string.Error_While_Deleting_Category,
                code = CustomExceptionCode.UNKNOWN_EXCEPTION.code
            )
        }
    }
}
