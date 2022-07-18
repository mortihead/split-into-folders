package ru.mortihead;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        FileHelper helper = new FileHelper();
        List<Path> paths = helper.listFiles();
        paths.forEach(x -> {
            helper.processFile(x);
        });

    }

}
