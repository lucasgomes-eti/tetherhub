package eti.lucasgomes.tetherhub

import android.app.Application
import initKoin
import org.koin.android.ext.koin.androidContext

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@App)
        }
    }
}