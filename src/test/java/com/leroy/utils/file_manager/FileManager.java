package com.leroy.utils.file_manager;

import com.leroy.core.configuration.DriverFactory;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileManager {
    private FileWaiter fw;
    public final static int SHORT_TIMEOUT = 5;
    public final static int LONG_TIMEOUT = 10;
    public final static int MINUTE_TIMEOUT = 60;

    private static String getDefaultDownloadDirectory() {
        String defaultDirectory = DriverFactory.getDefaultDownloadDirectory();
        if (defaultDirectory == null)
            defaultDirectory = System.getProperty("user.dir") + File.separator + "downloadFiles";
        return defaultDirectory;
    }

    public File getFileFromDefaultDownloadDirectory(String fileName) {
        return getFileFromDefaultDownloadDirectory(fileName, MINUTE_TIMEOUT);
    }

    public File getFileFromDefaultDownloadDirectory(String fileName, int timeOut) {
        File file = new File(getDefaultDownloadDirectory() + File.separator + fileName);
        fw = new FileWaiter(file, MINUTE_TIMEOUT);
        fw.start();
        return file;
    }

    public File downloadFileFromNetworkToDefaultDownloadDirectory(String uri, String expectedFileName) throws Exception {
        return downloadFileFromNetworkToDefaultDownloadDirectory(uri, expectedFileName, MINUTE_TIMEOUT);
    }

    public File downloadFileFromNetworkToDefaultDownloadDirectory(String uri, String expectedFileName, int timeOut) throws Exception {
        URI localUri = new URI(uri);
        InputStream is = localUri.toURL().openStream();
        String pathToFile = getDefaultDownloadDirectory() + File.separator + expectedFileName;
        Files.createDirectories(Paths.get(pathToFile).getParent());
        Files.copy(is, Paths.get(pathToFile), StandardCopyOption.REPLACE_EXISTING);
        File file = new File(pathToFile);
        fw = new FileWaiter(file, MINUTE_TIMEOUT);
        fw.start();
        return file;
    }

    public void waitUntilFileAppears() throws InterruptedException {
        fw.join();
    }

    public static void clearDownloadDirectory() {
        File directory = new File(getDefaultDownloadDirectory());
        if (directory.isDirectory()) {
            for (File myFile : directory.listFiles())
                if (myFile.isFile()) {
                    myFile.delete();
                }
        }
    }
}
