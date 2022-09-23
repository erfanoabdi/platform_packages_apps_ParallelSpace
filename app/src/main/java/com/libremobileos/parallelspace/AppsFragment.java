package com.libremobileos.parallelspace;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class AppsFragment extends PreferenceFragmentCompat {
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
    }
}
