package com.android.lenx.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.lenx.MessageBus
import com.android.lenx.R
import com.android.lenx.components.HomeComponents
import com.android.lenx.ui.theme.BgColor
import com.android.lenx.ui.theme.GreenConnected
import com.android.lenx.ui.theme.PurpleAccent
import com.android.lenx.ui.theme.RedDisconnected

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(routeSettings: () -> Unit, exposePort: (String) -> Unit) {
    var ipAddress by remember { mutableStateOf("192.168.1.5") }
    var isConnected by remember { mutableStateOf(false) };
    var isLocked by remember { mutableStateOf(false) };
    val homeComponents: HomeComponents = HomeComponents();
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(R.drawable.notification_icon), contentDescription = "Logo", tint = PurpleAccent, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "LenX", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }, actions = {
            IconButton(onClick = routeSettings) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFFCAC4D0))
            }
        }, colors = TopAppBarDefaults.topAppBarColors(containerColor = BgColor))
    }, containerColor = BgColor) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(horizontal = 24.dp).fillMaxSize()) {
            Spacer(modifier = Modifier.height(16.dp))
                homeComponents.ConnectionPanel(
                ipAddress = ipAddress, onIpChange = { ipAddress = it },
                isConnected = isConnected,
                onConnectionToggle = {
                    if (!isConnected) {
                        exposePort(ipAddress)
                    }
                    isConnected = !isConnected
                }
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "LIVE PC STATUS", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = PurpleAccent, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 16.dp))
            Column(modifier = Modifier.alpha(if (isConnected) 1f else 0.5f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                homeComponents.StatusCard(
                    title = "Power & Battery",
                    subtitle = "Plugged in • 100% charged",
                    icon = Icons.Default.BatteryChargingFull,
                    iconTint = PurpleAccent,
                    iconBg = PurpleAccent.copy(alpha = 0.1f),
                )
                homeComponents.MemoryStatusCard(42f)
                homeComponents.StatusCard(
                    title = "Session Status",
                    subtitle = "Window is ${if (isLocked) "Locked" else "Active"}",
                    icon = if (isLocked) Icons.Default.Lock else Icons.Default.Lock,
                    iconTint = if (isLocked) RedDisconnected else GreenConnected,
                    iconBg = if (isLocked) RedDisconnected.copy(alpha = 0.1f) else GreenConnected.copy(alpha = 0.1f),
                    modifier = Modifier.clickable(enabled = isConnected) {isLocked = !isLocked}
                )
            }
            /*Text(text = "LenX", style = MaterialTheme.typography.labelLarge)
            TextField(
                value = ipAddress,
                onValueChange = { ipAddress = it },
                label = { Text("IP Address") },
                keyboardOptions = KeyboardOptions(
                    autoCorrectEnabled = true,
                    showKeyboardOnFocus = true,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    if (ipAddress.contains("192")) MessageBus.send(ipAddress)
                })
            )
            Button(onClick = { exposePort(ipAddress) }) {
                Text(text = "Open Port")
            }
            Button(onClick = routeSettings) { Text("Settings") }*/
        }
    }
}
