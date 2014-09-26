package com.ezikche.babyschedule;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;


public class DetailActivity extends Activity implements ItemFragment.OnFragmentInteractionListener {
    private int[] mBackgroundPics;
    private int[] mColorList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mColorList =new int[]{Color.YELLOW, Color.MAGENTA, Color.CYAN};
        mBackgroundPics = new int[]{R.drawable.eat, R.drawable.poo,R.drawable.sleep};
        setContentView(R.layout.layout_large_detail);
        setTextViewByAct(0);
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String id, int position, View view)
    {
        int pos = position % mColorList.length;
        setTextViewByAct(pos);
        setRightBackgroundByAction(position);
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
            File inFile = getStorageFile(fileNames[position]);
            try {
                BufferedReader buf = new BufferedReader(new FileReader(inFile));
                int bufferSize = 10;
                CircularArrayList<String> rb = new CircularArrayList<String>(bufferSize);
                String tmp;
                while ((tmp = buf.readLine()) != null) {
                    try{
                        rb.add(tmp + "\n");
                    }
                    catch(IllegalStateException e){
                        rb.remove(0);
                        rb.add(tmp + "\n");
                    }
                }

                TextView rightView = (TextView) findViewById(R.id.textView);
                rightView.setText("");
                for (int i = rb.size()-1; i >= 0; --i)
                    rightView.append(rb.get(i));

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

    private File getStorageFile(String fileName) {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
    }
}
