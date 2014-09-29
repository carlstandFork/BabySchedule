package com.ezikche.babyschedule;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * Created by ezikche on 9/23/2014.
 */
public class ColorNumberPicker extends NumberPicker {
    public ColorNumberPicker(Context context)
    {
        super(context);
    }

    @Override
    public void addView(View child)
    {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params)
    {
        super.addView(child, index, params);
        updateView(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params)
    {
        super.addView(child, params);
        updateView(child);
    }

    public void updateView(View view)
    {
        if (view instanceof EditText)
        {
            ((EditText) view).setTextColor(Color.BLACK);
        }
    }
}
