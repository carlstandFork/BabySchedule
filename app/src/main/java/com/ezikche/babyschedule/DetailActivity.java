package com.ezikche.babyschedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GestureDetectorCompat;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//import org.apache.commons;

public class DetailActivity extends Activity implements ItemFragment.OnFragmentInteractionListener,
        GestureDetector.OnGestureListener{
    private AdView mAdView;
    private int mCurrentAct = Utils.EAT;
    private File[] mSortedFiles = null;
    private int mCurrentFileIndex = 0;
    private String[] mFolderNames = null;

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private Target mTarget = null;

    private GestureDetectorCompat mDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFolderNames = getResources().getStringArray(R.array.folderName);
        setContentView(R.layout.layout_large_detail);

        mAdView = (AdView) findViewById(R.id.adView);
//        mAdView.setAdListener(new ToastAdListener(this));
        mAdView.loadAd(new AdRequest.Builder().build());

        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDetector = new GestureDetectorCompat(this,this);
        setTextViewByAct(mCurrentAct);
        setRightBackgroundByAction(mCurrentAct);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        boolean result = false;
        try {
            float diffY = event2.getY() - event1.getY();
            float diffX = event2.getX() - event1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        showPrevRecord();
                    } else {
                        showNextRecord();
                    }
                }
                result = true;
            }
            else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
//                    onSwipeBottom();
                } else {
//                    onSwipeTop();
                }
            }
            result = true;

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }

    @Override
    public void onLongPress(MotionEvent event) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId())
        {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(this, upIntent);
                }
                break;
            case R.id.action_prev:
                showPrevRecord();
                break;
            case R.id.action_next:
                showNextRecord();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPrevRecord(){
        if(mSortedFiles!=null && mSortedFiles.length>0){
            ++mCurrentFileIndex;
            if(mCurrentFileIndex>mSortedFiles.length-1)
                mCurrentFileIndex = 0;
            setTextViewByAct(mCurrentAct);
            setRightBackgroundByAction(mCurrentAct);
        }
    }

    private void showNextRecord(){
        if(mSortedFiles!=null && mSortedFiles.length>0) {
            --mCurrentFileIndex;
            if (mCurrentFileIndex < 0)
                mCurrentFileIndex = mSortedFiles.length - 1;
            setTextViewByAct(mCurrentAct);
            setRightBackgroundByAction(mCurrentAct);
        }
    }
    @Override
    public void onFragmentInteraction(String id, int position, View view, boolean longPress)
    {
        int pos = position % Utils.colors.length;
        if (mCurrentAct != pos) {
            mCurrentFileIndex = 0;
            mCurrentAct = pos;
        }
        setTextViewByAct(mCurrentAct);
        setRightBackgroundByAction(mCurrentAct);
    }

    private void setDefaultBackground(View view, int action){
        view.setBackgroundResource(Utils.mBackgroundPics[action % Utils.mBackgroundPics.length]);
        view.getBackground().setAlpha(0x20);
    }

    private void setRightBackgroundByAction(final int action){
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
                    Picasso.with(DetailActivity.this).load(Utils.getPicPath()).resize(size.x, size.y).into(mTarget);

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

    private void setTextViewByAct(int position) {
        if (Utils.isExternalStorageReadable()) {
            ListView rightView = (ListView) findViewById(R.id.listView);
            File inFile = getLatestStorageFile(mFolderNames[position]);
            try {
                String[] actionUnits = getResources().getStringArray(R.array.actions_units);
                String value = String.valueOf((Utils.getYValue(mFolderNames[position], inFile)));
                getActionBar().setTitle(inFile.getName() + " " + value + actionUnits[position]);
            } catch (Exception e) {
                e.printStackTrace();
                getActionBar().setTitle("");
                ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, new ArrayList<String>());
                rightView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            try {
                BufferedReader buf = new BufferedReader(new FileReader(inFile));

                String tmp;
                Multimap<String, String> TBs = TreeMultimap.create();
                while ((tmp = buf.readLine()) != null) {
                    int pos = tmp.indexOf(":");
                    TBs.put(tmp.substring(0, pos) + "\n", tmp.substring(pos + 1) + "\n");
                }

                final ArrayList<String> titles = new ArrayList<String>(TBs.keys());
                final ArrayList<String> bodys = new ArrayList<String>(TBs.values());
                // avoid to be covered with ads
                titles.add("");
                bodys.add("");


                ArrayAdapter adapter = new ArrayAdapter(this, R.layout.simple_list_item_small_title, android.R.id.text1, titles) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                        TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                        text1.setText(titles.get(position));
                        text2.setText(bodys.get(position));
                        return view;
                    }
                };

                rightView.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        ((DetailActivity)v.getContext()).onTouchEvent(event);
                        return false;
                    }
                });

                rightView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String inTime = ((TextView) view.findViewById(android.R.id.text1)).getText().toString();
                        String inContent = ((TextView) view.findViewById(android.R.id.text2)).getText().toString();
                        int spacePos = inTime.indexOf(" ");
                        int separatorPos = inTime.lastIndexOf(".");
                        int hour = 0, min = 0;
                        try {
                            if (-1 != spacePos)
                                hour = Integer.parseInt(inTime.substring(spacePos + 1, separatorPos));
                            else
                                hour = Integer.parseInt(inTime.substring(0, separatorPos));

                            min = Integer.parseInt(inTime.substring(separatorPos + 1, separatorPos + 3));
                        } catch (Exception e) {
                            Toast.makeText(DetailActivity.this, getResources().getString(R.string.illegal_data_format), Toast.LENGTH_SHORT).show();
                        }

                        String[] displayedValues = Utils.getDisplayValuesByAct(mCurrentAct);

                        double oldValue = 0;
                        try {
                            oldValue = Utils.getDigValue(inContent);
                        } catch (Exception e) {
                            Toast.makeText(DetailActivity.this, getResources().getString(R.string.illegal_data_format), Toast.LENGTH_SHORT).show();
                        }

                        int valuePos = Arrays.asList(displayedValues).indexOf(String.valueOf(oldValue));

                        getDialog(hour, min, displayedValues, valuePos, titles, bodys, position).show();
                    }
                });
                rightView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private AlertDialog getDialog(final int hour, final int min, final String[] displayedValues, int valuePos,
                                  final ArrayList<String> titles, final ArrayList<String> bodys, final int itemPos) {
        //start Dialog to modify clicked line
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DetailActivity.this);
        String[] actions = getResources().getStringArray(R.array.actions);
        dialogBuilder.setTitle(actions[mCurrentAct]);
//                .setMessage(messages[mCurrentAct]);

        LayoutInflater inflater = DetailActivity.this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_change, null);


        final TimePicker tPicker =  (TimePicker)layout.findViewById(R.id.timePicker);
//        ColorNumberPicker nPicker = (ColorNumberPicker)layout.findViewById(R.id.colorNumberPicker);
        final NumberPicker nPicker = (NumberPicker) layout.findViewById(R.id.numberPicker);
        tPicker.setCurrentHour(hour);
        tPicker.setCurrentMinute(min);
        tPicker.setBackgroundColor(Utils.colors[mCurrentAct]);
        tPicker.setAlpha(0.5f);

        nPicker.setMinValue(0);
        nPicker.setMaxValue(displayedValues.length - 1);
        nPicker.setDisplayedValues(displayedValues);
        nPicker.setValue(valuePos);
        nPicker.setWrapSelectorWheel(false);
        nPicker.setBackgroundColor(Utils.colors[mCurrentAct]);
        nPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        nPicker.setAlpha(0.5f);

        dialogBuilder.setView(layout);

        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String title = String.format("%02d", tPicker.getCurrentHour()) + "." + String.format("%02d", tPicker.getCurrentMinute()) + "\n";
                int currentValuePos = nPicker.getValue();
                String body = Utils.getMessageBodyByAct(mCurrentAct, displayedValues, currentValuePos);
                titles.set(itemPos, title);
                bodys.set(itemPos, body);
                writeFile(titles, bodys);
            }
        });
        dialogBuilder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                titles.remove(itemPos);
                bodys.remove(itemPos);
                writeFile(titles, bodys);
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        return dialogBuilder.create();
    }

    private boolean writeFile(ArrayList<String> titles, ArrayList<String> bodys) {
        if (Utils.isExternalStorageWritable()) {
            File outFile = getLatestStorageFile(mFolderNames[mCurrentAct]);
            if(titles.size() == 1 && bodys.size() == 1) {
                try {
                    outFile.delete();
                    if(mSortedFiles!=null && mSortedFiles.length>0){
                        ++mCurrentFileIndex;
                        if(mCurrentFileIndex>mSortedFiles.length-1)
                            mCurrentFileIndex = 0;
                        setTextViewByAct(mCurrentAct);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
            try {
                FileOutputStream fos = new FileOutputStream(outFile, false);

                for (int i = 0; i < titles.size(); ++i) {
                    try{
                        String title = titles.get(i);
                        String message = title.substring(0, title.length() - 1) + ":" + bodys.get(i);
                        fos.write(message.getBytes());
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
                fos.close();

                Toast.makeText(DetailActivity.this, getResources().getString(R.string.write_file_succeed), Toast.LENGTH_SHORT).show();
                setTextViewByAct(mCurrentAct);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(DetailActivity.this, getResources().getString(R.string.file_not_writable), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private File getLatestStorageFile(String dir) {
        File f = new File(getPath()+File.separator+dir);
        if(f.exists()) {
            mSortedFiles = f.listFiles();
            if(null != mSortedFiles)
            {
                List<File> files = Arrays.asList(mSortedFiles);
                Collections.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        if (o1.isDirectory() && o2.isFile())
                            return -1;
                        if (o1.isFile() && o2.isDirectory())
                            return -1;
                        return o2.getName().compareTo(o1.getName());
                    }
                });
                try{
                    File retFile = files.get(mCurrentFileIndex);
                    return retFile;
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    private String getPath(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String path = sharedPref.getString(getString(R.string.pref_key_store_path), Utils.defaultPath);
        return path;
    }
}
