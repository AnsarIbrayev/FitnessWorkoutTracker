package kz.aitu.fitnessworkouttracker

import android.app.Application
import android.util.Log
import kz.aitu.fitnessworkouttracker.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FWTApp : Application() {

    override fun onCreate() {
        super.onCreate()

        try {
            startKoin {
                androidContext(this@FWTApp)
                modules(appModule)
            }
        } catch (e: Throwable) {
            // Чтобы приложение НЕ падало на дедлайне
            Log.e("FWTApp", "Koin init failed: ${e.message}", e)
        }
    }
}
