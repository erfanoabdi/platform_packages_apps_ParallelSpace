package com.libremobileos.parallelspace;

import ink.kaleidoscope.ParallelSpaceManager;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

public class SpaceAppInfo {

    private final String mLabel;
    private final String mPackageName;
    private final Drawable mIcon;
    private final ParallelSpaceManager mParallelSpaceManager;
    private final int mUserId;
    private boolean mAppDuplicated;

    SpaceAppInfo(ResolveInfo info, PackageManager pm, ParallelSpaceManager psm, int userId, boolean isDuplicated) {
        mLabel = info.loadLabel(pm).toString();
        mIcon = info.loadIcon(pm);
        mPackageName = info.activityInfo.packageName;
        mParallelSpaceManager = psm;
        mUserId = userId;
        mAppDuplicated = isDuplicated;
    }

    String getLabel() {
        return mLabel;
    }

    String getPackageName() {
        return mPackageName;
    }

    boolean isAppDuplicated() {
        return mAppDuplicated;
    }

    void setDuplicateApp(boolean duplicate) {
        if (duplicate)
            mParallelSpaceManager.duplicatePackage(mPackageName, mUserId);
        else
            mParallelSpaceManager.removePackage(mPackageName, mUserId);

        mAppDuplicated = duplicate;
    }

    Drawable getIcon() {
        return mIcon;
    }

    @Override
    public String toString() {
        return mLabel;
    }
}
