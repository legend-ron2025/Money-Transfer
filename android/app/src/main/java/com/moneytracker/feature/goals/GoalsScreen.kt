package com.moneytracker.feature.goals

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.moneytracker.domain.model.Goal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(navController: NavController, viewModel: GoalsViewModel = hiltViewModel()) {
    val state by viewModel.state.observeAsState(GoalsViewModel.GoalsState.Loading)
    var showCreate by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadGoals() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Goals", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreate = true },
                icon    = { Icon(Icons.Default.Add, null) },
                text    = { Text("New Goal") }
            )
        }
    ) { pad ->
        when (val s = state) {
            is GoalsViewModel.GoalsState.Loading ->
                Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) { CircularProgressIndicator() }

            is GoalsViewModel.GoalsState.Error ->
                Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(s.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadGoals() }) { Text("Retry") }
                    }
                }

            is GoalsViewModel.GoalsState.Success ->
                if (s.goals.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎯", fontSize = 56.sp)
                            Spacer(Modifier.height(12.dp))
                            Text("No goals yet", style = MaterialTheme.typography.titleMedium)
                            Text("Tap + to create your first financial goal.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(Modifier.padding(pad).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        item { Spacer(Modifier.height(4.dp)) }
                        items(s.goals, key = { it.id }) { goal -> GoalCard(goal) }
                        item { Spacer(Modifier.height(88.dp)) }
                    }
                }
        }
    }

    if (showCreate) {
        CreateGoalSheet(onDismiss = { showCreate = false }, onCreate = { name, icon, target, date ->
            viewModel.createGoal(name, icon, target, date)
            showCreate = false
        })
    }
}

@Composable
private fun GoalCard(goal: Goal) {
    val progress = goal.progress.value.toFloat().coerceIn(0f, 100f) / 100f
    val primary  = MaterialTheme.colorScheme.primary
    val track    = MaterialTheme.colorScheme.surfaceContainerHighest

    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        shape  = RoundedCornerShape(18.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Circular progress indicator (Canvas)
            Box(Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(72.dp)) {
                    val strokeW = 6.dp.toPx()
                    val inset   = strokeW / 2f
                    val rect    = Size(size.width - strokeW, size.height - strokeW)
                    // Track
                    drawArc(color = track, startAngle = -90f, sweepAngle = 360f, useCenter = false, topLeft = Offset(inset, inset), size = rect, style = Stroke(strokeW, cap = StrokeCap.Round))
                    // Progress
                    drawArc(color = primary, startAngle = -90f, sweepAngle = 360f * progress, useCenter = false, topLeft = Offset(inset, inset), size = rect, style = Stroke(strokeW, cap = StrokeCap.Round))
                }
                Text(goal.icon ?: "🎯", fontSize = 26.sp)
            }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Text(goal.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text("${goal.daysRemaining}d left", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Saved", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(goal.currentAmount.formatted, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Target", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(goal.targetAmount.formatted, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = primary)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateGoalSheet(onDismiss: () -> Unit, onCreate: (String, String, Double, String) -> Unit) {
    var name   by remember { mutableStateOf("") }
    var icon   by remember { mutableStateOf("🎯") }
    var target by remember { mutableStateOf("") }
    var date   by remember { mutableStateOf("") }

    val iconOptions = listOf("🎯","💻","🚗","🏠","✈️","📱","🎸","🎓","🏋️","🌴")

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            Text("New Goal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Goal Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            Spacer(Modifier.height(12.dp))
            Text("Icon", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                iconOptions.forEach { ic ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (icon == ic) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier.size(40.dp),
                        onClick = { icon = ic }
                    ) { Box(contentAlignment = Alignment.Center) { Text(ic, fontSize = 20.sp) } }
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = target, onValueChange = { target = it },
                label = { Text("Target Amount (₹)") }, prefix = { Text("₹") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Target Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { onCreate(name, icon, target.toDoubleOrNull() ?: 0.0, date) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled  = name.isNotBlank() && (target.toDoubleOrNull() ?: 0.0) > 0,
                shape    = RoundedCornerShape(14.dp)
            ) { Text("Create Goal") }
            Spacer(Modifier.height(24.dp))
        }
    }
}
