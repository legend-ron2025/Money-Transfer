package com.moneytracker.feature.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.moneytracker.domain.model.Account
import java.time.Duration
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(navController: NavController, viewModel: AccountsViewModel = hiltViewModel()) {
    val state by viewModel.state.observeAsState(AccountsViewModel.AccountsState.Loading)
    LaunchedEffect(Unit) { viewModel.loadAccounts() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Accounts", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.syncAllAccounts() }) { Icon(Icons.Default.Sync, "Sync") }
                    IconButton(onClick = {}) { Icon(Icons.Default.Add, "Add Account") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { pad ->
        when (val s = state) {
            is AccountsViewModel.AccountsState.Loading ->
                Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            is AccountsViewModel.AccountsState.Error ->
                Column(Modifier.fillMaxSize().padding(pad).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text("Failed to load accounts", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.loadAccounts() }) { Text("Retry") }
                }
            is AccountsViewModel.AccountsState.Success ->
                LazyColumn(Modifier.padding(pad).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item { Spacer(Modifier.height(4.dp)) }
                    item { TotalBalanceBanner(s.totalBalance.formatted) }
                    items(s.accounts, key = { it.id }) { account ->
                        AccountCard(account)
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
        }
    }
}

@Composable
private fun TotalBalanceBanner(total: String) {
    Box(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary, Color(0xFF5C6BC0))))
            .padding(20.dp)
    ) {
        Column {
            Text("Net Worth", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(4.dp))
            Text(total, color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
            Text("across all accounts", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun AccountCard(account: Account) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Institution logo placeholder
                    Box(
                        Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) { Text("🏦", fontSize = 24.sp) }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(account.institution.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        Text("${account.accountType.displayName} •••• ${account.maskedNumber.takeLast(4)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(account.effectiveBalance.formatted, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    SyncBadge(account.syncStatus)
                }
            }
            account.lastSyncedAt?.let { ts ->
                Spacer(Modifier.height(8.dp))
                Text(
                    "Last synced: ${relativeTime(ts)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SyncBadge(status: Account.SyncStatus) {
    val (label, color) = when (status) {
        Account.SyncStatus.SYNCED       -> "Synced"      to Color(0xFF4CAF50)
        Account.SyncStatus.SYNCING      -> "Syncing…"    to MaterialTheme.colorScheme.primary
        Account.SyncStatus.ERROR        -> "Error"       to MaterialTheme.colorScheme.error
        Account.SyncStatus.PENDING      -> "Pending"     to MaterialTheme.colorScheme.tertiary
        Account.SyncStatus.DISCONNECTED -> "Disconnected"to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(label, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Medium)
    }
}

private fun relativeTime(isoString: String): String {
    return try {
        val d = Duration.between(Instant.parse(isoString), Instant.now())
        when {
            d.toMinutes() < 1  -> "just now"
            d.toHours()   < 1  -> "${d.toMinutes()}m ago"
            d.toDays()    < 1  -> "${d.toHours()}h ago"
            else               -> "${d.toDays()}d ago"
        }
    } catch (_: Exception) { "" }
}
