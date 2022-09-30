package org.example.parser.impl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import org.example.data.ParseResult;
import org.example.data.assertType.NNN;
import org.example.parser.AssertionParser;
import org.example.util.readFile2str;

import java.io.File;
import java.io.IOException;

public class NullParser extends AssertionParser {

    NNN nnnResult;
    public NullParser(File assertion, File test, File fm, File truth) {
        super(assertion, test, fm, truth);
    }

    @Override
    protected void trivialCheck() {

    }

    @Override
    protected void findArgs() {

    }

    @Override
    protected void getTypeFromFM() {

    }

    @Override
    protected void getTypeFromContext() {

    }

    @Override
    protected ParseResult getParseResult() {
        return null;
    }

    @Override
    protected boolean compareWithTruth() throws IOException {
        return oneArgCompareWithTruth();
    }
}
