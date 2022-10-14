package org.example.testJavaParser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.Expression;

public class NewGetType {



    private static String getName2FindType(String argName) {
        Expression exp = StaticJavaParser.parseExpression(argName);
        if (exp.isArrayAccessExpr()) {
            return exp.asArrayAccessExpr().getName().toString();
        } else {
            return argName;
        }
    }

    private static void getName(String str){
        String s = "tran[]";
        Expression exp = StaticJavaParser.parseExpression(s);
        System.out.println(exp.asArrayInitializerExpr().getValues().get(0).toString());
    }



    public static void main(String[] args) {
//        System.out.println(getName2FindType("result [0]"));
        getName("tran[]");
    }
}
