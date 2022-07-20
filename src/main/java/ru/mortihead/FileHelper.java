package ru.mortihead;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileHelper {
    public static final String LOGGING_PROPERTIES = "logging.properties";
    private final String APPPROPFILE = "application.properties";
    private final String DATENAMETEMPLATE = "yyyyMMdd";
    private final String FOLDERTEMPLATE = "yyyy/MM";
    private String sourceFolder;
    private String targetFolder;
    private Logger log = Logger.getLogger(FileHelper.class.getName());

    public FileHelper() {
        try (InputStream input = FileHelper.class.getClassLoader().getResourceAsStream(APPPROPFILE)) {
            LogManager.getLogManager().readConfiguration(FileHelper.class.getClassLoader().getResourceAsStream(LOGGING_PROPERTIES));
            Properties prop = new Properties();
            if (input == null) {
                log.log(Level.WARNING, "Sorry, unable to find {0}", APPPROPFILE);
                return;
            }

            prop.load(input);

            sourceFolder = prop.getProperty("source.folder");
            targetFolder = prop.getProperty("target.folder");
            log.info("sourceFolder: " + sourceFolder);
            log.info("targetFolder: " + targetFolder);

        } catch (IOException ex) {
            log.throwing(getClass().getName(), "ERROR", ex);
        }
    }

    private void processFile(String counterStr, Path x) {
        log.log(Level.INFO, "{0} {1}", new Object[]{counterStr, x.toString()});
        SimpleDateFormat format = new SimpleDateFormat(DATENAMETEMPLATE);
        SimpleDateFormat folderFormat = new SimpleDateFormat(FOLDERTEMPLATE);
        Pattern p = Pattern.compile("\\d\\d\\d\\d\\d\\d\\d\\d_");
        Matcher m = p.matcher(x.getFileName().toString());
        Date tempDate;
        if (m.find()) {
            try {
                tempDate = format.parse(m.group());
                moveFileInTargetFolder(x, targetFolder + "/" + folderFormat.format(tempDate));
            } catch (ParseException pe) {
                log.log(Level.WARNING, "Filename wrong.");
            }
        } else {
            log.log(Level.WARNING, "Not found {0} template in file {1}. Move file to unsorted folder.", new Object[]{DATENAMETEMPLATE, x.toString()});
            String targetFolderName = targetFolder + "/unsorted";
            moveFileInTargetFolder(x, targetFolderName);
        }
    }

    private void moveFileInTargetFolder(Path x, String targetFolderName) {
        try {
            if (!Files.isDirectory(Paths.get(targetFolderName))) {
                log.info("  Folder not exist. Creating...");
                Files.createDirectories(Paths.get(targetFolderName));
            }
            Files.move(x, Paths.get(targetFolderName + "/" + x.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            log.info("  File moved");
        } catch (IOException ioe) {
            log.log(Level.SEVERE, "IO ERROR: " + ioe.getMessage(), ioe);
        }
    }

    List<Path> listFiles() throws IOException {
        List<Path> result;
        try (Stream<Path> walk = Files.walk(Paths.get(sourceFolder))) {
            result = walk.filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }
        return result;
    }

    public void processFiles() throws IOException {
        log.info("Calc files list...");
        List<Path> paths = listFiles();
        log.info("Files total: " + paths.size());
        for (int i = 0; i < paths.size(); i++) {
            processFile(String.format("%d/%d", i + 1, paths.size()), paths.get(i));
        }
    }

    public String getSourceFolder() {
        return sourceFolder;
    }

    public void setSourceFolder(String sourceFolder) {
        this.sourceFolder = sourceFolder;
    }

    public String getTargetFolder() {
        return targetFolder;
    }

    public void setTargetFolder(String targetFolder) {
        this.targetFolder = targetFolder;
    }


}
