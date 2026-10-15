package com.android.lenx.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.android.lenx.ui.theme.BgColor
import com.android.lenx.ui.theme.CardColor
import com.android.lenx.ui.theme.GreenConnected
import com.android.lenx.ui.theme.PurpleAccent
import com.android.lenx.ui.theme.PurpleDark
import com.android.lenx.ui.theme.RedDisconnected

class HomeComponents {
    @Composable
    fun MemoryStatusCard(memUsage: Float) {
        val animatedProgress by animateFloatAsState(targetValue = memUsage / 100f);
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardColor), modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(PurpleAccent.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                    Icon(imageVector = Icons.Default.Memory, contentDescription = "Memory", tint = PurpleAccent, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Memory Usage", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White)
                        Text(text = "${memUsage.toInt()}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = PurpleAccent)

                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(100.dp)),
                    color = PurpleAccent,
                    trackColor = BgColor,
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                    )
                }
            }
        }
    }

    @Composable
    fun ConnectionPanel(ipAddress: String, onIpChange: (String) -> Unit, isConnected: Boolean, onConnectionToggle: () -> Unit, enabled: Boolean) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardColor), border = CardDefaults.outlinedCardBorder(true)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Server Connection", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFCAC4D0), fontWeight = FontWeight.Medium);
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(if (isConnected) GreenConnected else RedDisconnected))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isConnected) "Connected" else "Disconnected", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = if (isConnected) GreenConnected else RedDisconnected)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = ipAddress, onValueChange = onIpChange, enabled = enabled, placeholder = {Text(text = "PC IP Address", color = Color.Gray)}, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PurpleAccent, unfocusedBorderColor = Color.White.copy(alpha = 0.1f), focusedContainerColor = BgColor, unfocusedContainerColor = BgColor, focusedTextColor = Color.White, unfocusedTextColor = Color.White), modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(12.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onConnectionToggle, colors = ButtonDefaults.buttonColors(containerColor = if (isConnected) Color(0xFF4A4458) else PurpleAccent, contentColor = if (isConnected) PurpleAccent else PurpleDark), modifier = Modifier.height(50.dp), shape = RoundedCornerShape(12.dp)) {
                        Text(text = if (isConnected) "Disconnect" else "Connect", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    @Composable
    fun StatusCard(title: String, subtitle: String, icon: ImageVector, iconTint: Color, iconBg: Color, modifier: Modifier = Modifier) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardColor), modifier = modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(iconBg), contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(24.dp));
                }
                Spacer(modifier = Modifier.width(16.dp));
                Column {
                    Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color.White);
                    Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFFCAC4D0), modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}