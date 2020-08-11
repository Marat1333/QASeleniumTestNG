package com.leroy.utils.file_manager;

import com.leroy.core.configuration.DriverFactory;

import java.io.File;

public class FileManager {
    public final static int SHORT_TIMEOUT = 5;
    public final static int LONG_TIMEOUT = 10;

    public static File getFileFromDefaultDownloadDirectory(String fileName) {
        return new File(DriverFactory.DOWNLOAD_DEFAULT_DIRECTORY + File.separator + fileName);
    }

    public static void waitUntilFileAppears(File file, int secTimeOut) throws InterruptedException {
        //на будущее можно добавить многопоточку, если вынести join
        FileWaiter fileWaiter = new FileWaiter(file, secTimeOut);
        fileWaiter.start();
        fileWaiter.join();
    }

    public static void clearDownloadDirectory() {
        File directory =  new File(DriverFactory.DOWNLOAD_DEFAULT_DIRECTORY);
        if (directory.isDirectory()) {
            for (File myFile : directory.listFiles())
                if (myFile.isFile()) {
                    myFile.delete();
                }
        }
    }
}
