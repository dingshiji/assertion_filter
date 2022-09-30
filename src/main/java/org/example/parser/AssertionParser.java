package org.example.parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.example.data.ArgResult;
import org.example.data.ParseResult;
import org.example.util.readFile2str;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public abstract class AssertionParser {

    protected static final String cantSolveType = "cannot solve all the type info";
    protected static final String goodAssertion = "Good assertion";
    protected static final String isMethodCall = "unsolved argument or method call exists";
    protected static final String incompatible = "incompatible type";

    protected File assertion;
    protected File test;
    protected File fm;
    protected File truth;

    protected HashMap<String, String> solvedToken, solvedMethod;

//    public ParseResult parseResult;

    TypeSolver typeSolver;
    JavaSymbolSolver javaSymbolSolver;
    protected ParseResult parseResult;

    public AssertionParser(File assertion, File test, File fm, File truth) {
        this.assertion = assertion;
        this.test = test;
        this.fm = fm;
        this.truth = truth;
    }

    private void setSolver() {
        typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver());
        javaSymbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(javaSymbolSolver);
    }

    protected void init() throws IOException {
        setSolver();
        solvedMethod = new HashMap<>();
        solvedToken = new HashMap<>();


    }

    protected abstract void trivialCheck();

    protected abstract void findArgs() throws FileNotFoundException;

    protected abstract void getTypeFromFM() throws FileNotFoundException;

    private Expression getInner(Expression exp) {
//        System.out.println(exp.toString());
        while (exp.isEnclosedExpr()) {
            exp = exp.asEnclosedExpr().getInner();
        }
//        System.out.println(exp.toString());
        return exp;
    }

    protected void findTypeInSolvedMethod(ArgResult arg) {
        if (arg.isMethodCall()) {
            System.out.println(arg.getArgName());
            for (String key : solvedMethod.keySet()) {
                if (key.equals(arg.getArgName())) {
                    arg.setType(solvedMethod.get(key));
                    parseResult.setMsg("find type in focal method", arg.getArgName());
                }
            }
        }
    }

    protected void findTypeInSolvedToken(ArgResult arg) {
        if (!arg.isMethodCall()) {
            System.out.println(arg.getArgName());
            for (String key : solvedToken.keySet()) {
                if (key.equals(arg.getArgName())) {
                    arg.setType(solvedToken.get(key));
                    parseResult.setMsg("find type in test", arg.getArgName());
                }
            }
        }
    }

    protected void parseFM() throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(fm);
        String fmName = cu.findAll(MethodDeclaration.class).get(0).getName().toString();
//        System.out.println(fmName);
        cu.findAll(ReturnStmt.class).forEach(rs -> {
            String retType = "";
            Expression retVal = StaticJavaParser.parseExpression(rs.getChildNodes().get(0).toString());
            retVal = getInner(retVal);
            if (retVal.isStringLiteralExpr()) {
                retType = "String";
            } else if (retVal.isDoubleLiteralExpr()) {
                retType = "double";
            } else if (retVal.isIntegerLiteralExpr()) {
                retType = "int";
            } else if (retVal.isBooleanLiteralExpr()) {
                retType = "boolean";
            } else if (retVal.isCharLiteralExpr()) {
                retType = "char";
            }

            solvedMethod.put(fmName, retType);
        });
    }

    ;

    protected void parseContext() throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(test);
        cu.findAll(VariableDeclarationExpr.class).forEach(vde -> {
            String varName = vde.getVariable(0).getName().toString();
            String varType = vde.getVariable(0).getType().toString();
            solvedToken.put(varName, varType);
        });
    }

    protected abstract void getTypeFromContext() throws FileNotFoundException;

    protected abstract ParseResult getParseResult();


    private void add_test_info() throws IOException {
        parseResult.setMsg("assertion", readFile2str.read(assertion));
        parseResult.setMsg("test", readFile2str.read(test));
        parseResult.setMsg("focal method", readFile2str.read(fm));
        parseResult.setMsg("truth", readFile2str.read(truth));
    }

    protected abstract boolean compareWithTruth() throws IOException;

    protected boolean oneArgCompareWithTruth() throws IOException {
        DefaultPrettyPrinter defaultPrettyPrinter = new DefaultPrettyPrinter();
        String code1 = readFile2str.read(truth);
        String code2 = readFile2str.read(assertion);
        MethodCallExpr exp1 = StaticJavaParser.parseExpression(code1).asMethodCallExpr();
        MethodCallExpr exp2 = StaticJavaParser.parseExpression(code2).asMethodCallExpr();

        String pretty_truth = defaultPrettyPrinter.print(exp1.getArgument(0));
        String pretty_assertion = defaultPrettyPrinter.print(exp2.getArgument(0));

        boolean result = (pretty_truth.equals(pretty_assertion) && exp1.getName().equals(exp2.getName()));

        // check if code1 and code2 have the same meaning

        if (result) {
            parseResult.setMsg("correct answer", "true");
        } else {
            parseResult.setMsg("correct answer", "false");
        }
        return result;
    }

    // template method here
    public ParseResult parse() throws IOException {
        init();

        add_test_info();

        try {
            findArgs();
        } catch (Exception e) {
            parseResult.setStopMsg("error in findArgs()");
        }

        if (parseResult.isStop()) return getParseResult();


        try {
            trivialCheck();
        } catch (Exception e) {
            parseResult.setStopMsg("error in trivialCheck()");
        }

        if (parseResult.isStop()) return getParseResult();


        try {
            getTypeFromFM();
        } catch (Exception e) {
            parseResult.setStopMsg("error in getTypeFromFM()");
        }

        if (parseResult.isStop()) return getParseResult();

        try {
            getTypeFromContext();
        } catch (Exception e) {
            parseResult.setStopMsg("error in getTypeFromContext()");
        }

        try{
            compareWithTruth();
        }catch (Exception e){
            parseResult.setStopMsg("error in compareWithTruth()");
        }

        return getParseResult();
    }

}
