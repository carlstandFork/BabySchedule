package com.ezikche.babyschedule;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.Toast;

import net.rdrei.android.dirchooser.DirectoryChooserFragment;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity implements DirectoryChooserFragment.OnFragmentInteractionListener{
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private DirectoryChooserFragment mDialog;
    private String oldPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        addPreferencesFromResource(R.xml.preferences);

        setupDirChooser();
    }

    private void setupDirChooser(){
        Preference DirChooser = findPreference(getString(R.string.pref_key_store_path));
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        oldPath = sharedPref.getString(getString(R.string.pref_key_store_path), getString(R.string.pref_default_store_path));
        DirChooser.setSummary(oldPath);

        DirChooser.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){
            @Override
            public boolean onPreferenceClick(Preference pref){
                mDialog = DirectoryChooserFragment.newInstance("BabySchedule",oldPath);
                mDialog.show(getFragmentManager(),null);
                return true;
            }
        });
    }

    @Override
    public void onSelectDirectory(@NonNull final String path) {

        if(Utils.moveFiles(oldPath,path)) {
            oldPath = path;
            Preference DirChooser = findPreference(getString(R.string.pref_key_store_path));
            DirChooser.setSummary(path);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.pref_key_store_path), path);
            editor.commit();
        }
        else
        {
            Toast.makeText(SettingsActivity.this, "目录无效", Toast.LENGTH_SHORT).show();
        }

        mDialog.dismiss();
    }

    @Override
    public void onCancelChooser() {
        mDialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(upIntent)
                        .startActivities();
            } else {
                upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, upIntent);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
