package com.moneytracker.feature.budgets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.moneytracker.domain.model.Budget
import com.moneytracker.domain.model.BudgetStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetsScreen(navController: NavController, viewModel: BudgetsViewModel = hiltViewModel()) {
    val state by viewModel.state.observeAsState(BudgetsViewModel.BudgetsState.Loading)
    var showCreateSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadBudgets() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budgets", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateSheet = true },
                icon    = { Icon(Icons.Default.Add, null) },
                text    = { Text("New Budget") }
            )
        }
    ) { pad ->
        when (val s = state) {
            is BudgetsViewModel.BudgetsState.Loading ->
                Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) { CircularProgressIndicator() }

            is BudgetsViewModel.BudgetsState.Error ->
                Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(s.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadBudgets() }) { Text("Retry") }
                    }
                }

            is BudgetsViewModel.BudgetsState.Success ->
                if (s.budgets.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💰", fontSize = 56.sp)
                            Spacer(Modifier.height(12.dp))
                            Text("No budgets yet", style = MaterialTheme.typography.titleMedium)
                            Text("Tap + to create your first budget.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(Modifier.padding(pad).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item { Spacer(Modifier.height(4.dp)) }
                        items(s.budgets, key = { it.id }) { budget -> BudgetCard(budget) }
                        item { Spacer(Modifier.height(88.dp)) }
                    }
                }
        }
    }

    if (showCreateSheet) {
        CreateBudgetSheet(
            onDismiss = { showCreateSheet = false },
            onConfirm = { category, amount, period ->
                viewModel.createBudget(category, amount, period)
                showCreateSheet = false
            }
        )
    }
}

@Composable
private fun BudgetCard(budget: Budget) {
    val pct   = budget.percentageUsed.value.toFloat().coerceIn(0f, 100f)
    val color = when (budget.status) {
        BudgetStatus.EXCEEDED   -> MaterialTheme.colorScheme.error
        BudgetStatus.NEAR_LIMIT -> Color(0xFFFFA726)
        BudgetStatus.WATCH      -> Color(0xFFFFCC02)
        else                    -> Color(0xFF4CAF50)
    }

    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        shape  = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(budget.category.icon, fontSize = 24.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(budget.category.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        Text(budget.period.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Surface(shape = RoundedCornerShape(20.dp), color = color.copy(alpha = 0.15f)) {
                    Text(
                        "${pct.toInt()}%",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { pct / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color    = color,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Spent: ${budget.spentAmount.formatted}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Left: ${budget.remainingAmount.formatted}", style = MaterialTheme.typography.bodySmall,
                    color = if (budget.status == BudgetStatus.EXCEEDED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Budget: ${budget.amount.formatted}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateBudgetSheet(onDismiss: () -> Unit, onConfirm: (String, Double, String) -> Unit) {
    var category by remember { mutableStateOf("Food") }
    var amount   by remember { mutableStateOf("") }
    var period   by remember { mutableStateOf("MONTHLY") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(24.dp)) {
            Text("New Budget", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            Spacer(Modifier.height(14.dp))
            OutlinedTextField(
                value = amount, onValueChange = { amount = it },
                label = { Text("Monthly Amount (₹)") },
                prefix = { Text("₹") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(14.dp))
            // Period chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("WEEKLY", "MONTHLY", "YEARLY").forEach { p ->
                    FilterChip(selected = period == p, onClick = { period = p }, label = { Text(p.lowercase().replaceFirstChar { it.uppercase() }) })
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { onConfirm(category, amount.toDoubleOrNull() ?: 0.0, period) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled  = category.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0,
                shape    = RoundedCornerShape(14.dp)
            ) { Text("Create Budget") }
            Spacer(Modifier.height(16.dp))
        }
    }
}
