package com.ezikche.babyschedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;


public class MainActivity extends Activity
        implements ItemFragment.OnFragmentInteractionListener{

    private AdView mAdView;
    private int mCurrentAct = Utils.EAT;
    private long exitTime = 0;
    private Target mTarget = null;

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
    public void onResume(){
        super.onResume();

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
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, StatisticActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.file_not_readable), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_detail:
                if (Utils.isExternalStorageReadable()) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, DetailActivity.class);
                    startActivity(intent);
                } else{
                    Toast.makeText(this, getResources().getString(R.string.file_not_readable), Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onButtonNextPressed(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(Utils.getTitle(mCurrentAct))
                .setMessage(Utils.getMessage(mCurrentAct));

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
        }
        picker.setWrapSelectorWheel(false);
        picker.setBackgroundColor(Utils.getBackgroundColor(mCurrentAct));
        picker.setAlpha(0.5f);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        builder.setView(picker);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DatePicker DP = (DatePicker) (MainActivity.this.findViewById(R.id.datePicker));
                TimePicker TP = (TimePicker) (MainActivity.this.findViewById(R.id.timePicker));
                String date = DP.getYear() + "." + String.format("%02d", DP.getMonth() + 1) + "." + String.format("%02d", DP.getDayOfMonth());
                String time = String.format("%02d", TP.getCurrentHour()) + "." + String.format("%02d", TP.getCurrentMinute());
                String[] folderNames = getResources().getStringArray(R.array.folderName);
                try{
                    BufferedReader buf = new BufferedReader(new FileReader(Utils.getStorageFile(Utils.getPath(),folderNames[mCurrentAct], date)));
                    String tmp;
                    while ((tmp = buf.readLine()) != null) {
                        int pos = tmp.indexOf(":");
                        if(tmp.substring(0, pos).indexOf(time) != -1)
                        {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.duplicate_date), Toast.LENGTH_SHORT).show();
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
                    try {
                        File outFile = Utils.getStorageFile(Utils.getPath(),folderNames[mCurrentAct], date);
                        FileOutputStream fos = new FileOutputStream(outFile, true);
                        fos.write(message.getBytes());
                        fos.close();

                        Toast.makeText(MainActivity.this, getResources().getString(R.string.write_file_succeed), Toast.LENGTH_SHORT).show();
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
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            boolean exitWith2Backs = sharedPref.getBoolean(getString(R.string.pref_key_exit), true);
            // press back twice with in 2 secs
            if (exitWith2Backs) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(getApplicationContext(), R.string.press_again_exit, Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    finish();
                    System.exit(0);
                }
                return true;
            } else {
                finish();
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onFragmentInteraction(String id, int position, View view, boolean longPress) {
        String text = String.valueOf(((TextView) view).getText());
        boolean isPlusItem = text.isEmpty();
        if(!longPress) {
            if(isPlusItem){
                getDialog(position,text, false).show();
            }else {
                mCurrentAct = position;
                view.setBackgroundColor(Utils.getBackgroundColor(mCurrentAct));

                setRightBackgroundByAction(mCurrentAct);
            }
        }
        else{
            if(!isPlusItem && position >= Utils.colors.length ) {
                Toast.makeText(getApplicationContext(), "长按了No.：" + String.valueOf(position) + text, Toast.LENGTH_SHORT).show();
                getDialog(position, text, true).show();
            }
        }
    }

    private AlertDialog getDialog(final int itemPos, final String name, boolean removable) {
        //start Dialog to modify clicked line
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
//        String[] actions = getResources().getStringArray(R.array.actions);
//        dialogBuilder.setTitle(actions[mCurrentAct]);
//                .setMessage(messages[mCurrentAct]);

        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_modify_action, null);


        final ColorPicker cPicker =  (ColorPicker)layout.findViewById(R.id.colorPicker);
        final EditText nameField = (EditText)layout.findViewById(R.id.actionName);
        final EditText unitField = (EditText)layout.findViewById(R.id.actionUnit);
        if(removable){
            nameField.setText(name);
            unitField.setText(Utils.getMessage(itemPos));
        }
        cPicker.setOldCenterColor(Utils.getBackgroundColor(itemPos));
        cPicker.setColor(Utils.getBackgroundColor(itemPos));

        dialogBuilder.setView(layout);

        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String name = String.valueOf(nameField.getText());
                String unit = String.valueOf(unitField.getText());
                int color = cPicker.getColor();
                if(addActionInFile(name,unit,color)){
                    refreshItemList();
                }
            }
        });

        if(removable) {
            dialogBuilder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if(removeActionInFile(name)) {
                        refreshItemList();
                    }
                }
            });
        }

        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        return dialogBuilder.create();
    }

    private boolean addActionInFile(String name, String unit, int color){
        if(!name.trim().isEmpty() && !unit.trim().isEmpty()){
            File f = Utils.getConfigFile();
            ArrayList<String> titles = new ArrayList<String>();
            try {
                BufferedReader buf = new BufferedReader(new FileReader(f));

                String tmp;
                while ((tmp = buf.readLine()) != null) {
                    int pos = tmp.indexOf(":");
                    titles.add(tmp.substring(0, pos));
                }
                buf.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            int index = titles.indexOf(name);
            if(index == -1){
                try {
                    FileOutputStream fos = new FileOutputStream(f, true);
                    String newLine = name + ":" + unit + ":" + String.valueOf(color)+"\n";
                    fos.write(newLine.getBytes());
                    fos.close();
                    return true;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "名字不能重复", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "名字和单位不能为空", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean modifyActionInFile(String name, String unit, int color){
        if(!name.trim().isEmpty() && !unit.trim().isEmpty()){
            File f = Utils.getConfigFile();
            ArrayList<String> titles = new ArrayList<String>();
            try {
                BufferedReader buf = new BufferedReader(new FileReader(f));

                String tmp;
                while ((tmp = buf.readLine()) != null) {
                    int pos = tmp.indexOf(":");
                    titles.add(tmp.substring(0, pos));
                }
                buf.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            int index = titles.indexOf(name);
            if(index != -1){
                try {
                    FileOutputStream fos = new FileOutputStream(f, true);
                    String newLine = name + ":" + unit + ":" + String.valueOf(color)+"\n";
                    fos.write(newLine.getBytes());
                    fos.close();
                    return true;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "名字不能重复", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "名字和单位不能为空", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean removeActionInFile(String name){
        try {
            File f = Utils.getConfigFile();
            BufferedReader buf = new BufferedReader(new FileReader(f));

            String tmp;
            Multimap<String, String> TBs = TreeMultimap.create();
            while ((tmp = buf.readLine()) != null) {
                int pos = tmp.indexOf(":");
                TBs.put(tmp.substring(0, pos), tmp.substring(pos + 1) + "\n");
            }

            ArrayList<String> titles = new ArrayList<String>(TBs.keys());
            ArrayList<String> bodys = new ArrayList<String>(TBs.values());
            buf.close();

            int index = titles.indexOf(name);
            if(index!= -1){
                titles.remove(index);
                bodys.remove(index);
                writeFile(f, titles, bodys);
                return true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void writeFile(File f, ArrayList<String> titles, ArrayList<String> bodys){
        try{
        FileOutputStream fos = new FileOutputStream(f, false);

        for (int i = 0; i < titles.size(); ++i) {
            try {
                String message = titles.get(i) + ":" + bodys.get(i);
                fos.write(message.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void refreshItemList(){
        ItemFragment itemFragment = (ItemFragment) getFragmentManager().findFragmentById(R.id.left_fragment);
        itemFragment.setList(Utils.initItemList(true));
        itemFragment.refresh();
    }

    private void setRightBackgroundByAction(final int action) {
        final View rightView = getFragmentManager().findFragmentById(R.id.right_fragment).getView();
        if (rightView != null) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            boolean backGroundEnable = sharedPref.getBoolean(getString(R.string.pref_key_pic_path_enable), false);
            if(backGroundEnable){
                try{
                    mTarget = new Target(){
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            rightView.setBackground(new BitmapDrawable(getResources(), bitmap));
                            rightView.getBackground().setAlpha(0x80);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            setDefaultBackground(rightView, action);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            setDefaultBackground(rightView, action);
                        }
                    };
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    Picasso.with(MainActivity.this).load(Utils.getPicPath()).resize(size.x, size.y).into(mTarget);

                }
                catch(Exception e){
                    e.printStackTrace();
                    setDefaultBackground(rightView, action);
                }
            }
            else
            {
                setDefaultBackground(rightView,action);
            }
        }
    }

    private void setDefaultBackground(View view, int action){
        view.setBackgroundResource(Utils.mBackgroundPics[action % Utils.mBackgroundPics.length]);
        view.getBackground().setAlpha(0x20);
    }
}
