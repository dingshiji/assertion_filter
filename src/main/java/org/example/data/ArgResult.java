package org.example.data;

public class ArgResult {
    String argName;
    boolean methodCall;
    String type;

    public ArgResult(String argName) {
        this.argName = argName;
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

    public void setType(String type) {
        this.type = type;
    }
}
