package org.example.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.data.ParseResult;
import org.example.parser.AssertionParser;
import org.example.parser.impl.EqualsParser;
import org.example.parser.impl.NullParser;
import org.example.parser.impl.TFParser;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class FileVisitor {

    File assertion, test, fm, truth;

    public FileVisitor(String assertion, String test, String fm, String truth) {
        this.assertion = new File(assertion);
        this.test = new File(test);
        this.fm = new File(fm);
        this.truth = new File(truth);
    }

    public String visit() throws IOException {
        String assertType = readAssertTypeFromFirstLine(assertion);

        AssertionParser assertionParser = null;
        if (assertType.equals("assertEquals")) {
            assertionParser = new EqualsParser(assertion, test, fm, truth);
        } else if (assertType.equals("assertTrue") || assertType.equals("assertFalse")) {
            assertionParser = new TFParser(assertion, test, fm, truth);
        } else if (assertType.equals("assertNull") || assertType.equals("assertNotNull")) {
            assertionParser = new NullParser(assertion, test, fm, truth);
        } else {
            return "Unknown assertion type";
//            throw new RuntimeException("Unknown assertion type");
        }
        ParseResult parseResult = assertionParser.parse();

        return parseResult.result2json();
//        System.out.println(parseResult.result2json());

        // todo: transform parseResult into json, then write to file.
    }

    public static void main(String[] args) throws IOException {

//        String filename = "1.txt";

        String folder = args[0];

        // walk through all files in the folder
        File folderFile = new File("src/main/resources/"+folder + "assertion");
        String[] files = folderFile.list();
//        System.out.println(files);

        PrintStream out = System.out;
        System.setOut(new PrintStream(args[1]));



        HashMap results = new LinkedHashMap();
//        int cnt = 0;

        for(String filename : files){
            FileVisitor fileVisitor = new FileVisitor("src/main/resources/" + folder +"assertion/"+filename,
                    "src/main/resources/" + folder +"test/"+filename,
                    "src/main/resources/" + folder +"fm/"+filename,
                    "src/main/resources/" + folder +"truth/"+filename);
            String result = fileVisitor.visit();
            results.put(filename, result);
//            System.out.println("=============================="); // =*30
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String output = gson.toJson(results);
        System.out.println(output);

    }

    private String readAssertTypeFromFirstLine(File f) throws IOException {
        FileReader fileReader = new FileReader(f);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        String assertType = line.split("\\(")[0].trim();
        return assertType;
    }
}
