package com.moneytracker

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moneytracker.core.security.TokenManager
import com.moneytracker.feature.accounts.AccountsScreen
import com.moneytracker.feature.analytics.AnalyticsScreen
import com.moneytracker.feature.budgets.BudgetsScreen
import com.moneytracker.feature.dashboard.DashboardScreen
import com.moneytracker.feature.goals.GoalsScreen
import com.moneytracker.feature.onboarding.AuthScreen
import com.moneytracker.feature.onboarding.OnboardingScreen
import com.moneytracker.feature.settings.SettingsScreen
import com.moneytracker.feature.subscriptions.SubscriptionsScreen
import com.moneytracker.feature.transactions.TransactionDetailScreen
import com.moneytracker.feature.transactions.TransactionsScreen
import com.moneytracker.ui.theme.MoneyTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var tokenManager: TokenManager
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Block screenshots / screen recording in release builds
        if (!BuildConfig.DEBUG) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }

        setContent {
            MoneyTrackerTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val startDest = if (tokenManager.getAccessToken() != null) "dashboard" else "onboarding"

                    NavHost(navController = navController, startDestination = startDest) {

                        composable("onboarding") {
                            OnboardingScreen(onComplete = {
                                navController.navigate("auth") { popUpTo("onboarding") { inclusive = true } }
                            })
                        }
                        composable("auth") {
                            AuthScreen(onAuthSuccess = {
                                navController.navigate("dashboard") { popUpTo("auth") { inclusive = true } }
                            })
                        }
                        composable("dashboard") {
                            DashboardScreen(navController = navController)
                        }
                        composable("accounts") {
                            AccountsScreen(navController = navController)
                        }
                        composable("transactions") {
                            TransactionsScreen(navController = navController)
                        }
                        composable("analytics") {
                            AnalyticsScreen(navController = navController)
                        }
                        composable("budgets") {
                            BudgetsScreen(navController = navController)
                        }
                        composable("goals") {
                            GoalsScreen(navController = navController)
                        }
                        composable("subscriptions") {
                            SubscriptionsScreen(navController = navController)
                        }
                        composable("settings") {
                            SettingsScreen(navController = navController)
                        }
                        composable("transaction_detail/{transactionId}") { back ->
                            TransactionDetailScreen(
                                transactionId = back.arguments?.getString("transactionId") ?: "",
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}
