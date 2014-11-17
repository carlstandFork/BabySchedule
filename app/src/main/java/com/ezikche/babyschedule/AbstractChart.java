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

import android.graphics.Paint;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * An abstract class for the demo charts to extend. It contains some methods for
 * building datasets and renderers.
 */
public abstract class AbstractChart {

    public XYMultipleSeriesDataset buildDatasetByTime(String[] titles, List<Date[]> xValues,
                                                      List<double[]> yValues) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        addXYSeriesByTime(dataset, titles, xValues, yValues, 0);
        return dataset;
    }

    public void addXYSeriesByTime(XYMultipleSeriesDataset dataset, String[] titles, List<Date[]> xValues,
                            List<double[]> yValues, int scale) {
        int length = titles.length;
        for (int i = 0; i < length; i++) {
            NewTimeSeries series = new NewTimeSeries(titles[i],scale);
            Date[] xV = xValues.get(i);
            double[] yV = yValues.get(i);
            int seriesLength = xV.length;
            double lowestY = yV[0];
            double highestY = yV[0];
            Date highestX = xV[0];
            Date lowestX = xV[0];
            for (int k = 0; k < seriesLength; k++) {
                series.add(xV[k], yV[k]);
                if(yV[k] >= highestY) {
                    highestY = yV[k];
                    highestX = xV[k];
                }
                if(yV[k] < lowestY){
                    lowestY = yV[k];
                    lowestX = xV[k];
                }
            }
            String lowestYStr;
            if(lowestY % 1.0f != 0){
                lowestYStr=new DecimalFormat("0.0").format(lowestY);
            }else{
                lowestYStr=new DecimalFormat("0").format(lowestY);
            }
            String highestYStr;
            if(highestY % 1.0f != 0) {
                highestYStr = new DecimalFormat("0.0").format(highestY);
            }else{
                highestYStr=new DecimalFormat("0").format(highestY);
            }
            float shift = (float) (0.01f*scale*(highestY-lowestY));
            series.addAnnotation(lowestYStr,lowestX,lowestY-shift);
            series.addAnnotation(highestYStr,highestX,highestY+shift);
            dataset.addSeries(i,series);
        }
    }

    protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles, int topMargin) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer(colors.length);
        setRenderer(renderer, colors, styles, topMargin);
        return renderer;
    }

    protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors, PointStyle[] styles, int topMargin) {
//        renderer.setAxisTitleTextSize(30);
//        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(30);
        renderer.setLegendTextSize(30);
        renderer.setPointSize(5f);
        renderer.setMargins(new int[]{topMargin, 80, 50, 60});
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[i]);
            r.setPointStyle(styles[i]);
            r.setLineWidth(3f);
            r.setAnnotationsTextSize(30);
            r.setAnnotationsTextAlign(Paint.Align.RIGHT);
            r.setAnnotationsColor(colors[i]);
            renderer.addSeriesRenderer(i,r);
        }
    }

    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String xTitle,
                                    int axesColor,int labelsColor) {
//        renderer.setXTitle(xTitle);
        renderer.setYAxisMin(0);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
    }

}
