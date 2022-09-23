package com.libremobileos.parallelspace;

import android.content.Intent;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserManager;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import java.util.List;

import ink.kaleidoscope.ParallelSpaceManager;

public class SettingsFragment extends PreferenceFragmentCompat {
    private ParallelSpaceManager mParallelSpaceManager;
    private UserManager mUserManager;
    private PreferenceCategory mPreferenceCategory;

    public static final String KEY_MAIN_SPACES = "main_spaces";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        mParallelSpaceManager = ParallelSpaceManager.getInstance();
        mUserManager = UserManager.get(getContext());
        mPreferenceCategory = findPreference(KEY_MAIN_SPACES);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateSpaceList();
    }

    public void updateSpaceList() {
        List<UserInfo> parallelUsers = mParallelSpaceManager.getParallelUsers();
        if (parallelUsers.size() == 0)
            return;

        mPreferenceCategory.removeAll();
        int order = 0;
        for (UserInfo info : parallelUsers) {
            Preference pref = new Preference(requireActivity());
            pref.setTitle(info.name);
            Drawable icon = ContextCompat.getDrawable(requireContext(), mUserManager.getUserBadgeResId(info.id));
            icon.setTint(mUserManager.getUserBadgeColor(info.id));
            pref.setIcon(icon);
            pref.setOnPreferenceClickListener(preference -> {
                final Intent appsIntent = new Intent(requireContext(), AppsActivity.class);
                appsIntent.putExtra(AppsActivity.EXTRA_USER_ID, info.id);
                appsIntent.putExtra(AppsActivity.EXTRA_USER_NAME, info.name);
                startActivity(appsIntent);
                return true;
            });
            mPreferenceCategory.addPreference(pref);
            pref.setOrder(order);
            order++;
        }
    }
}
