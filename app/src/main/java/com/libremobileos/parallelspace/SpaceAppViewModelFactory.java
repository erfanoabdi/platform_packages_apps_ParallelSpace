package com.libremobileos.parallelspace;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SpaceAppViewModelFactory implements ViewModelProvider.Factory {
    private final Application mApplication;
    private final int mUserId;

    public SpaceAppViewModelFactory(Application application, int userId) {
        mApplication = application;
        mUserId = userId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new SpaceAppViewModel(mApplication, mUserId);
    }
}
