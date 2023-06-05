package com.icesoft.msdb.android.activity

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import com.icesoft.msdb.android.service.MSDBService

class LoginActivity : ComponentActivity() {
    private lateinit var msdbService: MSDBService

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as MSDBService.LocalBinder
            msdbService = binder.getService()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
        }
    }

    override fun onStart() {
        super.onStart()
    }
}