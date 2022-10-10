package util;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.Expression;

public class getLiteralType {

    public static void main(String[] args) {
        String literal = "-1";
        Expression exp = StaticJavaParser.parseExpression(literal);
        System.out.println(exp.asLiteralExpr().isIntegerLiteralExpr());
    }
}
