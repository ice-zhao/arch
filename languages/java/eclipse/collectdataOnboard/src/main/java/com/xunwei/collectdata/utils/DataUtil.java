package com.xunwei.collectdata.utils;


import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataUtil
{
    public static void _reserveByteArray(byte[] originArray)
    {
        byte temp;
        for (int i = 0; i < originArray.length / 2; i++)
        {
            temp = originArray[i];
            originArray[i] = originArray[originArray.length - i - 1];
            originArray[originArray.length - i - 1] = temp;
        }
    }

    public static void _reserveIntArray(int[] originArray)
    {
        int temp;
        for (int i = 0; i < originArray.length / 2; i++)
        {
            temp = originArray[i];
            originArray[i] = originArray[originArray.length - i - 1];
            originArray[originArray.length - i - 1] = temp;
        }
    }

    public static byte[] IntToByteArray(int n)
    {
        byte[] byteArray = null;
        try
        {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(byteOut);
            dataOut.writeInt(n);
            byteArray = byteOut.toByteArray();
            _reserveByteArray(byteArray);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return byteArray;
    }

    /**
     * 字节数组转换成整数 关键技术：ByteArrayInputStream和DataInputStream
     *
     * @param byteArray 需要转换的字节数组
     * @return
     */
    public static int ByteArrayToInt(byte[] byteArray)
    {
        int n = 0;
        try
        {
            ByteArrayInputStream byteInput = new ByteArrayInputStream(byteArray);
            DataInputStream dataInput = new DataInputStream(byteInput);
            n = dataInput.readInt();
            System.out.println("整数为： " + n);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return n;
    }


    /**
     * @description: 讲字节数组转换为Int
     * @author: Gxy
     * @date: 2019/1/23 18:12
     */
    public static int ByteArr2Int(byte[] arr)
    {
        return (arr[3] & 0xff) << 24 | (arr[2] & 0xff) << 16
                | (arr[1] & 0xff) << 8 | (arr[0] & 0xff) << 0;
    }


    public static long ByteArr2Long(byte[] arr)
    {
        return (arr[7] & 0xff) << 54 | (arr[6] & 0xff) << 48
            | (arr[5] & 0xff) << 40 | (arr[4] & 0xff) << 32
            | (arr[3] & 0xff) << 24 | (arr[2] & 0xff) << 16
            | (arr[1] & 0xff) << 8 | (arr[0] & 0xff) << 0;
    }


    /**
     * @description: 将字节数组转换为Short
     * @author: Gxy
     * @date: 2019/1/23 18:13
     */
    public static short ByteArr2Short(byte[] arr)
    {
        Integer temp=(arr[1] & 0xff) << 8 | (arr[0] & 0xff) << 0;
        return temp.shortValue();
    }


    public static byte[] subByte(byte[] b,int off,int length)
    {
        byte[] b1 = new byte[length];
        System.arraycopy(b, off, b1, 0, length);
        return b1;
    }

    public static float ByteArr2Float(byte[] bytes)
    {
        return Float.intBitsToFloat(ByteArr2Int(bytes));
    }


    public static boolean IsNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

}
