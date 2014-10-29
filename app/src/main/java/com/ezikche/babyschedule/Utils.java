package com.ezikche.babyschedule;

import android.os.Environment;

import java.io.File;
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
    public static final int EAT_DEFAULT = 9;

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
            default:
                break;
        }
        return body;
    }

    public static String[] getDisplayValuesByAct(int act) {
        int NUMBER_OF_VALUES = 0;
        float PICKER_RANGE = 0;
        switch (act) {
            case EAT://eat
                NUMBER_OF_VALUES = 30;
                PICKER_RANGE = 10;
                break;
            case POO://poo
                NUMBER_OF_VALUES = 10;
                PICKER_RANGE = 0.5f;
                break;
            case SLEEP://sleep
                NUMBER_OF_VALUES = 20;
                PICKER_RANGE = 0.5f;
                break;
            default:
                break;
        }

        final String[] displayedValues = new String[NUMBER_OF_VALUES];
        if(act == EAT || act == POO) {
            for (int i = 0; i < NUMBER_OF_VALUES; i++)
                displayedValues[i] = String.valueOf(PICKER_RANGE * (i + 1));
        }
        else if(act == SLEEP){
            for (int i = 0; i < NUMBER_OF_VALUES; i++)
                displayedValues[i] = String.valueOf(PICKER_RANGE * (i));
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

    /* get get file from directory or create both */
    public static File getStorageFile(String dir, String fileName) {
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), dir);
        if (!f.exists()) {
            if (f.mkdirs()) {
                return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + dir, fileName);
            }
        } else
            return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + dir, fileName);

        return null;
    }

    public static List<File> getLatestStorageFile(String dir) {
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
