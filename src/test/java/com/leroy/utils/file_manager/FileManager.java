package com.leroy.utils.file_manager;

import com.leroy.core.configuration.DriverFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileManager {
    public final static int SHORT_TIMEOUT = 5;
    public final static int LONG_TIMEOUT = 10;

    public static File getFileFromDefaultDownloadDirectory(String fileName) {
        return new File(DriverFactory.DOWNLOAD_DEFAULT_DIRECTORY + File.separator + fileName);
    }

    public static File downloadFileFromNetworkToDefaultDownloadDirectory(String uri, String expectedFileName) throws Exception {
        URI localUri = new URI(uri);
        InputStream is = localUri.toURL().openStream();
        String pathToFile = DriverFactory.DOWNLOAD_DEFAULT_DIRECTORY + File.separator + expectedFileName;
        Files.copy(is, Paths.get(pathToFile), StandardCopyOption.REPLACE_EXISTING);
        return new File(pathToFile);
    }

    public static void waitUntilFileAppears(File file, int secTimeOut) throws InterruptedException {
        //на будущее можно добавить многопоточку, если вынести join
        FileWaiter fileWaiter = new FileWaiter(file, secTimeOut);
        fileWaiter.start();
        fileWaiter.join();
    }

    public static void clearDownloadDirectory() {
        File directory = new File(DriverFactory.DOWNLOAD_DEFAULT_DIRECTORY);
        if (directory.isDirectory()) {
            for (File myFile : directory.listFiles())
                if (myFile.isFile()) {
                    myFile.delete();
                }
        }
    }
}
