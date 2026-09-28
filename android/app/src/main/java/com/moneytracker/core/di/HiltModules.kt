package com.moneytracker.core.di

import android.content.Context
import androidx.room.Room
import com.moneytracker.core.database.MoneyTrackerDatabase
import com.moneytracker.core.database.dao.*
import com.moneytracker.core.network.NetworkModule
import com.moneytracker.core.security.SecurityModule
import com.moneytracker.core.sync.SyncModule
import com.moneytracker.core.analytics.AnalyticsModule
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.Singleton
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MoneyTrackerDatabase {
        return MoneyTrackerDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideUserDao(database: MoneyTrackerDatabase): UserDao = database.userDao()

    @Provides
    @Singleton
    fun provideAccountDao(database: MoneyTrackerDatabase): AccountDao = database.accountDao()

    @Provides
    @Singleton
    fun provideTransactionDao(database: MoneyTrackerDatabase): TransactionDao = database.transactionDao()

    @Provides
    @Singleton
    fun provideCategoryDao(database: MoneyTrackerDatabase): CategoryDao = database.categoryDao()

    @Provides
    @Singleton
    fun provideBudgetDao(database: MoneyTrackerDatabase): BudgetDao = database.budgetDao()

    @Provides
    @Singleton
    fun provideGoalDao(database: MoneyTrackerDatabase): GoalDao = database.goalDao()

    @Provides
    @Singleton
    fun provideConsentDao(database: MoneyTrackerDatabase): ConsentDao = database.consentDao()

    @Provides
    @Singleton
    fun provideNotificationDao(database: MoneyTrackerDatabase): NotificationDao = database.notificationDao()

    @Provides
    @Singleton
    fun provideAnalyticsCacheDao(database: MoneyTrackerDatabase): AnalyticsCacheDao = database.analyticsCacheDao()
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context
}