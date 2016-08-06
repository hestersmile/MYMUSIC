package com.studyandroid.hestersmile.mymusic.util;

/**
 * Created by hestersmile on 2016/5/23.
 */
public class Basetool {
    public static int getAndroidOSVersion()
    {
        int osVersion;
        try
        {
            osVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
        }
        catch (NumberFormatException e)
        {
            osVersion = 0;
        }

        return osVersion;
    }
}
