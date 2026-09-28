package com.moneytracker.feature.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

// ── Onboarding ────────────────────────────────────────────────────────────────

private data class OnboardPage(val emoji: String, val title: String, val body: String)

private val PAGES = listOf(
    OnboardPage("💰", "Know Your Money", "See all your accounts, balances and transactions in one place. Automatic sync keeps everything current."),
    OnboardPage("📊", "Smart Analytics",  "Understand where your money goes with intelligent categorisation, budgets and spending insights."),
    OnboardPage("🔒", "Bank-Grade Security", "Your data is encrypted end-to-end. Connect banks securely via India's Account Aggregator ecosystem."),
)

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    var page by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(
                listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.06f), MaterialTheme.colorScheme.background)
            ))
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(56.dp))

        // Page dots
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PAGES.indices.forEach { i ->
                Box(
                    Modifier
                        .height(4.dp)
                        .width(if (i == page) 32.dp else 12.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (i == page) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }
        Spacer(Modifier.height(48.dp))

        AnimatedContent(targetState = page, label = "page") { p ->
            val pg = PAGES[p]
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(pg.emoji, fontSize = 88.sp)
                Spacer(Modifier.height(32.dp))
                Text(pg.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(16.dp))
                Text(pg.body, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = { if (page < PAGES.lastIndex) page++ else onComplete() },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(if (page < PAGES.lastIndex) "Next" else "Get Started", style = MaterialTheme.typography.titleMedium)
        }

        TextButton(onClick = onComplete) {
            Text("Skip", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(24.dp))
    }
}

// ── Auth (Login / Register) ───────────────────────────────────────────────────

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.observeAsState(AuthViewModel.AuthState.Idle)
    var isLogin    by remember { mutableStateOf(true) }
    var email      by remember { mutableStateOf("") }
    var password   by remember { mutableStateOf("") }
    var name       by remember { mutableStateOf("") }
    var showPass   by remember { mutableStateOf(false) }
    val focus      = LocalFocusManager.current
    val snackHost  = remember { SnackbarHostState() }

    LaunchedEffect(authState) {
        when (val s = authState) {
            is AuthViewModel.AuthState.Success -> onAuthSuccess()
            is AuthViewModel.AuthState.Error   -> snackHost.showSnackbar(s.message)
            else -> Unit
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackHost) }) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(52.dp))

            // Logo
            Box(
                Modifier.size(80.dp).clip(RoundedCornerShape(22.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) { Text("💰", fontSize = 40.sp) }

            Spacer(Modifier.height(20.dp))
            Text("MoneyTracker", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text(
                if (isLogin) "Sign in to continue" else "Create your account",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(36.dp))

            // Tab toggle
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh).padding(4.dp)
            ) {
                listOf("Sign In" to true, "Sign Up" to false).forEach { (lbl, loginTab) ->
                    Button(
                        onClick = { isLogin = loginTab; viewModel.reset() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLogin == loginTab) MaterialTheme.colorScheme.primary else Color.Transparent,
                            contentColor   = if (isLogin == loginTab) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp)
                    ) { Text(lbl) }
                }
            }
            Spacer(Modifier.height(24.dp))

            // Name field (register only)
            AnimatedVisibility(!isLogin, enter = fadeIn(), exit = fadeOut()) {
                Column {
                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(14.dp)
                    )
                    Spacer(Modifier.height(14.dp))
                }
            }

            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focus.moveFocus(FocusDirection.Down) }),
                shape = RoundedCornerShape(14.dp)
            )
            Spacer(Modifier.height(14.dp))
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                    }
                },
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focus.clearFocus()
                    if (isLogin) viewModel.login(email, password)
                    else viewModel.register(name, email, password)
                }),
                shape = RoundedCornerShape(14.dp)
            )
            Spacer(Modifier.height(24.dp))

            val loading = authState is AuthViewModel.AuthState.Loading
            Button(
                onClick = {
                    if (isLogin) viewModel.login(email, password)
                    else viewModel.register(name, email, password)
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !loading && email.isNotBlank() && password.length >= 8 && (isLogin || name.isNotBlank())
            ) {
                if (loading) CircularProgressIndicator(Modifier.size(22.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                else Text(if (isLogin) "Sign In" else "Create Account", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
