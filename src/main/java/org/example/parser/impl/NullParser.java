package org.example.parser.impl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import org.example.data.ArgResult;
import org.example.data.ParseResult;
import org.example.data.assertType.NNN;
import org.example.parser.AssertionParser;
import org.example.util.readFile2str;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class NullParser extends AssertionParser {

    NNN nnnResult;

    public NullParser(File assertion, File test, File fm, File truth) {
        super(assertion, test, fm, truth);
    }

    @Override
    protected void init() throws IOException {
        super.init();
        nnnResult = new NNN(assertion.getName(), readFile2str.read(assertion), readFile2str.read(test), readFile2str.read(fm), readFile2str.read(truth));
        parseResult = nnnResult;
    }

    @Override
    protected void trivialCheck() {
        if (nnnResult.getArg().getArgName().equals("null")) {
            nnnResult.setStopMsg("arg is trivial");
        }
        nnnResult.setMsg("trivial_check", "finished");
    }

    @Override
    protected void findArgs() throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(test);
        cu.findAll(MethodCallExpr.class).forEach(mce -> {
            if (mce.getName().toString().equals("assertNull") || mce.getName().toString().equals("assertNotNull")) {
                if (mce.getArguments().size() != 1) {
                    nnnResult.setStopMsg("arg num != 1");
                    return;
                }
                ArgResult argResult = new ArgResult(mce.getArgument(0).toString());
                argResult.setMethodCall(mce.getArgument(0).isMethodCallExpr());
                if (argResult.isMethodCall()) {
                    argResult.setArgName(mce.getArgument(0).asMethodCallExpr().getName().toString());
                }

                argResult.setFieldAccess(mce.getArgument(0).isFieldAccessExpr());

                ArgResult[] argResults = {argResult};
                nnnResult.setArg(argResults);
            }
        });
    }

    @Override
    protected void getTypeFromFM() throws FileNotFoundException {
        ArgResult arg = nnnResult.getArg();

        if (!arg.isMethodCall()) {
            nnnResult.setMsg("parse focal method", "false");
            return;
        }

        parseFM();
        nnnResult.setMsg("parse focal method", "finished");
//
        findTypeInSolvedMethod(arg);
    }

    @Override
    protected void getTypeFromContext() throws FileNotFoundException {
        ArgResult arg = nnnResult.getArg();
        parseContext();

        findTypeInSolvedToken(arg);
        nnnResult.setMsg("parse context", "finished");
    }

    @Override
    protected ParseResult getParseResult() {



        ArgResult arg = nnnResult.getArg();


        if(checkEmptyArg(arg)){
            return (ParseResult) nnnResult;
        }

        String result = "";

        if (arg.isSolved()) {
            result = goodAssertion;
        } else if (arg.isMethodCall()) {
            if (arg.getArgName().equals(fmName)) {
                result = isMethodCallFM;
            }else{
                result = isMethodCall;
            }
        } else {
            result = cantSolveType;
        }

        nnnResult.setMsg("arg", arg.toString());

        nnnResult.setMsg("result", result);

        return (ParseResult) nnnResult;
    }

    @Override
    protected boolean compareWithTruth() throws IOException {
        return oneArgCompareWithTruth();
    }
}
