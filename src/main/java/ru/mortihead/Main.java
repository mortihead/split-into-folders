package ru.mortihead;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        FileHelper helper = new FileHelper();
        helper.processFiles();
    }

}
