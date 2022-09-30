package org.example.parser.impl;

import org.example.data.ParseResult;
import org.example.parser.AssertionParser;

import java.io.File;

public class TFParser extends AssertionParser {

    public TFParser(File assertion, File test, File fm, File truth) {
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
}
