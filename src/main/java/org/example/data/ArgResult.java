package org.example.data;

import java.util.HashMap;

public class ArgResult {
    String argName;
    boolean methodCall;
    boolean fm;
    String type;

    HashMap result;

    public boolean isFm() {
        return fm;
    }

    public void setFm(boolean fm) {
        this.fm = fm;
    }

    boolean fieldAccess;


    public boolean isFieldAccess() {
        return fieldAccess;
    }

    public void setFieldAccess(boolean fieldAccess) {
        this.fieldAccess = fieldAccess;
    }

    @Override
    public String toString() {
        return "ArgResult{" +
                "argName='" + argName + '\'' +
                ", methodCall=" + methodCall +
                ", fm=" + fm +
                ", type='" + type + '\'' +
                ", fieldAccess=" + fieldAccess +
                '}';
    }

    public HashMap getDictResult() {
        result = new HashMap();
        result.put("argName",argName);
        result.put("methodCall",methodCall);
        result.put("fm",fm);
        result.put("type",type);
        result.put("fieldAccess",fieldAccess);
        return result;
    }

    public ArgResult(String argName) {
        this.argName = argName;
        this.type = "";
        this.fm = false;
        this.fieldAccess = false;
    }

    public String getArgName() {
        return argName;
    }

    public void setArgName(String argName) {
        this.argName = argName;
    }

    public boolean isMethodCall() {
        return methodCall;
    }

    public void setMethodCall(boolean methodCall) {
        this.methodCall = methodCall;
    }

    public String getType() {
        return type;
    }

    public boolean isSolved() {
        return !type.equals("");
    }

    public void setType(String type) {
        this.type = type;
    }
}
