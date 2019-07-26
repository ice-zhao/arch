package com.xunwei.collectdata.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gxy on 2016/12/15.
 */
public class FormatUtil
{
    private static DecimalFormat decimalFormat=new DecimalFormat();
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

    public static String Date2String(String pattern, Date date)
    {
        simpleDateFormat.applyPattern(pattern);
        return simpleDateFormat.format(date);
    }
    public static String Float2String(String pattern, float val)
    {
        decimalFormat.applyPattern(pattern);
        return decimalFormat.format(val);
    }

    public static Integer[] FormatStrCastToInt(String strFormat, String spliter)throws NumberFormatException
    {
        String[] strArray = strFormat.split(spliter);
        Integer[] intArray=new Integer[strArray.length];
        for(int i=0;i<strArray.length;++i)
        {
            intArray[i]= Integer.parseInt(strArray[i]);
        }
        return intArray;
    }


    public static Map<String, String> ParseAttributes(String str, String outerSplitor, String innerSplitor)
    {
        Map<String, String> result= new HashMap<String, String>();
        try
        {
            String[] segment=str.split(outerSplitor);

            for(short i=0;i<segment.length;++i)
            {
                String strKv=segment[i];
                String[] keyVal=strKv.split(innerSplitor);
                if(2 == keyVal.length)
                {
                    result.put(keyVal[0],keyVal[1]);
                }
                else if(1 == keyVal.length)
                {
                    result.put(keyVal[0],"");
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return result;
    }

}
