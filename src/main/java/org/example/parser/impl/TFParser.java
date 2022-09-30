package org.example.parser.impl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import javassist.expr.MethodCall;
import org.example.data.ArgResult;
import org.example.data.ParseResult;
import org.example.data.assertType.TF;
import org.example.parser.AssertionParser;
import org.example.util.readFile2str;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TFParser extends AssertionParser {

    TF tfResult;

    public TFParser(File assertion, File test, File fm, File truth) {
        super(assertion, test, fm, truth);
    }


    protected void init() throws IOException {
        super.init();
        tfResult = new TF(assertion.getName(), readFile2str.read(assertion), readFile2str.read(test), readFile2str.read(fm), readFile2str.read(truth));
        parseResult = tfResult;

    }


    @Override
    protected void findArgs() throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(test);
        cu.findAll(MethodCallExpr.class).forEach(mce -> {
            if (mce.getName().toString().equals("assertTrue") || mce.getName().toString().equals("assertFalse")) {
                if (mce.getArguments().size() != 1) {
                    tfResult.setStopMsg("arg num != 1");
                    return;
                }
                ArgResult argResult = new ArgResult(mce.getArgument(0).toString());
                argResult.setMethodCall(mce.getArgument(0).isMethodCallExpr());
                if (argResult.isMethodCall()) {
                    argResult.setArgName(mce.getArgument(0).asMethodCallExpr().getName().toString());
                }

                ArgResult[] argResults = {argResult};
                tfResult.setArg(argResults);
            }
        });
    }

    @Override
    protected void trivialCheck() {
        if (tfResult.getArg().getArgName().equals("true") || tfResult.getArg().getArgName().equals("false")) {
            tfResult.setStopMsg("arg is trivial");
        }
        tfResult.setMsg("trivial_check", "finished");
    }

    @Override
    protected void getTypeFromFM() throws FileNotFoundException {
        ArgResult arg = tfResult.getArg();

        if (!arg.isMethodCall()) {
            tfResult.setMsg("parse focal method", "false");
            return;
        }

        parseFM();
        tfResult.setMsg("parse focal method", "finished");
//
        findTypeInSolvedMethod(arg);

    }

    @Override
    protected void getTypeFromContext() throws FileNotFoundException {
        ArgResult arg = tfResult.getArg();
        parseContext();

        findTypeInSolvedToken(arg);
        tfResult.setMsg("parse context", "finished");
    }

    @Override
    protected ParseResult getParseResult() {

        ArgResult arg = tfResult.getArg();

        if(checkEmptyArg(arg)){
            return (ParseResult) tfResult;
        }

        String result = "";

        if (arg.getType().equals("boolean")) {
            result = goodAssertion;
        } else if (arg.isSolved()) {
            result = incompatible;
        } else if (arg.isMethodCall()) {
            result = isMethodCall;
        } else {
            result = cantSolveType;
        }

        tfResult.setMsg("result", result);

        return (ParseResult) tfResult;
    }

    @Override
    protected boolean compareWithTruth() throws IOException {
        return oneArgCompareWithTruth();
    }
}
