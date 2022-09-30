package org.example.parser.impl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.example.data.ArgResult;
import org.example.data.ParseResult;
import org.example.data.assertType.Equal;
import org.example.parser.AssertionParser;
import org.example.util.readFile2str;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class EqualsParser extends AssertionParser {

    Equal equalResult;

    public EqualsParser(File assertion, File test, File fm, File truth) {
        super(assertion, test, fm, truth);
    }


    @Override
    protected void init() throws IOException {
        super.init();
        equalResult = new Equal(assertion.getName(), readFile2str.read(assertion), readFile2str.read(test), readFile2str.read(fm), readFile2str.read(truth));
        parseResult = equalResult;
    }

    @Override
    protected void findArgs() throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(test);
        cu.findAll(MethodCallExpr.class).forEach(mce->{
            if(mce.getName().toString().equals("assertEquals")){
                 if(mce.getArguments().size()!=2){
                     equalResult.setStopMsg("arg num != 2");
                     return;
                 }

                 ArgResult argResult1 = new ArgResult(mce.getArgument(0).toString());
                 argResult1.setMethodCall(mce.getArgument(0).isMethodCallExpr());
                 if(argResult1.isMethodCall()){
                     argResult1.setArgName(mce.getArgument(0).asMethodCallExpr().getName().toString());
                 }


                 ArgResult argResult2 = new ArgResult(mce.getArgument(1).toString());
                 argResult2.setMethodCall(mce.getArgument(1).isMethodCallExpr());
                 if(argResult2.isMethodCall()){
                     argResult2.setArgName(mce.getArgument(1).asMethodCallExpr().getName().toString());
                 }

                 ArgResult[] argResults = {argResult1, argResult2};
                 equalResult.setArg(argResults);
            }
        });

//        parseResult = equalResult;
    }

    @Override
    protected void trivialCheck() {
        if(equalResult.getArg1().getArgName().equals(equalResult.getArg2().getArgName())){
            equalResult.setStopMsg("arg1 == arg2");
        }
        equalResult.setMsg("trivial_check", "finished");
    }
    @Override
    protected void getTypeFromFM() throws FileNotFoundException {
        ArgResult arg1 = equalResult.getArg1();
        ArgResult arg2 = equalResult.getArg2();

        if(!arg1.isMethodCall() && !arg2.isMethodCall()){
            equalResult.setMsg("parse focal method","false");
            return;
        }

        parseFM();
        equalResult.setMsg("parse focal method", "true");
        System.out.println("solved method in fm:" + solvedMethod);

        findTypeInSolvedMethod(arg1);
        findTypeInSolvedMethod(arg2);

//        System.out.println(arg1.getType());
//        System.out.println(arg2.getType());

        equalResult.setMsg("parse_fm", "finished");
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


    protected ParseResult getParseResult() {

        ArgResult arg1 = equalResult.getArg1();
        ArgResult arg2 = equalResult.getArg2();

        String result = "";

        if(arg1.getType().equals(arg2.getType()) && arg1.isSolved() && arg2.isSolved()){
            result = goodAssertion;
        }else if(!arg1.getType().equals(arg2.getType()) && arg1.isSolved() && arg2.isSolved()){
            result = incompatible;
        }else if(arg1.isMethodCall()|| arg2.isMethodCall()){
            result = isMethodCall;
        }else if(!arg1.isSolved() && !arg2.isSolved()) {
            result = cantSolveType;
        }



        equalResult.setMsg("result",result);


        return (ParseResult) equalResult;
    }
}
