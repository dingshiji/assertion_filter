package org.example.util;

import java.io.*;

public class readFile2str {
    public static String read(File f) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        String ls = System.getProperty("line.separator");
        try {
            while((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        bufferedReader.close();
        return stringBuilder.toString();
    }

}
