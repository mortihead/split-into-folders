package ru.mortihead;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public void processFile(Path x) {
        log.info(x.toString());
        SimpleDateFormat format = new SimpleDateFormat(DATENAMETEMPLATE);
        SimpleDateFormat folderFormat = new SimpleDateFormat(FOLDERTEMPLATE);
        Pattern p = Pattern.compile("\\d\\d\\d\\d\\d\\d\\d\\d_");
        Matcher m = p.matcher(x.getFileName().toString());
        Date tempDate = null;
        if (m.find()) {
            try {
                tempDate = format.parse(m.group());
                log.info("filename ok");
                String targetFolderName = targetFolder + "/" + folderFormat.format(tempDate);
                log.info("Check " + targetFolderName);
                if (Files.isDirectory(Paths.get(targetFolderName))) {
                    log.info("Folder exist.");
                } else {
                    log.info("Folder not exist. Creating...");
                    Files.createDirectories(Paths.get(targetFolderName));
                }
            } catch (ParseException pe) {
                log.log(Level.WARNING, "filename not ok");
            } catch (IOException ioe) {
                log.log(Level.WARNING, "IO ERROR: "+ioe.getMessage());
            }
        }
    }

    public List<Path> listFiles() throws IOException {
        List<Path> result;
        try (Stream<Path> walk = Files.walk(Paths.get(sourceFolder))) {
            result = walk.filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }
        return result;
    }

}
