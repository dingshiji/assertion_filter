package org.example.data.assertType;

import com.google.gson.Gson;
import org.example.data.ArgResult;
import org.example.data.ParseResult;

public class Equal extends ParseResult {

    ArgResult arg1, arg2;

    public Equal(String fileName, String assertion, String test, String fm, String truth) {
        super(fileName, assertion, test, fm, truth);
    }

    @Override
    public void setArg(ArgResult args[]) {
        this.arg1 = args[0];
        this.arg2 = args[1];
    }

    public ArgResult getArg1() {
        return arg1;
    }

    public ArgResult getArg2() {
        return arg2;
    }



    @Override
    public String result2json() {
        Gson gson = new Gson();
        return gson.toJson(msg);
    }


}
