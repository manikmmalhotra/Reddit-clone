package com.jerry.redditclone;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ExtractXML {

    private String tag;
    private  String xml;

    public ExtractXML( String xml,String tag) {
        this.tag = tag;
        this.xml = xml;
    }

    public List<String> start(){
        List<String> result = new ArrayList<>();

        String[] spiltXML = xml.split(tag + "\"");
        int count = spiltXML.length;

        for (int i = 1; i< count; i++){
            String temp = spiltXML[i];
            int index = temp.indexOf("\"");
            Log.d("mmm","index" + index);
            temp = temp.substring(0,index);
            result.add(temp);
        }
        return result;
    }
}
