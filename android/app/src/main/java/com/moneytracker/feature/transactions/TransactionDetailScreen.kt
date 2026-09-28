package com.moneytracker.feature.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moneytracker.domain.model.Transaction
import com.moneytracker.feature.transactions.TransactionDetailViewModel.TransactionDetailState
import com.moneytracker.ui.theme.MoneyTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@Composable
fun TransactionDetailScreen(
    transactionId: String,
    navController: androidx.navigation.NavController,
    viewModel: TransactionDetailViewModel = viewModel()
) {
    val state by viewModel.state.observeAsState(TransactionDetailViewModel.TransactionDetailState.Loading)
    
    androidx.lifecycle.lifecycleScope.launch {
        viewModel.loadTransaction(transactionId)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        TopAppBar(
            title = { Text("Transaction Details") },
            navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack, contentDescription = "Back", tint = MoneyTrackerTheme.colorScheme.onSurface) } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MoneyTrackerTheme.colorScheme.surface)
        )
        
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            when (state) {
                is TransactionDetailState.Loading -> LoadingDetail()
                is TransactionDetailState.Error -> ErrorDetail(state.message, onRetry = { viewModel.loadTransaction(transactionId) })
                is TransactionDetailState.Success -> SuccessDetail(state.transaction, navController)
            }
        }
    }
}

@Composable
fun LoadingDetail() {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(5) {
            Card(modifier = Modifier.fillMaxWidth().height(80.dp), colors = CardDefaults.cardColors(containerColor = MoneyTrackerTheme.colorScheme.surfaceContainerHighest)) {
                Box(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun ErrorDetail(message: String, onRetry: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = "Error loading transaction", fontSize = 20.sp, color = MoneyTrackerTheme.colorScheme.error)
        Text(text = message, fontSize = 14.sp, color = MoneyTrackerTheme.colorScheme.onSurfaceVariant)
        androidx.compose.material3.Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
fun SuccessDetail(transaction: Transaction, navController: androidx.navigation.NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { TransactionHeaderCard(transaction) }
        item { TransactionDetailsCard(transaction) }
        item { TransactionActionsCard(transaction, navController) }
    }
}

@Composable
fun TransactionHeaderCard(transaction: Transaction) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MoneyTrackerTheme.colorScheme.surfaceContainer)) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(14.dp)).background(MoneyTrackerTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = transaction.category?.icon ?: "💰", fontSize = 28.sp)
                    }
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = transaction.merchantName ?: transaction.description, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(text = transaction.category?.name ?? "Uncategorized", fontSize = 14.sp, color = MoneyTrackerTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Text(
                    text = transaction.displayAmount.formattedWithSign,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.isIncome) MoneyTrackerTheme.colorScheme.tertiary else MoneyTrackerTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun TransactionDetailsCard(transaction: Transaction) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MoneyTrackerTheme.colorScheme.surfaceContainer)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Details", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
            
            DetailRow("Date", formatDateTime(transaction.transactionDate))
            DetailRow("Account", transaction.account?.let { "${it.institutionName} ${it.accountType.displayName} •••• ${it.maskedNumber}" } ?: "Unknown")
            DetailRow("Type", transaction.type.name)
            DetailRow("Status", transaction.status.name)
            DetailRow("Source", transaction.source.name)
            
            transaction.paymentChannel?.let { 
                DetailRow("Payment Channel", it.name) 
            }
            
            transaction.upiReference?.let { 
                DetailRow("UPI Reference", it) 
            }
            
            transaction.bankReference?.let { 
                DetailRow("Bank Reference", it) 
            }
            
            transaction.subcategory?.let { 
                DetailRow("Subcategory", it) 
            }
            
            transaction.notes?.let { 
                DetailRow("Notes", it) 
            }
            
            if (transaction.tags.isNotEmpty()) {
                DetailRow("Tags", transaction.tags.joinToString(", "))
            }
            
            transaction.transferPairId?.let { 
                DetailRow("Transfer Pair ID", it) 
            }
            
            DetailRow("Confidence", "${(transaction.confidence * 100).toInt()}%")
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp, color = MoneyTrackerTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 2, overflow = androidx.compose.ui.text.TextOverflow.Ellipsis)
    }
}

@Composable
fun TransactionActionsCard(transaction: Transaction, navController: androidx.navigation.NavController) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MoneyTrackerTheme.colorScheme.surfaceContainer)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Actions", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
            
            androidx.compose.material3.Button(
                onClick = { /* Edit category */ },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MoneyTrackerTheme.colorScheme.primaryContainer)
            ) {
                Text(text = "Edit Category")
            }
            
            androidx.compose.material3.Button(
                onClick = { /* Add note */ },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MoneyTrackerTheme.colorScheme.primaryContainer)
            ) {
                Text(text = "Add Note")
            }
            
            if (!transaction.isTransfer) {
                androidx.compose.material3.Button(
                    onClick = { /* Split transaction */ },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MoneyTrackerTheme.colorScheme.primaryContainer)
                ) {
                    Text(text = "Split Transaction")
                }
            }
        }
    }
}

private fun formatDateTime(isoString: String): String {
    try {
        val instant = java.time.Instant.parse(isoString)
        val zoned = instant.atZone(java.time.ZoneId.systemDefault())
        return java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a").format(zoned)
    } catch (e: Exception) {
        return isoString
    }
}

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TopAppBarDefaults