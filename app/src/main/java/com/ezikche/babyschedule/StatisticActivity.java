package com.ezikche.babyschedule;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;


public class StatisticActivity extends Activity {
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = new MultipleChart(this).execute(AdSize.BANNER.getHeightInPixels(this));
        if (view != null) {
            setContentView(view);
            setTitle(getResources().getText(R.string.title_activity_statistic));

            mAdView = new AdView(this);
            mAdView.setAdUnitId(getResources().getString(R.string.ad_unit_id));
            mAdView.setAdSize(AdSize.BANNER);
//        mAdView.setAdListener(new ToastAdListener(this));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT ,  RelativeLayout.LayoutParams.WRAP_CONTENT );
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            addContentView(mAdView, params);
            mAdView.loadAd(new AdRequest.Builder().build());
        }
        else {
            Toast.makeText(StatisticActivity.this, getResources().getText(R.string.no_enough_data), Toast.LENGTH_SHORT).show();
        }

        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.statistic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }
}
