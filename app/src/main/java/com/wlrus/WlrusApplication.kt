package com.wlrus

import android.app.Application
import android.content.res.Configuration
import com.tencent.mmkv.MMKV

class WlrusApplication : Application() {

    companion object {
        lateinit var application: WlrusApplication
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        MMKV.initialize(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}
