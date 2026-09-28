package com.moneytracker.core.di

import com.moneytracker.core.database.MoneyTrackerDatabase
import com.moneytracker.core.database.dao.*
import com.moneytracker.core.network.api.*
import com.moneytracker.core.network.NetworkModule
import com.moneytracker.core.security.SecurityModule
import com.moneytracker.core.sync.SyncModule
import com.moneytracker.core.analytics.AnalyticsModule
import com.moneytracker.data.mapper.*
import com.moneytracker.data.repository.*
import com.moneytracker.domain.repository.*
import com.moneytracker.domain.usecase.*
import dagger.hilt.android.scopes.Singleton
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

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

    @Provides
    @Singleton
    fun provideAccountRepository(impl: AccountRepositoryImpl): AccountRepository = impl

    @Provides
    @Singleton
    fun provideTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository = impl

    @Provides
    @Singleton
    fun provideCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository = impl

    @Provides
    @Singleton
    fun provideBudgetRepository(impl: BudgetRepositoryImpl): BudgetRepository = impl

    @Provides
    @Singleton
    fun provideGoalRepository(impl: GoalRepositoryImpl): GoalRepository = impl

    @Provides
    @Singleton
    fun provideConsentRepository(impl: ConsentRepositoryImpl): ConsentRepository = impl

    @Provides
    @Singleton
    fun provideNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository = impl

    @Provides
    @Singleton
    fun provideAnalyticsRepository(impl: AnalyticsRepositoryImpl): AnalyticsRepository = impl
}

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase = LoginUseCase(authRepository)

    @Provides
    @Singleton
    fun provideRegisterUseCase(authRepository: AuthRepository): RegisterUseCase = RegisterUseCase(authRepository)

    @Provides
    @Singleton
    fun provideRefreshTokenUseCase(authRepository: AuthRepository): RefreshTokenUseCase = RefreshTokenUseCase(authRepository)

    @Provides
    @Singleton
    fun provideLogoutUseCase(authRepository: AuthRepository): LogoutUseCase = LogoutUseCase(authRepository)

    @Provides
    @Singleton
    fun provideGetCurrentUserUseCase(authRepository: AuthRepository): GetCurrentUserUseCase = GetCurrentUserUseCase(authRepository)

    @Provides
    @Singleton
    fun provideGetAccountsUseCase(accountRepository: AccountRepository): GetAccountsUseCase = GetAccountsUseCase(accountRepository)

    @Provides
    @Singleton
    fun provideGetAccountUseCase(accountRepository: AccountRepository): GetAccountUseCase = GetAccountUseCase(accountRepository)

    @Provides
    @Singleton
    fun provideGetTotalBalanceUseCase(accountRepository: AccountRepository): GetTotalBalanceUseCase = GetTotalBalanceUseCase(accountRepository)

    @Provides
    @Singleton
    fun provideSyncAccountUseCase(accountRepository: AccountRepository): SyncAccountUseCase = SyncAccountUseCase(accountRepository)

    @Provides
    @Singleton
    fun provideSyncAllAccountsUseCase(accountRepository: AccountRepository): SyncAllAccountsUseCase = SyncAllAccountsUseCase(accountRepository)

    @Provides
    @Singleton
    fun provideAddManualAccountUseCase(accountRepository: AccountRepository): AddManualAccountUseCase = AddManualAccountUseCase(accountRepository)

    @Provides
    @Singleton
    fun provideUpdateAccountUseCase(accountRepository: AccountRepository): UpdateAccountUseCase = UpdateAccountUseCase(accountRepository)

    @Provides
    @Singleton
    fun provideDeleteAccountUseCase(accountRepository: AccountRepository): DeleteAccountUseCase = DeleteAccountUseCase(accountRepository)

    @Provides
    @Singleton
    fun provideReconnectAccountUseCase(accountRepository: AccountRepository): ReconnectAccountUseCase = ReconnectAccountUseCase(accountRepository)

    @Provides
    @Singleton
    fun provideGetTransactionsUseCase(transactionRepository: TransactionRepository): GetTransactionsUseCase = GetTransactionsUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideGetTransactionUseCase(transactionRepository: TransactionRepository): GetTransactionUseCase = GetTransactionUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideGetTransactionsByDateRangeUseCase(transactionRepository: TransactionRepository): GetTransactionsByDateRangeUseCase = GetTransactionsByDateRangeUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideGetTransactionsByCategoryUseCase(transactionRepository: TransactionRepository): GetTransactionsByCategoryUseCase = GetTransactionsByCategoryUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideGetTransactionsByAccountUseCase(transactionRepository: TransactionRepository): GetTransactionsByAccountUseCase = GetTransactionsByAccountUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideGetTransfersUseCase(transactionRepository: TransactionRepository): GetTransfersUseCase = GetTransfersUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideGetRecurringTransactionsUseCase(transactionRepository: TransactionRepository): GetRecurringTransactionsUseCase = GetRecurringTransactionsUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideSearchTransactionsUseCase(transactionRepository: TransactionRepository): SearchTransactionsUseCase = SearchTransactionsUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideGetTotalIncomeUseCase(transactionRepository: TransactionRepository): GetTotalIncomeUseCase = GetTotalIncomeUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideGetTotalExpenseUseCase(transactionRepository: TransactionRepository): GetTotalExpenseUseCase = GetTotalExpenseUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideGetCategorySpendingUseCase(transactionRepository: TransactionRepository): GetCategorySpendingUseCase = GetCategorySpendingUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideCreateTransactionUseCase(transactionRepository: TransactionRepository): CreateTransactionUseCase = CreateTransactionUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideUpdateTransactionUseCase(transactionRepository: TransactionRepository): UpdateTransactionUseCase = UpdateTransactionUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideDeleteTransactionUseCase(transactionRepository: TransactionRepository): DeleteTransactionUseCase = DeleteTransactionUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideBulkUpdateTransactionsUseCase(transactionRepository: TransactionRepository): BulkUpdateTransactionsUseCase = BulkUpdateTransactionsUseCase(transactionRepository)

    @Provides
    @Singleton
    fun provideGetCategoriesByTypeUseCase(categoryRepository: CategoryRepository): GetCategoriesByTypeUseCase = GetCategoriesByTypeUseCase(categoryRepository)

    @Provides
    @Singleton
    fun provideGetParentCategoriesUseCase(categoryRepository: CategoryRepository): GetParentCategoriesUseCase = GetParentCategoriesUseCase(categoryRepository)

    @Provides
    @Singleton
    fun provideGetChildCategoriesUseCase(categoryRepository: CategoryRepository): GetChildCategoriesUseCase = GetChildCategoriesUseCase(categoryRepository)

    @Provides
    @Singleton
    fun provideGetAllCategoriesUseCase(categoryRepository: CategoryRepository): GetAllCategoriesUseCase = GetAllCategoriesUseCase(categoryRepository)

    @Provides
    @Singleton
    fun provideGetCategoryUseCase(categoryRepository: CategoryRepository): GetCategoryUseCase = GetCategoryUseCase(categoryRepository)

    @Provides
    @Singleton
    fun provideCreateCategoryUseCase(categoryRepository: CategoryRepository): CreateCategoryUseCase = CreateCategoryUseCase(categoryRepository)

    @Provides
    @Singleton
    fun provideUpdateCategoryUseCase(categoryRepository: CategoryRepository): UpdateCategoryUseCase = UpdateCategoryUseCase(categoryRepository)

    @Provides
    @Singleton
    fun provideDeleteCategoryUseCase(categoryRepository: CategoryRepository): DeleteCategoryUseCase = DeleteCategoryUseCase(categoryRepository)

    @Provides
    @Singleton
    fun provideInitializeDefaultCategoriesUseCase(categoryRepository: CategoryRepository): InitializeDefaultCategoriesUseCase = InitializeDefaultCategoriesUseCase(categoryRepository)

    @Provides
    @Singleton
    fun provideGetBudgetsUseCase(budgetRepository: BudgetRepository): GetBudgetsUseCase = GetBudgetsUseCase(budgetRepository)

    @Provides
    @Singleton
    fun provideGetActiveBudgetsUseCase(budgetRepository: BudgetRepository): GetActiveBudgetsUseCase = GetActiveBudgetsUseCase(budgetRepository)

    @Provides
    @Singleton
    fun provideGetBudgetUseCase(budgetRepository: BudgetRepository): GetBudgetUseCase = GetBudgetUseCase(budgetRepository)

    @Provides
    @Singleton
    fun provideGetBudgetByCategoryUseCase(budgetRepository: BudgetRepository): GetBudgetByCategoryUseCase = GetBudgetByCategoryUseCase(budgetRepository)

    @Provides
    @Singleton
    fun provideGetBudgetsWithCategoryUseCase(budgetRepository: BudgetRepository): GetBudgetsWithCategoryUseCase = GetBudgetsWithCategoryUseCase(budgetRepository)

    @Provides
    @Singleton
    fun provideCreateBudgetUseCase(budgetRepository: BudgetRepository): CreateBudgetUseCase = CreateBudgetUseCase(budgetRepository)

    @Provides
    @Singleton
    fun provideUpdateBudgetUseCase(budgetRepository: BudgetRepository): UpdateBudgetUseCase = UpdateBudgetUseCase(budgetRepository)

    @Provides
    @Singleton
    fun provideUpdateBudgetSpentUseCase(budgetRepository: BudgetRepository): UpdateBudgetSpentUseCase = UpdateBudgetSpentUseCase(budgetRepository)

    @Provides
    @Singleton
    fun provideDeleteBudgetUseCase(budgetRepository: BudgetRepository): DeleteBudgetUseCase = DeleteBudgetUseCase(budgetRepository)

    @Provides
    @Singleton
    fun provideGetGoalsUseCase(goalRepository: GoalRepository): GetGoalsUseCase = GetGoalsUseCase(goalRepository)

    @Provides
    @Singleton
    fun provideGetActiveGoalsUseCase(goalRepository: GoalRepository): GetActiveGoalsUseCase = GetActiveGoalsUseCase(goalRepository)

    @Provides
    @Singleton
    fun provideGetGoalUseCase(goalRepository: GoalRepository): GetGoalUseCase = GetGoalUseCase(goalRepository)

    @Provides
    @Singleton
    fun provideCreateGoalUseCase(goalRepository: GoalRepository): CreateGoalUseCase = CreateGoalUseCase(goalRepository)

    @Provides
    @Singleton
    fun provideUpdateGoalUseCase(goalRepository: GoalRepository): UpdateGoalUseCase = UpdateGoalUseCase(goalRepository)

    @Provides
    @Singleton
    fun provideUpdateGoalProgressUseCase(goalRepository: GoalRepository): UpdateGoalProgressUseCase = UpdateGoalProgressUseCase(goalRepository)

    @Provides
    @Singleton
    fun provideUpdateGoalStatusUseCase(goalRepository: GoalRepository): UpdateGoalStatusUseCase = UpdateGoalStatusUseCase(goalRepository)

    @Provides
    @Singleton
    fun provideDeleteGoalUseCase(goalRepository: GoalRepository): DeleteGoalUseCase = DeleteGoalUseCase(goalRepository)

    @Provides
    @Singleton
    fun provideGetOverviewUseCase(analyticsRepository: AnalyticsRepository): GetOverviewUseCase = GetOverviewUseCase(analyticsRepository)

    @Provides
    @Singleton
    fun provideGetSpendingAnalyticsUseCase(analyticsRepository: AnalyticsRepository): GetSpendingAnalyticsUseCase = GetSpendingAnalyticsUseCase(analyticsRepository)

    @Provides
    @Singleton
    fun provideGetIncomeAnalyticsUseCase(analyticsRepository: AnalyticsRepository): GetIncomeAnalyticsUseCase = GetIncomeAnalyticsUseCase(analyticsRepository)

    @Provides
    @Singleton
    fun provideGetCashFlowAnalyticsUseCase(analyticsRepository: AnalyticsRepository): GetCashFlowAnalyticsUseCase = GetCashFlowAnalyticsUseCase(analyticsRepository)

    @Provides
    @Singleton
    fun provideGetTrendAnalyticsUseCase(analyticsRepository: AnalyticsRepository): GetTrendAnalyticsUseCase = GetTrendAnalyticsUseCase(analyticsRepository)

    @Provides
    @Singleton
    fun provideGetInsightsUseCase(analyticsRepository: AnalyticsRepository): GetInsightsUseCase = GetInsightsUseCase(analyticsRepository)

    @Provides
    @Singleton
    fun provideGetCategoryBreakdownUseCase(analyticsRepository: AnalyticsRepository): GetCategoryBreakdownUseCase = GetCategoryBreakdownUseCase(analyticsRepository)

    @Provides
    @Singleton
    fun provideInvalidateAnalyticsCacheUseCase(analyticsRepository: AnalyticsRepository): InvalidateAnalyticsCacheUseCase = InvalidateAnalyticsCacheUseCase(analyticsRepository)
}