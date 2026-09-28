package com.moneytracker.feature.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.moneytracker.domain.model.CategorySpend

private val PERIOD_TABS = listOf("Today", "Week", "Month", "Year")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(navController: NavController, viewModel: AnalyticsViewModel = hiltViewModel()) {
    val state by viewModel.analyticsState.observeAsState(AnalyticsViewModel.AnalyticsState.Loading)
    var selectedPeriod by remember { mutableIntStateOf(2) } // default: Month

    LaunchedEffect(selectedPeriod) {
        val period = when (selectedPeriod) { 0 -> "today"; 1 -> "week"; 3 -> "year"; else -> "month" }
        viewModel.loadAnalytics(period)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { pad ->
        Column(Modifier.padding(pad)) {
            // Period tabs
            TabRow(selectedTabIndex = selectedPeriod, containerColor = MaterialTheme.colorScheme.background, contentColor = MaterialTheme.colorScheme.primary) {
                PERIOD_TABS.forEachIndexed { i, label ->
                    Tab(selected = selectedPeriod == i, onClick = { selectedPeriod = i }, text = { Text(label) })
                }
            }

            when (val s = state) {
                is AnalyticsViewModel.AnalyticsState.Loading ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is AnalyticsViewModel.AnalyticsState.Error ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(s.message, color = MaterialTheme.colorScheme.error) }
                is AnalyticsViewModel.AnalyticsState.Success ->
                    AnalyticsContent(s)
            }
        }
    }
}

@Composable
private fun AnalyticsContent(s: AnalyticsViewModel.AnalyticsState.Success) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Summary cards
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard(Modifier.weight(1f), "Income",   s.overview.totalIncome.formatted,  Color(0xFF4CAF50))
                SummaryCard(Modifier.weight(1f), "Expense",  s.overview.totalExpense.formatted,  MaterialTheme.colorScheme.error)
                SummaryCard(Modifier.weight(1f), "Savings",  s.overview.netSavings.formatted,    MaterialTheme.colorScheme.primary)
            }
        }
        // Savings rate
        item {
            Card(colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Savings Rate", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { (s.overview.savingsRate / 100).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                        color = Color(0xFF4CAF50),
                        trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("${String.format("%.1f", s.overview.savingsRate)}% of income saved", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        // Spending by category
        if (s.overview.topCategories.isNotEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Spending by Category", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        s.overview.topCategories.forEach { cat ->
                            CategoryBar(cat, s.overview.totalExpense.amount)
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
        // Monthly trend
        if (s.monthlyTrend.isNotEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("6-Month Trend", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        MonthlyTrendBars(s.monthlyTrend)
                    }
                }
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun SummaryCard(modifier: Modifier, label: String, value: String, color: Color) {
    Card(modifier, colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color, maxLines = 1)
        }
    }
}

@Composable
private fun CategoryBar(cat: CategorySpend, totalExpense: Double) {
    val pct = if (totalExpense > 0) (cat.amount.amount / totalExpense).toFloat().coerceIn(0f, 1f) else 0f
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(cat.icon, fontSize = 18.sp, modifier = Modifier.width(28.dp))
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(cat.categoryName, style = MaterialTheme.typography.bodySmall)
                Text(cat.amount.formatted, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { pct },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        }
    }
}

@Composable
private fun MonthlyTrendBars(trend: List<AnalyticsViewModel.MonthlyData>) {
    val maxVal = trend.maxOfOrNull { maxOf(it.income, it.expense) }?.takeIf { it > 0 } ?: 1.0
    Row(Modifier.fillMaxWidth().height(100.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.Bottom) {
        trend.forEach { m ->
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                // Income bar
                Box(Modifier.fillMaxWidth(0.45f).fillMaxHeight((m.income / maxVal).toFloat().coerceIn(0.02f, 1f)).clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)).background(Color(0xFF4CAF50)))
                Spacer(Modifier.height(2.dp))
                // Expense bar
                Box(Modifier.fillMaxWidth(0.45f).fillMaxHeight((m.expense / maxVal).toFloat().coerceIn(0.02f, 1f)).clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)).background(MaterialTheme.colorScheme.error.copy(alpha = 0.7f)))
            }
        }
    }
    Spacer(Modifier.height(4.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        trend.forEach { m ->
            Text(m.month, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f), maxLines = 1)
        }
    }
}
