package com.ezikche.babyschedule;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by ezikche on 9/22/2014.
 */
public class ItemListAdapter<T> extends ArrayAdapter<T> {
    private int mPos = 0;
    private int mSize = 0;
    public ItemListAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
        mSize = objects.size();
    }

    public void setSelectedPos(int position){
        mPos = position;
    }

    public int getSelectedPos(){
        return mPos;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if(mPos == position && position < (mSize-1)){
            view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
            view.setBackgroundColor(Utils.colors[position % Utils.colors.length]);
        }else if((mSize-1) == position)
        {
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(params);
            view.setBackgroundResource(R.drawable.plus64);
        }
        else{
            view.setBackgroundColor(Color.TRANSPARENT);
        }
        return view;
    }

}
