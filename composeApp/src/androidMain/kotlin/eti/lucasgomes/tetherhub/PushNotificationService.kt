package eti.lucasgomes.tetherhub

import NotificationType
import PushNotificationManager
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.random.Random

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class PushNotificationService : FirebaseMessagingService(), KoinComponent {

    private val job = SupervisorJob()
    private lateinit var scope: CoroutineScope

    private val pushNotificationManager by inject<PushNotificationManager>()

    override fun onCreate() {
        super.onCreate()
        scope = CoroutineScope(job + Dispatchers.IO)
    }

    @SuppressLint("MissingPermission")
    override fun onMessageReceived(message: RemoteMessage) = with(message) {
        val notificationType = data["type"]?.let { NotificationType.valueOf(it) } ?: return
        scope.launch {
            pushNotificationManager.onPushNotificationWithData(notificationType, data)
        }

        val notificationBuilder = NotificationCompat.Builder(
            this@PushNotificationService,
            getChannelId(notificationType)
        ).setSmallIcon(R.drawable.ic_launcher_foreground)
            .apply {
                when (notificationType) {
                    NotificationType.CHAT -> {
                        setContentTitle(message.data["senderUsername"])
                        setContentText(message.data["content"])
                        setPriority(NotificationCompat.PRIORITY_HIGH)
                    }

                    NotificationType.FRIENDS -> {
                        setContentText(message.data["content"])
                        setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    }
                }
            }
            .setContentIntent(createPendingIntent())
            .setAutoCancel(true)

        createNotificationChannels()

        with(NotificationManagerCompat.from(this@PushNotificationService)) {
            notify(message.messageId?.hashCode() ?: Random.nextInt(), notificationBuilder.build())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun getChannelId(notificationType: NotificationType): String {
        return when (notificationType) {
            NotificationType.CHAT -> Channels.CHAT
            NotificationType.FRIENDS -> Channels.FRIENDS
        }
    }

    private fun RemoteMessage.createPendingIntent(): PendingIntent {
        val intent = Intent(this@PushNotificationService, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtras(toIntent())
        }
        return PendingIntent.getActivity(
            this@PushNotificationService,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createNotificationChannels() {
        createChatNotificationChannel()
        createFriendsNotificationChannel()
    }

    private fun createChatNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Chat"
            val descriptionText = "Chat notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Channels.CHAT, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createFriendsNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Friends"
            val descriptionText = "Friends requests notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Channels.FRIENDS, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    object Channels {
        const val CHAT = "chat"
        const val FRIENDS = "friends"
    }
}