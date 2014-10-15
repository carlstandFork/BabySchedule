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
import android.os.Environment;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
    public View execute(Context context) {
        String[] titles = new String[] { "喝奶量" };

        int[] colors = new int[] {Color.BLUE, Color.RED};
        PointStyle[] styles = new PointStyle[] { PointStyle.POINT, PointStyle.POINT};
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer(2);
        setRenderer(renderer, colors, styles);

        setChartSettings(renderer, "统计数据", "天", "ML", 0, 1000, 0, 1000,
                Color.BLACK, Color.BLACK);
//        renderer.setXLabels(12);
//        renderer.setYLabels(10);
        renderer.setShowGrid(true);
        renderer.setXLabelsAlign(Align.RIGHT);
        renderer.setYLabelsAlign(Align.RIGHT);
        renderer.setZoomButtonsVisible(true);
//        renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
//        renderer.setZoomLimits(new double[] { -10, null, -10, null });
        renderer.setZoomRate(1.05f);
        renderer.setLabelsColor(Color.BLACK);
        renderer.setXLabelsColor(Color.CYAN);
        renderer.setYLabelsColor(0, colors[0]);
        renderer.setYLabelsColor(1, colors[1]);

        renderer.setYTitle("次", 1);
        renderer.setYAxisAlign(Align.RIGHT, 1);
        renderer.setYLabelsAlign(Align.LEFT, 1);

        renderer.setMarginsColor(Color.WHITE);

        List<Date[]> date = new ArrayList<Date[]>();
        date.add(getXValues("eat"));
        List<double[]> values = new ArrayList<double[]>();
        values.add(getYValues("eat"));
        XYMultipleSeriesDataset dataset = buildDatasetByTime(titles, date, values, 0);

        date.clear();
        date.add(getXValues("poo"));
        values.clear();
        values.add(getYValues("poo"));
        addXYSeriesByTime(dataset, new String[] { "臭臭次数" }, date, values, 1);

        View view = ChartFactory.getTimeChartView(context, dataset, renderer, "yyyy-MM-dd");
        return view;
    }

    private Date[] getXValues(String action){
        List<File> fList = getLatestStorageFile(action);
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
        List<File> fList = getLatestStorageFile(action);
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

    private List<File> getLatestStorageFile(String dir) {
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+File.separator+dir);
        if(f.exists()) {
            File[] sortedFiles = f.listFiles();
            if(null != sortedFiles)
            {
                List<File> files = Arrays.asList(sortedFiles);
                Collections.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        if (o1.isDirectory() && o2.isFile())
                            return -1;
                        if (o1.isFile() && o2.isDirectory())
                            return -1;
                        return o2.getName().compareTo(o1.getName());
                    }
                });
                return files;
            }
        }
        return null;
    }
}
