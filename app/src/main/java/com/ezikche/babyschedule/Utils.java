package com.ezikche.babyschedule;

import android.os.Environment;

import java.io.File;

/**
 * Created by ezikche on 9/30/2014.
 */
public class Utils {

    public static final int EAT = 0;
    public static final int POO = 1;
    public static final int SLEEP = 2;

    public static String getMessageBodyByAct(int act, String[] displayedValues, int valuePos) {
        String body = "";
        switch (act) {
            case EAT: {
                body = "宝宝已经喝了" + displayedValues[valuePos] + "毫升奶\n";
            }
            break;
            case POO: {
                body = "宝宝已经拉了" + displayedValues[valuePos] + "次臭臭\n";
            }
            break;
            case SLEEP: {
                body = "宝宝已经睡了" + displayedValues[valuePos] + "小时觉觉\n";
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
                NUMBER_OF_VALUES = 25;
                PICKER_RANGE = 20;
                break;
            case POO://poo
                NUMBER_OF_VALUES = 10;
                PICKER_RANGE = 1;
                break;
            case SLEEP://sleep
                NUMBER_OF_VALUES = 20;
                PICKER_RANGE = 0.5f;
                break;
            default:
                break;
        }

        final String[] displayedValues = new String[NUMBER_OF_VALUES];
        for (int i = 0; i < NUMBER_OF_VALUES; i++)
            displayedValues[i] = String.valueOf(PICKER_RANGE * (i + 1));

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
}
