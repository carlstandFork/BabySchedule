/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ezikche.babyschedule;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.View;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Multiple temperature demo chart.
 */
public class MultipleTemperatureChart extends AbstractDemoChart {

    /**
     * Executes the chart demo.
     *
     * @param context the context
     * @return the built intent
     */
    public View execute(Context context, int topMargin) {
        int[] colors = new int[]{Color.rgb(0x9F, 0x9F, 0x5F), Color.MAGENTA, Color.BLUE};
        PointStyle[] styles = new PointStyle[colors.length];
        for(int i =0; i<colors.length ;++i ) {
            styles[i] = PointStyle.POINT;
        }
        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles, topMargin);
        String[] actionUnits = context.getResources().getStringArray(R.array.actions_units);

        setChartSettings(renderer, "天", Color.BLACK, Color.BLACK);
        renderer.setXLabels(10);
        renderer.setYLabels(10);
        renderer.setShowGrid(true);
        renderer.setXLabelsAlign(Align.CENTER);
        renderer.setYLabelsAlign(Align.RIGHT);
        renderer.setZoomButtonsVisible(true);
//        renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
//        renderer.setZoomLimits(new double[] { -10, null, -10, null });
        renderer.setZoomRate(2.0f);
        renderer.setXLabelsColor(Color.GREEN);
        for (int i = 0; i < colors.length; ++i) {
            renderer.setYLabelsColor(i, colors[colors.length - 1 - i]);
        }
        renderer.setYAxisAlign(Align.RIGHT, 1);
        renderer.setYLabelsAlign(Align.LEFT, 1);
        renderer.setYAxisAlign(Align.RIGHT, 2);
        renderer.setYLabelsAlign(Align.RIGHT, 2);

        renderer.setMarginsColor(Color.WHITE);

        List<Date[]> date = new ArrayList<Date[]>();
        List<double[]> values = new ArrayList<double[]>();
        String[] actions = context.getResources().getStringArray(R.array.actions);
        String[] fileNames = context.getResources().getStringArray(R.array.fileName);

        XYMultipleSeriesDataset dataset = null;
        for (int i = 0; i < colors.length; ++i){
            date.add(getXValues(fileNames[i]));
            if (date.get(0).length < 2) {
                return null;
            }
            values.add(getYValues(fileNames[i]));
//            renderer.setYTitle(actionUnits[i], i);

            if(dataset==null) {
                dataset = new XYMultipleSeriesDataset();
            }

            addXYSeriesByTime(dataset, new String[]{actions[i] + "(" + actionUnits[i] + ")"}, date, values, i);
            date.clear();
            values.clear();
        }

        View view = ChartFactory.getTimeChartView(context, dataset, renderer, "MM-dd");
        return view;
    }

    private Date[] getXValues(String action){
        List<File> fList = Utils.getLatestStorageFile(action);
        Date[] dates = new Date[fList.size()];
        for(int i=0; i<fList.size();++i)
        {
            try {
                Date fDate = new SimpleDateFormat("yyyy.MM.dd").parse(fList.get(i).getName());
                dates[i] = fDate;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dates;
    }

    private double[] getYValues(String action){
        List<File> fList = Utils.getLatestStorageFile(action);
        double[] values = new double[fList.size()];
        for(int i=0; i<fList.size();++i)
        {
            values[i] = getSumValuesFromFile(fList.get(i));
        }
        return values;
    }

    private double getSumValuesFromFile(File file){
        BufferedReader buf = null;
        try {
            buf = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        double sum = 0;
        final ArrayList<String> bodys = new ArrayList<String>();
        String tmp;
        try {
            while ((tmp = buf.readLine()) != null) {
                int pos = tmp.indexOf(":");
                bodys.add(tmp.substring(pos + 1));
            }

            for(String line : bodys){
                sum = sum + Utils.getDigValue(line);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return sum;
    }


}
