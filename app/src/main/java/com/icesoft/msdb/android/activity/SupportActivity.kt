package com.icesoft.msdb.android.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.icesoft.msdb.android.databinding.ActivitySupportBinding

import com.icesoft.msdb.android.R

class SupportActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        // binding.toolbarLayout.title = title
    }
}