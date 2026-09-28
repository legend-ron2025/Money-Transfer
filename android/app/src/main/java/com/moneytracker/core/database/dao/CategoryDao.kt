package com.moneytracker.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.moneytracker.core.database.entity.CategoryEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategories(categories: List<CategoryEntity>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(category: CategoryEntity): Completable

    @Update
    fun updateCategory(category: CategoryEntity): Completable

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    fun getCategory(categoryId: String): Maybe<CategoryEntity>

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY sort_order, name")
    fun getCategoriesByType(type: String): Flowable<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE parent_id IS NULL AND type = :type ORDER BY sort_order, name")
    fun getParentCategoriesByType(type: String): Flowable<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE parent_id = :parentId ORDER BY sort_order, name")
    fun getChildCategories(parentId: String): Flowable<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY type, sort_order, name")
    fun getAllCategories(): Flowable<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE is_system = 1 ORDER BY type, sort_order, name")
    fun getSystemCategories(): Flowable<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE is_system = 0 ORDER BY type, sort_order, name")
    fun getCustomCategories(): Flowable<List<CategoryEntity>>

    @Query("DELETE FROM categories WHERE id = :categoryId AND is_system = 0")
    fun deleteCustomCategory(categoryId: String): Completable
}