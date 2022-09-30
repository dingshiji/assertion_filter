package util;

import org.example.util.readFile2str;

import java.io.File;
import java.io.IOException;

public class testReadFile2str {
    public static void main(String[] args) throws IOException {
        String fileName = "src\\main\\resources\\example\\test\\1.txt";
        File f = new File(fileName);
        String content = readFile2str.read(f);
        System.out.println(content);
    }
}
