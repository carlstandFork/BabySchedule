package com.ezikche.babyschedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
    private String[] mFileNames = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFileNames = getResources().getStringArray(R.array.fileName);
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

            File inFile = getLatestStorageFile(mFileNames[position]);
            getActionBar().setTitle(inFile.getName());
            try {
                BufferedReader buf = new BufferedReader(new FileReader(inFile));

                final ArrayList<String> titles = new ArrayList<String>();
                final ArrayList<String> bodys = new ArrayList<String>();
                String tmp;
                while ((tmp = buf.readLine()) != null) {
                    int pos = tmp.indexOf(":");
                    titles.add(tmp.substring(0, pos) + "\n");
                    bodys.add(tmp.substring(pos + 1) + "\n");
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
                            Toast.makeText(DetailActivity.this, "时间数据格式非法", Toast.LENGTH_SHORT).show();
                        }

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
                        int endPos = getLastDig(inContent);
                        float fValue = 0f;
                        try {
                            if (-1 != startPos && -1 != endPos) {
                                fValue = Float.parseFloat(inContent.substring(startPos, endPos + 1));
                            }
                        } catch (Exception e) {
                            Toast.makeText(DetailActivity.this, "数据格式非法", Toast.LENGTH_SHORT).show();
                        }
                        int valuePos = 0;
                        String[] displayedValues = new String[NUMBER_OF_VALUES];
                        for(int i=0; i<NUMBER_OF_VALUES; i++) {
                            float tmp = PICKER_RANGE * (i + 1);
                            if ( fValue == tmp)
                                valuePos = i;
                            displayedValues[i] = String.valueOf(tmp);
                        }

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
        String[] messages = getResources().getStringArray(R.array.dialogMsg);
        dialogBuilder.setTitle(actions[mCurrentAct]);
//                .setMessage(messages[mCurrentAct]);

        LayoutInflater inflater = DetailActivity.this.getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_change, null);


        final TimePicker tPicker =  (TimePicker)layout.findViewById(R.id.timePicker);
//        ColorNumberPicker nPicker = (ColorNumberPicker)layout.findViewById(R.id.colorNumberPicker);
        final NumberPicker nPicker = (NumberPicker) layout.findViewById(R.id.numberPicker);
        tPicker.setCurrentHour(hour);
        tPicker.setCurrentMinute(min);
        tPicker.setBackgroundColor(mColorList[mCurrentAct]);
        tPicker.setAlpha(0.5f);

        nPicker.setMinValue(0);
        nPicker.setMaxValue(displayedValues.length - 1);
        nPicker.setDisplayedValues(displayedValues);
        nPicker.setValue(valuePos);
        nPicker.setWrapSelectorWheel(false);
        nPicker.setBackgroundColor(mColorList[mCurrentAct]);
        nPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        nPicker.setAlpha(0.5f);

        dialogBuilder.setView(layout);

        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String title = String.format("%02d", tPicker.getCurrentHour()) + "." + String.format("%02d", tPicker.getCurrentMinute()) + "\n";
                int currentValuePos = nPicker.getValue();
                String body = "";
                switch (mCurrentAct) {
                    case 0: {
                        body = "宝宝已经喝了" + displayedValues[currentValuePos] + "毫升奶\n";
                    }
                    break;
                    case 1: {
                        body = " 宝宝已经拉了" + displayedValues[currentValuePos] + "次臭臭\n";
                    }
                    break;
                    case 2: {
                        body = " 宝宝已经睡了" + displayedValues[currentValuePos] + "小时觉觉\n";
                    }
                    break;
                    default:
                        break;
                }
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

        AlertDialog dialog = dialogBuilder.create();
        return dialog;
    }

    private boolean writeFile(ArrayList<String> titles, ArrayList<String> bodys) {
        if (isExternalStorageWritable()) {
            File outFile = getLatestStorageFile(mFileNames[mCurrentAct]);
            try {
                FileOutputStream fos = new FileOutputStream(outFile, false);

                for (int i = 0; i < titles.size(); ++i) {
                    String title = titles.get(i);
                    String message = title.substring(0, title.length() - 1) + ":" + bodys.get(i);
                    fos.write(message.getBytes());
                }
                fos.close();

                Toast.makeText(DetailActivity.this, "文件写入成功", Toast.LENGTH_SHORT).show();
                setTextViewByAct(mCurrentAct);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(DetailActivity.this, "文件系统不可写", Toast.LENGTH_SHORT).show();
        }
        return false;
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
    }

    private int getLastDig(String string) {
        int pos = string.length()-1;
        for(;pos>=0;--pos)
        {
            if(string.charAt(pos)>=48 && string.charAt(pos)<=57)
                return pos;
        }
        return -1;
    }

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
