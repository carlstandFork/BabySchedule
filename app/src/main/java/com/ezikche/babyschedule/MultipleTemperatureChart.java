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
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
    List<double[]> x = new ArrayList<double[]>();
    for (int i = 0; i < titles.length; i++) {
      x.add(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 });
    }
    List<double[]> values = new ArrayList<double[]>();
    values.add(new double[] { 12.3, 12.5, 13.8, 16.8, 20.4, 24.4, 26.4, 26.1, 23.6, 20.3, 17.2,
        13.9 });
    int[] colors = new int[] { Color.BLUE, Color.RED };
    PointStyle[] styles = new PointStyle[] { PointStyle.POINT, PointStyle.POINT };
    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer(2);
    setRenderer(renderer, colors, styles);
    int length = renderer.getSeriesRendererCount();
    for (int i = 0; i < length; i++) {
      XYSeriesRenderer r = (XYSeriesRenderer) renderer.getSeriesRendererAt(i);
      r.setLineWidth(3f);
    }
    setChartSettings(renderer, "统计数据", "天", "ML", 0.5, 12.5, 0, 32,
        Color.BLACK, Color.BLACK);
    renderer.setXLabels(12);
    renderer.setYLabels(10);
    renderer.setShowGrid(true);
    renderer.setXLabelsAlign(Align.RIGHT);
    renderer.setYLabelsAlign(Align.RIGHT);
    renderer.setZoomButtonsVisible(true);
    renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
    renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });
    renderer.setZoomRate(1.05f);
    renderer.setLabelsColor(Color.BLACK);
    renderer.setXLabelsColor(Color.CYAN);
    renderer.setYLabelsColor(0, colors[0]);
    renderer.setYLabelsColor(1, colors[1]);

    renderer.setYTitle("次", 1);
    renderer.setYAxisAlign(Align.RIGHT, 1);
    renderer.setYLabelsAlign(Align.LEFT, 1);
    renderer.setMarginsColor(Color.WHITE);
    XYMultipleSeriesDataset dataset = buildDataset(titles, x, values);
    values.clear();
    values.add(new double[] { 4.3, 4.9, 5.9, 8.8, 10.8, 11.9, 13.6, 12.8, 11.4, 9.5, 7.5, 5.5 });
    addXYSeries(dataset, new String[] { "臭臭次数" }, x, values, 1);

    View view = ChartFactory.getCubeLineChartView(context, dataset, renderer, 0.3f);
    return view;
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
