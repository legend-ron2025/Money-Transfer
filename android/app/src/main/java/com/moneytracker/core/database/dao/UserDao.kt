package com.moneytracker.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.moneytracker.core.database.entity.UserEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity): Completable

    @Update
    fun updateUser(user: UserEntity): Completable

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUser(userId: String): Maybe<UserEntity>

    @Query("SELECT * FROM users WHERE email = :email")
    fun getUserByEmail(email: String): Maybe<UserEntity>

    @Query("DELETE FROM users WHERE id = :userId")
    fun deleteUser(userId: String): Completable
}