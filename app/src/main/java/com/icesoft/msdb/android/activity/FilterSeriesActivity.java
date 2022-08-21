package com.icesoft.msdb.android.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.icesoft.msdb.android.R;
import com.icesoft.msdb.android.exception.MSDBException;
import com.icesoft.msdb.android.model.Series;

import java.util.List;

public class FilterSeriesActivity extends AppCompatActivity {

    private static final String TAG = "FilterSeriesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            Context context = getPreferenceManager().getContext();
            PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);

            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context);
            if (sharedPreferences.contains("seriesList")) {
                JsonMapper mapper = new JsonMapper();
                List<Series> series = null;
                String seriesList = sharedPreferences.getString("seriesList", "[]");
                try {
                    series = mapper.readValue(seriesList, new TypeReference<List<Series>>() {});
                } catch (JsonProcessingException e) {
                    Log.e(TAG, "Couldn't deserialize series list " + seriesList);
                    throw new MSDBException(e);
                }
                series.forEach(item -> {
                    SwitchPreferenceCompat notificationPreference = new SwitchPreferenceCompat(context);
                    notificationPreference.setKey("series-" + item.getId());
                    notificationPreference.setTitle(item.getName());

                    screen.addPreference(notificationPreference);
                });
            }

            setPreferenceScreen(screen);
        }
    }
}