package com.test.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public class DimenTool {

    static String outPutPath = "app/src/main/res/";

    public static void gen(String name, float scaling) {

        File file = new File("app/src/main/res/values/dimens.xml");
        System.out.println("输出文件路径：" + outPutPath);
        BufferedReader reader = null;
        StringBuilder result = new StringBuilder();
        //StringBuilder w1280_720dp = new StringBuilder();
        try {
            System.out.println("生成不同分辨率：");
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            int line = 1;
            // Read in one line at a time, until null is the end of the file
            while ((tempString = reader.readLine()) != null) {
                if (tempString.contains("</dimen>")) {
                    //tempString = tempString.replaceAll(" ", "");
                    int endIndex = tempString.contains("dip") ? 3 : 2;
                    String start = tempString.substring(0, tempString.indexOf(">") + 1);
                    String end = tempString.substring(tempString.lastIndexOf("<") - endIndex);
                    //cut down the value in dimen tag
                    String numStr = tempString.substring(tempString.indexOf(">") + 1, tempString.indexOf("</dimen>") - endIndex);
                    double num = Double.parseDouble(numStr);
                    DecimalFormat df = new DecimalFormat(numStr.contains(".") ? "#.##" : "0");
                    result.append(start).append(df.format(num * scaling)).append(end).append("\r\n");
//                        result.append(start).append((int) Math.floor(num * scaling + 0.5)).append(end).append("\r\n");
                } else {
                    result.append(tempString).append("");
                }
                line++;
            }
            reader.close();
            createFile(name + "/dimens.xml", result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static boolean createFile(String destFileName, String info) {
        destFileName = outPutPath + destFileName;
        System.out.println("文件名：" + destFileName);
        File file = new File(destFileName);
        if (!file.exists()) {
            //判断目标文件所在的目录是否存在
            if (!file.getParentFile().exists()) {
                //如果目标文件所在的目录不存在，则创建父目录
                System.out.println("目标文件所在目录不存在，准备创建它！" + file.getParentFile());
                if (!file.getParentFile().mkdirs()) {
                    System.out.println("创建目标文件所在目录失败！");
                    return false;
                }
            }
            //创建目标文件
            try {
                if (file.createNewFile()) {
                    System.out.println("创建单个文件" + destFileName + "成功！");
                    writeFile(destFileName, info);
                    return true;
                } else {
                    System.out.println("创建单个文件" + destFileName + "失败！");
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("创建单个文件" + destFileName + "失败！" + e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * write method
     */

    public static void writeFile(String file, String text) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            out.println(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.close();
    }

    public static void main(String[] args) {
        gen("values-sw390dp", 13f / 12f);
        gen("values-sw420dp", 14f / 12f);
        gen("values-sw450dp", 15f / 12f);
        gen("values-sw480dp", 16f / 12f);
        gen("values-sw510dp", 17f / 12f);
        gen("values-sw540dp", 18f / 12f);
        gen("values-sw570dp", 19f / 12f);
        gen("values-sw600dp", 20f / 12f);
        gen("values-sw630dp", 21f / 12f);
    }
}
