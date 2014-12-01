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

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Multiple temperature demo chart.
 */
public class MultipleChart extends AbstractChart {
    Context mContext;
    public MultipleChart(Context context){
        mContext = context;
    }
    /**
     * Executes the chart demo.
     *
     * @return the built intent
     */
    public View execute(int topMargin) {
        List<Integer> listColor = Ints.asList(Utils.colors);
        listColor = Lists.reverse(listColor);
        int[] colors = Ints.toArray(listColor);

        PointStyle[] styles = new PointStyle[colors.length];
        for(int i =0; i<colors.length ;++i ) {
            styles[i] = PointStyle.POINT;
        }
        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles, topMargin);

        setChartSettings(renderer, mContext.getResources().getString(R.string.day), Color.BLACK, Color.BLACK);
        renderer.setXLabels(10);
        renderer.setYLabels(10);
        renderer.setShowGrid(true);
        renderer.setXLabelsAlign(Align.CENTER);
        renderer.setYLabelsAlign(Align.RIGHT);
        renderer.setZoomButtonsVisible(true);
//        renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
//        renderer.setZoomLimits(new double[] { -10, null, -10, null });
        renderer.setZoomRate(2.0f);
        renderer.setXLabelsColor(Color.DKGRAY);

        renderer.setYAxisAlign(Align.RIGHT, 1);
        renderer.setYLabelsAlign(Align.LEFT, 1);
        renderer.setYAxisAlign(Align.RIGHT, 2);
        renderer.setYLabelsAlign(Align.RIGHT, 2);
        renderer.setYAxisAlign(Align.LEFT, 3);
        renderer.setYLabelsAlign(Align.LEFT, 3);
        renderer.setYAxisAlign(Align.RIGHT, 4);
        renderer.setYLabelsAlign(Align.CENTER, 4);

        renderer.setMarginsColor(Color.WHITE);

        XYMultipleSeriesDataset dataset = getDataSet(mContext,colors.length);
        if(dataset==null)
            return null;

        while(dataset.getSeriesCount()<renderer.getSeriesRendererCount()) {
            renderer.removeSeriesRenderer(renderer.getSeriesRendererAt(renderer.getSeriesRendererCount()-1));
        }
        for (int i = 0; i < renderer.getSeriesRendererCount(); ++i) {
            renderer.setYLabelsColor(i, colors[renderer.getSeriesRendererCount() - 1 - i]);
        }

        double maxX = findMaxX(dataset);
        renderer.setXAxisMin(getOneWeekBefore((long) maxX));
        renderer.setXAxisMax(maxX);

        View view = ChartFactory.getTimeChartView(mContext, dataset, renderer, mContext.getResources().getString(R.string.monthAndDay));
        return view;
    }

    private double getOneWeekBefore(long data){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(data);
        c.add(Calendar.DAY_OF_YEAR, -7);
        return c.getTimeInMillis();
    }

    private double findMaxX(XYMultipleSeriesDataset dataset){
        double maxX = 0;
        for(int i = 0 ; i < dataset.getSeriesCount(); ++i){
            maxX = maxX>dataset.getSeriesAt(i).getMaxX()? maxX:dataset.getSeriesAt(i).getMaxX();
        }
        return maxX;
    }

    private XYMultipleSeriesDataset getDataSet(Context context, int colorsLength){
        List<Date[]> dates = new ArrayList<Date[]>();
        List<double[]> values = new ArrayList<double[]>();
        String[] actions = context.getResources().getStringArray(R.array.actions);
        String[] folderNames = context.getResources().getStringArray(R.array.folderName);
        String[] actionUnits = context.getResources().getStringArray(R.array.actions_units);
        XYMultipleSeriesDataset dataset = null;
        int SeriesNr = 0;
        for (int i = 0; i < colorsLength; ++i){
            Date[] date = Utils.getXValues(folderNames[i]);
            double[] yValue = Utils.getYValues(folderNames[i]);
            if ((date == null || date!=null && date.length < 2) ||
                    (yValue==null || yValue!=null && yValue.length < 2)) {
                continue;
            }
            dates.add(Utils.getXValues(folderNames[i]));
            values.add(Utils.getYValues(folderNames[i]));
//            renderer.setYTitle(actionUnits[i], i);

            if(dataset==null) {
                dataset = new XYMultipleSeriesDataset();
            }

            addXYSeriesByTime(dataset, new String[]{actions[i] + "(" + actionUnits[i] + ")"}, dates, values, SeriesNr++);
            dates.clear();
            values.clear();
        }
        return dataset;
    }


}
