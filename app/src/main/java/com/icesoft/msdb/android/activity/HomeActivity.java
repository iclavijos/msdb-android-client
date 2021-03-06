package com.icesoft.msdb.android.activity;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.Auth0Exception;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.VoidCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.icesoft.msdb.android.R;
import com.icesoft.msdb.android.tasks.RegisterTokenTask;
import com.icesoft.msdb.android.tasks.RemoveTokenTask;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    private AppBarConfiguration mAppBarConfiguration;

    private Auth0 auth0;
    private AuthenticationAPIClient authenticationAPIClient;
    private SecureCredentialsManager credentialsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);

        auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);
        authenticationAPIClient = new AuthenticationAPIClient(auth0);
        credentialsManager = new SecureCredentialsManager(this, new AuthenticationAPIClient(auth0), new SharedPreferencesStorage(this));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        updateProfile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_login:
                doLogin();
                break;
            case R.id.nav_subscriptions:
                credentialsManager.getCredentials(new BaseCallback<Credentials, CredentialsManagerException>() {
                    @Override
                    public void onSuccess(final Credentials credentials) {
                        if (credentialsManager.hasValidCredentials()) {
                            startUserSubscriptionsActivity(credentials.getIdToken());
                        }
                    }

                    @Override
                    public void onFailure(CredentialsManagerException error) {
                        Log.w(TAG, "Couldn't get credentials");
                    }
                });

                break;
            case R.id.nav_logout:
                doLogout();
                break;
        }
        return false;
    }

    private void updateProfile() {
        credentialsManager.getCredentials(new BaseCallback<Credentials, CredentialsManagerException>() {
            @Override
            public void onSuccess(final Credentials credentials) {
                runOnUiThread(() -> {
                    NavigationView navigationView = findViewById(R.id.nav_view);
                    Menu menu = navigationView.getMenu();
                    menu.clear();
                    navigationView.inflateMenu(R.menu.activity_main_loggedin_drawer);
                    navigationView.invalidate();
                });
                if (credentialsManager.hasValidCredentials()) {
                    getProfile(credentials.getAccessToken());
                }
            }

            @Override
            public void onFailure(CredentialsManagerException error) {
                Log.w(TAG, "Couldn't get credentials");
            }
        });
    }

    private void doLogin() {
        WebAuthProvider.login(auth0)
            .withScheme("msdbclient")
            .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
            .withScope("openid profile email offline_access user_metadata")
            .start(this, loginCallback);
    }

    private void doLogout() {
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(getTokenTask -> {
                if (!getTokenTask.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", getTokenTask.getException());
                    return;
                }

                // Get FCM registration token
                String token = getTokenTask.getResult();
                credentialsManager.getCredentials(new BaseCallback<Credentials, CredentialsManagerException>() {
                    @Override
                    public void onSuccess(final Credentials credentials) {
                        RemoveTokenTask task = new RemoveTokenTask(credentials.getIdToken(), token);
                        Future<Void> opResult = Executors.newFixedThreadPool(1).submit(task);
                        try {
                            opResult.get();
                        } catch (ExecutionException | InterruptedException e) {
                            Log.e(TAG, "Couldn't register token", e);
                        }
                    }

                    @Override
                    public void onFailure(CredentialsManagerException error) {
                        Log.w(TAG, "Couldn't get credentials");
                    }
                });
            });

        WebAuthProvider.logout(auth0)
            .withScheme("msdbclient")
            .start(this, logoutCallback);
    }

    private final AuthCallback loginCallback = new AuthCallback() {
        @Override
        public void onFailure(@NonNull final Dialog dialog) {
            runOnUiThread(() -> dialog.show());
        }

        @Override
        public void onFailure(final AuthenticationException exception) {
            runOnUiThread(() ->
                Toast.makeText(HomeActivity.this, "Log In - Error Occurred", Toast.LENGTH_SHORT).show()
            );
        }

        @Override
        public void onSuccess(@NonNull Credentials credentials) {
            try {
                credentialsManager.saveCredentials(credentials);
            } catch (CredentialsManagerException e) {
                // Retrying
                credentialsManager.saveCredentials(credentials);
            }
            updateProfile();

            FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(getTokenTask -> {
                    if (!getTokenTask.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", getTokenTask.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = getTokenTask.getResult();
                    RegisterTokenTask task = new RegisterTokenTask(credentials.getIdToken(), token);
                    Future<Void> opResult = Executors.newFixedThreadPool(1).submit(task);
                    try {
                        opResult.get();
                    } catch (ExecutionException | InterruptedException e) {
                        Log.e(TAG, "Couldn't register token", e);
                    }
                });
        }
    };

    private VoidCallback logoutCallback = new VoidCallback() {
        @Override
        public void onSuccess(Void payload) {
            runOnUiThread(() -> {
                NavigationView navigationView = findViewById(R.id.nav_view);
                Menu menu = navigationView.getMenu();
                menu.clear();
                navigationView.inflateMenu(R.menu.activity_main_drawer);
                navigationView.invalidate();
                ImageView avatarView = findViewById(R.id.avatarView);
                Glide.with(HomeActivity.this)
                        .load(R.mipmap.ic_launcher)
                        .fitCenter()
                        .into(avatarView);

                ((TextView)findViewById(R.id.userNameView)).setText("");
                ((TextView)findViewById(R.id.userEmailView)).setText("");
            });
            credentialsManager.clearCredentials();
        }

        @Override
        public void onFailure(Auth0Exception error) {
            //Log out canceled, keep the user logged in
            // showNextActivity();
        }
    };

    private void getProfile(String accessToken) {
        if (!isDestroyed()) {
            authenticationAPIClient.userInfo(accessToken)
                    .start(new BaseCallback<UserProfile, AuthenticationException>() {
                        @Override
                        public void onSuccess(UserProfile userInfo) {
                            runOnUiThread(() -> {
                                String name = "";
                                String email = "";

                                if (userInfo != null) {
                                    name = userInfo.getName() != null ? userInfo.getName() : "";
                                    email = userInfo.getEmail() != null ? userInfo.getEmail() : "";
                                }
                                ((TextView) findViewById(R.id.userNameView)).setText(name);
                                ((TextView) findViewById(R.id.userEmailView)).setText(email);
                                if (!isDestroyed()) {
                                    Glide.with(HomeActivity.this)
                                            .load(userInfo.getPictureURL())
                                            .circleCrop()
                                            .into((ImageView) findViewById(R.id.avatarView));
                                }
                            });
                        }

                        @Override
                        public void onFailure(AuthenticationException error) {
                            runOnUiThread(
                                    () -> Toast.makeText(HomeActivity.this, "User Info Request Failed", Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
        }
    }

    private void startUserSubscriptionsActivity(String accessToken) {
        Intent intent = new Intent(this, UserSubscriptionsActivity.class);
        intent.putExtra("accessToken", accessToken);
        startActivity(intent);
    }
}