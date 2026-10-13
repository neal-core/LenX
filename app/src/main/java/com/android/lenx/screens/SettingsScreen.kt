package com.android.lenx.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.lenx.components.SettingsToggle

@Composable
fun SettingsScreen(connected: Boolean) {
    val isConnected by remember { mutableStateOf(connected) }
    var powerAlerts by remember { mutableStateOf(false) };
    var winSession by remember { mutableStateOf(false) };
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(text = "LenX", style =  MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(bottom = 16.dp, top = 24.dp))
            Surface(color = if (isConnected) Color(0x4181D287) else MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(16.dp), modifier = Modifier.padding(bottom = 24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp, top = 10.dp, end = 14.dp)) {
                    Icon(imageVector = if (isConnected) Icons.Default.CheckCircle else Icons.Default.Cancel, contentDescription = "Status", tint = if (isConnected) Color(0xFF81C784) else MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(8.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isConnected) "Connected to server" else "Disconnected", style = MaterialTheme.typography.labelLarge, color = if (isConnected) Color(0xFF81C784) else MaterialTheme.colorScheme.onErrorContainer)
                }
            }
            SettingsToggle(icon = Icons.Default.BatteryAlert, title = "Power & Security", description = "Notify when unplugged or low battery", checked = powerAlerts, onCheckChange = {
                powerAlerts = it
            })
            Spacer(modifier = Modifier.height(8.dp))
            SettingsToggle(icon = Icons.Default.Lock, title = "Windows Session", description = "Notify on PC lock r unlock", checked = winSession, onCheckChange = {
                winSession = it
            })
        }
    }
}

@Preview
@Composable
fun SettingsPreview() {
    SettingsScreen(connected = false)
}