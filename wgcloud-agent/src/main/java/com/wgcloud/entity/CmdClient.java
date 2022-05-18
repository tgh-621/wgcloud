package com.wgcloud.entity;

import java.io.*;

public class CmdClient
{
    /**
     * 运行shell并获得结果，注意：如果sh中含有awk,一定要按new String[]{"/bin/sh","-c",shStr}写,才可以获得流
     *
     * @param shStr
     *            需要执行的shell
     * @return
     */
    public static String runShell(String shStr) {
        String strList = "";
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh","-c",shStr},null,null);
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            process.waitFor();
            while ((line = input.readLine()) != null){
                strList+=(line)+"\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strList;
    }

}
