package com.ezikche.babyschedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;


public class MainActivity extends Activity
        implements ItemFragment.OnFragmentInteractionListener {

    private AdView mAdView;
    private int mCurrentAct = Utils.EAT;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_large);

        mAdView = (AdView) findViewById(R.id.adView);
//        mAdView.setAdListener(new ToastAdListener(this));
        mAdView.loadAd(new AdRequest.Builder().build());

        ItemFragment itemFragment = (ItemFragment) getFragmentManager().findFragmentById(R.id.left_fragment);
        itemFragment.setSelectedItem(mCurrentAct);
        setRightBackgroundByAction(mCurrentAct);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings: {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
            return true;
            case R.id.action_statistic:
                if (Utils.isExternalStorageReadable()) {
//                    Toast.makeText(this, "统计功能仍在拼命(>_<)开发中", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, StatisticActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "文件系统不可读", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_detail:
                if (Utils.isExternalStorageReadable()) {
//                Toast.makeText(this, "明细即将打开:)", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, DetailActivity.class);
                    startActivity(intent);
                } else{
                    Toast.makeText(this, "文件系统不可读", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onButtonNextPressed(View view) {
//        Toast.makeText(this,"button next on act" + mCurrentAct + "pressed", Toast.LENGTH_LONG).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String[] actions = getResources().getStringArray(R.array.actions);
        String[] messages = getResources().getStringArray(R.array.dialogMsg);
        builder.setTitle(actions[mCurrentAct])
                .setMessage(messages[mCurrentAct]);

        final NumberPicker picker = new ColorNumberPicker(getApplicationContext());

        final String[] displayedValues = Utils.getDisplayValuesByAct(mCurrentAct);

        picker.setMinValue(0);
        picker.setMaxValue(displayedValues.length - 1);
        picker.setDisplayedValues(displayedValues);
        switch(mCurrentAct){
            case Utils.EAT:{
                picker.setValue(Utils.EAT_DEFAULT);
            }break;
            case Utils.WEIGHT:{
                picker.setValue(Utils.WEIGHT_DEFAULT);
            }break;
            case Utils.HEIGHT:{
                picker.setValue(Utils.HEIGHT_DEFAULT);
            }break;
            case Utils.TEMPERATURE:{
                picker.setValue(Utils.TEMPERATURE_DEFAULT);
            }break;
        }
        picker.setWrapSelectorWheel(false);
        picker.setBackgroundColor(Utils.colors[mCurrentAct]);
        picker.setAlpha(0.5f);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        builder.setView(picker);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DatePicker DP = (DatePicker) (MainActivity.this.findViewById(R.id.datePicker));
                TimePicker TP = (TimePicker) (MainActivity.this.findViewById(R.id.timePicker));
                String date = DP.getYear() + "." + String.format("%02d", DP.getMonth() + 1) + "." + String.format("%02d", DP.getDayOfMonth());
                String time = String.format("%02d", TP.getCurrentHour()) + "." + String.format("%02d", TP.getCurrentMinute());
                String[] fileNames = getResources().getStringArray(R.array.fileName);
                try{
                    BufferedReader buf = new BufferedReader(new FileReader(Utils.getStorageFile(fileNames[mCurrentAct], date)));
                    String tmp;
                    while ((tmp = buf.readLine()) != null) {
                        int pos = tmp.indexOf(":");
                        if(tmp.substring(0, pos).indexOf(time) != -1)
                        {
                            Toast.makeText(MainActivity.this, "时间重复", Toast.LENGTH_SHORT).show();
                            return ;
                        }
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                String body = Utils.getMessageBodyByAct(mCurrentAct, displayedValues, picker.getValue());

                String message = time + ":" + body;

                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
//              save to the file
                if (Utils.isExternalStorageWritable()) {
                    File outFile = Utils.getStorageFile(fileNames[mCurrentAct], date);
                    try {
                        FileOutputStream fos = new FileOutputStream(outFile, true);
                        fos.write(message.getBytes());
                        fos.close();

                        Toast.makeText(MainActivity.this, "文件写入成功", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
//        dialog.setInverseBackgroundForced(false);
//        Window window = dialog.getWindow();
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.alpha = 0.8f;
//        window.setAttributes(lp);
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean exitWith2Backs = sharedPref.getBoolean(SettingsActivity.KEY_PREF_EXIT, true);
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && exitWith2Backs) {

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), R.string.press_again_exit, Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }

            return true;
        }
        else{
            finish();
            System.exit(0);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onFragmentInteraction(String id, int position, View view) {
        mCurrentAct = position % Utils.colors.length;
        view.setBackgroundColor(Utils.colors[mCurrentAct]);

        setRightBackgroundByAction(mCurrentAct);

    }

    private void setRightBackgroundByAction(int action) {
        View rightView = getFragmentManager().findFragmentById(R.id.right_fragment).getView();
        if (rightView != null) {
            rightView.setBackgroundResource(Utils.mBackgroundPics[action % Utils.mBackgroundPics.length]);
            rightView.getBackground().setAlpha(0x20);
        }
    }
}
