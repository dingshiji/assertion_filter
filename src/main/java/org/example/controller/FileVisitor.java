package org.example.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.data.ParseResult;
import org.example.parser.AssertionParser;
import org.example.parser.impl.EqualsParser;
import org.example.parser.impl.NullParser;
import org.example.parser.impl.TFParser;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static com.google.common.io.Files.hash;
import static com.google.common.io.Files.readLines;

public class FileVisitor {

    File assertion, test, fm, truth;

    String assertionStr, testStr, fmStr, truthStr;
    String testName;


    public FileVisitor(String assertion, String test, String fm, String truth, String testName) {

        this.assertionStr = assertion;
        this.testStr = test;
        this.fmStr = fm;
        this.truthStr = truth;
        this.testName = testName;

    }


    public String visit_str() throws IOException {
        String assertType = getAssertType(assertionStr);

        AssertionParser assertionParser = null;
        if (assertType.equals("assertEquals")) {
            assertionParser = new EqualsParser(assertionStr, testStr, fmStr, truthStr, testName);
        } else if (assertType.equals("assertTrue") || assertType.equals("assertFalse")) {
            assertionParser = new TFParser(assertionStr, testStr, fmStr, truthStr, testName);
        } else if (assertType.equals("assertNull") || assertType.equals("assertNotNull")) {
            assertionParser = new NullParser(assertionStr, testStr, fmStr, truthStr, testName);
        } else {
            return "Unknown assertion type";
//            throw new RuntimeException("Unknown assertion type");
        }
        ParseResult parseResult = assertionParser.parse();

        return parseResult.result2json();
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

    private static void readFromFile(String[] args) throws IOException {

//        String filename = "1.txt";

        String folder = args[0];

        // walk through all files in the folder
        File folderFile = new File("src/main/resources/" + folder + "assertion");
        String[] files = folderFile.list();
//        System.out.println(files);

        PrintStream out = System.out;
        System.setOut(new PrintStream(args[1]));


        HashMap results = new LinkedHashMap();
//        int cnt = 0;

        for (String filename : files) {
            FileVisitor fileVisitor = new FileVisitor("src/main/resources/" + folder + "assertion/" + filename,
                    "src/main/resources/" + folder + "test/" + filename,
                    "src/main/resources/" + folder + "fm/" + filename,
                    "src/main/resources/" + folder + "truth/" + filename
                    , "example");
            String result = fileVisitor.visit();
            results.put(filename, result);
//            System.out.println("=============================="); // =*30
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String output = gson.toJson(results);
        System.out.println(output);
    }


    /*
        def add_fm_param(fm):
            lp = fm.find('{')
            body = fm[lp:]
            # print(body)
            signature = fm.split('{')[0]
            # print(signature)
            name = signature.split('(')[0].strip()
            params = signature.split('(')[1].split(')')[0].split(',')
            params_new = [x+' var24678 ' for x in params if x.strip() != '']
            params_new = ','.join(params_new)
            return name+'('+params_new+')' +' '+ body
     */
    private static String add_fm_param(String fm) {
        // java code for the function above
        int lp = fm.indexOf('{');
        String body = fm.substring(lp);
        String signature = fm.split("\\{")[0];
        String name = signature.split("\\(")[0].trim();
        String[] params = signature.split("\\(")[1].split("\\)")[0].split(",");
        List<String> params_new = new ArrayList<>();
        for (String param : params) {
            if (!param.trim().equals("")) {
                params_new.add(param + " var24678 ");
            }
        }
        String params_new_str = String.join(",", params_new);
        return "class Y{ public void " + name + "(" + params_new_str + ")" + " " + body + " }";
    }

    private static String replace_placeholder(String test_prefix, String assertion) {
        // replace "<AssertPlaceHolder>" with assertion
//        test_prefix.replace(""<AssertPlaceHolder>"", assertion);
        // replace the  "<AssertPlaceholder>" with assertion
        String replaced = "";
        try{
            replaced =  test_prefix.replaceAll("\"<AssertPlaceHolder>\"", assertion);
        }catch (Exception e){
            return "something wrong in test_prefix";
        }
        return "class X { " + replaced + " }";

    }


    private static void readFromStr(String[] args) throws IOException {
        String filePath = args[0];
        File file = new File(filePath);
        // read lines from file
        List<String> lines = readLines(file, StandardCharsets.UTF_8);

        HashMap results = new LinkedHashMap();
//        PrintStream out = System.out;
        System.setOut(new PrintStream(args[1]));

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            Gson gson = new Gson();
            HashMap hashMap = gson.fromJson(line, HashMap.class);
            for (int idx = 0; idx < ((ArrayList) hashMap.get("outputs")).size(); idx++) {
                Object output = ((ArrayList) hashMap.get("outputs")).get(idx);
                String truth = hashMap.get("truth").toString();
                String fm = add_fm_param(hashMap.get("fm").toString());
                String test = hashMap.get("test_prefix").toString();
                String replaced_test = replace_placeholder(test, output.toString());
                String assertion = output.toString();
                String testName = Integer.toString(i) + "_" + Integer.toString(idx);
                FileVisitor fileVisitor = new FileVisitor(assertion, replaced_test, fm, truth, testName);
                String result = fileVisitor.visit_str();
                results.put(testName, result);
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String output = gson.toJson(results);
        System.out.println(output);


    }


    public static void main(String[] args) throws IOException {
//        readFromFile(args);
        readFromStr(args);

    }

    private String readAssertTypeFromFirstLine(File f) throws IOException {
        FileReader fileReader = new FileReader(f);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        String assertType = line.split("\\(")[0].trim();
        return assertType;
    }

    private String getAssertType(String assertionStr) {
        String assertType = assertionStr.split("\\(")[0].trim();
        return assertType;
    }
}
