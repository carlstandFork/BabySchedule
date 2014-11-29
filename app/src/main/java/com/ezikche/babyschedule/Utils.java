package com.ezikche.babyschedule;

import android.graphics.Color;
import android.os.Environment;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ezikche on 9/30/2014.
 */
public class Utils {

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
    public static String getMessageBodyByAct(int act, String[] displayedValues, int valuePos) {
        String body = "";
        switch (act) {
            case EAT: {
                body = "宝宝喝了" + displayedValues[valuePos] + "毫升奶\n";
            }
            break;
            case POO: {
                body = "宝宝拉了" + displayedValues[valuePos] + "次臭臭\n";
            }
            break;
            case SLEEP: {
                body = "宝宝睡了" + displayedValues[valuePos] + "小时觉觉\n";
            }
            break;
            case WEIGHT: {
                body = "宝宝重" + displayedValues[valuePos] + "公斤\n";
            }
            break;
            case HEIGHT: {
                body = "宝宝长" + displayedValues[valuePos] + "CM\n";
            }
            break;
            case TEMPERATURE: {
                body = "宝宝体温" + displayedValues[valuePos] + "摄氏度\n";
            }
            break;
            default:
                break;
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

    public static boolean moveFiles(String src, String des){
        if (isExternalStorageWritable()){
            try {
//                Files.copy(new File(src), new File(des));
                return true;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }
}
