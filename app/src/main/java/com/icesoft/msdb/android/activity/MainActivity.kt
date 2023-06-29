package com.icesoft.msdb.android.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.CredentialsManagerException
import com.auth0.android.callback.Callback
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import com.bumptech.glide.Glide
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.json.JsonMapper
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.icesoft.msdb.android.BuildConfig
import com.icesoft.msdb.android.R
import com.icesoft.msdb.android.databinding.ActivityMainBinding
import com.icesoft.msdb.android.exception.MSDBException
import com.icesoft.msdb.android.model.Series
import com.icesoft.msdb.android.service.MSDBService
import com.icesoft.msdb.android.tasks.GetLatestVersionTask
import com.icesoft.msdb.android.tasks.GetSeriesTask
import com.icesoft.msdb.android.tasks.RegisterTokenTask
import com.icesoft.msdb.android.ui.OldVersionDialogFragment
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = "MainActivity"

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var msdbService: MSDBService
    private var serviceBound: Boolean = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MSDBService.LocalBinder
            msdbService = binder.getService()
            updateProfile()
            retrieveSeriesList()
            serviceBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            serviceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_agenda, R.id.nav_championships
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val bottomNavView: BottomNavigationView = binding.appBarMain.appContentMain.bottomNavView
        bottomNavView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener(this)

        checkVersion()
        val content: View = binding.navView
        content.viewTreeObserver.addOnPreDrawListener {
            serviceBound && msdbService.initialized()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()

        Intent(this, MSDBService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Delete old channels to ensure new notification sound is picked up
        notificationManager.deleteNotificationChannel("msdbChannel")
        notificationManager.deleteNotificationChannel("MSDB Channel id")

        // Since android Oreo notification channel is needed.
        val channel = NotificationChannel(
            getString(R.string.default_notification_channel_id),
            getString(R.string.default_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()
        channel.setSound(
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
            audioAttributes
        )
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        serviceBound = false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_support -> {
                // billingClient.launchBillingFlow(this)
                val intent = Intent(this, SupportActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_login -> msdbService.doLogin(this, loginCallback)
            R.id.nav_subscriptions -> {
                val credentials = runBlocking { msdbService.getCredentials() }
                if (credentials != null) {
                    if (msdbService.hasValidCredentials()) {
                        startUserSubscriptionsActivity(credentials.accessToken)
                    }
                }

            }

            R.id.nav_logout -> msdbService.doLogout(this, logoutCallback)
        }
        return false
    }

    private fun updateProfile() {
        Log.d(TAG, "Updating user profile")
        var credentials = runBlocking { msdbService.getCredentials() }
        if (credentials == null) {
            Log.w(TAG, "Couldn't get credentials")
            runOnUiThread {
                val navigationView = findViewById<NavigationView>(R.id.nav_view)
                val menu = navigationView.menu
                menu.removeItem(R.id.nav_subscriptions)
                menu.removeItem(R.id.nav_logout)
            }
        } else if (credentials.expiresAt.before(Date())) {
            credentials = runBlocking { msdbService.refreshToken() }
        }

        if (credentials != null) {
            runOnUiThread {
                val navigationView = findViewById<NavigationView>(R.id.nav_view)
                val menu = navigationView.menu
                menu.removeItem(R.id.nav_login)
                if (menu.findItem(R.id.nav_subscriptions) == null) {
                    val mItemSubs =
                        menu.add(0, R.id.nav_subscriptions, 0, R.string.subscriptions)
                    mItemSubs.setIcon(R.drawable.ic_baseline_settings_24)
                    val mItemLogout =
                        menu.add(0, R.id.nav_logout, 1, R.string.logout)
                    mItemLogout.setIcon(R.drawable.ic_log_out)
                    menu.removeItem(R.id.nav_support)
                    val mItemSupport =
                        menu.add(0, R.id.nav_support, 2, R.string.nav_support)
                    mItemSupport.setIcon(R.drawable.ic_support)
                }
            }
            getProfile()
        }
    }

    private fun getProfile() {
        if (!isDestroyed) {
            Log.d(TAG, "Retrieving user profile")
            msdbService.getUserInfo(::updateUserProfile)
        }
    }

    private fun updateUserProfile(userInfo: UserProfile?) {
        if (userInfo != null) {
            runOnUiThread {
                val name = if (userInfo.name != null) userInfo.name else "-"
                val email = if (userInfo.email != null) userInfo.email else "-"

                val userNameTextView = findViewById<View>(R.id.userNameView) as TextView
                val userEmailTextView = findViewById<View>(R.id.userEmailView) as TextView
                if (!Objects.isNull(userNameTextView) && !Objects.isNull(userEmailTextView)) {
                    userNameTextView.text = name
                    userEmailTextView.text = email
                    if (!isDestroyed) {
                        Glide.with(this@MainActivity)
                            .load(userInfo.pictureURL)
                            .circleCrop()
                            .into(findViewById<View>(R.id.avatarView) as ImageView)
                    }
                }
            }
        } else {
            runOnUiThread {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.user_info_request_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private val loginCallback: Callback<Credentials, AuthenticationException> =
        object : Callback<Credentials, AuthenticationException> {
            override fun onFailure(error: AuthenticationException) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Log In - Error Occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onSuccess(result: Credentials) {
                try {
                    msdbService.saveCredentials(result)
                } catch (e: CredentialsManagerException) {
                    // Retrying
                    msdbService.saveCredentials(result)
                }
                updateProfile()
                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener { getTokenTask: Task<String?> ->
                        if (!getTokenTask.isSuccessful) {
                            Log.w(
                                TAG,
                                "Fetching FCM registration token failed",
                                getTokenTask.exception
                            )
                            return@addOnCompleteListener
                        }

                        // Get new FCM registration token
                        val token = getTokenTask.result
                        val task = RegisterTokenTask(result.accessToken, token)
                        val opResult =
                            Executors.newFixedThreadPool(1).submit(task)
                        try {
                            opResult.get()
                        } catch (e: ExecutionException) {
                            Log.e(TAG, "Couldn't register token", e)
                        } catch (e: InterruptedException) {
                            Log.e(TAG, "Couldn't register token", e)
                        }
                    }
            }
        }

    private val logoutCallback: Callback<Void?, AuthenticationException> =
        object : Callback<Void?, AuthenticationException> {
            override fun onSuccess(result: Void?) {
                runOnUiThread {
                    val navigationView = findViewById<NavigationView>(R.id.nav_view)
                    val menu = navigationView.menu
                    menu.clear()
                    navigationView.inflateMenu(R.menu.activity_main_drawer)
                    menu.removeItem(R.id.nav_subscriptions)
                    menu.removeItem(R.id.nav_logout)
                    navigationView.invalidate()
                    val avatarView =
                        findViewById<ImageView>(R.id.avatarView)
                    Glide.with(this@MainActivity)
                        .load(R.mipmap.ic_launcher)
                        .fitCenter()
                        .into(avatarView)
                    (findViewById<View>(R.id.userNameView) as TextView).text = ""
                    (findViewById<View>(R.id.userEmailView) as TextView).text = ""
                }
                msdbService.clearCredentials()
            }

            override fun onFailure(error: AuthenticationException) {
                //Log out canceled, keep the user logged in
                // showNextActivity();
            }
        }

    private fun checkVersion() {
        val tokenizedLocalVersion =
            BuildConfig.VERSION_NAME.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        val doneSignal = CountDownLatch(1)
        val getLatestVersionTask = GetLatestVersionTask(doneSignal)
        val executor = Executors.newFixedThreadPool(2)
        val opVersion = executor.submit(getLatestVersionTask)

        try {
            doneSignal.await()
            Optional.ofNullable(opVersion.get()).ifPresent { response: String ->
                val tokenizedRemoteVersion =
                    response.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                val localVersion = Version(
                    tokenizedLocalVersion[0].toInt(),
                    tokenizedLocalVersion[1].toInt(),
                    tokenizedLocalVersion[2].toInt(),
                    null,
                    null,
                    null
                )
                val remoteVersion = Version(
                    tokenizedRemoteVersion[0].toInt(),
                    tokenizedRemoteVersion[1].toInt(),
                    tokenizedRemoteVersion[2].toInt(),
                    null,
                    null,
                    null
                )
                if (localVersion < remoteVersion) {
                    val newFragment: DialogFragment =
                        OldVersionDialogFragment(localVersion.toString(), remoteVersion.toString())
                    newFragment.show(supportFragmentManager, "old version")
                }
            }
        } catch (e: ExecutionException) {
            Log.w(TAG, "Couldn't retrieve latest version on time", e)
        } catch (e: InterruptedException) {
            Log.w(TAG, "Couldn't retrieve latest version on time", e)
        }
    }

    private fun startUserSubscriptionsActivity(accessToken: String) {
        val intent = Intent(this, UserSubscriptionsActivity::class.java)
        intent.putExtra("accessToken", accessToken)
        startActivity(intent)
    }

    private fun retrieveSeriesList() {
        val doneSignal = CountDownLatch(1)
        val getSeriesTask = GetSeriesTask(doneSignal)
        val executor = Executors.newFixedThreadPool(2)
        val opSeries = executor.submit(getSeriesTask)
        try {
            doneSignal.await()
            Optional.ofNullable(opSeries.get()).ifPresent { response: List<Series> ->
                response.sortedBy { it.name }
                val sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(this)
                val editor = sharedPreferences.edit()
                val mapper = JsonMapper()
                try {
                    editor.putString("seriesList", mapper.writeValueAsString(response))
                } catch (e: JsonProcessingException) {
                    Log.e(
                        TAG,
                        "Couldn't serialize series list $this"
                    )
                    throw MSDBException(e)
                }
                editor.apply()
            }
        } catch (e: ExecutionException) {
            Log.w(TAG, "Couldn't retrieve series list on time", e)
        } catch (e: InterruptedException) {
            Log.w(TAG, "Couldn't retrieve series list on time", e)
        }
    }
}