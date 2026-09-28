package com.moneytracker.data.repository

import com.moneytracker.core.database.MoneyTrackerDatabase
import com.moneytracker.core.database.dao.CategoryDao
import com.moneytracker.core.database.entity.CategoryEntity
import com.moneytracker.data.mapper.CategoryMapper
import com.moneytracker.domain.model.Category
import com.moneytracker.domain.repository.CategoryRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val database: MoneyTrackerDatabase,
    private val categoryDao: CategoryDao,
    private val categoriesApi: com.moneytracker.core.network.api.CategoriesApi
) : CategoryRepository {

    override fun getCategoriesByType(type: Category.CategoryType): Flowable<List<Category>> {
        return categoryDao.getCategoriesByType(type.name)
            .map { entities -> entities.map { CategoryMapper.toDomain(it) } }
    }

    override fun getParentCategoriesByType(type: Category.CategoryType): Flowable<List<Category>> {
        return categoryDao.getParentCategoriesByType(type.name)
            .map { entities -> entities.map { CategoryMapper.toDomain(it) } }
    }

    override fun getChildCategories(parentId: String): Flowable<List<Category>> {
        return categoryDao.getChildCategories(parentId)
            .map { entities -> entities.map { CategoryMapper.toDomain(it) } }
    }

    override fun getAllCategories(): Flowable<List<Category>> {
        return categoryDao.getAllCategories()
            .map { entities -> entities.map { CategoryMapper.toDomain(it) } }
    }

    override fun getCategory(categoryId: String): Maybe<Category> {
        return categoryDao.getCategory(categoryId)
            .map { CategoryMapper.toDomain(it) }
    }

    override fun insertCategory(category: Category): Completable {
        return Completable.fromAction {
            val entity = toEntity(category)
            categoryDao.insertCategory(entity)
        }.subscribeOn(Schedulers.io())
    }

    override fun insertCategories(categories: List<Category>): Completable {
        return Completable.fromAction {
            val entities = categories.map { toEntity(it) }
            categoryDao.insertCategories(entities)
        }.subscribeOn(Schedulers.io())
    }

    override fun updateCategory(category: Category): Completable {
        return Completable.fromAction {
            val entity = toEntity(category)
            categoryDao.updateCategory(entity)
        }.subscribeOn(Schedulers.io())
    }

    override fun deleteCustomCategory(categoryId: String): Completable {
        return Completable.fromAction {
            categoryDao.deleteCustomCategory(categoryId)
        }.subscribeOn(Schedulers.io())
    }

    private fun toEntity(category: Category): CategoryEntity {
        return CategoryEntity(
            id = category.id,
            name = category.name,
            parentId = category.parentId,
            icon = category.icon,
            type = category.type.name,
            color = category.color,
            isSystem = category.isSystem,
            sortOrder = category.sortOrder,
            createdAt = java.time.Instant.now().toString(),
            updatedAt = java.time.Instant.now().toString()
        )
    }
}