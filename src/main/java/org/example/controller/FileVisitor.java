package org.example.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.data.ParseResult;
import org.example.parser.AssertionParser;
import org.example.parser.impl.EqualsParser;
import org.example.parser.impl.NullParser;
import org.example.parser.impl.TFParser;

import java.io.*;

public class FileVisitor {

    File assertion, test, fm, truth;

    public FileVisitor(String assertion, String test, String fm, String truth) {
        this.assertion = new File(assertion);
        this.test = new File(test);
        this.fm = new File(fm);
        this.truth = new File(truth);
    }

    public void visit() throws IOException {
        String assertType = readAssertTypeFromFirstLine(assertion);

        AssertionParser assertionParser = null;
        if (assertType.equals("assertEquals")) {
            assertionParser = new EqualsParser(assertion, test, fm, truth);
        } else if (assertType.equals("assertTrue") || assertType.equals("assertFalse")) {
            assertionParser = new TFParser(assertion, test, fm, truth);
        } else if (assertType.equals("assertNull") || assertType.equals("assertNotNull")) {
            assertionParser = new NullParser(assertion, test, fm, truth);
        } else {
            throw new RuntimeException("Unknown assertion type");
        }
        ParseResult parseResult = assertionParser.parse();


        System.out.println(parseResult.result2json());

        // todo: transform parseResult into json, then write to file.
    }

    public static void main(String[] args) throws IOException {

        String filename = "1.txt";

        FileVisitor fileVisitor = new FileVisitor("src/main/resources/example/assertion/"+filename,
                "src/main/resources/example/test/"+filename,
                "src/main/resources/example/fm/"+filename,
                "src/main/resources/example/truth/"+filename);


        fileVisitor.visit();

    }

    private String readAssertTypeFromFirstLine(File f) throws IOException {
        FileReader fileReader = new FileReader(f);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        String assertType = line.split("\\(")[0].trim();
        return assertType;
    }
}
