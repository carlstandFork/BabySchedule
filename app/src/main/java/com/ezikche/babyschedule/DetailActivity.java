package com.ezikche.babyschedule;

import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
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
        getActionBar().setDisplayHomeAsUpEnabled(true);

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
            default:
                break;
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
                rightView.setAdapter(adapter);
//                adapter.notifyDataSetChanged();

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
