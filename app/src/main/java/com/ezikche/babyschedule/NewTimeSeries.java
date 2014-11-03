package com.ezikche.babyschedule;

import org.achartengine.model.XYSeries;

import java.util.Date;

/**
 * Created by ezikche on 10/15/2014.
 */
public class NewTimeSeries extends XYSeries {

    public NewTimeSeries(String title, int scaleNumber) {
        super(title, scaleNumber);
    }

    public synchronized void add(Date x, double y) {
        super.add(x.getTime(), y);
    }
    public synchronized void addAnnotation(String annotation, Date x, double y) {
        super.addAnnotation(annotation, x.getTime(), y);
    }

}