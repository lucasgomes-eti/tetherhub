package eti.lucasgomes.tetherhub

import android.app.Application
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import initKoin
import org.koin.android.ext.koin.androidContext

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@App)
            NotifierManager.initialize(
                configuration = NotificationPlatformConfiguration.Android(
                    notificationIconResId = R.drawable.ic_launcher_foreground,
                    showPushNotification = true
                )
            )
        }
    }
}