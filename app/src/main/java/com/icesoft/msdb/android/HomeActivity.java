package com.icesoft.msdb.android;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import com.auth0.android.management.UsersAPIClient;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.VoidCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.icesoft.msdb.android.service.BackendService;
import com.icesoft.msdb.android.tasks.RegisterTokenTask;

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
    private UsersAPIClient usersClient;
    private SecureCredentialsManager credentialsManager;
    private BackendService backendService = new BackendService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        getCredentials();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
//        if (getIntent().getExtras() != null) {
//            for (String key : getIntent().getExtras().keySet()) {
//                Object value = getIntent().getExtras().get(key);
//                Log.d(TAG, "Key: " + key + " Value: " + value);
//            }
//        }
        // [END handle_data_extras]
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
                break;
            case R.id.nav_logout:
                doLogout();
                break;
        }
        return false;
    }

    private void getCredentials() {
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
                    usersClient = new UsersAPIClient(auth0, credentials.getAccessToken());
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
            getCredentials();

            FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> getTokenTask) {
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
                ImageView avatarView = (ImageView)findViewById(R.id.avatarView);
                avatarView.setImageResource(R.mipmap.ic_launcher);
                avatarView.setScaleType(ImageView.ScaleType.CENTER);
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
        authenticationAPIClient.userInfo(accessToken)
            .start(new BaseCallback<UserProfile, AuthenticationException>() {
                @Override
                public void onSuccess(UserProfile userInfo) {
                    runOnUiThread(() -> {
                        ((TextView)findViewById(R.id.userNameView)).setText(userInfo.getName());
                        ((TextView)findViewById(R.id.userEmailView)).setText(userInfo.getEmail());
                        Glide.with(HomeActivity.this)
                            .load(userInfo.getPictureURL())
                            .circleCrop()
                            .into((ImageView)findViewById(R.id.avatarView));
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