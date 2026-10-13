package com.android.lenx.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.android.lenx.MessageBus

@Composable
fun HomeScreen(routeSettings: () -> Unit, exposePort: (String) -> Unit) {
    var ipAddress by remember { mutableStateOf("192.168.1.5") }
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text(text = "LenX", style = MaterialTheme.typography.labelLarge)
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
            Button(onClick = routeSettings) { Text("Settings") }
        }
    }
}
