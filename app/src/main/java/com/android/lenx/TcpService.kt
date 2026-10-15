package com.android.lenx

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.Socket
import kotlin.time.Duration.Companion.milliseconds

class TcpService : Service() {
    private var socket: Socket? = null
    private var ipAddress: String = ""
    private var serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getStringExtra("IP_ADDRESS")?.let {
            if (it.isNotEmpty()) {
                ipAddress = it
            }
        }
        connectToPC()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = Notification.Builder(this, "tcp_channel")
            .setContentTitle("PC Connection")
            .setContentText("Waiting...")
            .setSmallIcon(R.drawable.notification_icon)
            .build()
        startForeground(1, notification)
        serviceScope.launch {
            delay(5000.milliseconds);
            if (socket == null || socket?.isConnected != true) {
                showNotif("Connection Lost", "Failed to connect to other device. Connection timed out");
                delay(3500.milliseconds);
                MessageBus.send("disconnected");
                stopSelf();
            }

        }

    }

    override fun onDestroy() {
        serviceScope.cancel();
        try {
            socket?.close();
            socket = null;
        } catch (e: Exception) {
            e.printStackTrace();
        }
        MessageBus.send("disconnected");
        showNotif("Disconnected", "Connection timed out or lost");
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun connectToPC() {
        Thread {
            try {
                println("IP:: $ipAddress")
                if (ipAddress.isEmpty()) {
                    return@Thread
                }
                val currentSock = Socket(ipAddress, 8000);
                socket = currentSock
                if (currentSock.isConnected) {
                    MessageBus.send("connected");
                    showNotif("Connected", "Linked to $ipAddress");
                } else {
                    MessageBus.send("disconnected");
                    return@Thread;
                }
                val input = currentSock.getInputStream()
                val buffer = ByteArray(1024)
                while (true) {
                    val bytesRead = input.read(buffer)
                    if (bytesRead == -1) {
                        break
                    }
                    val message = String(buffer, 0, bytesRead)
                    println("PC sent $message")
                    MessageBus.send(message)
                    if (message.contains("lenx", ignoreCase = true)) {
                        showNotif("Status Update", "Your PC is 100%")
                    } else if (message.contains("RAM")) {
                        showNotif("System Alert", "PC RAM usage is too high: $message")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "tcp_channel",
            "PC Connection",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun showNotif(title: String, message: String) {
        val notification = Notification.Builder(this, "tcp_channel")
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .build()
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(1, notification)
    }
}
