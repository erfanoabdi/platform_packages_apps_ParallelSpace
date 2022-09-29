package com.libremobileos.parallelspace;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.android.internal.libremobileos.app.ParallelSpaceManager;

import java.util.ArrayList;
import java.util.List;

public class SpaceAppViewModel extends AndroidViewModel {
    private final SpaceAppListLiveData mLiveData;

    public SpaceAppViewModel(Application application, int userId) {
        super(application);
        mLiveData = new SpaceAppListLiveData(application, userId);
    }

    public LiveData<List<SpaceAppInfo>> getAppList() {
        return mLiveData;
    }
}

class SpaceAppListLiveData extends LiveData<List<SpaceAppInfo>> {

    private final PackageManager mPackageManager;
    private final ParallelSpaceManager mParallelSpaceManager;
    private final Context mContext;
    private int mCurrentDataVersion;

    public SpaceAppListLiveData(Context context, int userId) {
        mPackageManager = context.getPackageManager();
        mParallelSpaceManager = ParallelSpaceManager.getInstance();
        mContext = context;
        loadSupportedAppData(userId);
    }

    void loadSupportedAppData(int userId) {
        final int dataVersion = ++mCurrentDataVersion;

        Thread thread = new Thread(() -> {
            List<SpaceAppInfo> apps = new ArrayList<>();
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> infos = mPackageManager.queryIntentActivities(mainIntent, PackageManager.GET_META_DATA);
            List<ApplicationInfo> installedAppsOnSpace = mPackageManager.getInstalledApplicationsAsUser(0, userId);

            for (ResolveInfo info : infos) {
                if (info.activityInfo.applicationInfo.isSystemApp() ||
                        info.activityInfo.packageName.equals(mContext.getPackageName()))
                    continue;
                boolean isDuplicated = false;
                for (ApplicationInfo installedApp : installedAppsOnSpace) {
                    if (info.activityInfo.packageName.equals(installedApp.packageName)) {
                        isDuplicated = true;
                        break;
                    }
                }
                SpaceAppInfo app = new SpaceAppInfo(info, mPackageManager, mParallelSpaceManager, userId, isDuplicated);
                apps.add(app);
            }
            if (dataVersion == mCurrentDataVersion) {
                postValue(apps);
            }
        });
        thread.start();
    }
}