package com.ezikche.babyschedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends Activity
        implements ItemFragment.OnFragmentInteractionListener{

    private int[] mColorList;

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
        setContentView(R.layout.layout_large);
        View rightview = getFragmentManager().findFragmentById(R.id.right_fragment).getView();
        rightview.setBackgroundColor(mColorList[mCurrentAct]);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onButtonNextPressed(View view)
    {
//        Toast.makeText(this,"button next on act" + mCurrentAct + "pressed", Toast.LENGTH_LONG).show();
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String[] actions = getResources().getStringArray(R.array.actions);
        String[] messages = getResources().getStringArray(R.array.dialogMsg);
        builder.setTitle(actions[mCurrentAct])
                .setMessage(messages[mCurrentAct]);
// 2. Chain together various setter methods to set the dialog characteristics
        final NumberPicker picker = new ColorNumberPicker(getApplicationContext());

        int NUMBER_OF_VALUES = 10;
        int PICKER_RANGE = 50;
        final String[] displayedValues  = new String[NUMBER_OF_VALUES];

        switch(mCurrentAct)
        {
            case 0://eat
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

                break;
            case 1://poo


                break;
            case 2://sleep
                break;
            default:
                break;
        }


        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
//                Toast.makeText(getApplicationContext(),"button OK pressed", Toast.LENGTH_LONG).show();
                switch(mCurrentAct)
                {
                    case 0:
                        DatePicker DP = (DatePicker)(MainActivity.this.findViewById(R.id.datePicker));
                        TimePicker TP = (TimePicker)(MainActivity.this.findViewById(R.id.timePicker));

                        String message = DP.getYear()+"."+String.valueOf(DP.getMonth()+1) +"."+ DP.getDayOfMonth() + " "+
                                TP.getCurrentHour() +"." +TP.getCurrentMinute() +
                                ": 宝宝喝了" + displayedValues[picker.getValue()] + "毫升奶\n";

                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        if(isExternalStorageWritable())
                        {
                            File eatFile = getStorageFile("eat.txt");
                            try {
                                FileOutputStream fos = new FileOutputStream(eatFile,true);
                                fos.write(message.getBytes());
                                fos.close();

                                Toast.makeText(MainActivity.this, "文件写入成功", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    default:
                        break;
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

// 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
//        dialog.setInverseBackgroundForced(false);
//        Window window = dialog.getWindow();
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.alpha = 0.8f;
//        window.setAttributes(lp);
        dialog.show();
    }

@Override
    public void onFragmentInteraction(String id, int position)
    {
//        Toast.makeText(this, id, Toast.LENGTH_LONG).show();
        mCurrentAct = position % mColorList.length;

        View rightview = getFragmentManager().findFragmentById(R.id.right_fragment).getView();

        rightview.setBackgroundColor(mColorList[mCurrentAct]);
//                EatFragment eatFragment = new EatFragment();
//                FragmentTransaction transaction= getFragmentManager().beginTransaction();
//                transaction.replace(R.id.right_fragment, eatFragment);
//                transaction.commit();

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getStorageFile(String fileName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

        return file;
    }
}
