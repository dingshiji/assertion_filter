package org.example.parser.impl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.data.ArgResult;
import org.example.data.ParseResult;
import org.example.data.assertType.Equal;
import org.example.parser.AssertionParser;
import org.example.util.readFile2str;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class EqualsParser extends AssertionParser {

    Equal equalResult;

    public EqualsParser(File assertion, File test, File fm, File truth) {
        super(assertion, test, fm, truth);
    }

    public EqualsParser(String assertionStr, String testStr, String fmStr, String truthStr, String testName) {
        super(assertionStr, testStr, fmStr, truthStr, testName);
    }

    @Override
    protected void init() throws IOException {
        super.init();
        equalResult = new Equal(testName, assertionStr, testStr, fmStr, truthStr);
        parseResult = equalResult;
    }

    @Override
    protected void findArgs() throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(testStr);
        cu.findAll(MethodCallExpr.class).forEach(mce -> {
            if (mce.getName().toString().equals("assertEquals")) {
                if (mce.getArguments().size() != 2) {
                    equalResult.setStopMsg("arg num != 2");
                    return;
                }

                ArgResult argResult1 = new ArgResult(mce.getArgument(0).toString());
                ArgResult argResult2 = new ArgResult(mce.getArgument(1).toString());
                ArgResult[] argResults = {argResult1, argResult2};

                for(int i=0;i<2;i++){
                    ArgResult argResult = argResults[i];
                    argResult.setMethodCall(mce.getArgument(i).isMethodCallExpr());
                    if(argResult.isMethodCall()){
                        argResult.setArgName(mce.getArgument(i).asMethodCallExpr().getName().toString());
                    }
                    argResult.setFieldAccess(mce.getArgument(i).isFieldAccessExpr());
                    if(mce.getArgument(i).isStringLiteralExpr()){
                        argResult.setType("String");
                    }else if(mce.getArgument(i).isIntegerLiteralExpr()) {
                        argResult.setType("int");
                    }else if(mce.getArgument(i).isBooleanLiteralExpr()) {
                        argResult.setType("boolean");
                    }else if(mce.getArgument(i).isDoubleLiteralExpr()) {
                        argResult.setType("double");
                    }else if(mce.getArgument(i).isCharLiteralExpr()) {
                        argResult.setType("char");
                    }

                    // argument looks like lst[i]
                    if(mce.getArgument(i).isArrayAccessExpr()){
                        // 在ArgResult新增一个bool表示它是个数组访问
                        // 新增一个String记录它的名字 ArrayName
                        // 在context中, 如果找到ArrayName的类型, 设置这个参数的类型
                        // TODO: 处理下标访问的类型
                    }
                }


                equalResult.setArg(argResults);
            }
        });

//        parseResult = equalResult;
    }

    @Override
    protected void trivialCheck() {
        if (equalResult.getArg1().getArgName().equals(equalResult.getArg2().getArgName())) {
            equalResult.setStopMsg("arg1 == arg2");
        }
        equalResult.setMsg("trivial_check", "finished");
    }

    @Override
    protected void getTypeFromFM() throws FileNotFoundException {
        ArgResult arg1 = equalResult.getArg1();
        ArgResult arg2 = equalResult.getArg2();

        if (!arg1.isMethodCall() && !arg2.isMethodCall()) {
            equalResult.setMsg("parse focal method", "false");
            return;
        }

        parseFM();
        equalResult.setMsg("parse focal method", "true");
//        System.out.println("solved method in fm:" + solvedMethod);

        findTypeInSolvedMethod(arg1);
        findTypeInSolvedMethod(arg2);

//        System.out.println(arg1.getType());
//        System.out.println(arg2.getType());

//        equalResult.setMsg("parse_fm", "finished");
    }

    @Override
    protected void getTypeFromContext() throws FileNotFoundException {
        ArgResult arg1 = equalResult.getArg1();
        ArgResult arg2 = equalResult.getArg2();

        parseContext();

        findTypeInSolvedToken(arg1);
        findTypeInSolvedToken(arg2);

        equalResult.setMsg("parse_context", "finished");
    }

    @Override
    protected boolean compareWithTruth() throws IOException {
        String code1 = truthStr;
        String code2 = assertionStr;

        MethodCallExpr exp1 = StaticJavaParser.parseExpression(code1).asMethodCallExpr();
        MethodCallExpr exp2 = StaticJavaParser.parseExpression(code2).asMethodCallExpr();

        DefaultPrettyPrinter defaultPrettyPrinter = new DefaultPrettyPrinter();

        Set<String> st1 = new HashSet<>(), st2 = new HashSet<>();
        st1.add(defaultPrettyPrinter.print(exp1.getArgument(0)));
        st1.add(defaultPrettyPrinter.print(exp1.getArgument(1)));
        st2.add(defaultPrettyPrinter.print(exp2.getArgument(0)));
        st2.add(defaultPrettyPrinter.print(exp2.getArgument(1)));

        // compare two sets
        boolean result = st1.equals(st2);

        // check if code1 and code2 have the same meaning

        if (result) {
            equalResult.setMsg("correct answer", "true");
        } else {
            equalResult.setMsg("correct answer", "false");
        }
        return result;
    }

    protected ParseResult getParseResult() {

        ArgResult arg1 = equalResult.getArg1();
        ArgResult arg2 = equalResult.getArg2();

        if (checkEmptyArg(arg1)) {
            return (ParseResult) equalResult;
        }

        if (checkEmptyArg(arg2)) {
            return (ParseResult) equalResult;
        }

        String result = "";

        if (arg1.getType().equals(arg2.getType()) && arg1.isSolved() && arg2.isSolved()) {
            result = goodAssertion;
        } else if (!arg1.getType().equals(arg2.getType()) && arg1.isSolved() && arg2.isSolved()) {
            result = incompatible;
        } else if (arg1.isMethodCall() || arg2.isMethodCall()) {
            if (arg1.isMethodCall() && arg1.getArgName().equals(fmName)) {
                result = isMethodCallFM;
            } else if (arg2.isMethodCall() && arg2.getArgName().equals(fmName)) {
                result = isMethodCallFM;
            } else {
                result = isMethodCall;
            }
        } else if (!arg1.isSolved() || !arg2.isSolved()) {
            if (arg1.isFieldAccess() || arg2.isFieldAccess()) {
                result = cantSolveType_field;
            } else {
                result = cantSolveType;
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        equalResult.setMsg("arg1", gson.toJson(arg1.getDictResult()));
        equalResult.setMsg("arg2", gson.toJson(arg2.getDictResult()));

        equalResult.setMsg("result", result);

        return (ParseResult) equalResult;
    }
}
