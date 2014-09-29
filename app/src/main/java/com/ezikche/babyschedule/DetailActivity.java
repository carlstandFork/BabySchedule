package com.ezikche.babyschedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class DetailActivity extends Activity implements ItemFragment.OnFragmentInteractionListener {
    private int[] mBackgroundPics = new int[]{R.drawable.eat, R.drawable.poo,R.drawable.sleep};
    private int[] mColorList = new int[]{Color.YELLOW, Color.MAGENTA, Color.CYAN};
    private int mCurrentAct = 0;
    private File[] mSortedFiles = null;
    private int mCurrentFileIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_large_detail);
        getActionBar().setDisplayHomeAsUpEnabled(true);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId())
        {
            case R.id.action_settings:
                return true;
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
            case R.id.action_next:
                ++mCurrentFileIndex;
                if(mCurrentFileIndex>mSortedFiles.length-1)
                    mCurrentFileIndex = 0;
                setTextViewByAct(mCurrentAct);
                setRightBackgroundByAction(mCurrentAct);
                break;
            case R.id.action_prev:
                --mCurrentFileIndex;
                if(mCurrentFileIndex<0)
                    mCurrentFileIndex = mSortedFiles.length -1;
                setTextViewByAct(mCurrentAct);
                setRightBackgroundByAction(mCurrentAct);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String id, int position, View view)
    {
        int pos = position % mColorList.length;
        if(mCurrentAct != pos) {
            mCurrentFileIndex = 0;
            mCurrentAct = pos;
        }
        setTextViewByAct(mCurrentAct);
        setRightBackgroundByAction(mCurrentAct);
    }

    private void setRightBackgroundByAction(int action){
        View rightView = getFragmentManager().findFragmentById(R.id.right_fragment).getView();
        if (rightView != null) {
            rightView.setBackgroundResource(mBackgroundPics[action]);
            rightView.getBackground().setAlpha(0x20);
        }
    }

    private void setTextViewByAct(int position) {
        if (isExternalStorageReadable()) {
            String[] fileNames = getResources().getStringArray(R.array.fileName);
            File inFile = getLatestStorageFile(fileNames[position]);
            getActionBar().setTitle(inFile.getName());
            try {
                BufferedReader buf = new BufferedReader(new FileReader(inFile));
                int bufferSize = 10;
                final CircularArrayList<String> titles = new CircularArrayList<String>(bufferSize);
                final CircularArrayList<String> bodys = new CircularArrayList<String>(bufferSize);

                String tmp;
                while ((tmp = buf.readLine()) != null) {
                    int pos = tmp.indexOf(":");
                    try{
                        titles.add(tmp.substring(0,pos) + "\n");
                        bodys.add(tmp.substring(pos+1) + "\n");
                    }
                    catch(IllegalStateException e){
                        titles.remove(0);
                        bodys.remove(0);
                        titles.add(tmp.substring(0,pos) + "\n");
                        bodys.add(tmp.substring(pos+1) + "\n");
                    }
                }

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

                ListView rightView = (ListView) findViewById(R.id.listView);
                rightView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                        String inTime = ((TextView) view.findViewById(android.R.id.text1)).getText().toString();
                        String inContent = ((TextView) view.findViewById(android.R.id.text2)).getText().toString();
                        int separatorPos = inTime.indexOf(".");
                        int hour = Integer.parseInt(inTime.substring(0, separatorPos));
                        int min = Integer.parseInt(inTime.substring(separatorPos + 1, separatorPos + 3));

                        Toast.makeText(DetailActivity.this, String.valueOf(hour) + ":" + String.valueOf(min), Toast.LENGTH_SHORT).show();

                       //start Dialog to modify clicked line
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DetailActivity.this);
                        dialogBuilder.setTitle("test")
                                .setMessage("test");

                        TimePicker tPicker = new TimePicker(DetailActivity.this);
                        ColorNumberPicker nPicker = new ColorNumberPicker(DetailActivity.this);
                        tPicker.setCurrentHour(hour);
                        tPicker.setCurrentMinute(min);

                        int NUMBER_OF_VALUES = 0;
                        float PICKER_RANGE = 0;
                        switch(mCurrentAct)
                        {
                            case 0://eat
                                NUMBER_OF_VALUES = 25;
                                PICKER_RANGE = 20;
                                break;
                            case 1://poo
                                NUMBER_OF_VALUES = 10;
                                PICKER_RANGE = 1;
                                break;
                            case 2://sleep
                                NUMBER_OF_VALUES = 20;
                                PICKER_RANGE = 0.5f;
                                break;
                            default:
                                break;
                        }

                        int startPos = getFirstDig(inContent);
                        int endPos = getlastDig(inContent);
                        float fValue = 0f;

                        if(-1 != startPos && -1 != endPos) {
                            fValue = Float.parseFloat(inContent.substring(startPos, endPos));
                        }

                        int valuePos = 0;
                        String[] displayedValues = new String[NUMBER_OF_VALUES];
                        for(int i=0; i<NUMBER_OF_VALUES; i++) {
                            float tmp = PICKER_RANGE * (i + 1);
                            if ( fValue == tmp)
                                valuePos = i;
                            displayedValues[i] = String.valueOf(tmp);
                        }

                        nPicker.setMinValue(0);
                        nPicker.setMaxValue(NUMBER_OF_VALUES - 1);
                        nPicker.setDisplayedValues(displayedValues);
                        nPicker.setValue(valuePos);
                        nPicker.setWrapSelectorWheel(false);
                        nPicker.setBackgroundColor(mColorList[mCurrentAct]);
                        nPicker.setAlpha(0.5f);
                        nPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

                        dialogBuilder.setView(tPicker);
                        dialogBuilder.setView(nPicker);

                        AlertDialog dialog = dialogBuilder.create();
                        dialog.show();
                    }
                });
                rightView.setAdapter(adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private int getFirstDig(String string){
        int pos = 0;
        for(;pos<string.length();++pos)
        {
            if(string.charAt(pos)>=48 && string.charAt(pos)<=57)
                return pos;
        }
      return -1;
    };

    private int getlastDig(String string){
        int pos = string.length()-1;
        for(;pos>=0;--pos)
        {
            if(string.charAt(pos)>=48 && string.charAt(pos)<=57)
                return pos;
        }
        return -1;
    };
    private File getLatestStorageFile(String dir) {
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+dir);
        if(f.exists()) {
            mSortedFiles = f.listFiles();
            if(null != mSortedFiles)
            {
                List<File> files = Arrays.asList(mSortedFiles);
                Collections.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        if (o1.isDirectory() && o2.isFile())
                            return 11;
                        if (o1.isFile() && o2.isDirectory())
                            return -1;
                        return o2.getName().compareTo(o1.getName());
                    }
                });
                return mSortedFiles[mCurrentFileIndex];
            }
        }
        return null;
    }
}
