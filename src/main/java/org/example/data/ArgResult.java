package org.example.data;

public class ArgResult {
    String argName;
    boolean methodCall;
    boolean fm;
    String type;

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
