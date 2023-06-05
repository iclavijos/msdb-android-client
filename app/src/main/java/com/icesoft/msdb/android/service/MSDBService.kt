package com.icesoft.msdb.android.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.CredentialsManagerException
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider.login
import com.auth0.android.provider.WebAuthProvider.logout
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.icesoft.msdb.android.tasks.RemoveTokenTask
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

class MSDBService : Service() {

    private val TAG = "MSDBService"

    private val binder = LocalBinder()
    private var initialized = false

    private lateinit var auth0: Auth0
    private lateinit var authenticationAPIClient: AuthenticationAPIClient
    private lateinit var credentialsManager: SecureCredentialsManager

    inner class LocalBinder: Binder() {
        fun getService(): MSDBService = this@MSDBService
    }
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()

        auth0 = Auth0(this)
        authenticationAPIClient = AuthenticationAPIClient(auth0)
        credentialsManager = SecureCredentialsManager(
            this,
            AuthenticationAPIClient(auth0),
            SharedPreferencesStorage(this)
        )
        initialized = true
    }

    fun initialized(): Boolean = this.initialized

    fun getCredentials(): Credentials? {
        var credentials: Credentials? = null
        credentialsManager.getCredentials(object: Callback<Credentials, CredentialsManagerException> {
            override fun onSuccess(result: Credentials) {
                credentials = result
            }

            override fun onFailure(error: CredentialsManagerException) {
                Log.w(TAG, "Couldn't get credentials")
                credentials = null
            }
        })
        return credentials
    }

    fun hasValidCredentials(): Boolean = credentialsManager.hasValidCredentials()

    fun clearCredentials() = credentialsManager.clearCredentials()

    fun saveCredentials(credentials: Credentials) = credentialsManager.saveCredentials(credentials)

    fun getUserInfo(): UserProfile? {
        var userProfile: UserProfile? = null

        val credentials = getCredentials()

        if (credentials != null) {
            authenticationAPIClient.userInfo(credentials.accessToken)
                .start(object : Callback<UserProfile, AuthenticationException> {
                    override fun onSuccess(result: UserProfile) {
                        userProfile = result
                    }

                    override fun onFailure(error: AuthenticationException) {
                        Log.w(TAG, "Couldn't retrieve user profile", error)
                    }
                })
        }

        return userProfile
    }

    fun doLogin(loginCallback: Callback<Credentials, AuthenticationException>) {

        login(auth0)
            .withScheme("msdbclient")
            .withAudience("https://msdb.eu.auth0.com/api/v2/")
            .withScope("openid profile email offline_access user_metadata")
            .start(this, loginCallback)
    }

    fun doLogout(logoutCallback: Callback<Void?, AuthenticationException>) {
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

                // Get FCM registration token
                val token = getTokenTask.result
                credentialsManager.getCredentials(object :
                    Callback<Credentials, CredentialsManagerException> {
                    override fun onSuccess(result: Credentials) {
                        val task = RemoveTokenTask(result.accessToken, token)
                        val opResult =
                            Executors.newFixedThreadPool(1)
                                .submit(task)
                        try {
                            opResult.get()
                        } catch (e: ExecutionException) {
                            Log.e(TAG, "Couldn't register token", e)
                        } catch (e: InterruptedException) {
                            Log.e(TAG, "Couldn't register token", e)
                        }
                    }

                    override fun onFailure(error: CredentialsManagerException) {
                        Log.w(TAG, "Couldn't get credentials")
                    }
                })
            }

        logout(auth0)
            .withScheme("msdbclient")
            .start(this, logoutCallback)
    }

    fun getUserProfile(accessToken: String) {

    }
}