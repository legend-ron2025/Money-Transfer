package com.moneytracker.feature.subscriptions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(navController: NavController, viewModel: SubscriptionsViewModel = hiltViewModel()) {
    val state by viewModel.state.observeAsState(SubscriptionsViewModel.SubState.Loading)
    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscriptions", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { pad ->
        when (val s = state) {
            is SubscriptionsViewModel.SubState.Loading ->
                Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) { CircularProgressIndicator() }

            is SubscriptionsViewModel.SubState.Error ->
                Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) { Text(s.message, color = MaterialTheme.colorScheme.error) }

            is SubscriptionsViewModel.SubState.Success ->
                LazyColumn(Modifier.padding(pad).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item { Spacer(Modifier.height(4.dp)) }

                    // Annual total banner
                    item {
                        Card(
                            Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
                            shape  = RoundedCornerShape(16.dp)
                        ) {
                            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("Annual Subscriptions", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                                    Text("₹${String.format("%,.0f", s.totalAnnual)}/year", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Monthly", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                                    Text("₹${String.format("%,.0f", s.totalMonthly)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                        }
                    }

                    if (s.subscriptions.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("📺", fontSize = 48.sp)
                                    Spacer(Modifier.height(12.dp))
                                    Text("No subscriptions detected", style = MaterialTheme.typography.titleMedium)
                                    Text("They appear once recurring payments are detected.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    } else {
                        items(s.subscriptions, key = { it.merchant }) { sub ->
                            SubscriptionCard(sub)
                        }
                    }
                    item { Spacer(Modifier.height(88.dp)) }
                }
        }
    }
}

@Composable
private fun SubscriptionCard(sub: SubscriptionsViewModel.Subscription) {
    val daysUntil = try {
        val next = java.time.LocalDate.parse(sub.nextPaymentDate.take(10))
        java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), next).toInt()
    } catch (_: Exception) { 99 }

    val urgencyColor = when {
        daysUntil <= 3  -> MaterialTheme.colorScheme.error
        daysUntil <= 7  -> Color(0xFFFFA726)
        else            -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        shape  = RoundedCornerShape(14.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            // Icon
            Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.size(44.dp)) {
                Box(contentAlignment = Alignment.Center) { Text("📺", fontSize = 22.sp) }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(sub.merchant, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    if (daysUntil <= 0) "Due today!" else if (daysUntil == 1) "Due tomorrow" else "Due in ${daysUntil}d",
                    style = MaterialTheme.typography.bodySmall,
                    color = urgencyColor
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("₹${String.format("%,.0f", sub.amount)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text("₹${String.format("%,.0f", sub.amount * 12)}/yr", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
