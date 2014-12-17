package com.ezikche.babyschedule;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by ezikche on 9/30/2014.
 */
public class Utils extends Application{

    public static final int EAT = 0;
    public static final int POO = 1;
    public static final int SLEEP = 2;
    public static final int WEIGHT = 3;
    public static final int HEIGHT = 4;
    public static final int TEMPERATURE = 5;

    public static final int EAT_DEFAULT = 9;
    public static final int WEIGHT_DEFAULT = 60;
    public static final int HEIGHT_DEFAULT = 40;
    public static final int TEMPERATURE_DEFAULT = 20;

    public static String defaultPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

    public static final int[] colors = { Color.rgb(0xCD, 0xCD, 0x00), Color.rgb(0xF0, 0x80, 0x80),Color.rgb(0x7C,0xCD,0x7C),Color.rgb(0xEE,0x30,0xA7), Color.rgb(0x46,0x82,0xB4) };
    public static final int[] mBackgroundPics = new int[]{R.drawable.eat, R.drawable.poo,R.drawable.sleep};

    private static Utils mUtils = null;

    @Override
    public void onCreate(){
        super.onCreate();
        mUtils = this;
    }

    public static String getMessageBodyByAct(int act, String[] displayedValues, int valuePos) {
        String body = "";
        String[] acts = mUtils.getApplicationContext().getResources().getStringArray(R.array.actions_acts);
        String[] units = mUtils.getApplicationContext().getResources().getStringArray(R.array.actions_units);
        try{
            body =  acts[act] + displayedValues[valuePos] + units[act] + "\n";
        }catch(Exception e){
            e.printStackTrace();
        }
        return body;
    }

    public static String[] getDisplayValuesByAct(int act) {
        int NUMBER_OF_VALUES = 0;
        float PICKER_RANGE = 0;
        float START_VALUE = 0;
        switch (act) {
            case EAT:
                NUMBER_OF_VALUES = 30;
                PICKER_RANGE = 10;
                break;
            case POO:
                NUMBER_OF_VALUES = 10;
                PICKER_RANGE = 0.5f;
                break;
            case SLEEP:
                NUMBER_OF_VALUES = 20;
                PICKER_RANGE = 0.5f;
                break;
            case WEIGHT:
                NUMBER_OF_VALUES = 250;
                PICKER_RANGE = 0.1f;
                START_VALUE = 4.0f;
                break;
            case HEIGHT:
                NUMBER_OF_VALUES = 200;
                PICKER_RANGE = 0.5f;
                START_VALUE = 40.0f;
                break;
            case TEMPERATURE:
                NUMBER_OF_VALUES = 100;
                PICKER_RANGE = 0.1f;
                START_VALUE = 35.0f;
                break;
            default:
                break;
        }

        final String[] displayedValues = new String[NUMBER_OF_VALUES];
        if(act == EAT || act == POO) {
            for (int i = 0; i < NUMBER_OF_VALUES; ++i)
                displayedValues[i] = new DecimalFormat("0.0").format(START_VALUE + PICKER_RANGE * (i + 1));
        }
        else if(act == SLEEP || act == WEIGHT || act == HEIGHT || act == TEMPERATURE){
            for (int i = 0; i < NUMBER_OF_VALUES; ++i)
                displayedValues[i] =new DecimalFormat("0.0").format(START_VALUE + PICKER_RANGE * (i));
        }
        return displayedValues;
    }

    public static int getFirstDig(String string) {
        int pos = 0;
        for (; pos < string.length(); ++pos) {
            if (string.charAt(pos) >= 48 && string.charAt(pos) <= 57)
                return pos;
        }
        return -1;
    }

    public static int getLastDig(String string) {
        int pos = string.length() - 1;
        for (; pos >= 0; --pos) {
            if (string.charAt(pos) >= 48 && string.charAt(pos) <= 57)
                return pos;
        }
        return -1;
    }

    public static double getDigValue(String inContent) {
        int startPos = Utils.getFirstDig(inContent);
        int endPos = Utils.getLastDig(inContent);
        double value = 0f;
            if (-1 != startPos && -1 != endPos) {
                value = Float.parseFloat(inContent.substring(startPos, endPos + 1));
            }
        return value;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /* get file from directory or create both */
    public static File getStorageFile(String path, String dir, String fileName) {
        File f = new File(path, dir);
        if (!f.exists()) {
            if (f.mkdirs()) {
                return new File(path + File.separator + dir, fileName);
            }
        } else
            return new File(path + File.separator + dir, fileName);

        return null;
    }

    /* get file list in Dir for charts*/
    public static List<File> getLatestStorageFile(String path, String dir) {
        File f = new File(path+File.separator+dir);
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

    public static Date[] getXValues(String action){
        List<File> fList = Utils.getLatestStorageFile(getPath(), action);
        if(fList!=null && fList.size()>0) {
            Date[] dates = new Date[fList.size()];
            for (int i = 0; i < fList.size(); ++i) {
                try {
                    Date fDate = new SimpleDateFormat(mUtils.getApplicationContext().getResources().getString(R.string.yearMonthDay)).parse(fList.get(i).getName());
                    dates[i] = fDate;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return dates;
        }
        else
            return null;
    }

    public static double[] getYValues(String action){
        List<File> fList = Utils.getLatestStorageFile(getPath(),action);
        if(fList!=null && fList.size()>0) {
            double[] values = new double[fList.size()];
            for (int i = 0; i < fList.size(); ++i) {
                    values[i] = getYValue(action, fList.get(i));
            }
            return values;
        }
        else
            return null;
    }

    public static double getYValue(String action, File file){
        double value;
        String[] folderNames = mUtils.getApplicationContext().getResources().getStringArray(R.array.folderName);
        if(action.compareTo(folderNames[3])==0 || action.compareTo(folderNames[4])==0){
            value = getAverageValuesFromFile(file);
        }
        else{
            value = getSumValuesFromFile(file);
        }
        return value;
    }

    private static double getSumValuesFromFile(File file){
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

    private static double getAverageValuesFromFile(File file){
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
        return sum/bodys.size();
    }

    public static List<String> initItemList(){
        List<String> list = new ArrayList<String>(Arrays.asList(mUtils.getApplicationContext().getResources().getStringArray(R.array.actions)));
        File inFile = getStorageFile(Utils.getPath(),"config","actions");
        try {
            BufferedReader buf = new BufferedReader(new FileReader(inFile));
            String tmp;
            while ((tmp = buf.readLine()) != null) {
                String name = tmp.substring(0, tmp.indexOf(":"));
                if(!list.contains(name)) {
                    list.add(name);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        list.add("");
        return list;
    }

    public static String getPath(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mUtils.getApplicationContext());
        String path = sharedPref.getString(mUtils.getApplicationContext().getResources().getString(R.string.pref_key_store_path),Utils.defaultPath);
        return path;
    }

    public static String getPicPath(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mUtils.getApplicationContext());
        String path = sharedPref.getString(mUtils.getApplicationContext().getResources().getString(R.string.pref_key_pic_path),"");
        return path;
    }
}
