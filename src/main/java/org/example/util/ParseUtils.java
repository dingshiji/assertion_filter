package org.example.util;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import static org.example.parser.AssertionParser.getInner;

public class ParseUtils {

    // result[0] --> result
    public static String getName2FindType(String argName) {
        Expression exp = StaticJavaParser.parseExpression(argName);
        if (exp.isArrayAccessExpr()) {
            return exp.asArrayAccessExpr().getName().toString();
        } else {
            return argName;
        }
    }

    private static boolean is_super(String parent, String child){
        if(parent.equals(child)) return true;
        if(parent.equals("java.lang.Object")) return true;
        try{
            TypeSolver typeSolver = new ReflectionTypeSolver();
            ResolvedReferenceTypeDeclaration parent_type = typeSolver.solveType(parent);
            ResolvedReferenceTypeDeclaration child_type = typeSolver.solveType(child);
            for(ResolvedReferenceType ancestor : child_type.getAllAncestors()){
                if(ancestor.getQualifiedName().equals(parent_type.getQualifiedName())){
                    return true;
                }
            }}catch(Exception e){
            return false;
        }
        return false;
    }
    private static String remove_generic(String name){
        try{
            return name.substring(0, name.indexOf("<"));
        }catch(Exception e){
            return name;
        }
    }

    public static boolean isSameType(String a, String b){
        a = remove_generic(a);
        b = remove_generic(b);
        String a1 = "java.lang."+a;
        String a2 = "java.util."+a;
        String b1 = "java.lang."+b;
        String b2 = "java.util."+b;
        return is_super(a1, b1) || is_super(b1, a1) || is_super(a2, b2) || is_super(b2, a2);
    }

    public static String getLiteralType(Expression token){
        token = getInner(token);
        String retType = "";
        if (token.isStringLiteralExpr()) {
            retType = "String";
        } else if (token.isDoubleLiteralExpr()) {
            retType = "double";
        } else if (token.isIntegerLiteralExpr()) {
            retType = "int";
        } else if (token.isBooleanLiteralExpr()) {
            retType = "boolean";
        } else if (token.isCharLiteralExpr()) {
            retType = "char";
        }else if(token.isUnaryExpr()){
            retType = getLiteralType(token.asUnaryExpr().getExpression());
        }
        return retType;
    }

    public static boolean isLiteral(Expression token){
        token = getInner(token);
        if(!token.isUnaryExpr()){
            return token.isStringLiteralExpr() || token.isDoubleLiteralExpr() || token.isIntegerLiteralExpr() || token.isBooleanLiteralExpr() || token.isCharLiteralExpr();
        }else{
            token = token.asUnaryExpr().getExpression();
            return isLiteral(token);
        }
    }

    public static boolean isLiteral(String tk){
        Expression token = StaticJavaParser.parseExpression(tk);
        token = getInner(token);
        if(!token.isUnaryExpr()){
            return token.isStringLiteralExpr() || token.isDoubleLiteralExpr() || token.isIntegerLiteralExpr() || token.isBooleanLiteralExpr() || token.isCharLiteralExpr();
        }else{
            token = token.asUnaryExpr().getExpression();
            return isLiteral(token);
        }
    }

    private static void test(){
        String str = "-1";
        Expression exp = StaticJavaParser.parseExpression(str);
        System.out.println(getLiteralType(exp));
    }

    public static void main(String[] args) {
        test();
    }
}
