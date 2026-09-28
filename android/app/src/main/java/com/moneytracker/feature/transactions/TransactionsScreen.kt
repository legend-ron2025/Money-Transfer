package com.moneytracker.feature.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.moneytracker.domain.model.Transaction
import com.moneytracker.domain.repository.TransactionFilter
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay

private val FILTER_TABS = listOf("All", "Income", "Expense", "Transfer")

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun TransactionsScreen(navController: NavController, viewModel: TransactionsViewModel = hiltViewModel()) {
    val state by viewModel.state.observeAsState(TransactionsViewModel.TransactionsState.Loading)
    var selectedFilter by remember { mutableIntStateOf(0) }
    var searchQuery    by remember { mutableStateOf("") }
    var searchActive   by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) { viewModel.loadTransactions() }

    // Debounced search
    LaunchedEffect(searchQuery) {
        if (searchQuery.isBlank()) {
            viewModel.loadTransactions()
        } else {
            delay(300)
            viewModel.searchTransactions(searchQuery)
        }
    }

    // Filter by type
    LaunchedEffect(selectedFilter) {
        val type = when (selectedFilter) {
            1 -> "CREDIT"; 2 -> "DEBIT"; 3 -> "TRANSFER"; else -> null
        }
        viewModel.loadTransactions(TransactionFilter(type = type?.let { com.moneytracker.domain.model.TransactionType.valueOf(it) }))
    }

    Scaffold(
        topBar = {
            if (searchActive) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = {},
                    active = true,
                    onActiveChange = { searchActive = it; if (!it) searchQuery = "" },
                    placeholder = { Text("Search transactions…") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty())
                            IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Clear, null) }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {}
            } else {
                TopAppBar(
                    title = { Text("Transactions", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = { searchActive = true }) { Icon(Icons.Default.Search, "Search") }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            }
        }
    ) { pad ->
        Column(Modifier.padding(pad)) {
            // Filter chips
            ScrollableTabRow(
                selectedTabIndex = selectedFilter,
                containerColor   = MaterialTheme.colorScheme.background,
                contentColor     = MaterialTheme.colorScheme.primary,
                edgePadding      = 16.dp,
                divider          = {}
            ) {
                FILTER_TABS.forEachIndexed { i, label ->
                    Tab(
                        selected = selectedFilter == i,
                        onClick  = { selectedFilter = i },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (selectedFilter == i) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(
                                label,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = if (selectedFilter == i) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            when (val s = state) {
                is TransactionsViewModel.TransactionsState.Loading ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }

                is TransactionsViewModel.TransactionsState.Error ->
                    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(s.message, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { viewModel.loadTransactions() }) { Text("Retry") }
                    }

                is TransactionsViewModel.TransactionsState.Success ->
                    if (s.transactions.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🧾", fontSize = 48.sp)
                                Spacer(Modifier.height(12.dp))
                                Text("No transactions", style = MaterialTheme.typography.titleMedium)
                                Text("They'll appear here once synced.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        TransactionsList(s.transactions, listState, navController) {
                            if (s.hasMore) viewModel.loadTransactions(s.filter, s.currentPage + 1, append = true)
                        }
                    }
            }
        }
    }
}

@Composable
private fun TransactionsList(
    txns: List<Transaction>,
    listState: LazyListState,
    nav: NavController,
    onLoadMore: () -> Unit,
) {
    // Group by date
    val grouped = txns.groupBy { it.transactionDate.take(10) }

    LazyColumn(state = listState, contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
        grouped.forEach { (date, dateTxns) ->
            item(key = "header_$date") {
                Text(
                    formatDateHeader(date),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(dateTxns, key = { it.id }) { txn ->
                TransactionListItem(txn) { nav.navigate("transaction_detail/${txn.id}") }
                Spacer(Modifier.height(6.dp))
            }
        }
        item { Spacer(Modifier.height(88.dp)) }
    }
}

@Composable
private fun TransactionListItem(txn: Transaction, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors   = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        shape    = RoundedCornerShape(14.dp)
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) { Text(txn.category?.icon ?: if (txn.isIncome) "💰" else "💸", fontSize = 22.sp) }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    txn.merchantName ?: txn.description.take(40),
                    style    = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Text(
                    txn.category?.name ?: "Uncategorized",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                txn.displayAmount.formattedWithSign,
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color      = if (txn.isIncome) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
            )
        }
    }
}

private fun formatDateHeader(iso: String): String {
    return try {
        val local = java.time.LocalDate.parse(iso)
        val today = java.time.LocalDate.now()
        when {
            local == today             -> "Today"
            local == today.minusDays(1)-> "Yesterday"
            else -> java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM d").format(local)
        }
    } catch (_: Exception) { iso }
}
