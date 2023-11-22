package com.icesoft.msdb.android.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.icesoft.msdb.android.R
import com.icesoft.msdb.android.databinding.ActivitySupportBinding
import com.icesoft.msdb.android.service.MSDBService

class SupportActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupportBinding

    private lateinit var msdbService: MSDBService
    private var serviceConnected = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MSDBService.LocalBinder
            msdbService = binder.getService()
            serviceConnected = true
//            if (msdbService.isBillingAvailable() && !msdbService.isSubscribed()) {
//                binding.subscribeButton.text = getString(R.string.support_subscribe)
//
//            } else if (msdbService.isSubscribed()) {
//                binding.subscribeButton.isEnabled = false
//                binding.subscribeButton.text = getString(R.string.support_subscribed)
//            } else {
//                binding.subscribeButton.isEnabled = false
//                binding.subscribeButton.text = getString(R.string.support_cannot_subscribe)
//            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            serviceConnected = false
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, MSDBService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.support_toolbar))
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.supportTextView.text = getString(R.string.support_title)

        binding.subscribeButton.setOnClickListener {
            // msdbService.initBillingFlow(this)
        }
    }

    override fun onResume() {
        super.onResume()

        if (serviceConnected) {
//            if (msdbService.isSubscribed()) {
//                binding.subscribeButton.isEnabled = false
//                binding.subscribeButton.text = getString(R.string.support_subscribed)
//            }
        } else {
            binding.subscribeButton.isEnabled = false
            binding.subscribeButton.text = getString(R.string.support_cannot_subscribe)
        }
    }
}