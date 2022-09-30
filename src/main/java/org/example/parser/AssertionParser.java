package org.example.parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.example.data.ParseResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public abstract class AssertionParser {

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
    }

    protected abstract void trivialCheck();

    protected abstract void findArgs() throws FileNotFoundException;

    protected abstract void getTypeFromFM();

    private void parseFM(){

    };

    protected abstract void parseContext();

    protected abstract ParseResult getParseResult();

    // template method here
    public ParseResult parse() throws IOException {
        init();
        findArgs();


        if (!parseResult.isStop()) {
            trivialCheck();
        }
        if (!parseResult.isStop()) {
            parseFM();
            getTypeFromFM();
        }
//        if (!parseResult.isStop()) {
//            parseContext();
//        }


        return getParseResult();
    }

}
