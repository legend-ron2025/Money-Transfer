package com.moneytracker.feature.settings

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel = hiltViewModel()) {
    val prefs by viewModel.prefs.observeAsState(SettingsViewModel.Prefs())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { pad ->
        LazyColumn(Modifier.padding(pad).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item { Spacer(Modifier.height(4.dp)) }

            // Security
            item {
                SettingsSection("Security", Icons.Default.Security) {
                    SettingsToggle("App Lock", "Require authentication to open", prefs.appLockEnabled) {
                        viewModel.setAppLock(it)
                    }
                    SettingsToggle("Biometric Unlock", "Fingerprint or face recognition", prefs.biometricEnabled) {
                        viewModel.setBiometric(it)
                    }
                }
            }

            // Connected accounts (Privacy Center)
            item {
                SettingsSection("Privacy & Accounts", Icons.Default.AccountBalance) {
                    SettingsRow("Connected Accounts", "Manage bank connections", Icons.Default.AccountTree) {
                        navController.navigate("accounts")
                    }
                    SettingsRow("Active Consents", "View and revoke consents", Icons.Default.VerifiedUser) {}
                    SettingsRow("Export My Data", "Download CSV / PDF", Icons.Default.Download) {}
                    SettingsRow("Delete Account", "Permanently remove your data", Icons.Default.DeleteForever, textColor = MaterialTheme.colorScheme.error) {}
                }
            }

            // Data & Sync
            item {
                SettingsSection("Data & Sync", Icons.Default.Sync) {
                    SettingsToggle("Auto Sync", "Sync accounts in background", prefs.autoSync) { viewModel.setAutoSync(it) }
                    SettingsRow("Sync Now", "Trigger manual sync", Icons.Default.CloudSync) {}
                }
            }

            // Appearance
            item {
                SettingsSection("Appearance", Icons.Default.Palette) {
                    SettingsRow("Theme", prefs.theme, Icons.Default.LightMode) {}
                    SettingsRow("Currency", "Indian Rupee (₹)", Icons.Default.CurrencyRupee) {}
                }
            }

            // Notifications
            item {
                SettingsSection("Notifications", Icons.Default.Notifications) {
                    SettingsToggle("Transaction Alerts", "Notify for new transactions", prefs.txnAlerts)  { viewModel.setTxnAlerts(it) }
                    SettingsToggle("Budget Alerts",  "Alert near budget limits",    prefs.budgetAlerts) { viewModel.setBudgetAlerts(it) }
                    SettingsToggle("Subscription Reminders", "Upcoming payments",  prefs.subAlerts)    { viewModel.setSubAlerts(it) }
                }
            }

            // About
            item {
                SettingsSection("About", Icons.Default.Info) {
                    SettingsRow("Version", "1.0.0", Icons.Default.NewReleases) {}
                    SettingsRow("Privacy Policy", "", Icons.Default.Policy) {}
                    SettingsRow("Terms of Service", "", Icons.Default.Gavel) {}
                }
            }

            item { Spacer(Modifier.height(88.dp)) }
        }
    }
}

@Composable
private fun SettingsSection(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        shape  = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SettingsToggle(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            if (subtitle.isNotBlank())
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun SettingsRow(title: String, subtitle: String, icon: ImageVector, textColor: Color = MaterialTheme.colorScheme.onSurface, onClick: () -> Unit = {}) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(icon, null, Modifier.size(18.dp), tint = textColor.copy(alpha = 0.7f))
            Spacer(Modifier.width(10.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyMedium, color = textColor)
                if (subtitle.isNotBlank())
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(Icons.Default.ChevronRight, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ── WebView for AA consent URL ────────────────────────────────────────────────
@Composable
fun ConsentWebView(url: String, onFinished: (success: Boolean) -> Unit) {
    AndroidView(factory = { ctx ->
        WebView(ctx).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, urlStr: String): Boolean {
                    // Intercept deep-link callback
                    if (urlStr.startsWith("moneytracker://consent")) {
                        val success = urlStr.contains("status=approved") || urlStr.contains("status=active")
                        onFinished(success)
                        return true
                    }
                    return false
                }
            }
            loadUrl(url)
        }
    }, modifier = Modifier.fillMaxSize())
}
