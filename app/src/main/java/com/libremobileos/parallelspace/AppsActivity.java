package com.libremobileos.parallelspace;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;

import ink.kaleidoscope.ParallelSpaceManager;

public class AppsActivity extends CollapsingToolbarBaseActivity {
    public static final String EXTRA_USER_ID = "user_id";
    public static final String EXTRA_USER_NAME = "user_name";

    private ParallelSpaceManager mParallelSpaceManager;
    private int mUserId;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParallelSpaceManager = ParallelSpaceManager.getInstance();

        mUserId = getIntent().getIntExtra(EXTRA_USER_ID, -1);
        String userName = getIntent().getStringExtra(EXTRA_USER_NAME);
        if (mUserId == -1) {
            Toast.makeText(this, R.string.invalid_user_id, Toast.LENGTH_SHORT).show();
            finish();
        }
        this.setTitle(userName);

        if (savedInstanceState == null) {
            AppsFragment appsFragment = AppsFragment.newInstance(mUserId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(com.android.settingslib.R.id.content_frame, appsFragment)
                    .commit();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_apps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_space:
                AlertDialog builder = new AlertDialog.Builder(this)
                        .setTitle(R.string.remove_space_dialog_title)
                        .setMessage(R.string.remove_space_dialog_message)
                        .setPositiveButton(R.string.remove_space_dialog_positive, (dialog, which) -> removeSpace())
                        .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel()).create();
                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeSpace() {
        View loadingView = getLayoutInflater().inflate(R.layout.loading_dialog, null);
        TextView loadingText = loadingView.findViewById(R.id.loading_text);
        loadingText.setText(R.string.remove_space_progress_message);
        progressDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.please_wait)
                .setView(loadingView)
                .setCancelable(false)
                .create();
        progressDialog.show();

        Thread thread = new Thread(() -> {
            mParallelSpaceManager.remove(mUserId);
            runOnUiThread(() -> {
                AppsActivity.this.finish();
            });
        });
        thread.start();
    }
}
