package com.moneytracker.feature.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.moneytracker.domain.model.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(navController: NavController, viewModel: DashboardViewModel = hiltViewModel()) {
    val state by viewModel.overview.observeAsState(DashboardViewModel.DashboardState.Loading)
    LaunchedEffect(Unit) { viewModel.loadDashboard() }

    when (val s = state) {
        is DashboardViewModel.DashboardState.Loading -> DashboardSkeleton()
        is DashboardViewModel.DashboardState.Error   -> DashboardError(s.message) { viewModel.loadDashboard() }
        is DashboardViewModel.DashboardState.Success -> DashboardContent(s, navController)
    }
}

// ── Skeleton ──────────────────────────────────────────────────────────────────
@Composable
private fun DashboardSkeleton() {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        items(5) {
            Box(
                Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        }
    }
}

// ── Error ─────────────────────────────────────────────────────────────────────
@Composable
private fun DashboardError(msg: String, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.ErrorOutline, null, Modifier.size(56.dp), tint = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(16.dp))
        Text("Couldn't load dashboard", style = MaterialTheme.typography.titleMedium)
        Text(msg, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(20.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

// ── Main content ──────────────────────────────────────────────────────────────
@Composable
private fun DashboardContent(state: DashboardViewModel.DashboardState.Success, nav: NavController) {
    LazyColumn(contentPadding = PaddingValues(bottom = 88.dp)) {
        item { DashboardHeader(state.unreadNotificationCount) }
        item { Spacer(Modifier.height(4.dp)) }
        item { BalanceCard(state.overview) }
        item { Spacer(Modifier.height(14.dp)) }
        item { CashFlowRow(state.overview) }
        item { Spacer(Modifier.height(14.dp)) }
        if (state.overview.topCategories.isNotEmpty()) {
            item { CategoryBreakdownCard(state.overview.topCategories, nav) }
            item { Spacer(Modifier.height(14.dp)) }
        }
        if (state.insights.isNotEmpty()) {
            item { InsightsCard(state.insights) }
            item { Spacer(Modifier.height(14.dp)) }
        }
        if (state.overview.upcomingPayments.isNotEmpty()) {
            item { UpcomingCard(state.overview.upcomingPayments) }
            item { Spacer(Modifier.height(14.dp)) }
        }
        if (state.overview.recentTransactions.isNotEmpty()) {
            item { RecentTransactionsCard(state.overview.recentTransactions, nav) }
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────
@Composable
private fun DashboardHeader(unread: Int) {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greet = when { hour < 12 -> "Good morning"; hour < 17 -> "Good afternoon"; else -> "Good evening" }
    val date  = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())

    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("$greet 👋", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        BadgedBox(badge = { if (unread > 0) Badge { Text(unread.coerceAtMost(99).toString()) } }) {
            IconButton(onClick = {}) {
                Icon(Icons.Default.NotificationsNone, "Notifications")
            }
        }
    }
}

// ── Balance card (animated count-up) ─────────────────────────────────────────
@Composable
private fun BalanceCard(overview: AnalyticsOverview) {
    var started by remember { mutableStateOf(false) }
    val animated by animateFloatAsState(
        targetValue = if (started) overview.totalBalance.amount.toFloat() else 0f,
        animationSpec = tween(1400), label = "balance"
    )
    LaunchedEffect(Unit) { started = true }

    Box(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(listOf(
                    MaterialTheme.colorScheme.primary,
                    Color(0xFF5C6BC0)
                ))
            )
            .padding(24.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column {
                Text("Total Balance", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f), style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(6.dp))
                Text(
                    "₹${String.format("%,.0f", animated)}",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold
                )
                if (overview.balanceChange.amount != 0.0) {
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (overview.balanceChange.isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            null, Modifier.size(16.dp),
                            tint = if (overview.balanceChange.isPositive) Color(0xFF81C784) else Color(0xFFEF9A9A)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${overview.balanceChange.formattedWithSign} this month",
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            Box(
                Modifier.size(52.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AccountBalanceWallet, null, Modifier.size(28.dp), tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

// ── Cash-flow row ─────────────────────────────────────────────────────────────
@Composable
private fun CashFlowRow(overview: AnalyticsOverview) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FlowCard(Modifier.weight(1f), "Income",   "↑", overview.totalIncome.formatted,  Color(0xFF4CAF50))
        FlowCard(Modifier.weight(1f), "Expenses", "↓", overview.totalExpense.formatted, MaterialTheme.colorScheme.error)
    }
}

@Composable
private fun FlowCard(modifier: Modifier, label: String, arrow: String, amount: String, color: Color) {
    Card(modifier, colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(32.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                    Text(arrow, color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(Modifier.width(8.dp))
                Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            Text(amount, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

// ── Category breakdown ────────────────────────────────────────────────────────
@Composable
private fun CategoryBreakdownCard(cats: List<CategorySpend>, nav: NavController) {
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Top Categories", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                TextButton(onClick = { nav.navigate("analytics") }) { Text("See all") }
            }
            cats.take(5).forEachIndexed { idx, cat ->
                if (idx > 0) HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp))
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                            Text(cat.icon, fontSize = 20.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(cat.categoryName, style = MaterialTheme.typography.bodyMedium)
                            Text("${cat.transactionCount} txns", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(cat.amount.formatted, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        Text(cat.percentage.formatted, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

// ── Insights ──────────────────────────────────────────────────────────────────
@Composable
private fun InsightsCard(insights: List<Insight>) {
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("💡 Insights", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            insights.take(3).forEach { ins ->
                val (bg, fg) = when (ins.severity) {
                    InsightSeverity.CRITICAL -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
                    InsightSeverity.WARNING  -> Color(0xFFFFF8E1) to Color(0xFF5D4037)
                    else                     -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
                }
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(bg).padding(12.dp)
                ) {
                    Column {
                        Text(ins.title, style = MaterialTheme.typography.labelLarge, color = fg, fontWeight = FontWeight.Medium)
                        Text(ins.description, style = MaterialTheme.typography.bodySmall, color = fg.copy(alpha = 0.8f))
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ── Upcoming payments ─────────────────────────────────────────────────────────
@Composable
private fun UpcomingCard(payments: List<UpcomingPayment>) {
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Upcoming Payments", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            payments.take(3).forEach { p ->
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(p.name, style = MaterialTheme.typography.bodyMedium)
                        Text(p.dueDate.take(10), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(p.amount.formatted, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

// ── Recent transactions ───────────────────────────────────────────────────────
@Composable
private fun RecentTransactionsCard(txns: List<Transaction>, nav: NavController) {
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Recent Transactions", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                TextButton(onClick = { nav.navigate("transactions") }) { Text("See all") }
            }
            txns.take(5).forEachIndexed { i, txn ->
                if (i > 0) HorizontalDivider(thickness = 0.5.dp)
                Row(
                    Modifier.fillMaxWidth().clickable { nav.navigate("transaction_detail/${txn.id}") }.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.secondaryContainer), contentAlignment = Alignment.Center) {
                            Text(txn.category?.icon ?: if (txn.isIncome) "💰" else "💸", fontSize = 20.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(txn.merchantName ?: txn.description, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, maxLines = 1)
                            Text(txn.category?.name ?: "Uncategorized", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Text(
                        txn.displayAmount.formattedWithSign,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (txn.isIncome) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
