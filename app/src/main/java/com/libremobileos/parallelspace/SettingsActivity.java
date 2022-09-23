package com.libremobileos.parallelspace;

import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;

import ink.kaleidoscope.ParallelSpaceManager;

public class SettingsActivity extends CollapsingToolbarBaseActivity {
    private ParallelSpaceManager mParallelSpaceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mParallelSpaceManager = ParallelSpaceManager.getInstance();
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(com.android.settingslib.R.id.content_frame, new SettingsFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_spaces, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_space:
                View newSpaceView = getLayoutInflater().inflate(R.layout.new_space_dialog, null);
                EditText newSpaceName = newSpaceView.findViewById(R.id.new_space_name);
                newSpaceName.setHint(getString(R.string.space) + " " + (mParallelSpaceManager.getParallelUsers().size() + 1));
                newSpaceName.setInputType(InputType.TYPE_CLASS_TEXT);

                AlertDialog builder = new AlertDialog.Builder(this)
                        .setTitle(R.string.new_space_dialog_title)
                        .setView(newSpaceView)
                        .setMessage(R.string.new_space_dialog_message)
                        .setPositiveButton(R.string.new_space_dialog_positive, (dialog, which) -> {
                            String spaceName = newSpaceName.getText().toString();
                            if (spaceName.isEmpty()) {
                                Toast.makeText(SettingsActivity.this, R.string.space_name_empty, Toast.LENGTH_SHORT).show();
                            } else {
                                createSpace(spaceName);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel()).create();
                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createSpace(String spaceName) {
        View loadingView = getLayoutInflater().inflate(R.layout.loading_dialog, null);
        TextView loadingText = loadingView.findViewById(R.id.loading_text);
        loadingText.setText(R.string.new_space_progress_message);
        AlertDialog builder = new AlertDialog.Builder(this)
                .setTitle(R.string.please_wait)
                .setView(loadingView)
                .setCancelable(false)
                .create();
        builder.show();

        Thread thread = new Thread(() -> {
            try {
                mParallelSpaceManager.create(spaceName);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                if (SettingsActivity.this.isDestroyed()) {
                    return;
                }
                builder.dismiss();
            });
        });
        thread.start();
    }

}
