package com.ezikche.babyschedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;


public class MainActivity extends Activity
        implements ItemFragment.OnFragmentInteractionListener{

    private int[] mColorList;
    private int[] mBackgroundPics;
    private int mCurrentAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentAct = 0;
//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        if ( size.x < size.y) {
        mColorList =new int[]{Color.YELLOW, Color.MAGENTA, Color.CYAN};
        mBackgroundPics = new int[]{R.drawable.eat, R.drawable.poo,R.drawable.sleep};
        setContentView(R.layout.layout_large);

        ItemFragment itemFragment = (ItemFragment)getFragmentManager().findFragmentById(R.id.left_fragment);
        itemFragment.setSelectedItem(mCurrentAct);
        setRightBackgroundByAction(mCurrentAct);

//        ItemFragment itemfragment = new ItemFragment();
//        getFragmentManager().beginTransaction().add(R.id.left_fragment, itemfragment).commit();
//        rightFragment rightfragment = new rightFragment();
//        getFragmentManager().beginTransaction().add(R.id.right_fragment, rightfragment).commit();
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
        switch(id){
            case R.id.action_settings:
                Toast.makeText(this,"设定功能还没做好 :P", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_statistic:
                Toast.makeText(this,"统计功能仍在拼命(>_<)开发中", Toast.LENGTH_SHORT).show();
                if(isExternalStorageReadable()){
                    String[] fileNames = getResources().getStringArray(R.array.fileName);
                    File eatFile = getStorageFile(fileNames[0]);

                }
                else
                {
                    Toast.makeText(this,"文件系统不可读",Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_detail:

                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onButtonNextPressed(View view)
    {
//        Toast.makeText(this,"button next on act" + mCurrentAct + "pressed", Toast.LENGTH_LONG).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String[] actions = getResources().getStringArray(R.array.actions);
        String[] messages = getResources().getStringArray(R.array.dialogMsg);
        builder.setTitle(actions[mCurrentAct])
                .setMessage(messages[mCurrentAct]);

        final NumberPicker picker = new ColorNumberPicker(getApplicationContext());
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

        final String[] displayedValues = new String[NUMBER_OF_VALUES];
        for(int i=0; i<NUMBER_OF_VALUES; i++)
            displayedValues[i] = String.valueOf(PICKER_RANGE * (i+1));

        picker.setMinValue(0);
        picker.setMaxValue(NUMBER_OF_VALUES - 1);
        picker.setDisplayedValues(displayedValues);
        picker.setWrapSelectorWheel(false);
        picker.setBackgroundColor(mColorList[mCurrentAct]);
        picker.setAlpha(0.5f);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        builder.setView(picker);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DatePicker DP = (DatePicker)(MainActivity.this.findViewById(R.id.datePicker));
                TimePicker TP = (TimePicker)(MainActivity.this.findViewById(R.id.timePicker));
                String dateAndTime = DP.getYear()+"."+String.valueOf(DP.getMonth()+1) +"."+ DP.getDayOfMonth() + " "+
                        TP.getCurrentHour() +"." +TP.getCurrentMinute();
                String message ="";
                switch(mCurrentAct)
                {
                    case 0: {
                        message = dateAndTime + ": 宝宝已经喝了" + displayedValues[picker.getValue()] + "毫升奶\n";
                    }   break;
                    case 1: {
                        message = dateAndTime + ": 宝宝已经拉了" + displayedValues[picker.getValue()] + "次臭臭\n";
                    }   break;
                    case 2: {
                        message = dateAndTime + ": 宝宝已经睡了" + displayedValues[picker.getValue()] + "小时觉觉\n";
                    }   break;
                    default:
                        break;
                }

                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
//              save to the file
                if (isExternalStorageWritable()) {
                    String[] fileNames = getResources().getStringArray(R.array.fileName);
                    File outFile = getStorageFile(fileNames[mCurrentAct]);
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
    public void onFragmentInteraction(String id, int position, View view)
    {
        mCurrentAct = position % mColorList.length;
        view.setBackgroundColor(mColorList[mCurrentAct]);

        setRightBackgroundByAction(mCurrentAct);
//        rightView.setBackgroundColor(mColorList[mCurrentAct]);
//                EatFragment eatFragment = new EatFragment();
//                FragmentTransaction transaction= getFragmentManager().beginTransaction();
//                transaction.replace(R.id.right_fragment, eatFragment);
//                transaction.commit();

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

    private File getStorageFile(String fileName) {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
    }

    private void setRightBackgroundByAction(int action){
        View rightView = getFragmentManager().findFragmentById(R.id.right_fragment).getView();
        if (rightView != null) {
            rightView.setBackgroundResource(mBackgroundPics[action]);
            rightView.getBackground().setAlpha(0x20);
        }
    }
}
