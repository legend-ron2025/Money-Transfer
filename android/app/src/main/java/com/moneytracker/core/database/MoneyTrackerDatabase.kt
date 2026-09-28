package com.moneytracker.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.moneytracker.core.database.converter.Converters
import com.moneytracker.core.database.dao.AccountDao
import com.moneytracker.core.database.dao.AnalyticsCacheDao
import com.moneytracker.core.database.dao.BudgetDao
import com.moneytracker.core.database.dao.CategoryDao
import com.moneytracker.core.database.dao.ConsentDao
import com.moneytracker.core.database.dao.GoalDao
import com.moneytracker.core.database.dao.NotificationDao
import com.moneytracker.core.database.dao.TransactionDao
import com.moneytracker.core.database.entity.AccountEntity
import com.moneytracker.core.database.entity.AnalyticsCacheEntity
import com.moneytracker.core.database.entity.BudgetEntity
import com.moneytracker.core.database.entity.CategoryEntity
import com.moneytracker.core.database.entity.ConsentEntity
import com.moneytracker.core.database.entity.GoalEntity
import com.moneytracker.core.database.entity.NotificationEntity
import com.moneytracker.core.database.entity.TransactionEntity
import com.moneytracker.core.database.entity.UserEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.Singleton
import dagger.Module
import dagger.Provides

@Database(
    entities = [
        UserEntity::class,
        AccountEntity::class,
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class,
        GoalEntity::class,
        ConsentEntity::class,
        NotificationEntity::class,
        AnalyticsCacheEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MoneyTrackerDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun goalDao(): GoalDao
    abstract fun consentDao(): ConsentDao
    abstract fun notificationDao(): NotificationDao
    abstract fun analyticsCacheDao(): AnalyticsCacheDao

    companion object {
        @Volatile
        private var INSTANCE: MoneyTrackerDatabase? = null

        fun getInstance(context: Context): MoneyTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoneyTrackerDatabase::class.java,
                    "moneytracker.db"
                )
                    .fallbackToDestructiveMigration()
                    .setQueryExecutor(AppExecutors.diskIO())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): MoneyTrackerDatabase {
            return getInstance(context)
        }
    }
}