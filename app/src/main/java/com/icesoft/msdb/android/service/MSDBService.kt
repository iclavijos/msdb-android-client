package com.icesoft.msdb.android.service

import android.app.Activity
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
import com.icesoft.msdb.android.client.MSDBBillingClient
import com.icesoft.msdb.android.exception.MSDBException
import com.icesoft.msdb.android.tasks.RemoveTokenTask
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MSDBService : Service() {

    private val TAG = "MSDBService"

    private val binder = LocalBinder()
    private var initialized = false

    private lateinit var auth0: Auth0
    private lateinit var authenticationAPIClient: AuthenticationAPIClient
    private lateinit var credentialsManager: SecureCredentialsManager

    private lateinit var billingClient: MSDBBillingClient

    private var cachedCredentials: Credentials? = null

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
        billingClient = MSDBBillingClient(this)
        initialized = true
    }

    fun initialized() = this.initialized

    suspend fun getCredentials(): Credentials? {
        if (cachedCredentials == null) {
            Log.d(TAG, "Retrieving user credentials")
            cachedCredentials = suspendCoroutine { continuation ->
                credentialsManager.getCredentials(object :
                    Callback<Credentials, CredentialsManagerException> {
                    override fun onSuccess(result: Credentials) {
                        // Use credentials
                        Log.d(TAG, "Retrieved user credentials")
                        continuation.resume(result)
                    }

                    override fun onFailure(error: CredentialsManagerException) {
                        // No credentials were previously saved or they couldn't be refreshed
                        Log.w(TAG, "Couldn't get credentials")
                        continuation.resumeWith(Result.success(null))
                    }
                })
            }
        }

        return cachedCredentials
    }

    fun hasValidCredentials(): Boolean = credentialsManager.hasValidCredentials()

    fun clearCredentials() = credentialsManager.clearCredentials()

    fun saveCredentials(credentials: Credentials) = credentialsManager.saveCredentials(credentials)

    fun getUserInfo(updateUICallback:(UserProfile) -> Unit) {
        if (cachedCredentials == null) {
            cachedCredentials = runBlocking {
                getCredentials()
            }
        }

        Log.d(TAG, "Using user credentials to retrieve profile")

        authenticationAPIClient.userInfo(cachedCredentials!!.accessToken)
            .start(object : Callback<UserProfile, AuthenticationException> {
                override fun onSuccess(result: UserProfile) {
                    Log.d(TAG, "Successfully retrieved user profile")
                    updateUICallback.invoke(result)
                }

                override fun onFailure(error: AuthenticationException) {
                    Log.w(TAG, "Couldn't retrieve user profile", error)
                }
            })
    }

    fun doLogin(parentActivity: Activity, loginCallback: Callback<Credentials, AuthenticationException>) {

        login(auth0)
            .withScheme("msdbclient")
            .withAudience("https://msdb.eu.auth0.com/api/v2/")
            .withScope("openid profile email offline_access user_metadata")
            .start(parentActivity, loginCallback)
    }

    fun doLogout(parentActivity: Activity, logoutCallback: Callback<Void?, AuthenticationException>) {
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
            .start(parentActivity, logoutCallback)
    }

}