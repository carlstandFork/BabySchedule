package com.ezikche.babyschedule;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by ezikche on 9/22/2014.
 */
public class ItemListAdapter<T> extends ArrayAdapter<T> {
    private int mPos = 0;

    public ItemListAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
    }

    public void setSelectedPos(int position){
        mPos = position;
    }

    public int getSelectedPos(){
        return mPos;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if(mPos == position){
            view.setBackgroundColor(Utils.colors[position % Utils.colors.length]);
        }
        else
            view.setBackgroundColor(Color.TRANSPARENT);
        return view;
    }

}
