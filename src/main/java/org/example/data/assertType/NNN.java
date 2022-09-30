package org.example.data.assertType;


import com.google.gson.Gson;
import org.example.data.ArgResult;
import org.example.data.ParseResult;

public class NNN extends ParseResult {

    ArgResult arg;

    public NNN(String fileName, String assertion, String test, String fm, String truth) {
        super(fileName, assertion, test, fm, truth);
    }

    @Override
    public void setArg(ArgResult[] args) {
        this.arg = args[0];
    }



    @Override
    public String result2json() {
        return null;
    }
}
