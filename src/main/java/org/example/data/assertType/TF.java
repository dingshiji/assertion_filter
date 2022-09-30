package org.example.data.assertType;

import org.example.data.ArgResult;
import org.example.data.ParseResult;

public class TF extends ParseResult {

    ArgResult arg;

    public TF(String fileName, String assertion, String test, String fm, String truth) {
        super(fileName, assertion, test, fm, truth);
    }

    @Override
    public void setArg(ArgResult[] args) {
        arg = args[0];
    }


    @Override
    public String result2json() {
        return null;
    }

}
