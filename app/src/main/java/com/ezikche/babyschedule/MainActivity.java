package com.ezikche.babyschedule;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity
        implements ItemFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        if ( size.x < size.y) {
        setContentView(R.layout.layout_large);
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

    @Override
    public void onFragmentInteraction(String id, int position)
    {
//        Toast.makeText(this, id, Toast.LENGTH_LONG).show();

        View leftview = getFragmentManager().findFragmentById(R.id.left_fragment).getView();
        View rightview = getFragmentManager().findFragmentById(R.id.right_fragment).getView();

        switch (position)
        {
            case 0: {
                leftview.setBackgroundColor(Color.YELLOW);
//                EatFragment eatFragment = new EatFragment();
//                FragmentTransaction transaction= getFragmentManager().beginTransaction();
//                transaction.replace(R.id.right_fragment, eatFragment);
//                transaction.commit();
                rightview.setBackgroundColor(Color.YELLOW);
                break;
            }
            case 1: {
                leftview.setBackgroundColor(Color.MAGENTA);
                rightview.setBackgroundColor(Color.MAGENTA);
                break;
            }
            case 2: {
                leftview.setBackgroundColor(Color.CYAN);
                rightview.setBackgroundColor(Color.CYAN);
                break;
            }
            default:
                leftview.setBackgroundColor(Color.BLACK);
                rightview.setBackgroundColor(Color.BLACK);
                break;
        }
    }
}
