package com.android.lenx

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ServiceCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.time.Duration.Companion.milliseconds

class TcpService : Service() {
    private var socket: Socket? = null
    private var ipAddress: String = ""
    private var serviceScope: CoroutineScope? = null
    private var mediaPlayer: MediaPlayer? = null
    private var lowBatteryStatus: Boolean = false
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = Notification.Builder(this, "tcp_channel")
            .setContentTitle("PC Connection")
            .setContentText("Listening...")
            .setSmallIcon(R.drawable.notification_icon)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceCompat.startForeground(this, 1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(1, notification)
        }
        silentMP()
        /*val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LenX::TcpServiceWakeLock")
        wakeLock?.acquire(10*60*1000L)*/
        /*serviceScope?.launch {
            delay(20000.milliseconds)
            if (socket == null || socket?.isConnected != true) {
                showNotif("Connection Lost", "Failed to connect to other device. Connection timed out"
                delay(3500.milliseconds)
                MessageBus.send("disconnected")
                stopSelf()
            }

        }*/
        intent?.getStringExtra("IP_ADDRESS")?.let {
            if (it.isNotEmpty()) {
                ipAddress = it
            }
        }
        serviceScope?.launch() {
            delay(5000.milliseconds)
            if (socket?.isClosed == true || socket == null) {
                showNotif("Connection Timed Out", "Failed to connect to other device.")
                delay(2500.milliseconds)
                MessageBus.send("disconnected")
                stopSelf()
            }
        }
        if (socket == null || socket?.isClosed == true) {
            connectToPC()
        }
//        return super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        if (lowBatteryStatus) lowBatteryStatus = false
    }

    override fun onDestroy() {
        super.onDestroy()
        lowBatteryStatus = false
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        serviceScope?.cancel()
        try {
            socket?.close()
            socket = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        println("Closed socket")
        MessageBus.send("disconnected")
        showNotif("Disconnected", "Connection timed out or lost")
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun connectToPC() {
        serviceScope?.launch(Dispatchers.IO) {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO)
            try {
                println("IP:: $ipAddress")
                if (ipAddress.isEmpty()) {
                    return@launch
                }
                val currentSock = Socket().apply {
                    keepAlive = true
                    tcpNoDelay = true
                    connect(InetSocketAddress(ipAddress, 8000), 5000)
                }
                socket = currentSock
                currentSock.soTimeout = 10000
                if (currentSock.isConnected) {
                    MessageBus.send("connected")
                    showNotif("Connected", "Linked to $ipAddress")
                } else {
                    MessageBus.send("disconnected")
                    return@launch
                }
                val input = currentSock.getInputStream()
                val output = currentSock.getOutputStream()
                val buffer = ByteArray(1024)
                output.write("handshake".toByteArray())
                output.flush()

                while (true) {

                    val bytesRead = input.read(buffer)
                    if (bytesRead == -1) {
                        MessageBus.send("disconnected")
                        println("termination call: $bytesRead bytes")
                        showNotif("Disconnected", "Connection lost")
                        break
                    }
                    val message = String(buffer, 0, bytesRead)
//                    showNotif("Message", "pc message: $message")
                    if (message == "1") continue
                    if (message.contains("lenx", ignoreCase = true)) {
                        showNotif("Status Update", "Your PC is 100%")
                    } else if (message.contains("RAM")) {
                        showNotif("System Alert", "PC RAM usage is too high: $message")
                    } else if (message.contains("Battery Status")) {
                        val batteryPercent: Int = message.split(":")[1].filter { it.isDigit() }.toInt()
                        if (batteryPercent > 100) continue
                        if (batteryPercent == 100) {
                            showNotif("System Alert", "PC Battery is fully charged. Please disconnect the charger")
                        } else if (batteryPercent <= 30 && !lowBatteryStatus) {
                            showNotif("System Alert", "Your PC battery is running low. Please connect the charger")
                            lowBatteryStatus = true
                        }
                        MessageBus.send(message)
                    }
//                    output.write("1\n".toByteArray())
                }
            } catch (_: java.net.SocketTimeoutException) {
                Log.e("TcpService","TCP Timeout")
                MessageBus.send("disconnected")
                showNotif("Disconnected", "PC timed out")
            } catch (e: Exception) {
                Log.e("TcpService", "Error from ${e.javaClass.name} - ${e.message}")
                MessageBus.send("disconnected")
            } catch (e: java.net.SocketException) {
                Log.e("TcpService", "Killed by OS: ${e.message}")
                MessageBus.send("disconnected")
            } finally {
                try {
                    socket?.close()
                    socket = null
                } catch (_: Exception) {}
            }
        }
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
        manager.notify(2, notification)
    }
    private fun silentMP() {
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.calma)?.apply {
                isLooping = true
                setVolume(0.4f, 0.4f)
                start()
            }
        } catch (e: Exception) {
            Log.d("Audioplayer", "Error: ${e.message}")
        }
    }
}
