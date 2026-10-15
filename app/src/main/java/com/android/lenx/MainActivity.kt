package com.android.lenx

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.android.lenx.services.AppNavigator
import com.android.lenx.ui.theme.LenXTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LenXTheme {
                var receivedMsg by remember { mutableStateOf("Waiting For PC...") }
                var connected by remember { mutableStateOf(false) }
                var ipAddress by remember { mutableStateOf("192.168.1.5") }
                val context = LocalContext.current
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        val intent = Intent(context, TcpService::class.java).apply {
                            putExtra("IP_ADDRESS", ipAddress)
                        }
                        context.startForegroundService(intent)
                    }
                }
                LaunchedEffect(Unit) {
                    MessageBus.messages.collect { message ->
                        receivedMsg = message
                        connected = if (message == "connected") true else if (message == "disconnected") false else connected;
                        if (message.contains("192")) ipAddress = message
                    }
                }
                AppNavigator(
                    isConnected = connected,
                    disconnect = {
                        val intent = Intent(this, TcpService::class.java);
                        context.stopService(intent);
                    },
                    exposePort = {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            val intent = Intent(context, TcpService::class.java).apply {
                                putExtra("IP_ADDRESS", it)
                            }
                            context.startForegroundService(intent)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LenXTheme {
        Greeting("Android")
    }
}
