package org.example.parser.impl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
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
                 ArgResult argResult2 = new ArgResult(mce.getArgument(1).toString());
                 argResult2.setMethodCall(mce.getArgument(1).isMethodCallExpr());
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
    protected void getTypeFromFM() {
        ArgResult arg1 = equalResult.getArg1();
        ArgResult arg2 = equalResult.getArg2();
        if(arg1.isMethodCall() || arg2.isMethodCall()){
            equalResult.setMsg("parse focal method", "true");
        }else{
            equalResult.setMsg("parse focal method","false");
        }



        equalResult.setMsg("parse_fm", "finished");
    }

    @Override
    protected void parseContext() {

    }


    protected ParseResult getParseResult() {
        return (ParseResult) equalResult;
    }
}
