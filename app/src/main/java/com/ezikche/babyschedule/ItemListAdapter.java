package com.ezikche.babyschedule;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.ezikche.babyschedule.R;

import java.util.List;
import java.util.Map;

/**
 * Created by ezikche on 9/22/2014.
 */
public class ItemListAdapter<T> extends ArrayAdapter<T> {
    private int[] colors = { Color.YELLOW, Color.MAGENTA,Color.CYAN };

    public ItemListAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
//        view.setBackgroundColor(colors[position % 3]);
        return view;
    }

}
