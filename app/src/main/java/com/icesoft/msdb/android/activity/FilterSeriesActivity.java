package com.icesoft.msdb.android.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.android.gms.common.util.Strings;
import com.icesoft.msdb.android.R;
import com.icesoft.msdb.android.exception.MSDBException;
import com.icesoft.msdb.android.model.Series;

import java.util.List;
import java.util.stream.Collectors;

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

        ((EditText) findViewById(R.id.filterEditText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String filter = s.toString();

                SettingsFragment settingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentById(R.id.settings);
                settingsFragment.filterPreferences(filter);
            }
        });
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private PreferenceScreen preferenceScreen;
        private List<Series> series = null;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            Context context = getPreferenceManager().getContext();
            preferenceScreen = getPreferenceManager().createPreferenceScreen(context);

            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context);
            if (sharedPreferences.contains("seriesList")) {
                JsonMapper mapper = new JsonMapper();

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

                    preferenceScreen.addPreference(notificationPreference);
                });
            }

            setPreferenceScreen(preferenceScreen);
        }

        public void filterPreferences(String filter) {
            final int preferencesCount = preferenceScreen.getPreferenceCount();
            if (Strings.isEmptyOrWhitespace(filter)) {
                for (int index = 0; index < preferencesCount; index++) {
                    Preference preference = preferenceScreen.getPreference(index);
                    if (preference.getTitle().toString().startsWith("series-")) {
                        preference.setVisible(true);
                    }
                }
            } else {
                List<String> filteredSeriesNames = series.stream()
                        .map(Series::getName)
                        .filter(name -> name.toLowerCase().contains(filter.toLowerCase()))
                        .collect(Collectors.toList());

                for (int index = 0; index < preferencesCount; index++) {
                    Preference preference = preferenceScreen.getPreference(index);
                    if (preference.getKey().startsWith("series-")) {
                        preference.setVisible(filteredSeriesNames.contains(preference.getTitle()));
                    }
                }
            }
        }
    }
}