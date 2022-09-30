package org.example.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class ParseResult {

    String fileName;
    String assertion;
    String test;
    String fm;
    String truth;
//    protected String errorMessage;



    protected LinkedHashMap msg;

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    boolean stop;

    public ParseResult(String fileName, String assertion, String test, String fm, String truth) {
        this.fileName = fileName;
        this.assertion = assertion;
        this.test = test;
        this.fm = fm;
        this.truth = truth;
        msg = new LinkedHashMap();
    }

    public abstract void setArg(ArgResult args[]);

    public void setMsg(String key, String value){
        msg.put(key,value);
//        Gson gson = new Gson();
//        String output = gson.toJson(msg);
//        System.out.println(output);
    }

    public void setStopMsg(String errorMessage){
        this.stop = true;
        this.msg.put("stop parsing",errorMessage);
    }
    public  String result2json(){ 
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(msg);
    };
}
