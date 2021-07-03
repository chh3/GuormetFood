package com.example.gourmetfood

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.gourmetfood.logic.data.City
import com.example.gourmetfood.util.CityUtil
import kotlin.concurrent.thread

class InitService : Service() {
    private val mBinder = InitBinder()

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        thread {
            CityUtil.initCity(FoodApplication.context)
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    class InitBinder: Binder() {
        fun setInit() {
            CityUtil.initCity(FoodApplication.context)
        }
    }
}