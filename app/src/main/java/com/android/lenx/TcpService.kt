package com.android.lenx

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.net.Socket

class TcpService : Service() {
    private var socket: Socket? = null
    private var ipAddress: String = ""

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
    }

    override fun onDestroy() {
        socket?.close()
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
                socket = Socket(ipAddress, 8000)
                if (socket!!.isConnected) {
                    MessageBus.send("connected")
                } else {
                    MessageBus.send("disconnected")
                }
                val input = socket!!.getInputStream()
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
                        showNotif("Your PC is 100%")
                        break
                    } else if (message.contains("RAM")) {
                        showNotif("PC RAM usage is too high: $message")
                        break
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

    private fun showNotif(message: String) {
        val notification = Notification.Builder(this, "tcp_channel")
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Message From Your PC")
            .setContentText(message)
            .setAutoCancel(true)
            .build()
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(1, notification)
    }
}
