package com.libremobileos.parallelspace;

import android.os.Bundle;
import android.view.View;

import androidx.preference.PreferenceFragmentCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import java.util.List;

public class AppsFragment extends PreferenceFragmentCompat {
    private PreferenceScreen mPreferenceScreen;

    public static final AppsFragment newInstance(int userId) {
        AppsFragment fragment = new AppsFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt(AppsActivity.EXTRA_USER_ID, userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.apps_preferences, rootKey);

        mPreferenceScreen = getPreferenceScreen();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int userId = getArguments().getInt(AppsActivity.EXTRA_USER_ID);
        SpaceAppViewModelFactory spaceAppViewModelFactory = new SpaceAppViewModelFactory(requireActivity().getApplication(), userId);
        final SpaceAppViewModel model = new ViewModelProvider(this, spaceAppViewModelFactory).get(SpaceAppViewModel.class);
        model.getAppList().observeForever(data -> {
            updateAppsList(data);
        });
    }

    private void updateAppsList(List<SpaceAppInfo> apps) {
        int order = 0;
        for (SpaceAppInfo info : apps) {
            SwitchPreference pref = new SwitchPreference(requireActivity());
            pref.setTitle(info.getLabel());
            pref.setSummary(info.getPackageName());
            pref.setIcon(info.getIcon());
            pref.setChecked(info.isAppDuplicated());
            pref.setOnPreferenceChangeListener((preference, newValue) -> {
                info.setDuplicateApp((Boolean) newValue);
                return true;
            });
            mPreferenceScreen.addPreference(pref);
            pref.setOrder(order);
            order++;
        }
    }
}
