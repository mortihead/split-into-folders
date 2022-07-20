package ru.mortihead;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class FileHelperTest {

    @Test
    public void processFiles() throws IOException {
        FileHelper helper = new FileHelper();
        helper.setSourceFolder("F:\\!Save\\Graphics, Picture\\Canon (CR2) RAW image");
        helper.setTargetFolder("F:\\!Save\\Graphics, Picture\\Canon (CR2) RAW image\\target");
        helper.processFiles();
    }

    @Test
    public void processJpegFiles() throws Exception {
        FileHelper helper = new FileHelper();
        helper.setSourceFolder("F:\\!Save\\Graphics, Picture\\JPEG Digital Camera");
        System.out.println("Calc files list..."); //55228
        System.out.println("Start search wrong jpeg...");
        List<Path> paths = helper.listFiles();
        for (int i = 0; i < paths.size(); i++) {
            File jpegFile = paths.get(i).toFile();
            if (jpegFile.getName().toLowerCase(Locale.ROOT).endsWith(".jpg") || jpegFile.getName().toLowerCase(Locale.ROOT).endsWith(".jpeg")) {
                if (i % 100 == 0) {
                    System.out.println(String.format("%d/%d %s", i + 1, paths.size(), jpegFile.getName()));
                    memoryStats();
                }
                if (!isImage(jpegFile)) {
                    System.out.println("WRONG Jpeg: " + jpegFile.getName());
                    if (!jpegFile.delete()) {
                        System.out.println("Delete error!");
                    } else {
                        System.out.println("Delete ok!");
                    }
                }
            }
        }
        System.out.println("Complete.");
    }

    @Test
    public void processJpegFile() throws Exception {
        System.out.println(isImage(new File("F:\\!Save\\Graphics, Picture\\JPEG Digital Camera\\NIKON D850_20180828_122211.Id_940000000003076.jpg")));
        System.out.println(isImage(new File("F:\\!Save\\Graphics, Picture\\JPEG Digital Camera\\NIKON D850_20180828_122211.Id_9400000000061b4.jpg")));
    }


    public static boolean isImage(File file) {
        try {
            return ImageIO.read(file) != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static Boolean isJPEG(File filename) throws Exception {
        try (DataInputStream ins = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
            int firstBytes = ins.readInt();
            return (firstBytes == 0xffd8ffe0 || firstBytes == 0xffd8ffe1);
        }
    }

    public static void memoryStats() {
        int mb = 1024 * 1024;
        // get Runtime instance
        Runtime instance = Runtime.getRuntime();
        // available memory
        System.out.println("Total Memory: " + instance.totalMemory() / mb);
        // free memory
        System.out.println("Free Memory: " + instance.freeMemory() / mb);
        // used memory
        System.out.println("Used Memory: "
                + (instance.totalMemory() - instance.freeMemory()) / mb);
        // Maximum available memory
        System.out.println("Max Memory: " + instance.maxMemory() / mb);
    }
}